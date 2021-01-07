import sun.security.x509.X500Name;

import java.io.DataInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.UUID;

public class KDCServer {
    private RSAKeyPair rsaKeyPair;
    private  String[] nameList={"KDCServer","WEBServer","MailServer","Alice","DatabaseServer"};
    private FileIO fileIO;
    private String password;
    public KDCServer(){
      rsaKeyPair=new RSAKeyPair();
      fileIO=new FileIO();
    }
    public static void main(String[] args) {
        System.out.println("sa");
        HashOperations hashOperations=new HashOperations();
        KDCServer kdcServer = new KDCServer();
        kdcServer.checkCertsAndKeys();
        kdcServer.password=UUID.randomUUID().toString().replaceAll("-","");
        kdcServer.fileIO.appendStrToFile(new File("KDC_Log.txt"),kdcServer.fileIO.timeReturner()+kdcServer.password,1);
        kdcServer.fileIO.writeFile(new File("passwd"),new String(Base64.getEncoder().encode(hashOperations.SHA1(kdcServer.password))),1);
        try {

            ServerSocket serverSocket=new ServerSocket(3000);
            Socket socket=serverSocket.accept();
            DataInputStream din=new DataInputStream(socket.getInputStream());


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private  void checkCertsAndKeys(){
        boolean createRSA=false;
        File file=new File("cert");
        if (!file.exists()&&!file.isDirectory()) {
            file.mkdir();
            createRSA();
            return;
        }
        for(int i=0;i<this.nameList.length;i++){
           file=new File("cert/"+this.nameList[i]+".cer");
           if(!file.exists()){
               createRSA();
               return;
           }
        }
        file=new File("keys");
        if (!file.exists()&&!file.isDirectory()) {
            file.mkdir();
           createRSA();
           return;
        }
        for(int i=0;i<this.nameList.length;i++){
            file=new File("keys/"+this.nameList[i]+".txt");
            if(!file.exists()){
                createRSA();
                return;
            }
        }
    }
    public  void createRSA(){
                System.out.println("sxa");
        try {
            GenerateRSAKeyPair generateRSAKeyPair=new GenerateRSAKeyPair();

            this.rsaKeyPair=generateRSAKeyPair.createKeyPair();
            X500Name issuer = new X500Name("KDCServer", "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey");
            this.rsaKeyPair.setCertificate(generateRSAKeyPair.getSignedCertificate(issuer,issuer,this.rsaKeyPair.getPublicKey(),this.rsaKeyPair.getPrivateKey(),this.rsaKeyPair.getSignAlgorithm()));
            RSAKeyPair [] rsaKeyPairList=new RSAKeyPair[4];
            fileIO.writePrivateKey("keys/KDCServer.txt",this.rsaKeyPair.getPrivateKey());
            fileIO.writeCertificate("cert/KDCServer.cer",this.rsaKeyPair.getCertificate());
            for(int i=0;i<4;i++){
                rsaKeyPairList[i]=generateRSAKeyPair.createKeyPair();
                rsaKeyPairList[i].setCertificate(generateRSAKeyPair.getSignedCertificate(issuer,new X500Name(nameList[i+1], "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey"),rsaKeyPairList[i].getPublicKey(),this.rsaKeyPair.getPrivateKey(),this.rsaKeyPair.getSignAlgorithm()));
                fileIO.writePrivateKey("keys/"+nameList[i+1]+".txt",rsaKeyPairList[i].getPrivateKey());
                fileIO.writeCertificate("cert/"+nameList[i+1]+".cer",rsaKeyPairList[i].getCertificate());
              //  rsaKeyPairList[i].getCertificate().verify(rsaKeyPair.getPublicKey());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

