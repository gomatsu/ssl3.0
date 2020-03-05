package cn.rdtimes.tls.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.Closeable;

/**
 * �й�֤������Ĺ�����
 * 
 * @author BZ
 * Date: 2015-09-17
 */

public final class EapTLSCertUtil {
	/**
	 * ����ǩ����ͨ���㷨
	 */
	public static String SIGNATURE_ALG_SHA1RSA = "SHA1withRSA"; 
	public static String SIGNATURE_ALG_MD5RSA = "MD5withRSA"; 
	
	/**
	 * ͨ��֤���ļ����x509
	 * @param certFileName ֤���ļ�����
	 * @return
	 */
	public static X509Certificate getX509CertificateFromFile(String certFileName) {
		InputStream input = null;
		try {
			input = new FileInputStream(certFileName);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate)factory.generateCertificate(input);
			return cert;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		finally {
			closeStream(input);
		}
	}
	
	/**
	 * ��keystore�л�ȡ֤��
	 * @param keystore ����ļ�����
	 * @param keystorePwd �������
	 * @param alias ֤��ı���
	 * @return
	 */
	public static X509Certificate getX509CertificateFromKeyStore(String keystore,String keystorePwd,String alias) {
		InputStream input = null;
		try {
			input = new FileInputStream(keystore);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(input,keystorePwd.toCharArray());
			X509Certificate cert = (X509Certificate)ks.getCertificate(alias);
			return cert;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		finally {
			closeStream(input);
		}
	}
	
	/**
	 * ��֤���ļ��ж�ȡ֤�鵽�ֽ�������
	 * @param certFileName ֤���ļ�����
	 * @return
	 */
	public static byte[] getX509CertificateBytes(String certFileName) {
		InputStream input = null;
		try {
			input = new FileInputStream(certFileName);
			
			byte[] buff = new byte[input.available()];
			input.read(buff);
			
			return buff;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		finally {
			closeStream(input);
		}
	}
	
	/**
	 * ��keystore�л�ȡ˽Կ
	 * @param keystore ���ļ�����
	 * @param keystorePwd ������
	 * @param alias ֤�����
	 * @param privatekeyPwd ˽Կ����
	 * @return
	 */
	public static PrivateKey getPriveKeyFromKeyStore(String keystore,String keystorePwd,
													 String alias,String privatekeyPwd) {
		InputStream input = null;
		try {
			input = new FileInputStream(keystore);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(input,keystorePwd.toCharArray());
			
			return (PrivateKey)ks.getKey(alias, privatekeyPwd.toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		finally {
			closeStream(input);
		}
	}
	
	/**
	 * ͨ��֤����֤ǩ��
	 * @param alg ǩ���㷨("SHA1WithRSA")
	 * @param cert ֤��
	 * @param msg ��Ϣ
	 * @param signature ����֤��ǩ��
	 * @return
	 */
	public static boolean verifySignature(String alg, X509Certificate cert, byte[] msg, byte[] signature) {
		try {
			Signature sign = Signature.getInstance(alg);
			sign.initVerify(cert);
			sign.update(msg);
			return sign.verify(signature);
		}catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * ����һ������ǩ��
	 * @param alg ǩ���㷨("SHA1WithRSA")
	 * @param pk ˽Կ
	 * @param msg ��Ϣ
	 * @return
	 */
	public static byte[] getSignatureByPrivateKey(String alg, PrivateKey pk, byte[] msg) {
		try {
			Signature sign = Signature.getInstance(alg);
			sign.initSign(pk);
			sign.update(msg);
			return sign.sign();
		}catch(Exception e) {
			return null;
		}
	}
	
	public static void printX509Certificate(X509Certificate cert) {
		if (cert == null) return;
		
		System.out.println("SerialNumber:" + cert.getSerialNumber().toString(16));
		System.out.println("NotBefore:" + cert.getNotBefore().toString());
		System.out.println("NotAfter:" + cert.getNotAfter().toString());
		System.out.println("SigAlg:" + cert.getSigAlgName());
		System.out.println("Algorithm:" + cert.getPublicKey().getAlgorithm());
		System.out.println("IssuerDN:" + cert.getIssuerDN().getName());
		System.out.println("SubjectDN:" + cert.getSubjectDN().toString());
	}
	
	private static void closeStream(final Closeable stream) {
		try {
			if (stream != null) stream.close();
		} catch(Exception e) {}
	}
	
}
