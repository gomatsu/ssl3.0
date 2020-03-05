package cn.rdtimes.tls.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * Э����Կ�������Ϣ
 * 
 * @author BZ Date:2015-09-25
 */
public final class EapTLSSecurityKey {
	/**
	 * ֧�ֵ������׽�, 0x0009,0x0005 Ĭ�ϰ�0x0005����
	 */
	public static String[] CIPHER_SUITE = { "SSL_RSA_WITH_RC4_128_SHA",
											"SSL_RSA_WITH_DES_CBC_SHA" };
	// ȷ��ʹ�õ���Կ�׼�,�������ܺͽ�������
	private byte[] cipherSuite = null;
	// �����ܣ��������ܺͽ�������
	public byte[] masterKey = null;
	// �ͻ��˼�������ʱ������
	public byte[] clientWriteKey = null;
	// ����˼�������ʱ������
	public byte[] serverWriteKey = null;
	// �ͻ���дmac��Ϣʱ������
	public byte[] clientWriteMac = null;
	// �����дmac��Ϣʱ������
	public byte[] serverWriteMac = null;
	// �ͻ��˳�ʼ��������
	public byte[] clientWriteIV = null;
	// ����˳�ʼ������
	public byte[] serverWriteIV = null;
	// �Ựid
	public byte[] sessionId = null;
	// ѹ���㷨
	public byte compressMethod = 0;
	// �ͻ������ɵ������
	public byte[] clientRandom = null;
	// ��������������
	public byte[] serverRandom = null;
	// �����к�
	public long seq_number_read = 0;
	//д���к�
	public long seq_number_write = 0;
	// ֻ��������Կ�׼�ʱ�Żᴴ����ʹ��
	public EapTLSCipherSpec30 cipherSpec = null;

	// ��Ϣ����
	private ByteArrayOutputStream msgTotalBuffer = new ByteArrayOutputStream();

	public EapTLSSecurityKey() {
	}

	public void addBytesToMsgTotalBuffer(byte[] b) {
		if (msgTotalBuffer == null)
			msgTotalBuffer = new ByteArrayOutputStream();
		try {
			msgTotalBuffer.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] msgTotalBufferToByte() {
		return msgTotalBuffer.toByteArray();
	}

	public void clearMsgTotalBuffer() {
		try {
			if (msgTotalBuffer != null) {
				msgTotalBuffer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		msgTotalBuffer = null;
	}

	public byte[] getCipherSuite() {
		return cipherSuite;
	}

	public void setCipherSuite(byte[] cipherSuite) {
		this.cipherSuite = cipherSuite;
		if (this.cipherSuite != null) {
			this.cipherSpec = new EapTLSCipherSpec30(
					(int) EapTLSUtil.convertShot(this.cipherSuite, 0));
		}
	}

}
