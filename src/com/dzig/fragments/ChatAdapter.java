package com.dzig.fragments;

import java.util.ArrayList;

import com.dzig.R;
import com.dzig.model.ChatMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter{
	private final ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
	private final LayoutInflater inflater;
	
	public ChatAdapter(Context context) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ChatMessage getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = inflater.inflate(R.layout.chat_item, null);
		}
		TextView messageView = (TextView)convertView.findViewById(R.id.message);
		
		ChatMessage msg = getItem(position);
		messageView.setText(msg.getMessage());
		return convertView;
	}

}

