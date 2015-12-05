package com.aotobang.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.view.View;
import android.widget.AdapterView;

import com.aotobang.utils.LogUtil;


public class FragmentAnnotationManager {
	private static ContentView contentView;
	public static void startAnnotation(BaseFragment obj) {
		contentView(obj);
	}
	public static void startFindView(BaseFragment obj) {
		findView(obj);
	}

	private static void contentView(BaseFragment obj) {
		//
		Class<? extends BaseFragment> targetClazz = obj.getClass();
		contentView = targetClazz.getAnnotation(ContentView.class);
		
		if(contentView == null&&targetClazz.getSuperclass()!=null){
				findAnnoBySuper(targetClazz,obj);
			
		}else if(contentView!=null){
			Method method=null;
			Class<?> tmpClazz;
			tmpClazz = targetClazz;
			while(method == null){
				LogUtil.info(FragmentAnnotationManager.class, tmpClazz.getName());
				try {
					method = tmpClazz.getDeclaredMethod("setContentView", int.class);
					
					break;
				} catch(NoSuchMethodException e){
					if( tmpClazz.getSuperclass() != null )
						tmpClazz = tmpClazz.getSuperclass();
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
			
		}else{
			
			throw new RuntimeException("没有设置ContentView!");
		}
		
		
		/*Method method=null;
		Class<?> tmpClazz;
		tmpClazz = targetClazz;
		while(method == null){
			System.out.println(tmpClazz.getName());
			try {
				method = tmpClazz.getDeclaredMethod("setContentView", int.class);
				
				break;
			} catch(NoSuchMethodException e){
				if( tmpClazz.getSuperclass() != null )
					tmpClazz = tmpClazz.getSuperclass();
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
		}*/
	}

	private static void findAnnoBySuper(Class<? extends BaseFragment> clazz,BaseFragment obj) {
		Class<? extends BaseFragment> superclass=(Class<? extends BaseFragment>) clazz.getSuperclass();
		contentView=superclass.getAnnotation(ContentView.class);
		while(contentView==null){
			if(superclass.getSuperclass()==null)
				throw new RuntimeException("没有设置ContentView!");
			
			findAnnoBySuper(superclass,obj);
		}
		Method method=null;
		Class<?> tmpClazz;
		tmpClazz = superclass;
		while(method == null){
			LogUtil.info(FragmentAnnotationManager.class, tmpClazz.getName());
			try {
				method = tmpClazz.getDeclaredMethod("setContentView", int.class);
				
				break;
			} catch(NoSuchMethodException e){
				if( tmpClazz.getSuperclass() != null )
					tmpClazz = tmpClazz.getSuperclass();
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
	private static void findView(BaseFragment obj) {
		Class<? extends BaseFragment> targetClazz = obj.getClass();
		Field[] fields = targetClazz.getDeclaredFields();
		int count=0;
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
					count++;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException("---------------"+findView.value()+"找不到该view------------或类型转换异常");
				}
			}else {
				FindViewNoOnClick findViewNoOnClick = field.getAnnotation(FindViewNoOnClick.class);
				if(findViewNoOnClick!= null){
					field.setAccessible(true);
					try {
						field.set(obj,obj.findViewById(findViewNoOnClick.value()) );
						count++;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RuntimeException("---------------"+findViewNoOnClick.value()+"找不到该view------------或类型转换异常");
					}
				}
			}
			
		}
		if(count==0){
			findViewAnnoBySuper(obj.getClass(),obj);
			
		}else{
			count=0;
			
		}
		
		
		
	}
	private static void findViewAnnoBySuper(Class<? extends BaseFragment> clazz,BaseFragment obj) {
		Class<? extends BaseFragment> targetClazz = (Class<? extends BaseFragment>) clazz.getSuperclass();
		if(targetClazz==null)
			return;
		Field[] fields = targetClazz.getDeclaredFields();
		int count=0;
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
					count++;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException("---------------"+findView.value()+"找不到该view------------或类型转换异常");
				}
			}else {
				FindViewNoOnClick findViewNoOnClick = field.getAnnotation(FindViewNoOnClick.class);
				if(findViewNoOnClick!= null){
					field.setAccessible(true);
					try {
						field.set(obj,obj.findViewById(findViewNoOnClick.value()) );
						count++;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RuntimeException("---------------"+findViewNoOnClick.value()+"找不到该view------------或类型转换异常");
					}
				}
			}
			
		}
		if(count==0){
			findViewAnnoBySuper(targetClazz,obj);
			
		}else{
			count=0;
			
		}
	}
}
