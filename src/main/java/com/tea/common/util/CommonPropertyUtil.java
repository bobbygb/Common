package com.tea.common.util;

import java.util.HashMap;

import com.tea.common.base.constant.Constants;
import com.tea.common.spring.SpringConfigurerEx;

public class CommonPropertyUtil {

	private static HashMap<String,CommonProperty> propertyMap = new HashMap<>();
	
	public static String getValue(String name,String key){
		if(name.endsWith("/")){
			name = name.substring(0, name.length()-1);
		}
		CommonProperty cp =  propertyMap.get(name);
		if(cp==null){
			cp = CommonProperty.getInstance(name);
			propertyMap.put(name, cp);
		}
		return cp.getValue(key);
	}
	
	public static void remove(String name){
		propertyMap.remove(name);
	}
	
	
	public static void main(String[] args) {
		SpringConfigurerEx.zooKeeperUrl ="192.168.0.204:2181";
		SpringConfigurerEx.zooKeeperRootPath ="/AppConfig/ZYXR";
		SpringConfigurerEx.AppName ="Test";
		
		
//		ZkClient client = new ZkClient("192.168.0.204:2181");
//		client.setZkSerializer(new BytesPushThroughSerializer());
//		byte[] b = client.readData("/AppConfig/ZYXR/Common/MongoDb/connectTimeout");
//		String str = new String(b);
//		System.out.println(str);
		try {
			String name = Constants.YunWei+"/DataSource/DB_MAIN";
			String key = "driverClassName";
			System.out.println(CommonPropertyUtil.getValue(name, key));
			
			key = "url";
			System.out.println(CommonPropertyUtil.getValue(name, key));
			
			
			name = Constants.YunWei+"/Config";
			key = "RedisUrl";
			System.out.println(CommonPropertyUtil.getValue(name, key));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
