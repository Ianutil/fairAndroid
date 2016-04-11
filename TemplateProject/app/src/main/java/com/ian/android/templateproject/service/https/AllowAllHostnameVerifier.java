package com.ian.android.templateproject.service.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 设置证书
 *
 */
public class AllowAllHostnameVerifier implements HostnameVerifier {
   
	public AllowAllHostnameVerifier() {
    	
    }

    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}