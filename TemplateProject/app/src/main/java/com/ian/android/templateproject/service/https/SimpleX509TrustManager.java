package com.ian.android.templateproject.service.https;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/***********
 *
 * @author Ian
 * @date 2015-12-14 11:31
 * @describ 设置X509TrustManager
 *
 */
public class SimpleX509TrustManager implements X509TrustManager {
    private X509TrustManager standardTrustManager = null;

    public SimpleX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
       
    		TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init(keystore);
       
        TrustManager[] trustmanagers = factory.getTrustManagers();
      
        if(trustmanagers.length == 0) {
            throw new NoSuchAlgorithmException("no trust manager found");
        } else {
            this.standardTrustManager = (X509TrustManager)trustmanagers[0];
        }
    }

    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
