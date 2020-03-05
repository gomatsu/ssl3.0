package cn.rdtimes.tls.handle;

import cn.rdtimes.tls.EapTLSHandlerAdapter;
import cn.rdtimes.tls.exception.EapTLSException;
import cn.rdtimes.tls.msg.EapTLSChangeCipherMsg;
import cn.rdtimes.tls.msg.EapTLSClientFinishedMsg;
import cn.rdtimes.tls.msg.EapTLSClientHelloMsg;
import cn.rdtimes.tls.msg.EapTLSClientKeyExchangeMsg;
import cn.rdtimes.tls.msg.EapTLSHandShakeMsg;
import cn.rdtimes.tls.msg.EapTLSHandShakeType;
import cn.rdtimes.tls.msg.EapTLSRecordMsg;
import cn.rdtimes.tls.msg.EapTLSRecordType;
import cn.rdtimes.tls.msg.EapTLSServerCertificateMsg;
import cn.rdtimes.tls.msg.EapTLSServerDoneMsg;
import cn.rdtimes.tls.msg.EapTLSServerFinishedMsg;
import cn.rdtimes.tls.msg.EapTLSServerHelloMsg;
import cn.rdtimes.tls.security.EapTLSAlgorithmUtil;
import cn.rdtimes.tls.security.EapTLSHashMacUtil;
import cn.rdtimes.tls.security.EapTLSSecurityKey;
import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * ����Э�鴦����
 * 
 * @author BZ
 * Date:2015-10-23
 */
public class EapTLSHandShakeHandler extends EapTLSHandler {
	//��ǰ���ֵ����Э������
	private EapTLSHandShakeType currState = EapTLSHandShakeType.HELLO_REQUEST;
	private EapTLSSecurityKey securityKey = null;

	public EapTLSHandShakeHandler() {}
	
	public EapTLSHandShakeHandler(EapTLSHandlerAdapter adapter) {
		this.adapter = adapter;
		this.securityKey = adapter.getSecurityKey();
	}
	
	public void setCurrState(EapTLSHandShakeType currState) {
		this.currState = currState;
	}
	
	/**
	 * ��������Э����Ϣ
	 * @param content
	 * @throws EapTLSException
	 */
	public void processHandShake(byte[] content) throws EapTLSException {
		//1.�ȷ�������Э��
		EapTLSHandShakeMsg hsmsg = parseMsg(content);
		if (hsmsg.getHstype() == EapTLSHandShakeType.FINISHED) {
			processClientFinished(hsmsg);
		}
		//2.��ȡ��������Э����д���
		else {
			this.securityKey.addBytesToMsgTotalBuffer(content);
			processDetail(hsmsg);
		}
		//3.��������ʾ���ֳɹ�
		if (currState == EapTLSHandShakeType.FINISHED) {
			adapter.setHandShakeCompleted(true);
		}
	}
	
	/**
	 * ��������Э��
	 * @param content
	 * @return
	 * @throws EapTLSException
	 */
	private EapTLSHandShakeMsg parseMsg(byte[] content) throws EapTLSException {
		EapTLSHandShakeMsg hsmsg = new EapTLSHandShakeMsg();
		//1.�Ƿ�Ϊ�ͻ��˵������Ϣ
		if (currState == EapTLSHandShakeType.CHANGE_CIPER_SPEC) {
			hsmsg.setHstype(EapTLSHandShakeType.FINISHED);
			hsmsg.setContent(content);
			
			return hsmsg;
		}
		//2.����������Ϣ
		else {
			int i = 0;
			hsmsg.setHstype(EapTLSHandShakeType.valueOf(content[i++]));
			hsmsg.setLength(EapTLSUtil.convert3Integer(content, i));
			i += 3;
			if (hsmsg.getLength() > 0) {
				byte[] buff = new byte[hsmsg.getLength()];
				EapTLSUtil.copyArray(content, i, buff, 0, buff.length);
				hsmsg.setContent(buff);
				return hsmsg;
			}
			else {
				throw new EapTLSException("EapTLSHandShakeHandler.parseMsg() HandShake content is null");
			}
		}
	}
	
	/**
	 * ������������Э�����ͣ���Ҫ����Կͻ������͵Ĵ���
	 * @param hsmsg
	 */
	private void processDetail(EapTLSHandShakeMsg hsmsg) throws EapTLSException {
		EapTLSHandShakeType hst = hsmsg.getHstype();
		if (hst == EapTLSHandShakeType.CLIENT_HELLO) {
			processClientHello(hsmsg);
		}else if (hst == EapTLSHandShakeType.CERTIFICATE_VERIFY) {
			///nothing.
		}else if (hst == EapTLSHandShakeType.CLIENT_KEY_EXCHANGE) {
			processClientKeyExchang(hsmsg);
		}
	}
	
	private void processClientHello(EapTLSHandShakeMsg hsmsg)  throws EapTLSException {
		//0.��ȡ��Ϣ
		byte[] tmpbuff = hsmsg.getContent();
		//1.������Ϣ����clienthello
		EapTLSClientHelloMsg msg = new EapTLSClientHelloMsg(tmpbuff);
		this.securityKey.clientRandom = msg.getRandoms();
		
//		System.out.print(msg);
		
		//2.����serverhello��Ϣ
		EapTLSServerHelloMsg shmsg = new EapTLSServerHelloMsg();
		byte[] tmp1 = shmsg.combine();
		//2.1����˻Ựid��ֵ��
		this.securityKey.sessionId = shmsg.getSessionId();
		this.securityKey.serverRandom = shmsg.getRandoms();
		this.securityKey.compressMethod = shmsg.getCompressionMethod();
		this.securityKey.setCipherSuite(shmsg.getCipherSuite());
		//3.����server��֤����߷��ͷ�������Կ������ѡ����һ����
		EapTLSServerCertificateMsg cmsg = new EapTLSServerCertificateMsg(adapter);
		byte[] tmp2 = cmsg.combine();
		//4.��һ��serverdone��Ϣ
		EapTLSServerDoneMsg sdmsg = new EapTLSServerDoneMsg();
		byte[] tmp3 = sdmsg.combine();
		//5.�����Ϣ������
		byte[] buff = new byte[tmp1.length + tmp2.length + tmp3.length];
		int i = 0;
		EapTLSUtil.copyArray(tmp1, 0, buff, i, tmp1.length);
		i += tmp1.length;
		EapTLSUtil.copyArray(tmp2, 0, buff, i, tmp2.length);
		i += tmp2.length;
		EapTLSUtil.copyArray(tmp3, 0, buff, i, tmp3.length);
		//5.1����
		EapTLSRecordMsg rmsg = new EapTLSRecordMsg();
		rmsg.setRType(EapTLSRecordType.HAND_SHAKE);
		rmsg.setContent(buff);
		adapter.writeRecordMsg(rmsg);
		
		this.securityKey.addBytesToMsgTotalBuffer(buff);
		//6.���õ�ǰ״̬
		this.currState = EapTLSHandShakeType.SERVER_DONE;
	}
	
	private void processClientKeyExchang(EapTLSHandShakeMsg hsmsg) throws EapTLSException {
		byte[] tmpbuff = hsmsg.getContent();
		EapTLSClientKeyExchangeMsg ckemsg = new EapTLSClientKeyExchangeMsg(tmpbuff,this.adapter);
		//1.����������
		this.securityKey.masterKey = EapTLSAlgorithmUtil.generateMasterSecret(ckemsg.getPreMasterSecret(),
																this.securityKey.clientRandom,
																this.securityKey.serverRandom);
		//2.����״̬
		this.currState = EapTLSHandShakeType.CLIENT_KEY_EXCHANGE;
		
//		System.out.println("PreMasterSecret:\r\n" + EapTLSUtil.formatByteHex(ckemsg.getPreMasterSecret()));
	}
	
	private void processClientFinished(EapTLSHandShakeMsg hsmsg) throws EapTLSException {
		//0.����key
		EapTLSAlgorithmUtil.generateKeys(this.securityKey, this.securityKey.masterKey, 
										 this.securityKey.serverRandom, 
										 this.securityKey.clientRandom);
		this.adapter.generateReadCipher(this.securityKey.clientWriteKey);
		this.adapter.generateWriteCipher(this.securityKey.serverWriteKey);
		
//		System.out.println("clientRandom:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.clientRandom));
//		System.out.println("serverRandom:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.serverRandom));
//		System.out.println("masterKey:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.masterKey));
//		System.out.println("clientWriteMac:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.clientWriteMac));
//		System.out.println("serverWriteMac:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.serverWriteMac));
//		System.out.println("clientWriteKey:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.clientWriteKey));
//		System.out.println("serverWriteKey:\r\n" + EapTLSUtil.formatByteHex(this.securityKey.serverWriteKey));
		
		//1.��������client finished
		byte[] alg = this.adapter.getReadCipher().decrypt(hsmsg.getContent());
		if (alg == null) {
			throw new EapTLSException("EapTLSHandShakeHandler.processClientFinished() decryption is null");
		}
		byte[] head = new byte[4];
		EapTLSUtil.copyArray(alg, 0, head, 0, head.length);
		byte[] alg1 = new byte[alg.length - 4];
		EapTLSUtil.copyArray(alg, 4, alg1, 0, alg1.length);
		EapTLSClientFinishedMsg cfmsg = new EapTLSClientFinishedMsg(adapter);
		//2.��������
		cfmsg.parseDecryption(alg1);
		//�����֤��Ϣ
		byte[] verifyInfo = new byte[head.length + cfmsg.getContent().length];
		EapTLSUtil.copyArray(head, 0, verifyInfo, 0, head.length);
		EapTLSUtil.copyArray(cfmsg.getContent(), 0, verifyInfo, head.length, cfmsg.getContent().length);
		//��֤MAC,����ȷ˵����Ϣ����
		boolean b = EapTLSHashMacUtil.verifyClientMAC(this.adapter.getSecurityKey().cipherSpec.getMacAlgorithm(),
												      this.adapter.getSecurityKey().clientWriteMac,
												      this.adapter.getSecurityKey().seq_number_read,
												      verifyInfo.length, verifyInfo,
												      cfmsg.getClientHash(),cfmsg.getRType());
		if (!b) {
			throw new EapTLSException("EapTLSHandShakeHandler.processClientFinished() client finished msg verify mac error");
		}
		
		//3.��ѹ��
		byte[] unzip = EapTLSAlgorithmUtil.unzip(adapter.getSecurityKey(), cfmsg.getContent());
		if (unzip == null) {
			throw new EapTLSException("EapTLSHandShakeHandler.processClientFinished() unzip is null");
		}
		cfmsg.setContent(unzip);
		cfmsg.parseUnzip();
		//3.1mac��֤
		if (!cfmsg.verifyClientFinished())
			throw new EapTLSException("EapTLSHandShakeHandler.processClientFinished() client finished msg verify error");
		this.adapter.getSecurityKey().seq_number_read += 1;
		
		//4.���ͷ���˽�����Կ��Ϣ
		EapTLSChangeCipherHandler handler = this.adapter.getEapTLSChangeCipherHandler();
		EapTLSChangeCipherMsg ccmsg = handler.getEapTLSChangeCipherMsg();
		this.securityKey.seq_number_write = 0;
		//4.1������Ϣ
		EapTLSRecordMsg rmsg = new EapTLSRecordMsg();
		rmsg.setRType(EapTLSRecordType.CHANGE_CIPHER);
		rmsg.setContent(ccmsg.combine());
		byte[] tmp1 = rmsg.combine();
		
		//5.���ͷ���������Ϣ
		this.adapter.getSecurityKey().addBytesToMsgTotalBuffer(verifyInfo);
		EapTLSServerFinishedMsg sfmsg = new EapTLSServerFinishedMsg(this.adapter);
		byte[] tmp2 = sfmsg.combine();
		byte[] compress = EapTLSAlgorithmUtil.zip(adapter.getSecurityKey(), tmp2);
		int len = compress.length;
		//5.1дmac��Ϣ
		byte[] mac = EapTLSHashMacUtil.serverWriteMAC(adapter.getSecurityKey().cipherSpec.getMacAlgorithm(), 
													  adapter.getSecurityKey().serverWriteMac, 
													  adapter.getSecurityKey().seq_number_write,
													  len, compress,sfmsg.getRType());
		len += mac.length;
		tmp2 = new byte[len];
		EapTLSUtil.copyArray(compress, 0, tmp2, 0, compress.length);
		EapTLSUtil.copyArray(mac, 0, tmp2, compress.length, mac.length);
		tmp2 = this.adapter.getWriteCipher().encrypt(tmp2);
		if (tmp2 == null) {
			throw new EapTLSException("EapTLSHandShakeHandler.processClientFinished() encrypt finished is null");
		}
		//5.2��ϼ�¼Э��
		rmsg.setRType(EapTLSRecordType.HAND_SHAKE);
		rmsg.setContent(tmp2);
		tmp2 = rmsg.combine();
		
		//6.�����Ϣ�ͷ�����Ϣ
		byte[] buff = new byte[tmp1.length + tmp2.length];
		int i = 0;
		EapTLSUtil.copyArray(tmp1, 0, buff, i, tmp1.length);
		i += tmp1.length;
		EapTLSUtil.copyArray(tmp2, 0, buff, i, tmp2.length);
		//6.1����
		adapter.writeRecordMsg(buff);
		this.securityKey.seq_number_write += 1;
		
//		System.out.println("=========Finished=========");
		
		//7.����״̬������
		this.currState = EapTLSHandShakeType.FINISHED;
		this.securityKey.clearMsgTotalBuffer();
	}
	
}
