import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MailServer {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    private RSAOperations rsaOperations;
    public MailServer(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
        rsaOperations=new RSAOperations();
    }
    public static void main(String[] args) {
        MailServer mailServer=new MailServer();
        mailServer.rsaKeyPair.setPrivateKey(mailServer.fileIO.readPrivateKey("keys/MailServer.txt"));
        mailServer.rsaKeyPair.setPublicKey(mailServer.fileIO.readCertificate("cert/MailServer.cer"));
        ServerConnections serverConnections =new ServerConnections();
        serverConnections.connection(mailServer.rsaKeyPair.getPrivateKey(),3001,"\"Mail\"","Mail_Log.txt");



    }
}
