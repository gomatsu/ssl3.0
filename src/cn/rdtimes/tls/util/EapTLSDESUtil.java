package cn.rdtimes.tls.util;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * ���ܺͽ��ܹ���
 * 
 * @author BZ 
 * Date: 2015-10-23
 */
public final class EapTLSDESUtil {
	
	public final static String ALG_DES = "DES";			//key=8
	public final static String ALG_DESEDE = "DESede"; 	//3DES,key=24
	public final static String ALG_RC4 = "RC4";			//key=16

	public static byte[] encrypt(byte[] data, byte[] key, String alg) {
		try {
			SecretKey securekey = new SecretKeySpec(key, alg);  
			Cipher cipher = Cipher.getInstance(alg);
			cipher.init(Cipher.ENCRYPT_MODE, securekey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ���ݼ�ֵ���н���
	 * 
	 * @param data ����
	 * @param key ����
	 * @return
	 */
	public static byte[] decrypt(byte[] data, byte[] key, String alg) {
		try {
			SecretKey securekey = new SecretKeySpec(key, alg);
			Cipher cipher = Cipher.getInstance(alg);
			cipher.init(Cipher.DECRYPT_MODE, securekey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
