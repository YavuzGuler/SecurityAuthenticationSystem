import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    public WebServer(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
    }
    public static void main(String[] args) {
        System.out.println("sa");
        WebServer webServer=new WebServer();
        webServer.rsaKeyPair.setPublicKey(webServer.fileIO.readCertificate("cert/WEBServer.cer"));
        webServer.rsaKeyPair.setPrivateKey(webServer.fileIO.readPrivateKey("keys/WEBServer.txt"));
        try {
            ServerSocket serverSocket=new ServerSocket(3002);
            Socket socket=serverSocket.accept();
            DataInputStream din=new DataInputStream(socket.getInputStream());


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
