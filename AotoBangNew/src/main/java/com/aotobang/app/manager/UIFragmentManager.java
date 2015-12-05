package com.aotobang.app.manager;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.utils.LogUtil;

/**
 * fragment管理类
 * @author hhj
 *
 */
public class UIFragmentManager {
	private Map<String,Fragment> map=new HashMap<String, Fragment>();
	//private List<Fragment> list=new ArrayList<Fragment>();
	private static UIFragmentManager instance;
	private FragmentManager manager;
	private Fragment f;
	private Fragment t;
	public static UIFragmentManager getInstance() {
		if(instance==null){
		synchronized (UIFragmentManager.class) {
			if(instance==null)
				instance = new UIFragmentManager();
		}
	}
		return instance;
	}
	
	private UIFragmentManager() {
		
		
		
	}
	public Fragment getFragment(String name){
		
		return map.get(name);
		
	}
	public  void clearMap(){
		map.clear();
		
	}
	public void changeFragment2(Class<? extends Fragment> pre,Class<? extends Fragment> target, boolean isAddStack,
			Bundle bundle,int id) {
		 LogUtil.info(UIFragmentManager.class, map.size()+"---map.size()");
		Fragment tar=map.get(target.getName());
		if(tar==null){
			try {
				tar=target.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			map.put(target.getName(), tar);
			
		}
		
		if (bundle != null) {
			tar.setArguments(bundle);
		}
		 manager = GlobalParams.homeActivity.getSupportFragmentManager();
		
		 FragmentTransaction transaction= manager.beginTransaction();
		 //进入效果。上一个页面退出效果。上一个页面进入效果。当前界面 退出效果
		 transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out,R.anim.tran_pre_in,R.anim.tran_pre_out);
		// hidenAllFragment(transaction);
			 transaction.replace(id, tar);
		

		// 返回键操作
		if (isAddStack)
			transaction.addToBackStack(null);

		transaction.commitAllowingStateLoss();
		
	

	}

	public void changeFragment(Class<? extends Fragment> pre,Class<? extends Fragment> target, boolean isAddStack,
			Bundle bundle,int id) {
		 LogUtil.info(UIFragmentManager.class, map.size()+"---map.size()");
		Fragment tar=map.get(target.getName());
		if(tar==null){
			try {
				tar=target.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			map.put(target.getName(), tar);
			
		}
		
		if (bundle != null) {
			tar.setArguments(bundle);
		}
		 manager = GlobalParams.homeActivity.getSupportFragmentManager();
		
		 FragmentTransaction transaction= manager.beginTransaction();
		 //进入效果。上一个页面退出效果。上一个页面进入效果。当前界面 退出效果
		 transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out,R.anim.tran_pre_in,R.anim.tran_pre_out);
		
		 if(!tar.isAdded()){
			LogUtil.info(UIFragmentManager.class, tar.getClass().getName()+"---没有添加过");
			 	transaction.add(id, tar);
			 	if(pre!=null&&map.get(pre.getName())!=null)
					 transaction.hide(map.get(pre.getName()));
		}
		 else{
				LogUtil.info(UIFragmentManager.class, tar.getClass().getName()+"---添加过");
			 if(pre!=null&&map.get(pre.getName())!=null)
				 transaction.hide(map.get(pre.getName()));
			 
			 transaction.show(tar);
			 LogUtil.info(UIFragmentManager.class, tar.isHidden()+"---是否是显示的");
		 }

		// 返回键操作
		if (isAddStack)
			transaction.addToBackStack(null);

		transaction.commitAllowingStateLoss();
		
	}
	/*public void changeFragment(Fragment target, boolean isAddStack,
			Bundle bundle,int id,String tag) {
		if (bundle != null) {
			target.setArguments(bundle);
		}
		 manager = GlobalParams.homeActivity.getSupportFragmentManager();
		
		 FragmentTransaction transaction= manager.beginTransaction();
		 //进入效果。上一个页面退出效果。上一个页面进入效果。当前界面 退出效果
		 transaction.setCustomAnimations(R.anim.tran_next_in, R.anim.tran_next_out,R.anim.tran_pre_in,R.anim.tran_pre_out);
		 transaction.replace(id, target,tag);

		// 返回键操作
		if (isAddStack)
			transaction.addToBackStack(null);

		transaction.commitAllowingStateLoss();
	}
	 public void switchContent(Class<? extends Fragment> from, Class<? extends Fragment> to,boolean isAddStack,int id,String tagFrom,String tagTo) {
			
	            manager = GlobalParams.homeActivity.getSupportFragmentManager();
	         
					try {
						   if(manager.findFragmentByTag(tagFrom)==null)
							   	f= from.newInstance();
						
						  if(manager.findFragmentByTag(tagTo)==null)
				            	 t= to.newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	          
	            
	            FragmentTransaction transaction = manager.beginTransaction();
	            
	            if (!t.isAdded()) {    // 先判断是否被add过
	                transaction.hide(f).add(id, t,tagTo);
	                if(isAddStack)
	                	transaction.addToBackStack(null);
	                transaction.commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
	            } else {
	                transaction.hide(f).show(t);
	                if(isAddStack)
	                	transaction.addToBackStack(null);
	                transaction.commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
	            }
	    }*/
}
