import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DatabaseServer {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    public DatabaseServer(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
    }
    public static void main(String[] args) {
        DatabaseServer databaseServer=new DatabaseServer();
        databaseServer.rsaKeyPair.setPublicKey(databaseServer.fileIO.readCertificate("cert/DatabaseServer.cer"));
        databaseServer.rsaKeyPair.setPrivateKey(databaseServer.fileIO.readPrivateKey("keys/DatabaseServer.txt"));
        ServerConnections serverConnections =new ServerConnections();
        serverConnections.connection(databaseServer.rsaKeyPair.getPrivateKey(),3003,"Database_Log.txt");

    }
}
