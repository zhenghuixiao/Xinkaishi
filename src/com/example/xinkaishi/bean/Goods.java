package com.example.xinkaishi.bean;

public class Goods {
		private int id;//编号
		private String name;//商品名称
		private Double price;//现价
		private Double originalPrice;//原价
		private String img;//商品图
		private int recommend;//推荐
		private int inventory;//库存
		private int saleAmount;//销售数量
		private String subtitle;//副标题
		private String brandName;//品牌名称
		private int brandID;//品牌ID
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
		public Double getPrice() {
			return price;
		}
		public void setPrice(Double price) {
			this.price = price;
		}
		public Double getOriginalPrice() {
			return originalPrice;
		}
		public void setOriginalPrice(Double originalPrice) {
			this.originalPrice = originalPrice;
		}
		public String getImg() {
			return img;
		}
		public void setImg(String img) {
			this.img = img;
		}
		public int getRecommend() {
			return recommend;
		}
		public void setRecommend(int recommend) {
			this.recommend = recommend;
		}
		public int getInventory() {
			return inventory;
		}
		public void setInventory(int inventory) {
			this.inventory = inventory;
		}
		public int getSaleAmount() {
			return saleAmount;
		}
		public void setSaleAmount(int saleAmount) {
			this.saleAmount = saleAmount;
		}
		public String getBrand() {
			return brandName;
		}
		public void setBrand(String brand) {
			this.brandName = brand;
		}
		public int getBrandID() {
			return brandID;
		}
		public void setBrandID(int brandID) {
			this.brandID = brandID;
		}
		public String getSubtitle() {
			return subtitle;
		}
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		public String getBrandName() {
			return brandName;
		}
		public void setBrandName(String brandName) {
			this.brandName = brandName;
		}
}
