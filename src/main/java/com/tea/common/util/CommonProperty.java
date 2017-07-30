package com.tea.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonProperty {

	static Log log = LogFactory.getLog(CommonProperty.class);
	
	private Map<String, String> propertyMap = null;
	
	private static CommonProperty property = null;
	
	/**
	 * @function 获取该类实例对象
	 * @return
	 */
	public static CommonProperty getInstance(String name) {
		if (property == null) {
			property = new CommonProperty(name);
		}
		return property;
	}
	
	/**
	 * 返回Map
	 * 
	 * @return
	 * @author 
	 */
	public Map<String, String> getMap() {
		return propertyMap;
	}
	
	/**
	 * 返回String
	 * 
	 * @param key
	 * @return
	 * @author 
	 */
	public String getValue(Object key) {
		if(propertyMap==null){
			return "";
		}
		String tempStr = property.getMap().get(key.toString());
		if (tempStr == null && key instanceof Integer) {
			tempStr = property.getMap().get(
					"0x" + Integer.toHexString((Integer) key));
		}
		return tempStr == null ? "" : tempStr;
	}

	/**
	 * 构造方法
	 * 
	 * @author 
	 */
	public CommonProperty(String name) {
		
		InputStream inStream = null;
		
		String zooKeeperUrl = com.tea.common.spring.SpringConfigurerEx.zooKeeperUrl;
		String zooKeeperRootPath = com.tea.common.spring.SpringConfigurerEx.zooKeeperRootPath;
		String appName = com.tea.common.spring.SpringConfigurerEx.AppName;
		
		if(zooKeeperUrl==null){
			zooKeeperUrl=com.tea.common.spring.dubbo.SpringConfigurerEx.zooKeeperUrl;
		}
		if(zooKeeperRootPath==null){
			zooKeeperRootPath=com.tea.common.spring.dubbo.SpringConfigurerEx.zooKeeperRootPath;
		}
		if(appName==null){
			appName=com.tea.common.spring.dubbo.SpringConfigurerEx.AppName;
		}
		
		if(StringUtils.isNotEmpty(zooKeeperUrl))
		{
			ZkClient client = new ZkClient(zooKeeperUrl);
			client.setZkSerializer(new BytesPushThroughSerializer());
			String path = null;
			String commonPath=null;
			String rootPath = zooKeeperRootPath;
			if("/".equals(rootPath)){
				path = rootPath + appName + "/" + name;
				commonPath = rootPath + "Common" + "/" + name;
			}else{
				path = rootPath + "/" + appName +"/"+ name;
				commonPath = rootPath + "/" + "Common" +"/"+ name;
			}
			
			byte[] b = null;
			String changesPath = path;
			if(client.exists(path)){
				b = client.readData(path);
			}else {
				if(client.exists(commonPath)){
					changesPath = commonPath;
					b = client.readData(commonPath);
				}
			}
			if(b!=null){
				inStream = new ByteArrayInputStream(b); 
			}
			if(inStream!=null){
				Properties pro = null;
				try {
					pro = new Properties();
					
					BufferedReader bf = new BufferedReader(new    InputStreamReader(inStream));  
					pro.load(bf);
					propertyMap = new HashMap<String, String>();
					Enumeration<?> enumer = pro.keys();
					while (enumer.hasMoreElements()) {
						String keyName = (String) enumer.nextElement();
						propertyMap.put(keyName, pro.getProperty(keyName));
					}
				} catch (Exception e) {
//				new Throwable(e);
				} finally {
					if (inStream != null) {
						try {
							inStream.close();
						} catch (IOException e) {
//						new Throwable(e);
						}
					}
				}
				
				client.subscribeDataChanges(changesPath, new IZkDataListener() {
					
					@Override
					public void handleDataDeleted(String arg0) throws Exception {
						log.debug("handleDataDeleted->arg0->"+arg0);
						property = null;
						propertyMap = null;
						CommonPropertyUtil.remove(name);
					}
					
					@Override
					public void handleDataChange(String arg0, Object arg1) throws Exception {
						log.debug("handleDataChange->arg0->"+arg0 + ",arg1->"+arg1);
						property = null;
						propertyMap = null;
						CommonPropertyUtil.remove(name);
					}
				});
			}
		
			}
			
	}

	/**
	 * 是否能够继续下一步
	 * 
	 * @param retCode
	 * @return
	 * @author 
	 */
	public static boolean goNext(int retCode) {
		if (retCode == 0) {
			return true;
		} else {
			return false;
		}
	}

}

