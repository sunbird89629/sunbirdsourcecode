package com.hao.activityhelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import android.app.Activity;

public class ActivityHelper {
	
	
	public static void findViewById(Activity context){
		Field[] fields=context.getClass().getDeclaredFields();
		
		for(Field field:fields){
			ViewA viewA=field.getAnnotation(ViewA.class);
			if(viewA!=null){
				Object obj=context.findViewById(viewA.value());
				if(obj == null){
					throw new RuntimeException(String.format("Find nothing when use id %x for field %s",viewA.value(),field.getName()));
				}else{
					field.setAccessible(true);
					try {
						field.set(context, obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}