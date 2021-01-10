
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class Alice {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    private PublicKey kdcPublicKey;
    private DataInputStream din;
    private DataOutputStream dout;
    private Socket socket;
    private  String[] serverList;
    RSAOperations rsaOperations;
    public Alice(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
        rsaOperations=new RSAOperations();
        serverList= new String[]{"Mail", "Web", "Database"};
    }
    public static void main(String[] args) {
        Alice alice=new Alice();
        alice.rsaKeyPair.setPrivateKey(alice.fileIO.readPrivateKey("keys/Alice.txt"));
        alice.rsaKeyPair.setPublicKey(alice.fileIO.readCertificate("cert/Alice.cer"));
        alice.kdcPublicKey=alice.fileIO.readCertificate("cert/KDCServer.cer");


        Scanner scanner=new Scanner(System.in);
        String IP="localhost";
        int port=3000;


        String password;

        int choice;
        while(true){
            System.out.print("Enter Password: ");
            password=scanner.nextLine();
           System.out.println("\nTo Connect Mail Server press 1\n"+
                           "To Connect Web Server press 2:\n"+
                           "To Connect Database Server press 3:"
                   );
           try {
               port=3000;
               alice.socket=new Socket(IP,port);
               alice.din=new DataInputStream(alice.socket.getInputStream());
               alice.dout=new DataOutputStream(alice.socket.getOutputStream());
               choice=Integer.parseInt(scanner.nextLine());
               if(choice<=0||choice>=4){
                   System.out.println("wrong choice try again.".toUpperCase());
                   continue;
               }
               String timeStamp=alice.fileIO.timeReturner();;
               String packet=String.format("\"Alice\", %s, \"%s\", %s",password,alice.serverList[choice-1],timeStamp);

               alice.fileIO.appendStrToFile(new File("Alice_Log.txt"), timeStamp+"Alice->KDC : "+
                       packet,1);
               packet="\"Alice\", "+new String(Base64.getEncoder().encode(alice.rsaOperations.encryption(packet.getBytes(StandardCharsets.UTF_8),alice.kdcPublicKey)));
               alice.fileIO.appendStrToFile(new File("Alice_Log.txt"), timeStamp+"Alice->KDC : "+
                       packet,1);
               alice.dout.writeUTF(packet);
               packet=alice.din.readUTF();
               alice.fileIO.appendStrToFile(new File("Alice_Log.txt"), timeStamp+"KDC->Alice : "+
                       packet,1);
               if(packet.equals("\"Password Verified\"")){
                   packet=alice.din.readUTF();
                   alice.fileIO.appendStrToFile(new File("Alice_Log.txt"), timeStamp+"KDC->Alice : "+
                           packet,1);
                   String ticket=packet.split(", ")[1];
                   packet=packet.split(", ")[0];
                   packet= new String(alice.rsaOperations.decryption(Base64.getDecoder().decode(packet.getBytes(StandardCharsets.UTF_8)),alice.rsaKeyPair.getPrivateKey()));
                   alice.fileIO.appendStrToFile(new File("Alice_Log.txt"), timeStamp+"Message Decrypted : "+
                          packet,1);
                    alice.socket.close();
                    port=port+choice;
                    alice.socket=new Socket(IP,port);
                    alice.din=new DataInputStream(alice.socket.getInputStream());
                    alice.dout=new DataOutputStream(alice.socket.getOutputStream());
                    alice.connectServers(ticket,Base64.getDecoder().decode(packet.split(", ")[0]),choice);
                   alice.socket.close();

               }
           }catch (Exception e){
               System.out.println("wrong choice try again.\n".toUpperCase());

           }
       }
    }
    private void connectServers(String ticket, byte[] sessionKey,int choice){
        try {
            Random random=new Random();
            int n=random.nextInt();
            this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+" : \"Alice\", "+Integer.toString(n),1);
            String N=new String(Base64.getEncoder().encode(rsaOperations.aesencryption(sessionKey,Integer.toString(n).getBytes(StandardCharsets.UTF_8))));
            String packet=String.format("\"Alice\", %s, %s",ticket,N);
            this.fileIO.appendStrToFile(new File("Alice_Log.txt"),fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+packet,1);
            this.dout.writeUTF(packet);
            packet=this.din.readUTF();
            this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+this.serverList[choice-1]+"->Alice : "+packet,1);
            packet=new String(rsaOperations.aesdecryption(sessionKey,Base64.getDecoder().decode(packet)));
            if(Integer.parseInt(packet.split(", ")[0])==n+1){
                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Message Decrpyted : N1 is OK, N2="+packet.split(", ")[1],1);
                packet=Integer.toString(Integer.parseInt(packet.split(", ")[1])+1);
                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+" : "+packet,1);
                packet=new String(Base64.getEncoder().encode(this.rsaOperations.aesencryption(sessionKey,packet.getBytes(StandardCharsets.UTF_8))));
                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+" : "+packet,1);
                dout.writeUTF(packet);
                packet=din.readUTF();
                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+this.serverList[choice-1]+"->Alice"+" : "+new String(rsaOperations.aesdecryption(sessionKey,Base64.getDecoder().decode(packet.getBytes(StandardCharsets.UTF_8)))),1);

            }
            else{

                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+"Message Decrpyted : N1 isn't OK, N2="+packet.split(", ")[1],1);
                packet=Integer.toString(Integer.parseInt(packet.split(", ")[1])+2);
                packet=new String(Base64.getEncoder().encode(this.rsaOperations.aesencryption(sessionKey,packet.getBytes(StandardCharsets.UTF_8))));
                this.fileIO.appendStrToFile(new File("Alice_Log.txt"),this.fileIO.timeReturner()+"Alice->"+this.serverList[choice-1]+" : "+"\"Authentication isn't completed!\"",1);
                this.dout.writeUTF(packet);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
