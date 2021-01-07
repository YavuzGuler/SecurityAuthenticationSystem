import java.util.Base64;
import java.util.Scanner;

public class Alice {
    private RSAKeyPair rsaKeyPair;
    private FileIO fileIO;
    public Alice(){
        rsaKeyPair=new RSAKeyPair();
        fileIO=new FileIO();
    }
    public static void main(String[] args) {
        Alice alice=new Alice();
        alice.rsaKeyPair.setPrivateKey(alice.fileIO.readPrivateKey("keys/Alice.txt"));
        alice.rsaKeyPair.setPublicKey(alice.fileIO.readCertificate("cert/Alice.cer"));
        Scanner scanner=new Scanner(System.in);
        String password;
        System.out.print("Enter Password: ");
        password=scanner.nextLine();
        System.out.println(password);
        int choice;
        while(true){
           System.out.println("To Connect Web Server press 1\n"+
                           "To Connect Mail Server press 2:\n"+
                           "To Connect Database Server press 3:"
                   );
           try {
               choice=Integer.parseInt(scanner.nextLine());
               if(choice<=0||choice>=4){
                   System.out.println("wrong choice try again.".toUpperCase());
                   continue;
               }
                System.out.println(choice);
           }catch (Exception e){
               System.out.println("wrong choice try again.\n".toUpperCase());

           }
       }
    }
}
