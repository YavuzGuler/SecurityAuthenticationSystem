import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PSSParameterSpec;
import java.util.Date;
import java.util.Random;

public class GenerateRSAKeyPair {
    public RSAKeyPair createKeyPair(){
        try {
            String signAlgorithm="SHA256WithRSA";
            CertAndKeyGen keypair = new CertAndKeyGen("RSA", signAlgorithm);
            keypair.generate(2048);
            RSAKeyPair rsaKeyPair = new RSAKeyPair();
            rsaKeyPair.setPrivateKey(keypair.getPrivateKey());
            rsaKeyPair.setPublicKey(keypair.getPublicKey());
            rsaKeyPair.setSignAlgorithm(signAlgorithm);
            return rsaKeyPair;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public X509Certificate getSignedCertificate( X500Name issuer,X500Name subject, PublicKey publicKey, PrivateKey privateKey, String sigAlg) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        try {

            long validThrough=(long) 365 * 24 * 60 * 60;
            Date date = new Date();
            date.setTime(date.getTime()+validThrough * 1000L);
            date.setTime(date.getTime() + validThrough * 1000L);
            CertificateValidity certificateValidity = new CertificateValidity(date, date);
            X509CertInfo certInfo = new X509CertInfo();
            PSSParameterSpec pssParameterSpec = AlgorithmId.getDefaultAlgorithmParameterSpec(sigAlg, privateKey);
            certInfo.set("version", new CertificateVersion(2));
            certInfo.set("serialNumber", new CertificateSerialNumber((new Random()).nextInt() & 2147483647));
            AlgorithmId var11 = AlgorithmId.getWithParameterSpec(sigAlg, pssParameterSpec);
            certInfo.set("algorithmID", new CertificateAlgorithmId(var11));
            certInfo.set("subject", subject);
            certInfo.set("key", new CertificateX509Key(publicKey));
            certInfo.set("validity", certificateValidity);
            certInfo.set("issuer", issuer);
            X509CertImpl x509Cert = new X509CertImpl(certInfo);
            x509Cert.sign(privateKey, pssParameterSpec, sigAlg, (String)null);
            return x509Cert;
        } catch (IOException var12) {
            throw new CertificateEncodingException("getSelfCert: " + var12.getMessage());
        } catch (InvalidAlgorithmParameterException var13) {
            throw new SignatureException("Unsupported PSSParameterSpec: " + var13.getMessage());
        }
    }
}
