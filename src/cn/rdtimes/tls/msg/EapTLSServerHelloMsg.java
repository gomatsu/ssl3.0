package cn.rdtimes.tls.msg;

import java.util.Date;
import java.util.Random;

import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * ServerHelloЭ�� �����Ƿ���ˣ���������ֻ����Ϣ���ɴ���
 * 
 * @author BZ
 * Date��2015-10-12
 */

public class EapTLSServerHelloMsg extends EapTLSHandShakeMsg {
	// ���汾
	private byte maxServerVersion = 0x3;
	// ��С�汾
	private byte minServerVersion = 0x0;
	// �����
	private byte[] randoms = new byte[32];
	// �Ựid,0��ʾ���ɸ�������
	private byte[] sessionId = null;
	// �����׼�
	private byte[] cipherSuite = new byte[2];
	// ѹ���㷨
	private byte compressionMethod = 0x0;

	public EapTLSServerHelloMsg() {
		this.hstype = EapTLSHandShakeType.SERVER_HELLO;
		init();
	}

	private void init() {
		// ��Կ�׼�Ĭ��ʹ��
		//SSL_RSA_WITH_RC4_128_SHA (0x0005)
		this.cipherSuite[0] = 0x00;
		this.cipherSuite[1] = 0x05;
		//�����
		generateRandom();
	}

	/**
	 * ���������Э���ʽ
	 * 
	 * @param content
	 */
	@Override
	protected void combineBody() {
		// ������Ϣ��ĳ���
		int len = 2 + 32 + 1 + (this.sessionId==null?0:this.sessionId.length) + 2 + 1 ; 
		//7���ֽڵĶ�����Ϣ
//		len += 7;
		
		this.content = new byte[len];

		int i = 0;
		// �汾��Ϣ
		this.content[i++] = this.maxServerVersion;
		this.content[i++] = this.minServerVersion;
		// �����
		EapTLSUtil.copyArray(this.randoms, 0, this.content, i,
				this.randoms.length);
		i += 32;
		// �Ựid�ɲ����ͣ����Ի���0����
		if (this.sessionId != null && this.sessionId.length > 0) {
			this.content[i++] = (byte)this.sessionId.length;
			EapTLSUtil.copyArray(this.sessionId, 0, this.content, i,
					this.sessionId.length);
			i += this.sessionId.length;
		}
		else {
			this.content[i++] = 0;
		}
		// ��Կ�׼�
		EapTLSUtil.copyArray(this.cipherSuite, 0, this.content, i,
				this.cipherSuite.length);
		i += 2;
		// ѹ���㷨
		this.content[i++] = this.compressionMethod;
		//����7���ֽ�
//		this.content[i++] = 0x00;
//		this.content[i++] = 0x05;
//		this.content[i++] = (byte)0xff;
//		this.content[i++] = 0x01;
//		this.content[i++] = 0x00;
//		this.content[i++] = 0x01;
//		this.content[i++] = 0x00;
	}

	public byte getMaxVersion() {
		return maxVersion;
	}

	public byte getMinVersion() {
		return minVersion;
	}

	public byte[] getRandoms() {
		return randoms;
	}

	public byte[] getSessionId() {
		return sessionId;
	}

	public byte[] getCipherSuite() {
		return cipherSuite;
	}

	public byte getCompressionMethod() {
		return compressionMethod;
	}

	public void setMaxVersion(byte maxVersion) {
		this.maxVersion = maxVersion;
	}

	public void setMinVersion(byte minVersion) {
		this.minVersion = minVersion;
	}

	public void setSessionId(byte[] sessionId) {
		this.sessionId = sessionId;
	}

	public void setCipherSuite(byte[] cipherSuite) {
		this.cipherSuite = cipherSuite;
	}

	public void setCompressionMethod(byte compressionMethod) {
		this.compressionMethod = compressionMethod;
	}

	@Override
	public String toString() {
		return  "EapTLSServerHelloMsg: \r\n" +
				"Version:" + this.maxServerVersion + "." + this.minServerVersion + "\r\n"
				+ "Random:" + EapTLSUtil.formatByteHex(this.randoms) + "\r\n"
				+ "SessionId:" + EapTLSUtil.formatByteHex(this.sessionId) + "\r\n"
				+ "CipherSuite:" + EapTLSUtil.formatByteHex(this.cipherSuite)
				+ "\r\n" + "CompressionMethod:" + this.compressionMethod
				+ "\r\n";
	}

	private byte[] generateRandom28() {
		Random rnd = new Random();
		byte[] nums = new byte[28];
		for (int i = 1; i < 29; i++) {
			int p = rnd.nextInt(28);
			if (nums[p] != 0)
				i--;
			else
				nums[p] = (byte)i;
		}
		return nums;
	}
	
	private void generateRandom() {
		//ת������
		long times = (new Date()).getTime() / 1000;
		EapTLSUtil.convertIntegerToByte(this.randoms, 0, times);
		//���
		EapTLSUtil.copyArray(generateRandom28(), 0, this.randoms, 4, 28);
	}
	

}
