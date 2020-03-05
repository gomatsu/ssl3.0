package cn.rdtimes.tls.handle;

import cn.rdtimes.tls.EapTLSHandlerAdapter;
import cn.rdtimes.tls.exception.EapTLSException;
import cn.rdtimes.tls.msg.EapTLSApplicationMsg;
import cn.rdtimes.tls.msg.EapTLSRecordMsg;
import cn.rdtimes.tls.msg.EapTLSRecordType;
import cn.rdtimes.tls.security.EapTLSAlgorithmUtil;
import cn.rdtimes.tls.security.EapTLSHashMacUtil;

/**
 * Ӧ������Э�鴦����
 * @author BZ
 * Date: 2015-10-24
 */
public class EapTLSApplicationHandler extends EapTLSHandler {

	public EapTLSApplicationHandler() {}
	
	public EapTLSApplicationHandler(EapTLSHandlerAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * �����ͻ��˷��͵�Ӧ������
	 * @param msg
	 * @return
	 * @throws EapTLSException
	 */
	public EapTLSApplicationMsg processApplication(byte[] msg) throws EapTLSException {
		byte[] alg = adapter.getReadCipher().decrypt(msg);
		if (alg == null) {
			throw new EapTLSException("EapTLSApplicationHandler.processApplication() decryption is null");
		}
		EapTLSApplicationMsg app = new EapTLSApplicationMsg(this.adapter.getSecurityKey());
		app.parseContent(alg);
		//��֤MAC,����ȷ˵���Ǵ۸ĵ���Ϣ
		boolean b = EapTLSHashMacUtil.verifyClientMAC(this.adapter.getSecurityKey().cipherSpec.getMacAlgorithm(),
												      this.adapter.getSecurityKey().clientWriteMac,
												      this.adapter.getSecurityKey().seq_number_read,
												      app.getContent().length, app.getContent(), 
												      app.getMac(),app.getRType());
		if (!b) {
			throw new EapTLSException("EapTLSApplicationHandler.processApplication() verify client application mac error");
		}
		this.adapter.getSecurityKey().seq_number_read += 1;
		byte[] unzip = EapTLSAlgorithmUtil.unzip(adapter.getSecurityKey(), app.getContent());
		if (unzip == null) {
			throw new EapTLSException("EapTLSApplicationHandler.processApplication() unzip is null");
		}
		app.setContent(unzip);
		return app;
	}
	
	/**
	 * ��װ�ͷ��ͷ���˵�Ӧ������
	 * @param data
	 * @throws EapTLSException
	 */
	public void writeAppliationMsg(byte[] data) throws EapTLSException {
		byte[] compress = EapTLSAlgorithmUtil.zip(adapter.getSecurityKey(), data);
		if (compress == null) {
			throw new EapTLSException("EapTLSApplicationHandler.writeAppliationMsg() zip is null");
		}
		EapTLSApplicationMsg app = new EapTLSApplicationMsg(this.adapter.getSecurityKey());
		app.setContent(compress);
		byte[] alg = adapter.getWriteCipher().encrypt(app.combine());
		if (alg == null) {
			throw new EapTLSException("EapTLSApplicationHandler.writeAppliationMsg() encrypt is null");
		}
		//��ɼ�¼Э�鷢��
		EapTLSRecordMsg rmsg = new EapTLSRecordMsg();
		rmsg.setRType(EapTLSRecordType.APPLICATION_DATA);
		rmsg.setContent(alg);
		//����
		adapter.writeRecordMsg(rmsg);
		this.adapter.getSecurityKey().seq_number_write += 1;
	}
	
}
