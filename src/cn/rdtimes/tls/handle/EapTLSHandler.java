package cn.rdtimes.tls.handle;

import cn.rdtimes.tls.EapTLSHandlerAdapter;

/**
 * handler����������
 * ���������ͷ��͸�������Ϣ
 * 
 * @author BZ
 * Date:2015-10-20
 */
public abstract class EapTLSHandler {
	
	protected EapTLSHandlerAdapter adapter = null;

	public EapTLSHandlerAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(EapTLSHandlerAdapter adapter) {
		this.adapter = adapter;
	}
	
	
}
