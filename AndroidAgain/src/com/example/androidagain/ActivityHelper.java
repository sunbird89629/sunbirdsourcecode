package com.example.androidagain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import android.app.Activity;

public class ActivityHelper {
	
	public static void findViewById(Activity context) throws Exception{
		Field[] fields=context.getClass().getDeclaredFields();
		
		for(Field field:fields){
			ViewA viewA=field.getAnnotation(ViewA.class);
			if(viewA!=null){
				Object obj=context.findViewById(viewA.value());
				field.setAccessible(true);
				field.set(context, obj);
			}else{
				
			}
		}
	}
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface ViewA {
	public int value();
}
