import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

public class ServerConnections {
    private RSAOperations rsaOperations;
    private FileIO fileIO;
    public ServerConnections(){
        rsaOperations=new RSAOperations();
        fileIO=new FileIO();
    }
    public void connection(PrivateKey privateKey,int port,String name,String fileName){
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream din = new DataInputStream(socket.getInputStream());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                String packet=din.readUTF();
                String ticket=new String(rsaOperations.decryption(Base64.getDecoder().decode(packet.split(", ")[1].getBytes(StandardCharsets.UTF_8)),privateKey));
                String sessionKey=ticket.split(", ")[3];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
