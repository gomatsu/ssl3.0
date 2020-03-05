package cn.rdtimes.tls.security;


/**
 * ��Կ˵��(SSL3.0)
 * 
 * ֻ֧��0x0005��0x0009������Э�飬0x0005��ΪĬ��Э�顣
 * 
 * @author BZ
 * Date:2015-09-29
 */
public final class EapTLSCipherSpec30 {
	//���������Ĭ��Ϊ0x0005�׼�
	//�㷨�Ƿ���exported
	private boolean isExportable = false;
	//��������
	private EapTLSCipherType cipherType = EapTLSCipherType.STREAM;
	//�����㷨
	private EapTLSBulkCipherAlgorithm cipherAlgorithm = EapTLSBulkCipherAlgorithm.RC4;
	//��Ҫ�㷨
	private EapTLSMACAlgorithm macAlgorithm = EapTLSMACAlgorithm.SHA;
	//��Ҫ�㷨���������
	private int hashSize = 20;
	//��������ĳ���
	private int keyMaterial = 16;
	//��ʼ�������ĳ���
	private int ivSize = 0;
	
	
	public EapTLSCipherSpec30() {
		parseSuite(0);
	}
	
	public EapTLSCipherSpec30(int cipherSuite) {
		parseSuite(cipherSuite);
	}
	
	private void parseSuite(int cipherSuite) {
		if (cipherSuite <= 0) return;
		if (cipherSuite == 0x0005) {
			this.cipherType = EapTLSCipherType.STREAM;
			this.cipherAlgorithm = EapTLSBulkCipherAlgorithm.RC4;
			this.macAlgorithm = EapTLSMACAlgorithm.SHA;
			this.keyMaterial = 16;
			this.ivSize = 0;
		}else if (cipherSuite == 0x0009) {
			this.cipherType = EapTLSCipherType.BLOCK;
			this.cipherAlgorithm = EapTLSBulkCipherAlgorithm.DES;
			this.macAlgorithm = EapTLSMACAlgorithm.SHA;
			this.keyMaterial = 8;
			this.ivSize = 8;
		}
	}

	public boolean isExportable() {
		return isExportable;
	}

	public EapTLSCipherType getCipherType() {
		return cipherType;
	}

	public EapTLSBulkCipherAlgorithm getCipherAlgorithm() {
		return cipherAlgorithm;
	}

	public EapTLSMACAlgorithm getMacAlgorithm() {
		return macAlgorithm;
	}

	public int getHashSize() {
		return hashSize;
	}

	public int getKeyMaterial() {
		return keyMaterial;
	}

	public int getIvSize() {
		return ivSize;
	}
	
	@Override
	public String toString() {
		return "EapTLSCipherSpec30: \r\n" + 
			   "IsExportable:" + this.isExportable + "\r\n" +
			   "CipherType:" + this.cipherType.toString() + "\r\n" + 
			   "CipherAlgorithm:" + this.cipherAlgorithm.toString() + "\r\n" +
			   "MacAlgorithm:" + this.macAlgorithm.toString() + "\r\n" +
			   "HashSize:" + this.hashSize + "\r\n" +
			   "KeyMaterial:" + this.keyMaterial + "\r\n" +
			   "IVSize:" + this.ivSize + "\r\n" ;
	}
	
}
