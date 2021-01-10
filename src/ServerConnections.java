import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Random;

public class ServerConnections {
    private RSAOperations rsaOperations;
    private FileIO fileIO;
    public ServerConnections(){
        rsaOperations=new RSAOperations();
        fileIO=new FileIO();
    }
    public void connection(PrivateKey privateKey,int port,String fileName){
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream din = new DataInputStream(socket.getInputStream());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                String packet=din.readUTF();

                String ticket=new String(rsaOperations.decryption(Base64.getDecoder().decode(packet.split(", ")[1].getBytes(StandardCharsets.UTF_8)),privateKey));
                String serverName=ticket.split(", ")[1].substring(1,ticket.split(", ")[1].length()-1);
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"Alice->"+serverName+" : "+packet,1);
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"\"Ticket Decrpyted\" : "+ticket,1);
                byte[] sessionKey=Base64.getDecoder().decode(ticket.split(", ")[3]);
                int N1=Integer.parseInt(new String(rsaOperations.aesdecryption(sessionKey,Base64.getDecoder().decode(packet.split(", ")[2].getBytes(StandardCharsets.UTF_8)))));
                Random random=new Random();
                int N2=random.nextInt();
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"\"Message Decrpyted\" : N1="+Integer.toString(N1),1);
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+serverName+"->Alice : "+Integer.toString(N1+1)+", "+Integer.toString(N2) ,1);
                packet=new String(Base64.getEncoder().encode(rsaOperations.aesencryption(sessionKey,(Integer.toString(N1+1)+", "+Integer.toString(N2)).getBytes(StandardCharsets.UTF_8))));
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+serverName+"->Alice : "+packet,1);
                dout.writeUTF(packet);
                packet=din.readUTF();
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"Alice->"+serverName+" : "+packet,1);
                packet=new String(rsaOperations.aesdecryption(sessionKey,Base64.getDecoder().decode(packet.getBytes(StandardCharsets.UTF_8))));
                fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"\"Message Decrpyted\" : "+packet,1);
                if(Integer.parseInt(packet)==N2+1){
                    packet=new String(Base64.getEncoder().encode(rsaOperations.aesencryption(sessionKey,"\"Authentication is completed!\"".getBytes(StandardCharsets.UTF_8))));
                    fileIO.appendStrToFile(new File(fileName),fileIO.timeReturner()+"Alice->"+serverName+" : "+"\"Authentication is completed!\"",1);
                    dout.writeUTF(packet);
                    socket.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
