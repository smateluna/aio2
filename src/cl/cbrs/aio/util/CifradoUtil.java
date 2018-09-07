package cl.cbrs.aio.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import cl.cbrs.aio.certificado.GeneraCertificado;

public class CifradoUtil {
	
	private static Logger logger = Logger.getLogger(CifradoUtil.class);

	String key = "0c572a085dc739b1";

	public CifradoUtil(){}


	public String cifrado(String mensaje){
		String salida = null;

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

		// build the initialization vector.  This example is all zeros, but it 
		// could be any value or generated using a random number generator.
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

			// encrypt the message
			byte[] encrypted = cipher.doFinal(mensaje.getBytes());


			char[] arr = Hex.encodeHex(encrypted);

			salida = new String(arr);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}



		return salida;
	}


	public String descifrado(String cifrado){
		String salida = null;

		try {
			char[] arr = cifrado.toCharArray();

			byte[] data = Hex.decodeHex(arr);

			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");

			// build the initialization vector.  This example is all zeros, but it 
			// could be any value or generated using a random number generator.
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			// initialize the cipher for encrypt mode
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


			// reinitialize the cipher for decryption
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

			// decrypt the message
			byte[] decrypted = cipher.doFinal(data);

			salida= new String(decrypted);


		} catch (RuntimeException e1) {
			logger.error(e1.getMessage(),e1);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(),e);
		} catch (NoSuchPaddingException e) {
			logger.error(e.getMessage(),e);
		} catch (InvalidKeyException e) {
			logger.error(e.getMessage(),e);
		} catch (InvalidAlgorithmParameterException e) {
			logger.error(e.getMessage(),e);
		} catch (IllegalBlockSizeException e) {
			logger.error(e.getMessage(),e);
		} catch (BadPaddingException e) {
			logger.error(e.getMessage(),e);
		}



		return salida;
	}


}
