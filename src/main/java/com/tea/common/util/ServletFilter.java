package com.tea.common.util;

import java.io.IOException;
import java.util.*;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpUtils;

//@WebFilter(urlPatterns = "*")
public abstract class ServletFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}


	private static class HttpServletRequestWrapperEx extends  HttpServletRequestWrapper
	{
		private ServletInputStreamEx servletInputStreamEx;
		private HashMap<String,String[]> maps;
		public HttpServletRequestWrapperEx(HttpServletRequest request) throws IOException {
			super(request);
			servletInputStreamEx = new ServletInputStreamEx(request.getInputStream());
			createParameter();
		}

		public ServletInputStream getInputStream() throws IOException {
			return servletInputStreamEx;
		}

		private synchronized void createParameter()
		{
			if(maps == null) {
				Map<String, String[]>  getmaps =  super.getParameterMap();
				Map<String, String[]>  postmaps =  null;
				try
				{
					postmaps = com.tea.common.util.HttpUtils.parseQueryString(new String(servletInputStreamEx.getData(),"8859_1"));
				}catch (Exception e)
				{
				}
				HashMap<String,String[]> selfmap = new HashMap<>();
				if(getmaps != null)
				{
					Iterator<String> getkeys =	getmaps.keySet().iterator();
					while (getkeys.hasNext())
					{
						String key = getkeys.next();
						String[] getvalues = getmaps.get(key);
						String[] postvalues = null;
						if(postmaps != null)
						{
							postvalues = postmaps.get(key);
						}
						int size = 0;
						if(getvalues != null)
						{
							size += getvalues.length;
						}
						if(postvalues != null)
						{
							size += postvalues.length;
						}
						String[] values = new String[size];
						int i = 0;
						if(getvalues != null)
						{
							for(i = 0; i < getvalues.length;i++)
							{
								values[i] = getvalues[i];
							}
						}
						if(postvalues != null)
						{
							for(String s : postvalues)
							{
								values[i++] = s;
							}
						}
						selfmap.put(key,values);
					}
				}
				if(postmaps != null)
				{
					Iterator<String> postkeys =	postmaps.keySet().iterator();
					while (postkeys.hasNext())
					{
						String key = postkeys.next();
						if(!selfmap.containsKey(key))
						{
							selfmap.put(key,postmaps.get(key));
						}
					}
				}
				maps = selfmap;
			}
		}

		@Override
		public String getParameter(String name) {
			String[] s = maps.get(name);
			if(s != null && s.length > 0)
			{
				return s[0];
			}
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return maps;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return  new Vector<>(maps.keySet()).elements();
		}

		@Override
		public String[] getParameterValues(String name) {
			return maps.get(name);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			request = new HttpServletRequestWrapperEx((HttpServletRequest) request);
		}
		chain.doFilter(request, response);
	}
	@Override
	public void destroy() {

	}

}
