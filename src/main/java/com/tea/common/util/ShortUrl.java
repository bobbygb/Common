package com.tea.common.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class ShortUrl {
	static Log log = LogFactory.getLog(ShortUrl.class);
	
	
	
	public static String createShortUrl(String url)
	{
		final String turl = "http://api.t.sina.com.cn/short_url/shorten.json";
		String str = null;
		try
		{
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
			HttpGet get = new HttpGet(turl + "?source=1116446731&url_long=" + URLEncoder.encode(url, "utf-8")  );
			RequestConfig config =RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).build();
			get.setConfig(config);
			HttpResponse resp = client.execute(get);
			str = EntityUtils.toString(resp.getEntity());
			
			final Gson gs = new Gson();
			List<?> l =  gs.fromJson(str, List.class);
			if(l.size() > 0)
			{
				Map<?, ?> map = (Map<?, ?>) l.get(0);
			    if(map.containsKey("url_short"))
			    {
			    	return map.get("url_short").toString();
			    }
			}
			
		}catch(Exception ex)
		{
			log.error(str, ex);
		}
		return null;
		
	}
	
	
	
	@Deprecated
	public static String createShortUrlold(String url)
	{
		final String burl = "http://dwz.cn/create.php";
		try
		{
			org.apache.http.client.HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(burl);
			
			ArrayList<BasicNameValuePair> list = new  ArrayList<BasicNameValuePair>();
			list.add(new BasicNameValuePair("url",url));
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			HttpResponse resp = client.execute(post);
			
			String str = EntityUtils.toString(resp.getEntity());
			
			final Gson gs = new Gson();
		    Map<?, ?> map = gs.fromJson(str, Map.class);
		    int status = ((Number) map.get("status")).intValue();
		    if(status != 0)
		    {
		    	log.debug(str);
		    }else
		    {
		    	return map.get("tinyurl").toString();
		    	
		    }
		    
		}catch(Exception ex)
		{
			log.error("", ex);
		}
		return null;
		
		
	}
	
	
	
	public static void main(String[] args)
	{
		//http://t.cn/RIp2y3t
		System.out.println(createShortUrl("https://www.zyxr.com/wapactivity/activity/activityKuayear.html?cix=qmHYDhnt&referee=ada6b598f8d31f360f142f37313b8007"));
	}

}
