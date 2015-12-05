package com.aotobang.annotation;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.view.View;
import android.widget.AdapterView;



public class AnnotationManager {
	public static void startAnnotation(BaseActivity obj) {
		contentView(obj);
		findView(obj);
	}

	private static void contentView(BaseActivity obj) {
		//？
		Class<? extends Object> targetClazz = obj.getClass();
		ContentView contentView = targetClazz.getAnnotation(ContentView.class);

		if(contentView == null)
			throw new RuntimeException("没有设置ContentView!");

		Method method=null;
		Class<? extends Object > tmpClazz;
		tmpClazz = targetClazz;

		while(method == null){
			//System.out.println(tmpClazz.getName());
			try {
				method = tmpClazz.getDeclaredMethod("setContentView", int.class);

				break;
			} catch(NoSuchMethodException e){
				if( tmpClazz.getSuperclass() != null ){
					tmpClazz = tmpClazz.getSuperclass();

				}
				else{
					e.printStackTrace();
					break;
				}
			}
		}
		method.setAccessible(true);
		try {
			method.invoke(obj, contentView.value());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void findView(BaseActivity obj) {
		Class targetClazz = obj.getClass();
		Field[] fields = targetClazz.getDeclaredFields();

		for( Field field : fields ){
			View view;
			FindViewOnClick findView = field.getAnnotation(FindViewOnClick.class);
			if(findView != null){
				field.setAccessible(true);
				try {

					field.set(obj,obj.findViewById(findView.value()) );
					view = (View) field.get(obj);
					if(view instanceof AdapterView)
						continue;

					//设置点击事件
					view.setOnClickListener(obj);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException("---------------"+findView.value()+"找不到该view------------");
				}
			}else {
				FindViewNoOnClick findViewNoOnClick = field.getAnnotation(FindViewNoOnClick.class);
				if(findViewNoOnClick!= null){
					field.setAccessible(true);
					try {
						field.set(obj,obj.findViewById(findViewNoOnClick.value()) );
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RuntimeException("---------------"+findView.value()+"找不到该view------------");
					}
				}
			}

		}
	}
}
