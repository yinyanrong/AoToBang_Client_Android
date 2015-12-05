package com.aotobang.app.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.adapter.MessageAdapter;
import com.aotobang.app.manager.AotoConversation;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.app.view.ListenClickRelativeLayout;
import com.aotobang.app.view.ListenClickRelativeLayout.OnRlTouchListener;
import com.aotobang.utils.LogUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
@ContentView(R.layout.activity_message)
public class MessageActivity extends BaseActivity {
@FindViewOnClick(R.id.btn)
private ListenClickRelativeLayout btn;
@FindViewNoOnClick(R.id.interaction_text)
private TextView interaction_text;
@FindViewNoOnClick(R.id.interaction_icon)
private ImageView interaction_icon;
@FindViewNoOnClick(R.id.pull_refresh_list)
private PullToRefreshListView pull_refresh_list;
@FindViewOnClick(R.id.btn_back)
private ImageButton btn_back;
private MessageAdapter adapter;
private BroadcastReceiver newMsgReceiver=new NewMsgBroadcastReceiver();

private List<AotoConversation> conversationList = new ArrayList<AotoConversation>();
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn:
			break;
		case R.id.btn_back:
			finish();
			break;
			
		}
	}
@Override
	protected void onResume() {
	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(ChatManager.NEW_MESSAGE_ACTION);
	intentFilter.addAction(ChatManager.NEW_FRIEND_ACTION);
	intentFilter.setPriority(20);
	registerReceiver(newMsgReceiver, intentFilter);
	refresh();
	AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
	prload.pushActivity(this);
		super.onResume();
	}
@Override
	protected void onPause() {
		unregisterReceiver(newMsgReceiver);
		super.onPause();
	}
@Override
	protected void onStop() {
	AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
	prload.popActivity(this);
		super.onStop();
	}
	private void initData() {
		conversationList.addAll(loadConversations());
		LogUtil.info(MessageActivity.class,conversationList.size()+"---"+ChatManager.getInstance().getAllAotoConversations().size());
	}

	private List<AotoConversation> loadConversations() {
				
				Hashtable<String, AotoConversation> conversations = ChatManager.getInstance().getAllAotoConversations();
				
				// 过滤掉messages size为0的conversation
				/**
				 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
				 * 影响排序过程，Collection.sort会产生异常
				 * 保证Conversation在Sort过程中最后一条消息的时间不变 
				 * 避免并发问题
				 */
				List<Pair<Long, AotoConversation>> sortList = new ArrayList<Pair<Long, AotoConversation>>();
				synchronized (conversations) {
					for (AotoConversation conversation : conversations.values()) {
						if (conversation.getAllMessages().size() != 0) {
							sortList.add(new Pair<Long, AotoConversation>(conversation.getLastMessage().getMsgTime(), conversation));
						}
					}
				}
				try {
					// Internal is TimSort algorithm, has bug
					sortConversationByLastChatTime(sortList);
					LogUtil.info(MessageActivity.class,sortList.size()+"---sortList"+ChatManager.getInstance().getAllAotoConversations().size());
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<AotoConversation> list = new ArrayList<AotoConversation>();
				for (Pair<Long, AotoConversation> sortItem : sortList) {
					if(!list.contains(sortItem.second))
					list.add(sortItem.second);
				}
				LogUtil.info(MessageActivity.class,list.size()+"---list"+ChatManager.getInstance().getAllAotoConversations().size());
				return list;
	}

	private void sortConversationByLastChatTime(List<Pair<Long, AotoConversation>> sortList) {
		Collections.sort(sortList, new Comparator<Pair<Long, AotoConversation>>() {
			@Override
			public int compare(final Pair<Long, AotoConversation> con1, final Pair<Long, AotoConversation> con2) {
				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
		
		
	}

	@Override
	public void initView() {
		
		btn.setOnRlTouchlistener(new OnRlTouchListener() {
			@Override
			public void OnRlDown() {
				interaction_text.setTextColor(getResources().getColor(android.R.color.white));
				interaction_icon.setImageResource(R.drawable.interaction_icon_white);
			}
			@Override
			public void OnRlMove(boolean focused) {
				if(focused){
					interaction_text.setTextColor(getResources().getColor(android.R.color.white));
					interaction_icon.setImageResource(R.drawable.interaction_icon_white);
				}
				else{
				interaction_text.setTextColor(getResources().getColor(R.color.main_text_color_red));
				interaction_icon.setImageResource(R.drawable.interaction_icon);
				}
			}
			@Override
			public void OnRlUp() {
				interaction_text.setTextColor(getResources().getColor(R.color.main_text_color_red));
				interaction_icon.setImageResource(R.drawable.interaction_icon);
				
			}
		});
		//pull_refresh_list.setMode(Mode.PULL_FROM_START);
		pull_refresh_list.setPullToRefreshEnabled(false);
		pull_refresh_list.setScrollingWhileRefreshingEnabled(true);
		pull_refresh_list.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(MessageActivity.this, System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
						pull_refresh_list.onRefreshComplete();
						// Do work to refresh the list here.
					}
				});
		pull_refresh_list.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
			}
		});
		
		ListView actualListView = pull_refresh_list.getRefreshableView();
		adapter = new MessageAdapter(MessageActivity.this,conversationList);
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
				Intent intent=new Intent();
				intent.putExtra("userId", conversationList.get(postion-1).getUserId());
				intent.setClass(MessageActivity.this, ChatActivity.class);
				startActivity(intent);
				
			}
		});
	}
	private  class NewMsgBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.info(MessageActivity.class, "收到一条新消息");
					abortBroadcast();
					refresh();
			
		}
		
		
		
	}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversations());
		if(adapter != null){
		    adapter.notifyDataSetChanged();
		}
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
		
	}
	@Override
		public void handleDataResultOnError(int responseId, int errCode, Object error) {
			super.handleDataResultOnError(responseId, errCode, error);
			
			
		}
}
