package com.example.ctrain;

public class Achievement {
	String line;
	int crowd;
	Achievement (String line, int crowd) {
		this.line = line;
		this.crowd = crowd;
	}
	
	public String getLine() {
		if (line.equals(MainActivity.Chuo))
			return MainActivity.Chuo;
		else if (line.equals(MainActivity.Yamanote))
			return MainActivity.Yamanote;
		return "";
	}
	
	public int getCrowd() {
		return crowd;
	}
	
}
