package com.aotobang.app.fragment;

import android.os.Bundle;
import android.view.View;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.app.R;
@ContentView(R.layout.interact_voice)
public class VoiceInteractFragment extends BaseFragment {
private String toUserId;
	@Override
	public void onClick(View v) {
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}

	@Override
	public void initView() {
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toUserId=getArguments().getString("toUserId");
	}
}
