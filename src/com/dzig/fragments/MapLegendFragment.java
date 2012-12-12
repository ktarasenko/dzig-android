package com.dzig.fragments;


import java.util.List;

import com.dzig.R;
import com.dzig.activities.CustomMapActivity;
import com.dzig.model.Coordinate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MapLegendFragment extends Fragment{
	MapLegendAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map_legend, container);
		ListView list = (ListView) view.findViewById(R.id.listView);
		list.setAdapter(adapter = new MapLegendAdapter(getActivity()));
		return view;
	}
	
	@Override
	public void onResume() {
		Activity activity = getActivity(); 
		activity.registerReceiver(receiver, new IntentFilter(CustomMapActivity.ACTION_UPDATE_POINTS));
		activity.sendBroadcast(new Intent(CustomMapActivity.ACTION_GET_POINTS));
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		Activity activity = getActivity(); 
		activity.unregisterReceiver(receiver);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if (CustomMapActivity.ACTION_UPDATE_POINTS.equals(intent.getAction())){
				Bundle bundle = intent.getExtras();
				final List<Coordinate> list = bundle.getParcelableArrayList(CustomMapActivity.EXTRA_POINTS);
				Activity activity = getActivity(); 
				if (activity != null){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MapLegendAdapter mapAdapter = adapter;
							if (mapAdapter != null){
								mapAdapter.set(list);
							}
						}
					});
				}
			}
		}
	};
	
	
	static class MapLegendAdapter extends SimpleArrayAdapter<Coordinate>{
		java.text.DateFormat dateFormat;
		Context context;
		MapLegendAdapter(Context context){
			this.context = context;
			dateFormat = DateFormat.getMediumDateFormat(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.map_legend_item, null);
			}
			ImageView userpic = (ImageView) convertView.findViewById(R.id.userpic);
			TextView username = (TextView)  convertView.findViewById(R.id.username);
			TextView lastseen = (TextView)  convertView.findViewById(R.id.lastseen);
			Coordinate coordinate = getItem(position);
			username.setText(coordinate.getCreator().toString());
			lastseen.setText(context.getString(R.string.last_seen_message, dateFormat.format(coordinate.getDate())));
			return convertView;
		}
		
	}
}
