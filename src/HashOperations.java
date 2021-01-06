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

}
