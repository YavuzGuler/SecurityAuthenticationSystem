import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
public class RSAOperations {
    public byte[] encryption(byte[] plainText,PublicKey publicKey){
        try{
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");

            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            return cipher.doFinal(plainText);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public byte[] decryption(byte[] cipherText,PrivateKey privateKey){
        try {
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherText);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public byte[] sign(byte[] bytes,PrivateKey privateKey){
        try{
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] signVerification(byte[] plainText,PublicKey publicKey){
        try{
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE,publicKey);
            return cipher.doFinal(plainText);
        }catch (Exception e){
            return null;
        }
    }

    public byte[] aesencryption(byte[] key, byte[] plainText){
        try {
            HashOperations hashOperations=new HashOperations();
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey=new SecretKeySpec(key,"AES");
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            return cipher.doFinal(plainText);
        }catch (Exception e){
            return null;
        }
    }

    public byte[] aesdecryption(byte[] key,byte[] cipherText){
        try {
            HashOperations hashOperations=new HashOperations();
            Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey=new SecretKeySpec(key,"AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            return cipher.doFinal(cipherText);
        }catch (Exception e){
            return null;
        }
    }

}

