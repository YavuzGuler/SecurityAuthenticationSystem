import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashOperations {

    public  byte[] SHA1(String text){
        byte[] output=null;
        try {
            MessageDigest sha1=null;
            sha1=MessageDigest.getInstance("SHA-1");
            sha1.update(text.getBytes(StandardCharsets.UTF_8));
            output=sha1.digest();
            return Base64.getEncoder().encode(output);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public byte[] MD5(String text){
        byte[] output=null;
        try {
            MessageDigest md5=null;
            md5=MessageDigest.getInstance("MD5");
            md5.update(text.getBytes(StandardCharsets.UTF_8));
            output=md5.digest();
            return output;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public byte[] MD5(byte[] in){
        byte[] output=null;
        try {
            MessageDigest md5=null;
            md5=MessageDigest.getInstance("MD5");
            md5.update(in);
            output=md5.digest();
            return output;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
