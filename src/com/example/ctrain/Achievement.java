package com.example.ctrain;

public class Achievement {
	Lines line;
	int crowd;
	Achievement (Lines line, int crowd) {
		this.line = line;
		this.crowd = crowd;
	}
	
	public String getLine() {
		if (line == Lines.Chuo)
			return "Chuo";
		else if (line == Lines.Yamanote)
			return "Yamanote";
		return "";
	}
	
	public String getCrowd() {
		return Integer.toString(crowd);
	}
	
}
