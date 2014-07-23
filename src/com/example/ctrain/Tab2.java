package com.example.ctrain;

import java.util.*;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Tab2 extends Fragment {
	
	private static long prev = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tab2, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		long pres = System.currentTimeMillis();
		if (prev < 0 || pres-prev > 1000) {
			List<Achievement> list = new ArrayList<Achievement>();
			int n = MainActivity.num;
			boolean[][] g = MainActivity.gotten;
			String[] l = MainActivity.lines;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < 4; j++) {
					if (g[i][j]) {
						list.add(new Achievement(l[i], j));
					}
				}
			}
			
			ListView listview = (ListView) view.findViewById(R.id.achievements);
			listview.setAdapter(new CustomAdapter(getActivity(), 0, list));
		}
		prev = pres;
	}
}