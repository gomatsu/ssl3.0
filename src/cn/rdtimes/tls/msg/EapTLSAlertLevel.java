package cn.rdtimes.tls.msg;

/**
 * ������Ϣ����
 * 
 * @author BZ
 *
 */
public enum EapTLSAlertLevel {
	UNKNOWN,
	WARNING,
	FATA;
	
	public static EapTLSAlertLevel valueOf(byte i) {
		if (i == 1) return WARNING;
		else if (i == 2) return FATA;
		else return UNKNOWN;
	}
	
}
