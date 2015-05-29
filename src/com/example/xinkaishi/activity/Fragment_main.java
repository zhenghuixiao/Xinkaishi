package com.example.xinkaishi.activity;


import com.example.xinkaishi.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_main extends Activity implements OnClickListener{
	private Fragment_goods frag_goods; //全部商品展示页
	private Fragment_recommend frag_recommend; //推荐商品展示页
	private Fragment_news fargment1;
	private Fragment_zice frag_zice;
	private RelativeLayout title_all, title_zice,title_zixun;
	private ImageView iv_all, iv_zice, iv_zixun;
	private boolean isok;
	private int state;//当前页面
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_fragment_main);
		initView();//初始化控件
		setDefaultFragment();  //设置默认页
		
	}

	private void initView() {
		title_all = (RelativeLayout)findViewById(R.id.title_all);
		title_zice = (RelativeLayout)findViewById(R.id.title_zice);
		title_zixun = (RelativeLayout)findViewById(R.id.title_zixun);
		title_all.setOnClickListener(this);
		title_zice.setOnClickListener(this);
		title_zixun.setOnClickListener(this);
		iv_all = (ImageView)findViewById(R.id.iv_all);
		iv_zice = (ImageView)findViewById(R.id.iv_zice);
		iv_zixun = (ImageView)findViewById(R.id.iv_zixun);
		isok = false;
		state = 1;
//		webview = new WebView(this);
	}

	//设置默认fragment
	private void setDefaultFragment() {
		FragmentManager fm = getFragmentManager();  
        // 开启Fragment事务  
        FragmentTransaction transaction = fm.beginTransaction();  
        frag_goods = new Fragment_goods();  
        frag_zice = new Fragment_zice();  
        fargment1 = new Fragment_news();  
        transaction.replace(R.id.id_content, frag_goods);  
        transaction.commit(); 		
	}

	@Override
	public void onClick(View v) {
		if(isok){
			return;
		}
		isok = true;
		FragmentManager fm = getFragmentManager();  
        // 开启Fragment事务  
        FragmentTransaction transaction = fm.beginTransaction();  
        
        switch (v.getId())  
        {  
        case R.id.title_all:  
        	state = 1;
        	
        	iv_all.setImageResource(R.drawable.menu_btn1_hover);
        	iv_zice.setImageResource(R.drawable.menu_btn3);
        	iv_zixun.setImageResource(R.drawable.menu_btn2);
        	
        	frag_goods.onStop();
        	frag_zice.onStop(); 
        	fargment1.onStop();
        	if(frag_goods == null){
        		frag_goods = new Fragment_goods();  
        	}else{
        		transaction.remove(frag_goods);
        		transaction.remove(frag_zice);
        		transaction.remove(fargment1);
        		frag_goods = new Fragment_goods();  
        	}
        	transaction.add(R.id.id_content, frag_goods);  
            break;  
            
        case R.id.title_zice:
        	state = 2;
        	
        	iv_zice.setImageResource(R.drawable.menu_btn3_hover);
        	iv_all.setImageResource(R.drawable.menu_btn1);
        	iv_zixun.setImageResource(R.drawable.menu_btn2);
        	
        	frag_goods.onStop();
        	frag_zice.onStop(); 
        	fargment1.onStop();
        	if(frag_zice == null){
        		frag_zice = new Fragment_zice();    
        	}else{
        		transaction.remove(frag_goods);
        		transaction.remove(frag_zice);
        		transaction.remove(fargment1);
        		frag_zice = new Fragment_zice();    
        	}
        	transaction.add(R.id.id_content, frag_zice); 
            break;  
            
        case R.id.title_zixun:
        	state = 3;
        	
        	iv_zice.setImageResource(R.drawable.menu_btn3);
        	iv_all.setImageResource(R.drawable.menu_btn1);
        	iv_zixun.setImageResource(R.drawable.menu_btn2_hover);
        	
        	frag_goods.onStop();
        	frag_zice.onStop(); 
        	fargment1.onStop();
        	if(fargment1 == null){
        		fargment1 = new Fragment_news();  
        	}else{
        		transaction.remove(frag_goods);
        		transaction.remove(frag_zice);
        		transaction.remove(fargment1);
        		fargment1 = new Fragment_news();    
        	}
        	transaction.add(R.id.id_content, fargment1); 
            break;  
            
        }  
//         transaction.addToBackStack();  
        // 事务提交  
        transaction.commit();
        isok = false;
	}
	
	/** 
     * 将所有的Fragment都置为隐藏状态。 
     *  
     * @param transaction 
     *            用于对Fragment执行操作的事务 
     */  
    private void hideFragments(FragmentTransaction transaction) {  
        if (frag_goods != null) {  
            transaction.hide(frag_goods);  
        }  
        if (frag_recommend != null) {  
            transaction.hide(frag_recommend);  
        }  
    } 
    @Override 
    //设置回退  
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
    	switch(state){
    		case 1: 
    			if(keyCode == KeyEvent.KEYCODE_BACK) {
    				this.finish();  
    				return true;  
    			}
    		case 2: 
    			if(keyCode == KeyEvent.KEYCODE_BACK) {
    				this.finish();
    				return true;  
    			}
    		case 3: 
    			if(keyCode == KeyEvent.KEYCODE_BACK&&Fragment_news.webview_news.canGoBack()) {
    				Fragment_news.webview_news.goBack(); //goBack()表示返回WebView的上一页面  
    	            return true;  
    			}
    	}
    	return super.onKeyDown(keyCode, event);  
    }
    
}
