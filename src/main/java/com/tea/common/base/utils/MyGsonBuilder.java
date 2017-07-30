package com.tea.common.base.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.tea.common.base.utils.ReflectUtil;


public class MyGsonBuilder {
	
	
	public static Gson createNewGson(GsonBuilder gb)
	{
		Gson gs = gb.create();
		
		
		try {
			@SuppressWarnings("unchecked")
			List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) ReflectUtil.getField(gs, "factories");
			
			List<TypeAdapterFactory> al = new ArrayList<TypeAdapterFactory>(factories);
			
			
			al.remove(ObjectTypeAdapter.FACTORY);
			al.add(1,MyObjectTypeAdapter.FACTORY);
			
			al = Collections.unmodifiableList(al);
			
			ReflectUtil.setField(gs, "factories", al);
			
//			System.out.println(al);
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
		
		return gs;
		
		
	}

}
