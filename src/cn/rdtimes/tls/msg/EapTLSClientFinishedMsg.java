package cn.rdtimes.tls.msg;


import cn.rdtimes.tls.EapTLSHandlerAdapter;
import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * ClientFinishedЭ�� �����Ƿ���ˣ���������ֻ����������
 * 
 * @author BZ
 * Date:2015-10-23
 */

public class EapTLSClientFinishedMsg extends EapTLSHandShakeMsg {
	private EapTLSHandlerAdapter adapter = null;
	//md5
	private byte[] md5 = null;
	//sha
	private byte[] sha = null;
	//�ͻ��˵�mac
	private byte[] clientHash = null;
	
	private static long client = 0x434C4E54;
	
	/**
	 * �����յ������ݴ���Ȼ�����
	 * @param content
	 */
	public EapTLSClientFinishedMsg(EapTLSHandlerAdapter adapter) {
		this.hstype = EapTLSHandShakeType.FINISHED;
		this.adapter = adapter;
	}
	
	/**
	 * ���ܺ�Ľ���
	 * @param content
	 */
	public void parseDecryption(byte[] content) {
		///�����ǰ�������ʽ����ģ�����ǿ鷽ʽ�Ǵ���� !!!!!
		int len = content.length - adapter.getSecurityKey().cipherSpec.getHashSize();
		this.content = new byte[len];
		EapTLSUtil.copyArray(content, 0, this.content, 0, len);
		this.length = this.content.length;
		
		this.clientHash = new byte[adapter.getSecurityKey().cipherSpec.getHashSize()];
		EapTLSUtil.copyArray(content, len, this.clientHash, 0, adapter.getSecurityKey().cipherSpec.getHashSize());
	}
	
	/**
	 * ���������еľ�������
	 * @param content
	 */
	public void parseUnzip() {
		this.md5 = new byte[16];
		this.sha = new byte[20];
		EapTLSUtil.copyArray(this.content, 0, this.md5, 0, this.md5.length);
		EapTLSUtil.copyArray(this.content, 16, this.sha, 0, this.sha.length);
	}

	public byte[] getMd5() {
		return md5;
	}

	public byte[] getSha() {
		return sha;
	}
	
	public long getClient() {
		return client;
	}
	
	public byte[] getClientHash() {
		return this.clientHash;
	}

	@Override
	public String toString() {
		return "EapTLSClientFinishedMsg: \r\n" + 
			   "Version:" + this.maxVersion + "." + this.minVersion + "\r\n" +
			   "MD5:" + EapTLSUtil.formatByteHex(this.md5) + "\r\n" +
			   "SHA:" + EapTLSUtil.formatByteHex(this.sha) + "\r\n";
	}
	
	/**
	 * ��֤�ͻ��˵������Ϣ�Ƿ���ȷ
	 * �˷�����ʱ��ʵ��
	 * @return true-�ɹ�
	 */
	public boolean verifyClientFinished() {
		byte[] md5 = EapTLSServerFinishedMsg.generateHashMD5(adapter, client);
		byte[] sha = EapTLSServerFinishedMsg.generateHashSHA(adapter, client);
		
		if (!EapTLSUtil.compareBytes(this.md5, md5)) return false;
		if (!EapTLSUtil.compareBytes(this.sha, sha)) return false;
		
		return true;
	}
	
}
