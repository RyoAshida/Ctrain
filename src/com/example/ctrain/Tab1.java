package com.example.ctrain;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tab1 extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tab1, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView text = (TextView) view.findViewById(R.id.textView1);
		text.setText(Integer.toString(MainActivity.btCount));
		TextView lat = (TextView) view.findViewById(R.id.latitude_view);
		TextView lon = (TextView) view.findViewById(R.id.longitude_view);
		lat.setText(Double.toString(MainActivity.latitude));
		lon.setText(Double.toString(MainActivity.longitude));
		TextView line = (TextView) view.findViewById(R.id.route);
		line.setText(MainActivity.route);
	}
}
