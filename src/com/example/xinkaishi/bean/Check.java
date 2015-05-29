package com.example.xinkaishi.bean;

import java.util.ArrayList;
import java.util.HashMap;


public class Check {
	private int num;
	private int count;
	private HashMap<String, Object> hm = new HashMap<String, Object>();
	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
	public ArrayList<HashMap<String, Object>> getList() {
		return list;
	}

	public HashMap<String, Object> getHm() {
		return hm;
	}

	public void setHm(HashMap<String, Object> hm) {
		this.hm = hm;
	}

	public void setList(ArrayList<HashMap<String, Object>> list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
}
