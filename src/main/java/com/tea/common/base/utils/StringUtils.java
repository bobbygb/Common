package com.tea.common.base.utils;

public class StringUtils {
	
	
	/**
	 * 判断是否不为空：不依赖其他包
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(final String str){
		if(null == str){
			return false;
		}
		if("".equals(str.trim())){
			return false;
		}
		return true;
	}

	public static class SqlColumnNameException extends RuntimeException
	{

		public SqlColumnNameException() {
			super();
		
		}

		public SqlColumnNameException(String message) {
			super(message);
			
		}
		private static final long serialVersionUID = -7823459049602508521L;
		
	}
	
	public static boolean sqlColumnEquals(String a,String b)
	{
		if(org.apache.commons.lang3.StringUtils.equals(a, b))
		{
			return true;
		}
		if(a == null)
		{
			return false;
		}
		if(b == null)
		{
			return false;
		}
		a = sqlColumnName2JavaName(a);
		b = sqlColumnName2JavaName(b);
		return org.apache.commons.lang3.StringUtils.equals(a,b);
	}
	
	
	
	public static String sqlColumnName2JavaName(String name)
	{
		if(org.apache.commons.lang3.StringUtils.isEmpty(name))
		{
			return name;
		}
		StringBuilder sb = new StringBuilder();
		
		boolean isNextUpper = false;
		for(int i = 0; i < name.length();i++)
		{
			char c = name.charAt(i);
			if(i == 0)
			{
				sb.append(Character.toLowerCase(c));
			}else
			{
				if(isNextUpper)
				{
					if(c == '_')
					{
						throw new SqlColumnNameException(name);
					}
					sb.append(Character.toUpperCase(c));
					isNextUpper = false;
				}else
				{
					if(c == '_')
					{
						isNextUpper = true;
					}else
					{
						sb.append(Character.toLowerCase(c));
					}
				}
				
			}
		}
		
	

		return sb.toString();
	}
	
	public static String javaName2SqlColumnName(String name)
	{
		if(org.apache.commons.lang3.StringUtils.isEmpty(name))
		{
			return name;
		}
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < name.length();i++)
		{
			char c = name.charAt(i);
			if(Character.isUpperCase(c))
			{
				if(i == 0)
				{
					sb.append(Character.toLowerCase(c));
				}else
				{
					sb.append('_');
					sb.append(Character.toLowerCase(c));
				}
			}else
			{
				sb.append(c);
			}
			
		}
		
		
		return sb.toString();
	}
	
	
	public static void main(String[] args)
	{
//		Class<?> cc = Integer.class;
//		
//		System.out.println(cc.isPrimitive());
//		
//		
//		String s = javaName2SqlColumnName("I_my_name");
//		
//		System.out.println(s);
//		
//		System.out.println(sqlColumnName2JavaName(s));
//		System.out.println(sqlColumnName2JavaName(s));
//		System.out.println(sqlColumnName2JavaName(s));
		
		System.out.println(isNotEmpty(" "));
	}
	
}
