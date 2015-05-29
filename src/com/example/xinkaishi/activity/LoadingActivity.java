package com.example.xinkaishi.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.xinkaishi.R;
import com.example.xinkaishi.bean.GoodsURLPath;
import com.example.xinkaishi.bean.MenuURLpath;
import com.example.xinkaishi.util.DataAnalysis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoadingActivity extends Activity {
	private String username;
	private String password;
	private Handler handler;
	private EditText userName, passWord;
	private boolean isFirst;
	private SharedPreferences sp, sp_phone;
	private HashMap<String,Object> shop_hm;
	private LinearLayout ll_login;
	private RelativeLayout login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		userName = (EditText)findViewById(R.id.et_username);
		passWord = (EditText)findViewById(R.id.et_password);
		ll_login = (LinearLayout)findViewById(R.id.ll_login);
		login = (RelativeLayout)findViewById(R.id.login);
		sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
		sp_phone = getSharedPreferences("sp_phone", Context.MODE_PRIVATE);
		isFirst = sp.getBoolean("isFirst", true);
		if(!isFirst){
			userName.setText(sp.getString("username", username));
			passWord.setText(sp.getString("password", password));
		}
		Button bt = (Button)findViewById(R.id.bt_post);
		controlKeyboardLayout(login, ll_login);
        bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username=userName.getText().toString();  
				password=passWord.getText().toString();  
				if(username.equals("")||password.equals("")){
					Toast.makeText(LoadingActivity.this, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
					return;
				}
				 init();
			}
		});
        
        handler = new MyHandler();
	}
	
	public void init(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();  
				
		        //添加用户名和密码  
		        params.add(new BasicNameValuePair("username",username));  
		        params.add(new BasicNameValuePair("password",password ));  
				try {
					String result = DataAnalysis.doPost(params, GoodsURLPath.urlPathOL);
					HashMap<String,Object> hm = new HashMap<String,Object>();
					if(result.equals("0")){
						hm.put("state", 1);//状态码 1表示错误
						Message msg = Message.obtain();
						msg.obj = hm;
						handler.sendMessage(msg);
						return;
					}
					JSONObject dataJson;
					dataJson = new JSONObject(result);
					hm.put("state", dataJson.getInt("error"));  //状态码
					hm.put("message", dataJson.getString("message")); //返回文字
					//账密正确
					if(dataJson.getInt("error") == 0){
						JSONObject info = dataJson.getJSONObject("data"); //门店ID，余额， 店名
						hm.put("shop_id", info.getInt("shop_id"));
						hm.put("account_balance", info.getDouble("account_balance"));
						hm.put("shop_name", info.getString("name"));
					}
					Message msg = Message.obtain();
					msg.obj = hm;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
 			}
		}).start();
	}
	class MyHandler extends Handler{
		public void handleMessage(Message msg){
			shop_hm = (HashMap<String,Object>)msg.obj;
				int state =(Integer) shop_hm.get("state");
				if(state == 0){
					Editor edit = sp.edit();
					edit.putBoolean("isFirst", false);
					edit.putInt("shop_id", (Integer) shop_hm.get("shop_id"));
					edit.putString("shop_name", (String) shop_hm.get("shop_name"));
					edit.putString("account_balance",shop_hm.get("account_balance") + "");//这里格式暂为string  实际double
					
					//账密正确则储存
					edit.putString("username", username);
					edit.putString("password", password);
					edit.commit();
					finish();
					Intent intent = new Intent(LoadingActivity.this,Fragment_main.class);
					 startActivity(intent);
				}else{
					 Toast.makeText(LoadingActivity.this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
				}
		}
	};
	
	
	/** 
     * @param root 最外层布局，需要调整的布局 
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部 
     */  
    private void controlKeyboardLayout(final View root, final View scrollToView) {  
        root.getViewTreeObserver().addOnGlobalLayoutListener( new OnGlobalLayoutListener() {  
            @Override  
            public void onGlobalLayout() {  
                Rect rect = new Rect();  
                //获取root在窗体的可视区域  
                root.getWindowVisibleDisplayFrame(rect);  
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)  
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;  
                //若不可视区域高度大于100，则键盘显示  
                if (rootInvisibleHeight > 100) {  
                    int[] location = new int[2];  
                    //获取scrollToView在窗体的坐标  
                    scrollToView.getLocationInWindow(location);  
                    //计算root滚动高度，使scrollToView在可见区域  
                    int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;  
                    root.scrollTo(0, srollHeight);  
                } else {  
                    //键盘隐藏  
                    root.scrollTo(0, 0);  
                }  
            }  
        });  
    } 
}
