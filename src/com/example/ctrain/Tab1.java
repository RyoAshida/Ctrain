package com.example.ctrain;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Tab1 extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tab1, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		long start = System.currentTimeMillis();
		super.onViewCreated(view, savedInstanceState);
		//number of terminals
		int count = MainActivity.btCount;
		TextView text = (TextView) view.findViewById(R.id.textView1);
		text.setText(Integer.toString(count));
		//location
		TextView lat = (TextView) view.findViewById(R.id.latitude_view);
		TextView lon = (TextView) view.findViewById(R.id.longitude_view);
		lat.setText(Double.toString(MainActivity.latitude));
		lon.setText(Double.toString(MainActivity.longitude));
		//line
		TextView line = (TextView) view.findViewById(R.id.route);
		String r = MainActivity.route;
		line.setText(r);
		//crowd image
		ImageView crowd = (ImageView) view.findViewById(R.id.crowd);
		int n = Math.max(0, MainActivity.rank(count));
		switch(n) {
		case 0: crowd.setImageResource(R.drawable.level0); break;
		case 1: crowd.setImageResource(R.drawable.level1); break;
		case 2: crowd.setImageResource(R.drawable.level2); break;
		case 3: crowd.setImageResource(R.drawable.level3); break;
		}	
		crowd.refreshDrawableState();
		//line image
		ImageView lineimage = (ImageView) view.findViewById(R.id.lineimage);
		String[] ls = MainActivity.lines;
		int j = -1;
		for (int i = 0; i < ls.length; i++) {
			if (r.equals(ls[i])) {
				j = i;
				break;
			}
		}
		switch(j) {
		case 0: lineimage.setImageResource(R.drawable.yamanote); break;
		case 1: lineimage.setImageResource(R.drawable.chuo); break;
		}
		lineimage.refreshDrawableState();
		Toast.makeText(getActivity(), Long.toString(System.currentTimeMillis()-start), Toast.LENGTH_LONG).show();
	}
}
