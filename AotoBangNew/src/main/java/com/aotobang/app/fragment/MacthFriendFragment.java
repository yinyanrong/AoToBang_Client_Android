package com.aotobang.app.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.HomeActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadPhotoTask;
import com.aotobang.app.entity.RandomFriendInfo;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.local.bean.Photo;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageTools;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.PathUtil;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

@ContentView(R.layout.fragment_match_friend)
public class MacthFriendFragment extends BaseFragment {
	@FindViewNoOnClick(R.id.cards_frame)
	private SwipeFlingAdapterView cards_frame;
	@FindViewOnClick(R.id.dislike)
	private ImageView dislike;
	@FindViewOnClick(R.id.detail)
	private ImageView detail;
	@FindViewOnClick(R.id.like)
	private ImageView like;
	private List<RandomFriendInfo> infos;
	private LayoutInflater mInflater;
	private CardsAdapter adapter;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.like:
			cards_frame.getTopCardListener().selectRight();
			break;
		case R.id.dislike:
			cards_frame.getTopCardListener().selectLeft();
			break;
		
		
		}
		
	}
	

	private void sendLikeRequest(boolean  like,String toUserId) {
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.MACTH_FRIEND_LIKE);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("to", toUserId);
		if(like)
		map.put("type", 1);
		else
			map.put("type", 0);
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		request.setParameters(map);
		request.setCallBack(MacthFriendFragment.this);
		sendRequest(request);
	}


	@Override
	public void initView() {
		mInflater= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Bundle bundle=getArguments();
		infos= (List<RandomFriendInfo>) bundle.getSerializable("list");
		 adapter=new CardsAdapter();
	    cards_frame.setAdapter(adapter);
		cards_frame.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
			@Override
			public void removeFirstObjectInAdapter() {
				infos.remove(0);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onLeftCardExit(Object o) {
				RandomFriendInfo info = (RandomFriendInfo) o;
				sendLikeRequest(false, info.getUuid());
			}

			@Override
			public void onRightCardExit(Object o) {
				RandomFriendInfo info = (RandomFriendInfo) o;
				sendLikeRequest(true, info.getUuid());
			}

			@Override
			public void onAdapterAboutToEmpty(int itemsInAdapter) {
				loadMoreData();


			}

			@Override
			public void onScroll(float scrollProgressPercent) {
				View view = cards_frame.getSelectedView();
				if (view != null) {
					view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
					view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
				}
			}
		});

	}

	private void loadMoreData() {
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.RANDOM_FRIEND);//随机获取在线用户
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("num", 10);
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		request.setParameters(map);
		request.setCallBack(MacthFriendFragment.this);
		sendRequest(request);



	}


	@Override
	public void onStart() {
		LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--onStart");
		super.onStart();
	}
	@Override
	public void onResume() {
		HomeActivity.slidingMenu.addIgnoredView(cards_frame);
		LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--onResume");
		super.onResume();
	}
	@Override
	public void onPause() {
		HomeActivity.slidingMenu.removeIgnoredView(cards_frame);
		LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--onPause");
		super.onPause();
	}
	@Override
	public void onStop() {
		LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--onStop");
		super.onStop();
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		
		if(hidden)
			LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--hidden");
		else
			LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--显示了");
	}
	@Override
	public void onDestroy() {
		LogUtil.info(MacthFriendFragment.class, "MacthFriendFragment--onDestroy");
		super.onDestroy();
	}
	
	/*public void onKeyDow(){
		dislike();
	}*/
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
		switch (responseId){
			case  AotoPacket.Message_Type_ACK|InterfaceIds.RANDOM_FRIEND://随机获取在线用户
				List<RandomFriendInfo> newList= (List<RandomFriendInfo>) data;
				infos.addAll(newList);
				break;
		}

	}


	private  class CardsAdapter extends BaseAdapter{


		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int i) {
			return infos.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder holder;
			if(view!=null){
				holder=(ViewHolder) view.getTag();
			}else{
				view = mInflater.inflate(R.layout.macth_cards_item, viewGroup, false);
				holder=new ViewHolder();
				holder.tv_image_description=(TextView) view.findViewById(R.id.tv_image_description);
				holder.match_info_text=(TextView) view.findViewById(R.id.match_info_text);
				holder.photo=(ImageView) view.findViewById(R.id.photo);
				view.setTag(holder);
			}
			if(infos.get(i).getPhotoInfo()!=null&&infos.get(i).getPhotoInfo().size()>0)
				new MatchPhotoTask().execute(holder.photo, infos.get(i).getPhotoInfo().get(0));

			LogUtil.info(MacthFriendFragment.class,"getView--"+i);
			holder.tv_image_description.setText(infos.get(i).getNick());
			return view;
		}
	}
	private class ViewHolder{
		private TextView tv_image_description;
		private TextView match_info_text;
		private ImageView photo;
	}


	private class MatchPhotoTask extends AsyncTask<Object,Void,Bitmap> {
		private String  remote_url;
		private ImageView child;
		@Override
		protected Bitmap doInBackground(Object... objects) {
			child= (ImageView) objects[0];
			remote_url= (String) objects[1];
			Bitmap  img=null;
			if(remote_url!=null){
				OSSData data= OssManager.getInstance().downLoadData(remote_url, ConstantValues.PHOTO_BUCKET);
				try {
					byte[]  imgData=data.get();
					img= BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				} catch (OSSException e) {
					e.printStackTrace();
				}


			}
			return img;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(bitmap!=null)
				child.setImageBitmap(bitmap);
		}
	}

}
