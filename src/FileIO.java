import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Scanner;

public class FileIO {
    public String timeReturner(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss ");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    //function for appending new lines to output file.
    public  void appendStrToFile(File file,String str,int newLine)
    {
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(file, true));
            out.write(str+((newLine==1)?"\n":""));
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }
    public void writeFile(File file,String str,int newLine){
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(file, false));
            out.write(str+((newLine==1)?"\n":""));
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    public PublicKey readCertificate(String certificatePath){
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream (certificatePath);
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            is.close();
            return cer.getPublicKey();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public PrivateKey readPrivateKey(String privateKeyPath){
        try {
            Scanner scanner=new Scanner(new File(privateKeyPath));
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            String privateKey=scanner.nextLine();
            scanner.close();
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey.getBytes(StandardCharsets.UTF_8))));

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void writePrivateKey(String privateKeyPath,PrivateKey privateKey){
        try {
            FileWriter fileWriter=new FileWriter(new File(privateKeyPath));
            fileWriter.write(new String(Base64.getEncoder().encode(privateKey.getEncoded())));
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void writeCertificate(String certificatePath, X509Certificate certificate){
        try {
            FileWriter fileWriter=new FileWriter(new File(certificatePath));
            fileWriter.write(certToString(certificate));
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String certToString(X509Certificate certificate){
        StringWriter stringWriter= new StringWriter();
        try {
            stringWriter.write("-----BEGIN CERTIFICATE-----\n");
            stringWriter.write(DatatypeConverter.printBase64Binary(certificate.getEncoded()).replaceAll("(.{64})", "$1\n"));
            stringWriter.write("\n-----END CERTIFICATE-----\n");
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

}
