package com.example.xinkaishi.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.xinkaishi.R;
import com.example.xinkaishi.adapter.PropsAdapter;
import com.example.xinkaishi.bean.GoodsPostURLPath;
import com.example.xinkaishi.bean.GoodsURLPath;
import com.example.xinkaishi.bean.Time_phonedel;
import com.example.xinkaishi.custom.MyGridView;
import com.example.xinkaishi.custom.MyLayout;
import com.example.xinkaishi.util.Cache;
import com.example.xinkaishi.util.DataAnalysis;
import com.example.xinkaishi.util.LoadImg;
import com.example.xinkaishi.util.LoadImg.OnLoadImageListener;
import com.example.xinkaishi.util.SaveFile;
import com.example.xinkaishi.util.ValueLength;

public class GoodsDetail_Activity extends Activity {
	private Handler handler, handler_postPrice;
	private String urlYunFei; //运费
	private Double postPrice, real_price;//运费  选中SKU的商品价格
	private ArrayList<Button> bt_all;
	private HashMap<String,Object> hm;
	private HashMap<String, Object> sku_on;
	private TextView tv_name, tv_saleAmount, tv_inventory, tv_price, tv_originalPrice, tv_yunfei, tv_num,tv_subtitle;
	private WebView wv_pic;
	private MyGridView  gv_props;
	private LinearLayout llMenu;
	private ArrayList<HashMap<String,Object>> list, list_click, list_sku, list_props, list_url, spec_keys; //分类列表  选中的属性  sku列表  产品参数  图片地址   分类的keys
	private Cache cc; //缓存
	private ViewPager viewPager;
	private ArrayList<ImageView> list_img;  
	private ArrayList<View> list_dot;
	private LinearLayout ll_dotPic;
	private int currentItem = 0; // 当前图片的索引号  
	private int inventory = 0;
	private View darkview; //背景灰屏
	private PopupWindow popWindow_setPhone, popWindow_succeed;//购买时两个窗口
	private boolean isSku;
	private boolean p_mark;//提交手机号码界面的标记
	
	private String txsting_phone;
	private SharedPreferences sp_phone, sp_time;
	//要post的参数
	private String goodsId, cellphone;
	private int num = 1;
	
	//账户信息
    private int shop_id;
    private String account_balance; // 余额
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_detail_);
		
		SharedPreferences sp = getSharedPreferences("sp_name", Context.MODE_PRIVATE);
		sp_phone = getSharedPreferences("sp_phone", Context.MODE_PRIVATE);
		sp_time = getSharedPreferences("del_time", Context.MODE_PRIVATE);
		
		shop_id = sp.getInt("shop_id", 1);
		account_balance = sp.getString("account_balance", 0 + "");
		
		initView();//初始化控件
		initDetail();
		handler  = new MyHandler();
		handler_postPrice = new PostHandler();
	}
	
	private void initDetail() {
		Intent intent = getIntent();
		String goodId = intent.getExtras().getString("id");
		String url = (GoodsURLPath.urlPath_detail + "goods_id=" + goodId + "&shop_id=" + shop_id);
		initGoodDetail(url);
	}
	
	class MyHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg){
			hm = (HashMap<String, Object>) msg.obj;
			goodsId = hm.get("goodsId").toString(); //post属性
			inventory = (Integer) hm.get("inventory");//库存
			list_props = new ArrayList<HashMap<String,Object>>();
			list_props = (ArrayList<HashMap<String, Object>>) hm.get("props");
			list_url = (ArrayList<HashMap<String, Object>>) hm.get("images");
			Collections.reverse(list_props);  //参数取出来顺序反了，这里进行倒序
			PropsAdapter adapter = new PropsAdapter(GoodsDetail_Activity.this, list_props, R.layout.listview_props, new String[]{"name","value"}, new int[]{R.id.tv_props_name,R.id.tv_props_value});
			gv_props.setAdapter(adapter);
			
			for(int i = 0; i < list_url.size(); i ++){
				final ImageView iv = new ImageView(getApplicationContext());
					LoadImg.onLoadImage(list_url.get(i).get("url").toString(), cc, new OnLoadImageListener() {
						@Override
						public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
							iv.setImageBitmap(bitmap);
						}
					});
				list_img.add(iv);
			}
			for(int a = 0; a < list_img.size(); a ++){
				View dot = new View(getApplicationContext());
				LayoutParams lp = new LayoutParams(15,15);
				if(a ==0){
					dot.setBackgroundResource(R.drawable.dot_focused); //第一个默认选中
				}else{
					dot.setBackgroundResource(R.drawable.dot_normal); 
					lp.leftMargin = 5; //除了第一个 设置间距
				}
				dot.setLayoutParams(lp);
				list_dot.add(dot);
				ll_dotPic.addView(dot);
			}
			
	        viewPager.setAdapter(new MyPagerAdapter());// 设置填充ViewPager页面的适配器  
	        // 设置一个监听器，当ViewPager中的页面改变时调用  
	        viewPager.setOnPageChangeListener(new MyPageChangeListener());  
			tv_name.setText(hm.get("name") + "");
			tv_saleAmount.setText(hm.get("saleAmount") + "");
			tv_inventory.setText(hm.get("inventory") + "");
			tv_price.setText(String.format("%.2f", hm.get("price")));
			real_price = (Double) hm.get("price");
			tv_originalPrice.setText(String.format("%.2f", hm.get("originalPrice")));
			
			//这边采取将HTML字符串储存到本地sdcard，再进行读取。
			String myFile = "/Android/data/com.example.xinkaishi/";
			String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath()
											+ myFile + goodsId + ".html"; // 保存路径
			SaveFile sf = new SaveFile();
			sf.save(hm.get("desc").toString(), sdCard);
			WebSettings ws = wv_pic.getSettings();
			ws.setDefaultTextEncodingName("utf-8"); //设置文本编码
			wv_pic.loadUrl("file:///sdcard" + myFile + goodsId + ".html");    //读取路径
			
			//加载分类选项
			if(hm.get("specs") != null){
				list_sku = (ArrayList<HashMap<String,Object>>) hm.get("skus");
				list = (ArrayList<HashMap<String,Object>>) hm.get("specs");
				spec_keys = (ArrayList<HashMap<String,Object>>) hm.get("specs_key");
				list_click = new ArrayList<HashMap<String,Object>>();  //所选选项的属性集
				for (int i = 0; i < list.size(); i++) {
					HashMap<String,Object> hm_click = new HashMap<String, Object>();
					hm_click.put("id", i);   //类别的ID  按顺序给予ID
					hm_click.put("name",  "");                   //类别的ValueName  例如  颜色中的 白色
					list_click.add(hm_click);       
				}
				for (int i = 0; i < list.size(); i++) {
					String name = (String) spec_keys.get(i).get("key");//分类的名字  例：颜色分类
					ArrayList<HashMap<String,Object>> list_value = (ArrayList<HashMap<String,Object>>) list.get(i).get("list");
					menuName(name, list_value, i);//分类控件的生成
				}
				//初始化运费
				urlYunFei = GoodsURLPath.urlPath_yunfei + "sku_id=" + list_sku.get(0).get("skuId") + "&num=1" + "&shop_id=" + shop_id;
				getYunFei(urlYunFei); 
			}
		}
	}
	
	/**
	 * 动态生成分类控件以及子控件按钮
	 *@param String , HashMap<String, Object> 
	 */
	private void menuName(String name, ArrayList<HashMap<String,Object>> list_value, int i) {
		llMenu = (LinearLayout)findViewById(R.id.ll_menu);
		TextView tv = new TextView(getApplicationContext());
    	LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    	lp.topMargin = 15;
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(26);
    	tv.setTextColor(getResources().getColor(android.R.color.black));
		tv.setLayoutParams(lp); 
    	tv.setText(name);
    	tv.setClickable(false);
    	llMenu.addView(tv);
    	MyLayout myLayout = new MyLayout(getApplicationContext());//自定义空间大小
    	valueName(myLayout, list_value, i, name);//生成子控件 
	}
	
	private void valueName(MyLayout llValueName, ArrayList<HashMap<String,Object>> list_value, final int i, final String menuName) {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		llValueName.setLayoutParams(lp);
		int height = 0;
		int leng = 0,leng1 = 0;
		//这里判定子控件的高度，以最高高度为准
		for(int j = 0; j < list_value.size(); j ++){
			leng = ValueLength.String_length(list_value.get(j).get("value").toString());
			leng1 = leng1 > leng ? leng1: leng;
			height = leng1/13;
		}
		for(int j = 0; j < list_value.size(); j ++){
			LayoutParams lpp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lpp.rightMargin = 5;
	    	
			final Button bt = new Button(getApplicationContext());
	    	bt.setGravity(Gravity.CENTER);
	    	bt.setTextSize(20);
	    	bt.setWidth(150);
	    	bt.setHeight(50+30*height);//多一行字加30的高度
	    	bt.setSingleLine(false);
	    	bt.setBackgroundResource(R.drawable.textview_shape);
	    	bt.setTextColor(getResources().getColor(android.R.color.black));
	    	bt.setText(list_value.get(j).get("value") +"");
	    	if(ValueLength.String_length((String) list_value.get(j).get("value")) > 13){
	    		lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	}
	    	bt.setLayoutParams(lpp);
	    	bt.setId((Integer) list_click.get(i).get("id"));
	    	bt.setClickable(true); 
	    	if(j == 0){
	    		bt.setTextColor(getResources().getColor(R.color.menuName_red));
	    		bt.setBackgroundResource(R.drawable.textview_shape_onclick);
				list_click.get(i).put("name", menuName + ":" + bt.getText());
	    	}
	    	llValueName.addView(bt);
	    	bt_all.add(bt);
	    	
	    	bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isOnc = true;
					list_click.get(i).put("name", menuName + ":" + bt.getText());
					for (Button bt1 : bt_all) {
						if(bt.getId() ==bt1.getId()){ //是否同一类别ID
							bt1.setTextColor(getResources().getColor(android.R.color.black));
							bt1.setBackgroundResource(R.drawable.textview_shape); //点击还原其他背景框
						}
					}
					bt.setTextColor(getResources().getColor(R.color.menuName_red));
					bt.setBackgroundResource(R.drawable.textview_shape_onclick);
//					每个分类都已被选中 取到对应的SkuId
					if(isOnc){
						getSkuId(list_click);//获取sku信息
						urlYunFei = GoodsURLPath.urlPath_yunfei + "sku_id=" + sku_on.get("skuId") + "&num=" + tv_num.getText() + "&shop_id=" + shop_id;
//						getYunFei(urlYunFei); //获取运费
						tv_saleAmount.setText(sku_on.get("saleAmount") + "");
						tv_price.setText(String.format("%.2f", sku_on.get("price")));
						tv_inventory.setText(sku_on.get("inventory") + "");
						inventory = (Integer) sku_on.get("inventory");
						real_price = (Double)sku_on.get("price");
					}
				}
			});
		} 
		sku_on = list_sku.get(0);//默认为第一项SKU
		llMenu.addView(llValueName);
	}
	
	public void getSkuId(ArrayList<HashMap<String, Object>> list_onclick) {
		isSku = false;
		for(int a = 0; a < list_sku.size(); a ++){
			ArrayList<HashMap<String,Object>> value = (ArrayList<HashMap<String,Object>>) list_sku.get(a).get("spec");
			for(int b = 0; b < value.size(); b ++){
				String valueName = value.get(b).get("valueName").toString();
				for(int c = 0; c < value.size(); c ++){
					if(valueName.equals(list_onclick.get(c).get("name"))){   //判定方法待优化
						isSku = true;
						break;
					}else{
						isSku = false;
						continue;
					} 
				}
			}
			if(isSku){
				sku_on = list_sku.get(a);
				break;
			}
		}
	}
	
	private void initView() {
		llMenu = (LinearLayout)findViewById(R.id.ll_menu);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_subtitle = (TextView) findViewById(R.id.tv_subtitle);
		tv_saleAmount = (TextView) findViewById(R.id.tv_saleAmount);
		tv_inventory = (TextView) findViewById(R.id.tv_inventory);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_originalPrice = (TextView) findViewById(R.id.tv_originalPrice);
		tv_yunfei = (TextView) findViewById(R.id.tv_yunfei);
		tv_num = (TextView) findViewById(R.id.tv_num);
		sku_on = new HashMap<String, Object>();
		bt_all = new ArrayList<Button>();
		wv_pic = (WebView) findViewById(R.id.wv_goods_pic);  
		wv_pic.getSettings().setJavaScriptEnabled(true);
		
		gv_props = (MyGridView)findViewById(R.id.gv_props);
		cc = new Cache();
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		list_img = new ArrayList<ImageView>();
		list_dot = new ArrayList<View>();
		ll_dotPic = (LinearLayout)findViewById(R.id.ll_dotPic);
		darkview =findViewById(R.id.detail_goods_darkview);
		txsting_phone = "";
		tv_originalPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );//加删除线
	}
	
	public void add(View v){
		if(num < (Integer)hm.get("inventory")){
			tv_num.setText(num + 1 + "");
			num ++;
			if(sku_on != null){
				urlYunFei = GoodsURLPath.urlPath_yunfei + "sku_id=" + sku_on.get("skuId") + "&num=" + tv_num.getText() + "&shop_id=" + shop_id;
				getYunFei(urlYunFei);
			}
		}
	}
	public void min(View v){
		if(num > 1){
			tv_num.setText(num - 1 + "");
			num --;
			if(sku_on != null){
				urlYunFei = GoodsURLPath.urlPath_yunfei + "sku_id=" + sku_on.get("skuId") + "&num=" + tv_num.getText() + "&shop_id=" + shop_id;
				getYunFei(urlYunFei);
			}
		}
	}
	class PostHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			postPrice = (Double) msg.obj;
			tv_yunfei.setText(String.format("%.2f", postPrice) + "");//两位小数
			super.handleMessage(msg);
		}
	}
	public void getYunFei(final String url){
		new Thread(new Runnable() {
			@Override
			public void run() {
					JSONObject dataJson;
					try {
						dataJson = new JSONObject(DataAnalysis.readParse(url));//获取json对象
						Double info= dataJson.getDouble("data");
						Message msg = Message.obtain();
						msg.obj = info;
						handler_postPrice.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}).start();
	}
	
	/**
	 * 获取详细页商品信息
	 * @param url
	 * @return HashMap<String, Object>
	 */
	public void initGoodDetail(final String url){
		new Thread(new Runnable()  
	    {  
	        @Override  
	        public void run()  
	        {  
	        	try {
	        		HashMap<String,Object> hm = new HashMap<String,Object>();
					ArrayList<HashMap<String,Object>> listSku= new ArrayList<HashMap<String,Object>>();
					ArrayList<HashMap<String,Object>> listSpec_keys= new ArrayList<HashMap<String,Object>>();
					ArrayList<HashMap<String,Object>> listProps= new ArrayList<HashMap<String,Object>>();
					ArrayList<HashMap<String,Object>> listImg_url = new ArrayList<HashMap<String,Object>>();
					ArrayList<HashMap<String,Object>> listSpec_name = new ArrayList<HashMap<String,Object>>();
					
					JSONObject dataJson = new JSONObject(DataAnalysis.readParse(url));//获取json对象
					JSONObject data=dataJson.getJSONObject("data");
					JSONArray skus =data.getJSONArray("skus");
					JSONObject specs =data.getJSONObject("specs");
					JSONArray images =data.getJSONArray("images");
					JSONObject props = data.getJSONObject("props");
					JSONObject info, info1,info2,info3;
					JSONArray skus_spec,specs_list;
					
					//其实属性列表
					 Iterator<?> keys = props.keys();  
		            String value = null;  
		            String key = "";  
		            while(keys.hasNext()){//遍历JSONObject  
		            	HashMap<String, Object> hm1 = new HashMap<String, Object>();
		            	key = (String) keys.next().toString();  
		                value = props.getString(key);  
		                hm1.put("name", key);
		                hm1.put("value", value);
		                listProps.add(hm1);
		            } 
			            
		            hm.put("goodsId", data.getString("goodsId")); 
					hm.put("name", data.getString("name")); 
					hm.put("price", data.getDouble("price")); 
					hm.put("originalPrice", data.getDouble("originalPrice")); 
					hm.put("inventory", data.getInt("inventory")); //库存
					hm.put("saleAmount", data.getInt("saleAmount")); //这里指销售数量
//					hm.put("subtitle", data.getString("subtitle"));//额外的内容  推荐商品中需要展示
					hm.put("props", listProps);  //产品参数
					hm.put("status", data.getInt("status"));//商品状态  
					hm.put("desc", data.getString("desc"));
					//滚动图片
					for(int i = 0; i < images.length(); i++){
						HashMap<String,Object> hm_img = new HashMap<String, Object>();
						hm_img.put("url", images.get(i).toString());//图片地址
						listImg_url.add(hm_img);
					}
					hm.put("images", (ArrayList<HashMap<String,Object>>)listImg_url);//图片
					//SKU
					for (int i = 0; i < skus.length(); i++) {
						HashMap<String,Object> hmSku = new HashMap<String,Object>();
						ArrayList<HashMap<String,Object>> listSku_spec = new ArrayList<HashMap<String,Object>>();
						info=skus.getJSONObject(i);
						hmSku.put("skuId", info.getInt("skuId")); 
						hmSku.put("price", info.getDouble("price"));
						hmSku.put("saleAmount", info.getInt("saleAmount"));
						hmSku.put("inventory", info.getInt("inventory")); //每个SKU对应商品的库存 
						skus_spec = info.getJSONArray("spec");
						for (int a = 0; a < skus_spec.length(); a++) {
							HashMap<String,Object> hmSku_spec = new HashMap<String,Object>();
							hmSku_spec.put("valueName", skus_spec.get(a) + "");//类别名字:具体的分类名字
							listSku_spec.add(hmSku_spec);
						}
						hmSku.put("spec", (ArrayList<HashMap<String,Object>>)listSku_spec);
						listSku.add(hmSku);
					}
					hm.put("skus", (ArrayList<HashMap<String,Object>>)listSku);   
					
					//获取商品的分类 类属性
					Iterator<?> specs_keys = specs.keys();  
		            String specs_key = "";  
		            while(specs_keys.hasNext()){//遍历JSONObject  
		            	HashMap<String, Object> hm2 = new HashMap<String, Object>();
		            	specs_key = (String) specs_keys.next().toString();  
		                hm2.put("key", specs_key);
		                listSpec_keys.add(hm2);
		            } 
		            hm.put("specs_key", listSpec_keys);//specs的key 
					for(int i = 0; i < specs.length(); i++){
						HashMap<String,Object> hmSpecs_name = new HashMap<String,Object>();
						//属性子选项
						ArrayList<HashMap<String,Object>> listSpec_valueName= new ArrayList<HashMap<String,Object>>();
						
						specs_list = specs.getJSONArray((String) listSpec_keys.get(i).get("key"));
						for(int a = 0; a < specs_list.length(); a++){
							HashMap<String,Object> hmSpecs_valuName = new HashMap<String,Object>();
							hmSpecs_valuName.put("value", specs_list.get(a));
							listSpec_valueName.add(hmSpecs_valuName);
						}
						hmSpecs_name.put("list", (ArrayList<HashMap<String,Object>>)listSpec_valueName);
						listSpec_name.add(hmSpecs_name);
					}
					hm.put("specs", (ArrayList<HashMap<String,Object>>)listSpec_name);
					Message msg = Message.obtain();
					msg.obj = hm;
					handler.sendMessage(msg);
	        	} catch (Exception e) {
					e.printStackTrace();
				} 
	        }
	    }).start();
	}
	private class MyPagerAdapter extends PagerAdapter {  
		  
        @Override  
        public int getCount() {  
            return list_img.size();  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            ((ViewPager) arg0).addView(list_img.get(arg1));  
            return list_img.get(arg1);  
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            ((ViewPager) arg0).removeView((View) arg2);  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
  
        }  
    }  
	 @Override
    protected void onResume() {
        super.onResume();
        //activity启动3秒钟后，发送一个message，用来将viewPager中的图片切换到下一个
        handlerPage.sendEmptyMessageDelayed(1, 3000);
    }
    @Override  
    protected void onStop() {  
        // 当Activity不可见的时候停止切换  
    	handlerPage.removeMessages(1);
        super.onStop();  
    }  
  
    private Handler handlerPage = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
        	switch(msg.what) {
            case 1:
            	if(list_img.size() == 0){
            		break;
            	}
                currentItem = (currentItem + 1) % list_img.size();
                try {
                    Field field = ViewPager.class.getDeclaredField("mScroller");
                    field.setAccessible(true);
                    FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                            new AccelerateInterpolator());
                    field.set(viewPager, scroller);
                    scroller.setmDuration(300); //设置轮播图片的滑动速度
                } catch (Exception e) {
                }
                viewPager.setCurrentItem(currentItem);// 切换当前显示的图片  
                
                //每5秒钟发送一个message，用于切换viewPager中的图片
                this.sendEmptyMessageDelayed(1, 5000);
            }
        };  
    };  
    
    public class FixedSpeedScroller extends Scroller {
        private int mDuration = 500;
     
        public FixedSpeedScroller(Context context) {
            super(context);
        }
     
        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }
     
        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
     
        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
     
        public void setmDuration(int time) {
            mDuration = time;
        }
     
        public int getmDuration() {
            return mDuration;
        }
    }
    
    /** 
     * 当ViewPager中页面的状态发生改变时调用 
     *  
     * @author Administrator 
     *  
     */  
    private class MyPageChangeListener implements OnPageChangeListener {  
        private int oldPosition = 0;  
  
        /** 
         *  设置图片下的dot图切换
         */  
        public void onPageSelected(int position) {  
            currentItem = position;  
            list_dot.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);  //上一张的dot
            list_dot.get(position).setBackgroundResource(R.drawable.dot_focused);   //当前图片的dot
            oldPosition = position;  
        }  
  
        public void onPageScrollStateChanged(int arg0) {  
  
        }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
  
        }  
    }  
    
    
    /**
     *   返回按键以及自带后退
     *
     */
    public void iv_back(View v){
    	this.finish();  //finish当前activity  
        overridePendingTransition(R.anim.pic_right_in, R.anim.pic_right_out);  
    }
    
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if(keyCode==KeyEvent.KEYCODE_BACK){  
              
            this.finish();  
            overridePendingTransition(R.anim.pic_right_in,  R.anim.pic_right_out);  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }  
    
    //新增
    public void Buy(View v){
    	//判定商品库存
    	if(inventory == 0){
    		Toast.makeText(GoodsDetail_Activity.this, "库存不足，请联系店员！", Toast.LENGTH_LONG).show();
    		return;
    	}
    	//判断是否商品SKU是否存在
    	getSkuId(list_click);//获取sku信息
    	
    	if(!isSku){ //如果该sku没有找到 则商品不存在
    		Toast.makeText(GoodsDetail_Activity.this, "暂无此规格商品，请联系店员！", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	View view = LayoutInflater.from(this).inflate(R.layout.popup_phone, null);
    	showPup(this, view);
    	
    	if (popWindow_setPhone.isShowing()||popWindow_succeed.isShowing()) {
        		popWindow_setPhone.dismiss();
        		popWindow_succeed.dismiss();
        } else {
        	// 背景变暗
              darkview.setVisibility(View.VISIBLE);
        	popWindow_setPhone.showAtLocation(view, Gravity.CENTER, 0, 0); //显示popup
        }
    }  
    
    private void showPup(Context context, final View view) {
    	LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
        final View vPopWindow=inflater.inflate(R.layout.popup_phone, null, false);  
        final View vPopWindow_succeed =inflater.inflate(R.layout.popup_succeed, null, false);  
        //宽300 高300      
        popWindow_setPhone = new PopupWindow(vPopWindow,390,260,false);
        popWindow_succeed = new PopupWindow(vPopWindow_succeed,420,310,true);
        popWindow_setPhone.setFocusable(true);
        final EditText et_phone = (EditText)vPopWindow.findViewById(R.id.et_phone);
        final TextView tv_phone = (TextView)vPopWindow.findViewById(R.id.tv_phone);
        TextView tv_removePhone = (TextView)vPopWindow.findViewById(R.id.tv_removePhone);
        final LinearLayout ll_getphone = (LinearLayout)vPopWindow.findViewById(R.id.ll_getphone);
      //是否超过内设清空记录时间 是则清空
    	Date date = new Date();
    	long nowtime = date.getTime();
    	long oldtime = sp_time.getLong("time", nowtime);
    	if(nowtime - oldtime > Time_phonedel.INTERVAL_TIME){
    		Editor edit = sp_phone.edit();
			edit.putString("phone", "");
			edit.commit();
    	};
    	
        txsting_phone = sp_phone.getString("phone", "");
        
        if(txsting_phone.equals("")){
        	ll_getphone.setVisibility(View.INVISIBLE);
        	et_phone.setVisibility(View.VISIBLE);
        }else{
        	ll_getphone.setVisibility(View.VISIBLE);
        	tv_phone.setText(txsting_phone.substring(0,3) + "****" + txsting_phone.substring(7));
        	et_phone.setVisibility(View.INVISIBLE);
        }
//        p_mark = true;
        final TextView enter = (TextView)vPopWindow.findViewById(R.id.tv_enter);
        final TextView tv_tishi = (TextView)vPopWindow.findViewById(R.id.tv_tishi);
        //清空按钮
        tv_removePhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txsting_phone = "";
				Editor edit = sp_phone.edit();
				edit.putString("phone", "");
				edit.commit();
				ll_getphone.setVisibility(View.INVISIBLE);
				et_phone.setVisibility(View.VISIBLE);
			}
		});
        enter.setOnClickListener(new View.OnClickListener() { 
            @Override
            public void onClick(View v) {
            	if(txsting_phone.equals("")&et_phone.getText().length() != 11){//
                	Toast.makeText(GoodsDetail_Activity.this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
                	return;
                }else if(!txsting_phone.equals("")){
                	cellphone = sp_phone.getString("phone", "");
//                	if(p_mark){
//                		enter.setText("确认");
//                		tv_tishi.setText("请确认手机号码！");
//                		p_mark = false;
//                		return;
//                	}
                }else{
                	cellphone = et_phone.getText().toString();//post属性
                }
        		popWindow_setPhone.dismiss();
            	post();//发送
            	
            	Editor edit = sp_phone.edit();
				edit.putString("phone", cellphone);
				edit.commit();
            	txsting_phone = cellphone;
            	
            	//记录号码成功后保存时间
            	Date date = new Date();
            	long oldtime = date.getTime();
            	Editor edit1 = sp_time.edit();
				edit1.putLong("time", oldtime);
				edit1.commit();
				
            	popWindow_succeed.setFocusable(true);
            	TextView tv_goout = (TextView)vPopWindow_succeed.findViewById(R.id.tv_goout);
            	TextView tv_succeed_price = (TextView)vPopWindow_succeed.findViewById(R.id.tv_succeed_price);
            	tv_succeed_price.setText("￥ " +  String.format("%.2f", num*real_price+ postPrice));
            	tv_goout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						popWindow_succeed.dismiss();
					}
				});
            	popWindow_succeed.showAtLocation(view, Gravity.CENTER, 0, 0);
            	
            	darkview.setVisibility(View.VISIBLE);
            	
            	popWindow_succeed.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                 	// 背景变回初始
                        darkview.setVisibility(View.GONE);
                    }
                });
            }
        });
         
        TextView cancle = (TextView)vPopWindow.findViewById(R.id.tv_cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	popWindow_setPhone.dismiss(); //Close the Pop Window
            }
        });
        
        popWindow_setPhone.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
         	// 背景变回初始
            	if(!popWindow_succeed.isShowing()){
            		darkview.setVisibility(View.GONE);
            	}
            }
        });
	}

	public void post(){
    	new Thread(new Runnable() { 
			@Override
			public void run() {
	             Map<String, String> params = new HashMap<String, String>();
	             params.put("shop_id", shop_id + "");
	             params.put("sku_id", sku_on.get("skuId") + "'");
	             params.put("num", num + "");
	             params.put("cellphone", cellphone);
	             String a = submitPostData(params);
	             System.out.println(a);
			}
			/**
		     * 发送Post请求到服务器
		     * params请求体内容
			 * @return 
		     */
			private String submitPostData(Map<String, String> params) {
				URL url;
		    	byte[] data = getRequestData(params).toString().getBytes();
		        try {      
		        	url = new URL(GoodsPostURLPath.POST_PATH);
		            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
		            httpURLConnection.setConnectTimeout(3000);        //设置连接超时时间
		            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
		            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
		            httpURLConnection.setRequestMethod("POST");    //设置以Post方式提交数据
		            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
		            //设置请求体的类型是文本类型
		            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		            //设置请求体的长度
		            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
		            //获得输出流，向服务器写入数据
		            OutputStream outputStream = httpURLConnection.getOutputStream();
		            outputStream.write(data);
		            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
		            if(response == HttpURLConnection.HTTP_OK) {
		                InputStream inptStream = httpURLConnection.getInputStream();
		                return dealResponseResult(inptStream);                     //处理服务器的响应结果
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        return "";				
			}
			
			/**
		     * 封装请求体信息
		     * 
		     */
			private StringBuffer getRequestData(Map<String, String> params){
				StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
		        try {
		            for(Map.Entry<String, String> entry : params.entrySet()) {
		                stringBuffer.append(entry.getKey())
		                            .append("=")
		                            .append(entry.getValue())
		                            .append("&");
		            }
		            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return stringBuffer;
			}
			
			/**
		     * 处理服务器的响应结果（将输入流转化成字符串）
		     *  inputStream服务器的响应输入流
		     * 
		     */
			private String dealResponseResult(InputStream inputStream) {
				String resultData = null;      //存储处理结果
		        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		        byte[] data = new byte[1024];
		        int len = 0;
		        try {
		            while((len = inputStream.read(data)) != -1) {
		                byteArrayOutputStream.write(data, 0, len);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        resultData = new String(byteArrayOutputStream.toByteArray());    
		        return resultData;
			}
		}).start();
    }
}
