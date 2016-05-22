package model;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;


public class Message implements Serializable {
 
	//~ --- [INSTANCE FIELDS] ------------------------------------------------------------------------------------------
	//https://github.com/codvio/diffie-hellman-helloworld
    private PrivateKey privateKey;
    private PublicKey  publicKey;
    private PublicKey  receivedPublicKey;
    private byte[]     secretKey;
    private String     secretMessage;



    //~ --- [METHODS] --------------------------------------------------------------------------------------------------

    public void encryptAndSendMessage(final String message, ObjectOutputStream out /*final Message msg*/) {

        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            final Cipher        cipher  = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            final byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            out.writeObject(encryptedMessage);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //~ ----------------------------------------------------------------------------------------------------------------

    public void generateCommonSecretKey() {

        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, true);

            secretKey = shortenSecretKey(keyAgreement.generateSecret());
            System.out.println("Llave secretKey: "+ secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //~ ----------------------------------------------------------------------------------------------------------------

    public void generateKeys() {

        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);

            final KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey  = keyPair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //~ ----------------------------------------------------------------------------------------------------------------

    public PublicKey getPublicKey() {

        return publicKey;
    }

    //~ ----------------------------------------------------------------------------------------------------------------

    public void receiveAndDecryptMessage(final byte[] message) {

        try {

            // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            final Cipher        cipher  = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            secretMessage = new String(cipher.doFinal(message));
            System.out.println("Mensaje Desencriptado: "+secretMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * In a real life example you must serialize the public key for transferring.
     *
     * @param  msg
     */
    public void receivePublicKeyFrom(final Message msg) {

        receivedPublicKey = msg.getPublicKey();
    }
    //~ ----------------------------------------------------------------------------------------------------------------

    public void whisperTheSecretMessage() {

        System.out.println(secretMessage);
    }
    public String getMessage(){
    	return secretMessage;
    }
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * 1024 bit symmetric key size is so big for DES so we must shorten the key size. You can get first 8 longKey of the
     * byte array or can use a key factory
     *
     * @param   longKey
     *
     * @return
     */
    private byte[] shortenSecretKey(final byte[] longKey) {

        try {
        	byte[] newKey = new byte[16];
        	for(int i=0;i<15;i++){ 
    			newKey[i] = longKey[i];		
    		}
    		return newKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
   
}
