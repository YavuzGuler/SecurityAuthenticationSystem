import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MailServer {
    public static void main(String[] args) {
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
