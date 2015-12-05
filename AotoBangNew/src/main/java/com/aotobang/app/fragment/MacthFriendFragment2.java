package com.aotobang.app.fragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.fragment_match_friend2)
public class MacthFriendFragment2 extends BaseFragment {
	@FindViewOnClick(R.id.viewpager)
	private ViewPager mViewPager;
	@FindViewNoOnClick(R.id.tv_image_description)
	private TextView tvImageDescription;
	@FindViewNoOnClick(R.id.ll_point_group)
	private LinearLayout llPointGroup;
	@FindViewOnClick(R.id.dislike)
	private ImageView dislike;
	@FindViewOnClick(R.id.detail)
	private ImageView detail;
	@FindViewOnClick(R.id.like)
	private ImageView like;
	@FindViewOnClick(R.id.like_seal)
	private ImageView like_seal;
	@FindViewNoOnClick(R.id.match_info_text)
	private TextView match_info_text;
	private String[] imageIDs;
	private List<ImageView> imageViewList;
	private String[] imageDescriptions;
	private int previousPosition = 0; // 点的前一个下标
	private Animation sealAnim;
	private boolean animStart=false;
	private String toUserId;
	private String nick;
	private int likeType;
	@Override
	public void onClick(View v) {
		if(animStart)
			return;
		switch(v.getId()){
		case R.id.like:
			like_seal.setVisibility(View.VISIBLE);
			like_seal.startAnimation(sealAnim);
			animStart=true;
			break;
		case R.id.dislike:
			dislike();
			break;
		
		
		}
		
	}
	

	private void dislike() {
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.MACTH_FRIEND_LIKE);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("to", toUserId);
		map.put("type", 0);
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		request.setParameters(map);
		request.setCallBack(MacthFriendFragment2.this);
		sendRequest(request);
	}


	@Override
	public void initView() {
		Bundle bundle=getArguments();
		toUserId=bundle.getString("userId");
		likeType=bundle.getInt("likeType");
		nick=bundle.getString("nick");
		match_info_text.setText(nick==null?"空的":nick);
		initViewPager();
		sealAnim=AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_enter);
		sealAnim.setFillAfter(true);
		sealAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(toUserId!=null){
				AotoRequest request=AotoRequest.createRequest(InterfaceIds.MACTH_FRIEND_LIKE);
				Map<String,Object> map=new HashMap<String, Object>();
				map.put("to", toUserId);
				map.put("type", 1);
				map.put("sessionId", AotoBangApp.getInstance().getSessionId());
				request.setParameters(map);
				request.setCallBack(MacthFriendFragment2.this);
				sendRequest(request);
				}else{
					Toast.makeText(getActivity(), "toUserId null", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
	}


	
	private void initViewPager() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onCreate");
		mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		//GlobalParams.SLIDINGMENU.addIgnoredView(mViewPager);
		imageIDs = getImageIDs();
		imageDescriptions = getImageDescription();
		imageViewList = new ArrayList<ImageView>();
		ImageView iv;
		View v;
		LayoutParams imageparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams params = new LayoutParams(35, 35);
		params.leftMargin = 10;
		for (int i = 0; i < imageIDs.length; i++) {
			iv = new ImageView(getActivity());
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setLayoutParams(imageparams);
			iv.setImageResource(R.drawable.img_test);
			imageViewList.add(iv);
			// 每循环一次添加一个点
			v = new View(getActivity());
			v.setBackgroundResource(R.drawable.point_background);
			v.setLayoutParams(params);
			v.setEnabled(false);
			llPointGroup.addView(v);
		}

		mViewPager.setAdapter(new MyPageAdapter());

	/*	int item = Integer.MAX_VALUE / 2;

		// 取一下余数, 把position去掉余数
		item = item - (item % imageViewList.size());*/

		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		llPointGroup.getChildAt(0).setEnabled(true);

		// 设置图片描述
		tvImageDescription.setText(imageDescriptions[0]);

		previousPosition = 0;
	}
	public String[] getImageIDs() {
		return new String[] {"", ""};
	}

	public String[] getImageDescription() {
		return new String[] { "a ", "b", "c","d"};
	}
	class MyPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViewList.size();
		}

		/**
		 * 当滑动中的view对象和进来的对象是同一个时, 返回true 复用当前view对象
		 */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		/**
		 * 销毁一个Item position 对象的是将要销毁的下标
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			mViewPager.removeView(imageViewList.get(position));
		}

		/**
		 * 预加载一个item
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 需要把imageView加到ViewPager中, 把imageView对象返回给上一层
			ImageView view =imageViewList.get(position);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(animStart)
						return;
					
					
				}
			});
			mViewPager.addView(view);
			return imageViewList.get(position );
			
		}
	}
	public class MyOnPageChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
		//	int realPosition = position % imageViewList.size();

			// 切换文本显示的内容
			tvImageDescription.setText(imageDescriptions[position]);

			// 切换点的状态
			llPointGroup.getChildAt(position).setEnabled(true);
			// 把前一个的点置为false
			llPointGroup.getChildAt(previousPosition).setEnabled(false);

			previousPosition = position;
		}
	}
	@Override
	public void onStart() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onStart");
		super.onStart();
	}
	@Override
	public void onResume() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onResume");
		super.onResume();
	}
	@Override
	public void onPause() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onPause");
		super.onPause();
	}
	@Override
	public void onStop() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onStop");
		super.onStop();
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		
		if(hidden)
			LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--hidden");
		else
			LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--显示了");
	}
	@Override
	public void onDestroy() {
		LogUtil.info(MacthFriendFragment2.class, "MacthFriendFragment--onDestroy");
		super.onDestroy();
	}
	
	public void onKeyDow(){
		dislike();
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
				animStart=false;
				getActivity().getSupportFragmentManager().popBackStack();
				
	}


	@Override
	public void handleDataResultOnError(int responseId, int errCode, Object error) {
		super.handleDataResultOnError(responseId, errCode, error);
	}
}
