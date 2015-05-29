package com.example.xinkaishi.bean;

import java.util.List;

public class MenuList1 {
	private int id;
	private String name;
	private int parentId;
	private String img;
	private int leftNum;
	private String img_s;
	public String getImg_s() {
		return img_s;
	}
	public void setImg_s(String img_s) {
		this.img_s = img_s;
	}
	public int getLeftNum() {
		return leftNum;
	}
	public void setLeftNum(int leftNum) {
		this.leftNum = leftNum;
	}
	private List<MenuList2> menuList2;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public List<MenuList2> getMenuList2() {
		return menuList2;
	}
	public void setMenuList2(List<MenuList2> menuList2) {
		this.menuList2 = menuList2;
	}
}
