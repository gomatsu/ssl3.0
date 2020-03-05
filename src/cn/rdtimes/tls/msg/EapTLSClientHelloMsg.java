package cn.rdtimes.tls.msg;

import java.util.ArrayList;
import java.util.List;

import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * ClientHelloЭ�� �����Ƿ���ˣ���������ֻ����������
 * 
 * @author BZ
 * Date:2015-10-08
 */

public class EapTLSClientHelloMsg extends EapTLSHandShakeMsg {
	//���汾
	private byte maxClientVersion = 0x3;
	//��С�汾
	private byte minClientVersion = 0x0;
	//�����
	private byte[] randoms = new byte[32];
	//�Ựid
	private byte[] sessionId = null;
	//�����׼��б�
	private List<byte[]> cipherSuite = new ArrayList<byte[]>();
	//ѹ���㷨
	private List<Byte> compressionMethod = new ArrayList<Byte>();
	
	/**
	 * �����յ������ݴ���Ȼ�����
	 * @param content
	 */
	public EapTLSClientHelloMsg(byte[] content) {
		this.hstype = EapTLSHandShakeType.CLIENT_HELLO;
		parseBody(content);
	}
	
	/**
	 * ���������еľ�������
	 * @param content
	 */
	private void parseBody(byte[] content) {
		int i = 0; int len = 0;
		//0.��ȡ�汾�������
		this.maxClientVersion = content[i++];
		this.minClientVersion = content[i++];
		EapTLSUtil.copyArray(content, i, this.randoms, 0, this.randoms.length);
		i += 32;
		//1.�Ȼ�ȡ�ػ�id�ĳ��ȣ��ڻ�ȡ�ػ�����
		len = content[i++];
		if (len > 0) {
			this.sessionId = new byte[len];
			EapTLSUtil.copyArray(content, i, this.sessionId, 0, this.sessionId.length);
			i += len;
		}
		//2.��ȡ��Կ�׼��ĳ��Ⱥ�����
		byte[] cipher = new byte[2];
		EapTLSUtil.copyArray(content, i, cipher, 0, 2);
		i += 2;
		len = EapTLSUtil.convertShot(cipher, 0);
		for (int j = 0; j < len; j=j+2) {
			byte[] tb = new byte[2];
			EapTLSUtil.copyArray(content, i, tb, 0, 2);
			this.cipherSuite.add(tb);
			i += 2;
		}
		//3.��ȡѹ���㷨
		len = content[i++];
		for (int j = 0; j < len; j++) {
			this.compressionMethod.add(content[i++]);
		}
	}

	public byte getMaxClientVersion() {
		return maxClientVersion;
	}

	public byte getMinClientVersion() {
		return minClientVersion;
	}

	public byte[] getRandoms() {
		return randoms;
	}

	public byte[] getSessionId() {
		return sessionId;
	}

	public List<byte[]> getCipherSuite() {
		return cipherSuite;
	}

	public List<Byte> getCompressionMethod() {
		return compressionMethod;
	}
	
	@Override
	public String toString() {
		return "EapTLSClientHelloMsg: \r\n" + 
			   "Version:" + this.maxClientVersion + "." + this.minClientVersion + "\r\n" +
			   "Random:" + EapTLSUtil.formatByteHex(this.randoms) + "\r\n" +
			   "SessionId:" + EapTLSUtil.formatByteHex(this.sessionId) + "\r\n" +
			   "CipherSuite Length:" + this.cipherSuite.size() + "\r\n" +
			   "CipherSuite:" + formatCipherSuit() + "\r\n" +
			   "CompressionMethod Length:" + this.compressionMethod.size() + "\r\n" +
			   "CompressionMethod:" + formatCompressMethod() + "\r\n"  ;
	}
	
	private String formatCipherSuit() {
		String ret = "";
		for(int i = 0; i < this.cipherSuite.size(); i++) {
			if (i > 0) ret += " ";
			ret += EapTLSUtil.formatByteHex(this.cipherSuite.get(i));
		}
		
		return ret;
	}
	
	private String formatCompressMethod() {
		String ret = "";
		for(int i = 0; i < this.compressionMethod.size(); i++) {
			if (i > 0) ret += " ";
			ret += EapTLSUtil.formatByteHex(this.compressionMethod.get(i));
		}
		return ret;
	}

}
