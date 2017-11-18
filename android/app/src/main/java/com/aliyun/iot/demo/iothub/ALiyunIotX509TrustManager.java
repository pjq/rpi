/**
 * aliyun.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.aliyun.iot.demo.iothub;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import me.pjq.rpicar.BaseApplication;

/**
 * 
 * @version $Id: ALiyunIotX509TrustManager.java, v 0.1 2017年3月10日 下午4:34:07 lvjianwen Exp $
 */
public class ALiyunIotX509TrustManager extends X509ExtendedTrustManager{
    
    //根证书认证
    private X509TrustManager rootTrusm;
    
     public ALiyunIotX509TrustManager() throws Exception{
         //CA根证书，可以从官网下载
         InputStream in = BaseApplication.getContext().getAssets().open("root.crt");
//         InputStream in = SimpleClient4IOT.class.getResourceAsStream("/root.crt");
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         Certificate ca = null;
         try {
             ca = cf.generateCertificate(in);
         } catch (CertificateException e) {
            throw e;
         } finally {
             in.close();
         }
         String keyStoreType = KeyStore.getDefaultType();
         KeyStore keyStore = KeyStore.getInstance(keyStoreType);
         keyStore.load(null, null);
         keyStore.setCertificateEntry("ca", ca);
         String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
         TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
         tmf.init(keyStore);
         
         rootTrusm = (X509TrustManager) tmf.getTrustManagers()[0];
    
    }

   

    @Override
    public void checkClientTrusted(X509Certificate[] arg0,
                                   String arg1) throws CertificateException {
        
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
        
      //验证服务器证书合法性
        rootTrusm.checkServerTrusted(chain, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType,
                                   Socket socket) throws CertificateException {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType,
                                   SSLEngine engine) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType,
                                   Socket socket) throws CertificateException {
        
        //验证服务器证书合法性
        rootTrusm.checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType,
                                   SSLEngine engine) throws CertificateException {
      //验证服务器证书合法性
        rootTrusm.checkServerTrusted(chain, authType);
    }

}
