package cn.rdtimes.tls.msg;

import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * �޸���Կ��Ϣ,�������ֻ��һ���ֽڣ�����ֵ�ǡ�1��
 * 
 * ������Ϣ���ݽ����Ǽ��ܺ�ѹ����
 * 
 * @author BZ
 *
 * Date: 2015-09-24
 */

public class EapTLSChangeCipherMsg extends EapTLSMessage {
	//���ݽ���ֵ��EapTLSRecordMsg.contentͳһ����
	private byte[] content = null;
	
	public EapTLSChangeCipherMsg() {
		this.rtype = EapTLSRecordType.CHANGE_CIPHER;
	}

	@Override
	public byte[] combine() {
		return content;
	}
	
	@Override
	public String toString() {
		return "EapTLSChangeCipherMsg: \r\n" + EapTLSUtil.formatByteHex(content) + "\r\n";
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
		if (this.content != null) this.length = this.content.length;
	}

}
