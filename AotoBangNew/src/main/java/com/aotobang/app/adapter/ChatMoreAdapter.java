package com.aotobang.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aotobang.app.R;

public class ChatMoreAdapter extends BaseAdapter {
	private int[] res=new int[]{R.drawable.selector_more_gift,R.drawable.selector_more_photo,
			R.drawable.selector_more_picture,R.drawable.selector_more_interact,R.drawable.selector_more_location};
	private String[] strs;
	private Context ctx;

	public ChatMoreAdapter(Context ctx) {
	
		this.ctx=ctx;
		strs=new String[]{ctx.getString(R.string.chat_more_gift),
	ctx.getString(R.string.chat_more_photo),ctx.getString(R.string.chat_more_picture),
	ctx.getString(R.string.chat_more_interact),ctx.getString(R.string.chat_more_location)};
	}

	@Override
	public int getCount() {

		return res.length;
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(ctx, R.layout.chat_more_griditem, null);
			holder.more_img=(ImageView) convertView.findViewById(R.id.more_img);
			holder.more_text=(TextView) convertView.findViewById(R.id.more_text);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.more_img.setImageResource(res[position]);
		holder.more_text.setText(strs[position]);
		
		return convertView;
	}
private class ViewHolder{
	private ImageView more_img;
	private TextView  more_text;
	
}
}
