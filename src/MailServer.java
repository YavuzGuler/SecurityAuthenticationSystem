import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MailServer {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    public MailServer(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
    }
    public static void main(String[] args) {
        MailServer mailServer=new MailServer();
        mailServer.rsaKeyPair.setPrivateKey(mailServer.fileIO.readPrivateKey("keys/MailServer.txt"));
        mailServer.rsaKeyPair.setPublicKey(mailServer.fileIO.readCertificate("cert/MailServer.cer"));
        System.out.println("sa");
        try {
            ServerSocket serverSocket=new ServerSocket(3001);
            Socket socket=serverSocket.accept();
            DataInputStream din=new DataInputStream(socket.getInputStream());


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
