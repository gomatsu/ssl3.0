package cn.rdtimes.tls.msg;

/**
 * ��Ϣ����
 * �Լ�¼��ϢЭ��Ϊ������
 * 
 * @author BZ
 * Date: 2015-09-15
 */

public abstract class EapTLSMessage {
	//��Ϣ���ͣ�����ָ���Ǽ�¼Э���е�����
	protected EapTLSRecordType rtype = EapTLSRecordType.HAND_SHAKE;
	//��Ҫ�汾 SSL3.0,TLS1.0=SSL3.1,TLS1.1=SSL3.2,TLS1.2=SSL3.3
	protected byte maxVersion = 0x03;
	//��Ҫ�汾
	protected byte minVersion = 0x00;
	//��Ϣ����,2���ֽڳ�
	protected int length = 0;

	public EapTLSRecordType getRType() {
		return rtype;
	}
	
	public void setRType(EapTLSRecordType rtype) {
		this.rtype = rtype;
	}

	public byte getMaxVersion() {
		return maxVersion;
	}
	
	public void setMaxVersion(byte maxVersion) {
		this.maxVersion = maxVersion;
	}

	public void setMinVersion(byte minVersion) {
		this.minVersion = minVersion;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getMinVersion() {
		return minVersion;
	}

	public int getLength() {
		return length;
	}
	
	/**
	 * �����Ϣ����
	 */
	public abstract byte[] combine();
		
}
