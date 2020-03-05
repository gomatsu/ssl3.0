package cn.rdtimes.tls.handle;

import java.io.InputStream;
import java.io.OutputStream;

import cn.rdtimes.tls.EapTLSHandlerAdapter;
import cn.rdtimes.tls.msg.EapTLSRecordMsg;
import cn.rdtimes.tls.msg.EapTLSRecordType;
import cn.rdtimes.tls.util.EapTLSUtil;

/**
 * ��¼��Ϣ������
 * 
 * @author BZ
 * Date:2015-10-20
 */
public class EapTLSRecordHandler extends EapTLSHandler {
	
	private InputStream input;
	private OutputStream output;

	public EapTLSRecordHandler() {
	}

	public EapTLSRecordHandler(EapTLSHandlerAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * ������¼Э��
	 * 
	 * @return ��¼��������
	 */
	public EapTLSRecordMsg parseRecordMsg() {
		try {
			EapTLSRecordMsg msg = new EapTLSRecordMsg();
			//�ȶ�ȡͷ��Ϣ
			byte[] head = new byte[EapTLSRecordMsg.FIX_HEAD_LEN];
			int readLen = input.read(head, 0, EapTLSRecordMsg.FIX_HEAD_LEN);
			if (readLen < 0) {
				return null;
			}
			int i = 0;
			msg.setRType(EapTLSRecordType.valueOf(head[i++]));
			msg.setMaxVersion(head[i++]);
			msg.setMinVersion(head[i++]);
			msg.setLength(EapTLSUtil.convertShot(head, i));
			i += 2;
			if (msg.getLength() > 0) {
				byte[] content = new byte[msg.getLength()];
				//��ȡ����
				if (input.read(content,0,msg.getLength()) < 0) 
					return null;
				//��������
				msg.setContent(content);
			}
			return msg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * д��Ϣ
	 * @param msg
	 */
	public void writeRecordMsg(EapTLSRecordMsg msg) {
		if (msg == null || msg.getContent() == null) return;
		try {
			byte[] b = msg.combine();
			output.write(b);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeRecordMsg(byte[] msg) {
		if (msg == null) return;
		try {
			output.write(msg);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public InputStream getInputStream() {
		return input;
	}

	public void setInputStream(InputStream input) {
		this.input = input;
	}

	public OutputStream getOutputStream() {
		return output;
	}

	public void setOutputStream(OutputStream output) {
		this.output = output;
	}

}
