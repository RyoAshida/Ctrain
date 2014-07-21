package com.example.ctrain;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Achievement>{
	private LayoutInflater layoutInflater_;
	
	public CustomAdapter(Context context, int textViewResourceId, List<Achievement> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@SuppressLint("InflateParams")
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		Achievement item = (Achievement)getItem(position);
		 
		 if (null == convertView) {
			 convertView = layoutInflater_.inflate(R.layout.list_item, null);
		 }
		 
		 TextView line = (TextView)convertView.findViewById(R.id.line);
		 line.setText(item.getLine());
		 TextView cd = (TextView)convertView.findViewById(R.id.crowd);
		 cd.setText(item.getCrowd());
		 
		 return convertView;
	 }
}
