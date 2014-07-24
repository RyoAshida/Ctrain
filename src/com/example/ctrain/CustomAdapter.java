package com.example.ctrain;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		 
		 ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		 String l = item.getLine();
		 int n = item.getCrowd();
		 if (l.equals(MainActivity.Yamanote)) {
			 icon.setImageResource(R.drawable.y_icon);
		 } else if (l.equals(MainActivity.Chuo)) {
			 icon.setImageResource(R.drawable.c_icon);
		 } 
		 TextView line = (TextView)convertView.findViewById(R.id.line);
		 if (l.equals(MainActivity.Yamanote))
			 line.setText("山手線");
		 else if (l.equals(MainActivity.Chuo))
		 	line.setText("中央線");
		 TextView cd = (TextView)convertView.findViewById(R.id.crowd);
		 switch(n) {
		 case 0: cd.setText("がらがら"); break;
		 case 1: cd.setText("普通"); break;
		 case 2: cd.setText("混雑"); break;
		 case 3: cd.setText("圧死"); break;
		 }
		 
		 return convertView;
	 }
}
