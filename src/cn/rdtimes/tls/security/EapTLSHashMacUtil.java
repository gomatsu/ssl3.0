package cn.rdtimes.tls.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.rdtimes.tls.msg.EapTLSRecordType;
import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * �㷨��MAC�ȹ�����.
 * 
 * @author BZ
 * Date:2015-10-14
 */
public final class EapTLSHashMacUtil {
	
	public static String HASH_MD5 = "MD5";
	public static String HASH_SHA1 = "SHA-1";
	
	/**
	 * ����hash�㷨����
	 * @param hashType
	 * @param input
	 * @return
	 */
	public static byte[] hashMac(String hashType, byte[] input) {
		// ���ժҪ�㷨�� MessageDigest ����
        MessageDigest mdInst;
		try {
			mdInst = MessageDigest.getInstance(hashType);
			// ʹ��ָ�����ֽڸ���ժҪ
	        mdInst.update(input);
	        // �������
	        return mdInst.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * ����һ������˵�MAC
	 * @param writeMAC �����дMAC key
	 * @param seqnum ���к�
	 * @param length ��Ϣ�ܳ���
	 * @param data ԭʼ����
	 * @return 
	 */
	public static byte[] serverWriteMAC(EapTLSMACAlgorithm hash, byte[] writeMAC,
										long seqnum,int length,byte[] data,EapTLSRecordType rt) {
		return generateClientServerMac(hash,writeMAC,seqnum,length,data,rt);
	}
	
	/**
	 * ��֤�ͻ��˷��͵�MAC
	 * �˷�����ʱ��ʵ��
	 * @param writeMAC �ͻ���дmac key
	 * @param seqnum ���к�
	 * @param length ��Ϣ�ܳ���
	 * @param data ԭʼ����
	 * @param srcMAC ����֤��MAC
	 * @return true-�ɹ���false-ʧ��
	 */
	public static boolean verifyClientMAC(EapTLSMACAlgorithm hash, byte[] writeMAC,
										  long seqnum, int length, byte[] data, 
										  byte[] srcMAC,EapTLSRecordType rt) {
		byte[] tmp1 = generateClientServerMac(hash,writeMAC,seqnum,length,data,rt);
		
//		System.out.println(EapTLSUtil.formatByteHex(tmp1));
//		return true;
		
		return EapTLSUtil.compareBytes(tmp1, srcMAC);
	}
	///���ڿͻ��˺ͷ������˼�¼������mac����
	private static byte[] generateClientServerMac(EapTLSMACAlgorithm hash, byte[] writeMAC,
			  									  long seqnum, int length, byte[] data,
			  									  EapTLSRecordType rt) {
		byte[] pad1 = EapTLSUtil.generatePad1(hash==EapTLSMACAlgorithm.MD5?48:40);
		byte[] pad2 = EapTLSUtil.generatePad2(hash==EapTLSMACAlgorithm.MD5?48:40);
		int len = writeMAC.length + pad1.length + 8 + 1 + 2 + data.length;
		byte[] tmp = new byte[len];
		int i = 0;
		EapTLSUtil.copyArray(writeMAC, 0, tmp, i, writeMAC.length);
		i += writeMAC.length;
		EapTLSUtil.copyArray(pad1, 0, tmp, i, pad1.length);
		i += pad1.length;
		EapTLSUtil.convertLongToByte(tmp, i, seqnum);
		i += 8;
		tmp[i++] = rt.getValue();
		EapTLSUtil.convertShortToByte(tmp, i, length);
		i += 2;
		EapTLSUtil.copyArray(data, 0, tmp, i, data.length);
		String strhash = (hash==EapTLSMACAlgorithm.MD5?EapTLSHashMacUtil.HASH_MD5:EapTLSHashMacUtil.HASH_SHA1);
		//��һ��
		byte[] sh1 = EapTLSHashMacUtil.hashMac(strhash, tmp);
		
		len = writeMAC.length + pad2.length + sh1.length;
		tmp = new byte[len];
		i = 0;
		EapTLSUtil.copyArray(writeMAC, 0, tmp, i, writeMAC.length);
		i += writeMAC.length;
		EapTLSUtil.copyArray(pad2, 0, tmp, i, pad2.length);
		i += pad2.length;
		EapTLSUtil.copyArray(sh1, 0, tmp, i, sh1.length);
		
		return EapTLSHashMacUtil.hashMac(strhash, tmp);
	}
	
}