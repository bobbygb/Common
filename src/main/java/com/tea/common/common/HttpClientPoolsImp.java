package com.tea.common.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.tea.common.base.utils.StringUtils;
import com.tea.common.xnex.CloseEx;
/**
 * @category http请求连接池实现
 *
 */
@Repository
public class HttpClientPoolsImp implements IHttpClientPools {

	private static PoolingHttpClientConnectionManager phcm = null;
	static Log log = LogFactory.getLog(HttpClientPoolsImp.class);
	/**
	 * @category 用于保存连接池
	 */
	static HashMap<String, ConnectionRequest> maps = new HashMap<String, ConnectionRequest>();
	static int excucount = 0;

	@Autowired
	CloseEx closeEx;
	
	/**
	 * @category 自动启动
	 */
	@PostConstruct
	void Init() {
		closeEx.addClose(this);
		excucount = 0;
		maps.clear();
//		log.debug("init  HttpClientPools");
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//		log.debug("PoolingHttpClientConnectionManager MaxTotal :"
//				+ cm.getMaxTotal());
		cm.setMaxTotal(Integer.MAX_VALUE);
		cm.setDefaultMaxPerRoute(Integer.MAX_VALUE);
		phcm = cm;
	}
	
	void close2() {
		maps.clear();
		log.debug("Destory  HttpClientPools");
		if (phcm != null) {
			phcm.shutdown();
		}
	}
	
	private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		if (phcm == null) {
			log.debug("Not Start in Spring?");
			phcm = new PoolingHttpClientConnectionManager();
			phcm.setMaxTotal(1024);
			phcm.setDefaultMaxPerRoute(1024);
		}
		return phcm;
	}
	
	/**
	 * @category 关闭无用的连接，不要调用这个方法，系统会自动的调用
	 */
	@Scheduled(cron = "0/5 * *  * * ? ")
	// 每5秒执行一次
	@Override
	public void CloseIdleConnection() {
		try {
			//log.debug("CloseIdleConnection .....");
			PoolingHttpClientConnectionManager cm = getPoolingHttpClientConnectionManager();
			cm.closeExpiredConnections();
			cm.closeIdleConnections(15, TimeUnit.SECONDS);
		} catch (Exception ex) {
			log.error("CloseIdleConnection", ex);
		}
	}

	/**
	 *@category 执行http请求
	 * @param paramHttpUriRequest
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@Override
	public HttpResponse execute(HttpUriRequest paramHttpUriRequest)
			throws ClientProtocolException, IOException {
		 CloseableHttpClient http = null;
		 paramHttpUriRequest.setHeader("Connection", "close");
		 
		 PoolingHttpClientConnectionManager cm = getPoolingHttpClientConnectionManager();
		
		 http = HttpClients.custom().setConnectionManager(cm).build();
		// HttpProtocolParams.setUseExpectContinue(http.getParams(),false);
		 return http.execute(paramHttpUriRequest);
	}

    @Override
    public String executePost(String url, String params) throws ClientProtocolException, IOException {
        if(!StringUtils.isNotEmpty(url)){
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient http = null;
        String contentEncoding="UTF-8";
        String contentType="application/json";
        int requstTimeOut=2000;//请求超时时间
        int socketTimeOut=2000;//传输超时时间
        if(null != params){
            StringEntity stringEntity = new StringEntity(params, contentEncoding);
            stringEntity.setContentEncoding(contentEncoding);
            stringEntity.setContentType(contentType);
            httpPost.setEntity(stringEntity);
        }
        
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(requstTimeOut).build();// 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        
        PoolingHttpClientConnectionManager cm = getPoolingHttpClientConnectionManager();
        
        http = HttpClients.custom().setConnectionManager(cm).build();

        HttpResponse response = http.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String out = EntityUtils.toString(entity, contentEncoding);
                return out;
            }
        }
        return null;
    }
	

}
