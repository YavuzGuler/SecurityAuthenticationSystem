import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class RSAKeyPair {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String signAlgorithm;
    private X509Certificate certificate;

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }



}
