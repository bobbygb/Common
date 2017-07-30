package com.tea.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date date = addYear(5);
		System.out.println(date);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(addYear(5)));
	}
	
	
	/**
	 * 当前时间延后amount后时间
	 * @param amount
	 * @return
	 */
	public static Date addYear(int amount) {
		return addYear(null, amount);
	}
	
	
	
	/**
	 * 给定时间date延后amount后时间
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date addYear(Date date, int amount) {
		Calendar calendar = Calendar.getInstance();
		if(null != date){
			calendar.setTime(date);
		}
		calendar.add(Calendar.YEAR, amount);
		return calendar.getTime();
	}
	
	
	

}
