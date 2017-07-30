package com.tea.common.common;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
/**
 * @category http请求连接池，用于高效的请求http
 *
 */
public interface IHttpClientPools {

	/**
	 * @category 关闭无用的连接，不要调用这个方法，系统会自动的调用
	 */
	void  CloseIdleConnection();
	
	/**
	 *@category 执行http请求
	 * @param paramHttpUriRequest
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse execute(HttpUriRequest paramHttpUriRequest) throws ClientProtocolException, IOException;
	
	/**
	 * 
	 * @param url   请求url
	 * @param params  请求参数
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
    public String executePost(String url,String params) throws ClientProtocolException, IOException;
}
