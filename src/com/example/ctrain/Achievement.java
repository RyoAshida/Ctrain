package com.example.ctrain;

public class Achievement {
	String line;
	int crowd;
	Achievement (String line, int crowd) {
		this.line = line;
		this.crowd = crowd;
	}
	
	public String getLine() {
		if (line.equals("Chuo"))
			return "Chuo";
		else if (line.equals("Yamanote"))
			return "Yamanote";
		return "";
	}
	
	public int getCrowd() {
		return crowd;
	}
	
}
