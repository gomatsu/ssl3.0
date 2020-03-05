package cn.rdtimes.tls.handle;

import cn.rdtimes.tls.EapTLSHandlerAdapter;
import cn.rdtimes.tls.exception.EapTLSException;
import cn.rdtimes.tls.msg.EapTLSChangeCipherMsg;
import cn.rdtimes.tls.msg.EapTLSHandShakeType;
import cn.rdtimes.tls.msg.EapTLSRecordMsg;
import cn.rdtimes.tls.msg.EapTLSRecordType;

/**
 * ��Կ������Ϣ������
 * @author BZ
 * Date: 2015-10-13
 */
public class EapTLSChangeCipherHandler extends EapTLSHandler {

	public EapTLSChangeCipherHandler() {}
	
	public EapTLSChangeCipherHandler(EapTLSHandlerAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * �����ͻ�����Ϣ
	 * @param msg
	 * @return
	 * @throws EapTLSException
	 */
	public EapTLSChangeCipherMsg processChangeCipher(byte[] msg) throws EapTLSException {
		//1.����������Ϣ��ͨ��������1
		EapTLSChangeCipherMsg cc = new EapTLSChangeCipherMsg();
		cc.setContent(msg);
		//2.�ı�����Э���е�״̬
		adapter.getEapTLSHandShakeHandler().setCurrState(EapTLSHandShakeType.CHANGE_CIPER_SPEC);
		adapter.getSecurityKey().seq_number_read = 0;
		
		return cc;
	}
	
	/**
	 * ���ͷ������Ϣ
	 * @throws EapTLSException
	 */
	public void writeChangeCipherMsg() throws EapTLSException {
		EapTLSChangeCipherMsg cc = getEapTLSChangeCipherMsg();
		EapTLSRecordMsg rmsg = new EapTLSRecordMsg();
		rmsg.setRType(EapTLSRecordType.CHANGE_CIPHER);
		rmsg.setContent(cc.combine());
		
		adapter.writeRecordMsg(rmsg);
		adapter.getSecurityKey().seq_number_write = 0;
	}
	
	/**
	 * ���һ��������Ϣ
	 * @return
	 */
	public EapTLSChangeCipherMsg getEapTLSChangeCipherMsg() {
		EapTLSChangeCipherMsg cc = new EapTLSChangeCipherMsg();
		byte[] b = new byte[1];
		b[0] = 0x01;
		cc.setContent(b);
		return cc;
	}
	
}
