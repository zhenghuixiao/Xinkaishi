package com.example.xinkaishi.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.xinkaishi.R;
import com.example.xinkaishi.adapter.Adapter;
import com.example.xinkaishi.bean.GoodsURLPath;
import com.example.xinkaishi.util.Cache;
import com.example.xinkaishi.util.DataAnalysis;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_recommend extends Fragment {
	private View view;
	private GridView gv_tuijian;
	private Handler handler;
	private Adapter adapter;
	private Cache cache;
	private int shop_id;
	private ArrayList<HashMap<String, Object>> arrayList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_fragment_recommend, container, false);  
		
		initView();
		initGoods(GoodsURLPath.urlPath_tuijian + "shop_id=" + shop_id);
		
		handler  = new MyHandler(); 
		return view;
	} 
	
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			arrayList = (ArrayList<HashMap<String,Object>>)msg.obj;
			
			adapter = new Adapter(getActivity(),arrayList,R.layout.listview_own_item,
					new String[] { "img","name","price","originalPrice","saleAmount"},
					new int[] { R.id.iv_pic,R.id.tv_ownName,R.id.tv_pri,R.id.tv_oldpri,R.id.tv_sales},cache);
			gv_tuijian.setAdapter(adapter);
			
			gv_tuijian.setOnItemClickListener(new OnItemClickListener() {    
	               public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,   
	                       long arg3) {  
	            	   String goodId = arrayList.get(arg2).get("id").toString();
	            	   Intent intent = new Intent(getActivity(),GoodsDetail_Activity.class);
	            	   intent.putExtra("id", goodId);
	            	   intent.putExtra("title_Num", 2);//2位推荐商品 
	            	   startActivity(intent);
	            	   getActivity().overridePendingTransition(R.anim.pic_left_in,R.anim.pic_left_out); //切换动画
	               }   
	           });
		}
		
	}
	
	private void initView() {
		gv_tuijian = (GridView)view.findViewById(R.id.gv_tuijian);
		
		SharedPreferences sp = getActivity().getSharedPreferences("sp_name", Context.MODE_PRIVATE);
		shop_id = sp.getInt("shop_id", 1);
		
		cache = new Cache();
	}
	
	private void initGoods(final String url) {
		new Thread(new Runnable()  
	    {  
	        @Override  
	        public void run()  
	        {  
				try {
					Message msg = Message.obtain();
					msg.obj = getGood(url);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }).start();
	}
	
	public ArrayList<HashMap<String,Object>> getGood(String url){
		ArrayList<HashMap<String,Object>> arrayListGood = new ArrayList<HashMap<String,Object>>();
		try {
			JSONObject dataJson = new JSONObject(DataAnalysis.readParse(url));//获取json对象
			JSONObject data=dataJson.getJSONObject("data");
			JSONArray list=data.getJSONArray("list");
			JSONObject info;
			for (int i = 0; i < list.length(); i++) {
				HashMap<String,Object> hm = new HashMap<String,Object>();
				info=list.getJSONObject(i);
				hm.put("index", i);
				hm.put("id", info.getString("id")); 
				hm.put("name", info.getString("name")); 
				hm.put("price", info.getDouble("price")); 
				hm.put("originalPrice", info.getDouble("originalPrice")); 
				hm.put("saleAmount", info.getInt("saleAmount")); 
				hm.put("img", info.getString("img") + "!i"); //!i 为压缩图片地址
				arrayListGood.add(hm);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrayListGood;
	}
}
