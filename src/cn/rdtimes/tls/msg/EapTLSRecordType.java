package cn.rdtimes.tls.msg;

/**
 * TLS��¼Э���е�����
 * 
 * @author BZ
 *
 * Date: 2015-09-15
 */

public enum EapTLSRecordType {
	CHANGE_CIPHER(20), 		//�ı������ʽЭ��
	ALERT(21),				//����Э��
	HAND_SHAKE(22),			//����Э��
	APPLICATION_DATA(23),	//Ӧ������Э��
	RECORD_MSG(-1);			//�Զ���ļ�¼Э����Ϣ����,������
	
	private byte value = 22;
	
	public static EapTLSRecordType valueOf(byte value) {
		switch(value) {
			case 20:
				return CHANGE_CIPHER;
			case 21:
				return ALERT;
			case 22:
				return HAND_SHAKE;
			case 23:
				return APPLICATION_DATA;
		}
		
		return RECORD_MSG;
	}
	
	private EapTLSRecordType(int value) {
		this.value = (byte)value;
	}
	
	public byte getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.name() + "(" + Byte.toString(this.value) + ")";
	}
	
}
