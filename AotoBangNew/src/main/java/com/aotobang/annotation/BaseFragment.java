package com.aotobang.annotation;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aotobang.app.ConstantValues;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.AotoPreferenceUtils;
import com.aotobang.utils.DialogUtils;

public abstract class BaseFragment extends Fragment implements OnClickListener,IDataCallBack{
	public SharedPreferences sp;//
	private  int contentViewId;
	private View content;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentAnnotationManager.startAnnotation(this);
		content = inflater.inflate(contentViewId, null);
		FragmentAnnotationManager.startFindView(this);
		sp=PreferenceManager.getDefaultSharedPreferences(getActivity());
		initView();
		return content;
	}
	@Override
	public void onStart() {

		super.onStart();
	}

	public  void setContentView(int id){
		contentViewId=id;
	}
	public View findViewById(int id){
		return content.findViewById(id);

		
	}
	public void sendRequest(AotoRequest request){
		AotoNetEngineProxy.getInstance(getActivity()).sendReqeust(request);
	}
	public abstract void initView();
	@Override
	public void handleDataResultOnError(int responseId, int errCode, final Object error) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				DialogUtils.closeProgressDialog();
				Toast.makeText(getActivity(), (String)error, 0).show();
			}
		});
		
		
		
		
		
	}
		
	

	
}
