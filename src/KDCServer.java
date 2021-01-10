import sun.security.x509.X500Name;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

public class KDCServer {
    private RSAKeyPair rsaKeyPair;
    private String[] nameList = {"KDCServer", "WEBServer", "MailServer", "Alice", "DatabaseServer"};
    private FileIO fileIO;
    private String password;
    private DataOutputStream dout;
    private DataInputStream din;
    private RSAOperations rsaOperations;
    private PublicKey alicePublicKey;
    private PublicKey webPublicKey;
    private PublicKey databasePublicKey;
    private PublicKey mailPublicKey;

    public KDCServer() {
        rsaKeyPair = new RSAKeyPair();
        fileIO = new FileIO();
        rsaOperations = new RSAOperations();
    }

    public static void main(String[] args) {
        System.out.println("sa");
        HashOperations hashOperations = new HashOperations();
        KDCServer kdcServer = new KDCServer();
        kdcServer.checkCertsAndKeys();
        kdcServer.password = UUID.randomUUID().toString().replaceAll("-", "");
        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + kdcServer.password, 1);
        kdcServer.fileIO.writeFile(new File("passwd"), new String(Base64.getEncoder().encode(hashOperations.SHA1(kdcServer.password))), 1);
        String packet;

        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            while (true) {
                Socket socket = serverSocket.accept();
                kdcServer.din = new DataInputStream(socket.getInputStream());
                kdcServer.dout = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    packet = kdcServer.din.readUTF();
                    kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "Alice->KDC : " + packet, 1);
                    String message = kdcServer.decMessage(packet);
                    kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "Message Decrypted : " + message, 1);
                    if (!message.split(", ")[1].equals(kdcServer.password)) {
                        kdcServer.dout.writeUTF("\"Password Denied\"");
                        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "KDC->Alice : \"Password Denied\"", 1);
                        break;
                    } else {
                        kdcServer.dout.writeUTF("\"Password Verified\"");
                        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "KDC->Alice : \"Password Verified\"", 1);
                        String timeStamp=kdcServer.fileIO.timeReturner();
                        String sessionKey = new String(Base64.getEncoder().encode(hashOperations.MD5(kdcServer.password+timeStamp)));

                        String ticket = String.format("\"Alice\", %s, %s, %s", message.split(", ")[2], timeStamp, sessionKey);

                        packet = new String(Base64.getEncoder().encode(kdcServer.rsaOperations.encryption(String.format("%s, %s, %s", sessionKey, message.split(", ")[2], timeStamp).getBytes(StandardCharsets.UTF_8), kdcServer.alicePublicKey))) +
                                ", " +
                                new String(Base64.getEncoder().encode(kdcServer.rsaOperations.encryption(ticket.getBytes(StandardCharsets.UTF_8),
                                        message.split(", ")[2].equals("\"Mail\"") ? kdcServer.mailPublicKey : message.split(", ")[2].equals("\"Web\"") ? kdcServer.webPublicKey : kdcServer.databasePublicKey)));
                        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "KDC->Alice : "+String.format("%s, %s, %s", sessionKey, message.split(", ")[2], timeStamp), 1);
                        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"), kdcServer.fileIO.timeReturner() + "KDC->Alice : "+packet, 1);
                        kdcServer.dout.writeUTF(packet);
                        socket.close();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String decMessage(String packet) {
        return new String(rsaOperations.decryption(Base64.getDecoder().decode(packet.split(", ")[1].getBytes(StandardCharsets.UTF_8)), this.rsaKeyPair.getPrivateKey()));

    }

    private void checkCertsAndKeys() {
        boolean createRSA = false;
        File file = new File("cert");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
            createRSA();
            return;
        }
        for (int i = 0; i < this.nameList.length; i++) {
            file = new File("cert/" + this.nameList[i] + ".cer");
            if (!file.exists()) {
                createRSA();
                return;
            }
        }
        file = new File("keys");
        if (!file.exists() && !file.isDirectory()) {
            file.mkdir();
            createRSA();
            return;
        }
        for (int i = 0; i < this.nameList.length; i++) {
            file = new File("keys/" + this.nameList[i] + ".txt");
            if (!file.exists()) {
                createRSA();
                return;
            }
        }
        readCertificates();
    }

    private void readCertificates() {
        this.rsaKeyPair.setPublicKey(this.fileIO.readCertificate("cert/KDCServer.cer"));
        this.rsaKeyPair.setPrivateKey(this.fileIO.readPrivateKey("keys/KDCServer.txt"));
        this.alicePublicKey = this.fileIO.readCertificate("cert/Alice.cer");
        this.webPublicKey = this.fileIO.readCertificate("cert/WEBServer.cer");
        this.mailPublicKey = this.fileIO.readCertificate("cert/MailServer.cer");
        this.databasePublicKey = this.fileIO.readCertificate("cert/DatabaseServer.cer");

    }

    public void createRSA() {
        System.out.println("sxa");
        try {
            GenerateRSAKeyPair generateRSAKeyPair = new GenerateRSAKeyPair();

            this.rsaKeyPair = generateRSAKeyPair.createKeyPair();
            X500Name issuer = new X500Name("KDCServer", "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey");
            this.rsaKeyPair.setCertificate(generateRSAKeyPair.getSignedCertificate(issuer, issuer, this.rsaKeyPair.getPublicKey(), this.rsaKeyPair.getPrivateKey(), this.rsaKeyPair.getSignAlgorithm()));
            RSAKeyPair[] rsaKeyPairList = new RSAKeyPair[4];
            fileIO.writePrivateKey("keys/KDCServer.txt", this.rsaKeyPair.getPrivateKey());
            fileIO.writeCertificate("cert/KDCServer.cer", this.rsaKeyPair.getCertificate());
            for (int i = 0; i < 4; i++) {
                rsaKeyPairList[i] = generateRSAKeyPair.createKeyPair();
                rsaKeyPairList[i].setCertificate(generateRSAKeyPair.getSignedCertificate(issuer, new X500Name(nameList[i + 1], "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey"), rsaKeyPairList[i].getPublicKey(), this.rsaKeyPair.getPrivateKey(), this.rsaKeyPair.getSignAlgorithm()));
                fileIO.writePrivateKey("keys/" + nameList[i + 1] + ".txt", rsaKeyPairList[i].getPrivateKey());
                fileIO.writeCertificate("cert/" + nameList[i + 1] + ".cer", rsaKeyPairList[i].getCertificate());
                //  rsaKeyPairList[i].getCertificate().verify(rsaKeyPair.getPublicKey());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

