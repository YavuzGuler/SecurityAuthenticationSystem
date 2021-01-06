import sun.security.x509.X500Name;

import java.io.File;
import java.util.Objects;

public class KDCServer {
    private RSAKeyPair rsaKeyPair=new RSAKeyPair();

    public static void main(String[] args) {
        System.out.println("sa");
        KDCServer kdcServer = new KDCServer();
        kdcServer.checkCertsAndKeys();

    }
    private  void checkCertsAndKeys(){
        boolean createRSA=false;
        File file=new File("cert");
        if (!file.exists()) {
            file.mkdir();
            createRSA=true;
        }
       /* else{
            for(int i =0;i<file.listFiles().length;i++){
                System.out.println(file.listFiles());
            }
        }*/
        file=new File("keys");
        if (!file.exists()) {
            file.mkdir();
            createRSA=true;
        }
        if(Objects.requireNonNull(file.listFiles()).length!=5)
            createRSA=true;
        if(createRSA)
            createRSA();


    }
    public  void createRSA(){

        try {
            GenerateRSAKeyPair generateRSAKeyPair=new GenerateRSAKeyPair();
            FileIO fileIO=new FileIO();
            this.rsaKeyPair=generateRSAKeyPair.createKeyPair();
            X500Name issuer = new X500Name("KDCServer", "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey");
            this.rsaKeyPair.setCertificate(generateRSAKeyPair.getSignedCertificate(issuer,issuer,this.rsaKeyPair.getPublicKey(),this.rsaKeyPair.getPrivateKey(),this.rsaKeyPair.getSignAlgorithm()));
            RSAKeyPair [] rsaKeyPairList=new RSAKeyPair[4];

            String[] nameList={"WEBServer","MailServer","Alice","DatabaseServer"};
            fileIO.writePrivateKey("keys/KDCServer.txt",this.rsaKeyPair.getPrivateKey());
            fileIO.writeCertificate("cert/KDCServer.cer",this.rsaKeyPair.getCertificate());

            for(int i=0;i<4;i++){
                rsaKeyPairList[i]=generateRSAKeyPair.createKeyPair();
                rsaKeyPairList[i].setCertificate(generateRSAKeyPair.getSignedCertificate(issuer,new X500Name(nameList[i], "CSU", "BBM465 Cooperation", "Ankara", "Mamak", "Turkey"),rsaKeyPairList[i].getPublicKey(),this.rsaKeyPair.getPrivateKey(),this.rsaKeyPair.getSignAlgorithm()));
                fileIO.writePrivateKey("keys/"+nameList[i]+".txt",rsaKeyPairList[i].getPrivateKey());
                fileIO.writeCertificate("cert/"+nameList[i]+".cer",rsaKeyPairList[i].getCertificate());
                rsaKeyPairList[i].getCertificate().verify(rsaKeyPair.getPublicKey());
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}

