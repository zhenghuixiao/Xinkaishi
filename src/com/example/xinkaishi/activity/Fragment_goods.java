package com.example.xinkaishi.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.PaintDrawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinkaishi.R;
import com.example.xinkaishi.adapter.Adapter;
import com.example.xinkaishi.adapter.FirstClassAdapter;
import com.example.xinkaishi.adapter.SecondClassAdapter;
import com.example.xinkaishi.bean.Brand;
import com.example.xinkaishi.bean.GoodsURLPath;
import com.example.xinkaishi.bean.MenuList1;
import com.example.xinkaishi.bean.MenuList2;
import com.example.xinkaishi.bean.MenuURLpath;
import com.example.xinkaishi.util.Cache;
import com.example.xinkaishi.util.DataAnalysis;
import com.example.xinkaishi.util.ScreenUtils;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class Fragment_goods extends Fragment {
    private RelativeLayout rl_Menulist, rl_button_default, rl_button_sales, rl_button_price, rl_backAllMenu;// 菜单按钮  排序按钮
    private TextView tv_pic_sales, tv_pic_ascprice, tv_pic_descprice, tv_shopId; //排序的3个图标  ID  原价格
    private int mark = 1;//价格排序标记
    private int page = 2; //加载page
    private ArrayList<HashMap<String,Object>> arrayList, arrayList_tuijian;
    private ArrayList<HashMap<String,Object>> arrayList_new;
    private Handler handler, brand_handler;
    private Adapter adapter;//图片适配器
    private Cache cache;
    private PullToRefreshGridView gridview; //上下拉view
    
    private TextView tv_menu;
    private HorizontalScrollView hs;
    private LinearLayout ll_menu_grands, ll;
    private List<MenuList1> menulist1;//左侧一级分类的数据
    private List<MenuList2> menulist2;//右侧二级分类的数据
    private List<Brand> brandlist;
    private int leftNum = 0, rightNum = -1;//选中分类的位置 
    private PopupWindow popupWindow;
    private ListView leftLV, rightLV; //左侧和右侧两个ListView
    private View darkView; //弹出PopupWindow时背景变暗
    private Animation animIn, animOut; //弹出PopupWindow时，背景变暗的动画
    private View view;
    private boolean isnull;
    
    //账户信息
    private int shop_id;
    private String account_balance; // 余额
    private String shop_name;// 店名
    // 排序标记 
    private int categoryId;  //类别ID
    private int brandId;  //品牌ID
    private boolean isOnclick_default;
    private boolean isOnclick_sales;
    private int status;  //默认1  销量2  价格↑3  价格↓4
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_fragment_goods, container, false);
		
		initElement();//这里初始化标记参数 账户信息
		initMenu(); //初始化类别数据
		initGoods(GoodsURLPath.urlPath + "&shop_id=" + shop_id);//初始化商品数据
		initUI();
		initRE();
		
		ViewGroup p = (ViewGroup) view.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
		return view;
	}

	private void initRE() {
		rl_backAllMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rl_backAllMenu.setVisibility(View.INVISIBLE);
				ll.removeAllViews();
				ll_menu_grands.removeAllViews();
				hs.setVisibility(View.GONE);
				tv_menu.setText("全部分类");
				leftNum = 0;
				rightNum = -1;
				initElement();//这里初始化标记参数 账户信息
				initButton();
				rl_button_default.setBackgroundResource(R.color.button_onclick);
				initGoods(GoodsURLPath.urlPath + "&shop_id=" + shop_id);//初始化商品数据
			}
		});
	}

	private void initElement() {
		// 排序标记 
	    categoryId = -1;  //类别ID
	    brandId = -1; //品牌ID
	    isOnclick_default = false;
	    isOnclick_sales = true;
	    status = 1;  //默认1  销量2  价格↑3  价格↓4
	    
	    //账户信息 初始化店名
	    SharedPreferences sp = getActivity().getSharedPreferences("sp_name", Context.MODE_PRIVATE);
		shop_id = sp.getInt("shop_id", 1);
		System.out.println(shop_id + "");
		account_balance = sp.getString("account_balance", 0 + "");
		shop_name = sp.getString("shop_name", "新开始");
		tv_shopId = (TextView)getActivity().findViewById(R.id.tv_shopId);
		tv_shopId.setText(shop_name + "");
		
	}

	private void initUI() {
		handler  = new MyHandler();
		cache = new Cache();
		rl_backAllMenu = (RelativeLayout)view.findViewById(R.id.rl_backAllMenu);
		rl_button_default = (RelativeLayout)view.findViewById(R.id.rl_button_default);
		rl_button_sales = (RelativeLayout)view.findViewById(R.id.rl_button_sales);
		rl_button_price = (RelativeLayout)view.findViewById(R.id.rl_button_price);
		tv_pic_sales = (TextView)view.findViewById(R.id.tv_pic_sales);
		tv_pic_ascprice = (TextView)view.findViewById(R.id.tv_pic_ascprice);
		tv_pic_descprice = (TextView)view.findViewById(R.id.tv_pic_descprice);
		ll_menu_grands = (LinearLayout)view.findViewById(R.id.ll_menu_grands);
		hs = (HorizontalScrollView)view.findViewById(R.id.hs_menu_grands);
		ll = new LinearLayout(getActivity());
		
		menuOnclick();
		sortDefault(); //按默认
		sortAaleAmount();// 按销量排序
		sortPrice(); // 按价格排序
		
	}

	class MyHandler extends Handler{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg){
			arrayList = (ArrayList<HashMap<String,Object>>)msg.obj;
		    gridview = (PullToRefreshGridView)view.findViewById(R.id.gv);
		    // 设置PullToRefreshListView的模式  上拉 下拉 或两个
		    gridview.setMode(Mode.PULL_UP_TO_REFRESH);
    		// 设置PullRefreshListView上提加载时的加载提示
	        gridview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载...");
	        gridview.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中...");
	        gridview.getLoadingLayoutProxy(false, true).setReleaseLabel("松开加载更多...");

    		// 设置PullRefreshListView下拉加载时的加载提示
//	        gridview.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
//	        gridview.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
//	        gridview.getLoadingLayoutProxy(true, false).setReleaseLabel("松开刷新列表...");
	        
	        setEventListener();  //滑动监听
	        
			adapter = new Adapter(getActivity(),arrayList,R.layout.listview_own_item,
								new String[] { "img","name","price","originalPrice","saleAmount"},
								new int[] { R.id.iv_pic,R.id.tv_ownName,R.id.tv_pri,R.id.tv_oldpri,R.id.tv_sales},cache);
			gridview.setAdapter(adapter);
           
         //  添加点击   
           gridview.setOnItemClickListener(new OnItemClickListener() {    
               public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,   
                       long arg3) {  
            	   String goodId = arrayList.get(arg2).get("id").toString();
            	   Intent intent = new Intent(getActivity(),GoodsDetail_Activity.class);
            	   intent.putExtra("id", goodId);
            	   startActivity(intent);
            	   getActivity().overridePendingTransition(R.anim.pic_left_in,R.anim.pic_left_out); //切换动画
               }   
           });
		}
	}
	
	/**
	 * 设置监听
	 */
	@SuppressWarnings("unchecked")
	private void setEventListener() {
		gridview.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// 下拉刷新触发的事件
				//获取格式化的时间
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				//	更新LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				Log.e("test", "*************************************************下拉");
				new GetDataTask().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// 上拉加载触发的事件
				Log.e("test", "*************************************************上拉");
				new GetDataTask().execute();
			}
		});
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, ArrayList<HashMap<String,Object>>> {

		@Override
		protected ArrayList<HashMap<String,Object>> doInBackground(Void... params) {
			String url = null;
			try {
				//列表下拉的url
				switch(status){
					case 1: url = GoodsURLPath.urlPath_jiazai + page +"&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
						break;
					case 2: url = GoodsURLPath.urlPath_jiazai + page + "&order=sale_amount" + "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
						break;
					case 3: url = GoodsURLPath.urlPath_jiazai + page + "&order=price_asc" + "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
						break;
					case 4: url = GoodsURLPath.urlPath_jiazai + page + "&order=price" + "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
						break;
				}
				arrayList_new = getGood(url);
				arrayList.addAll(getGood(url));  //加入新的数据
				page ++;
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return arrayList_new;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String,Object>> result) {
			if(gridview.isHeaderShown()){
				initGoods(GoodsURLPath.urlPath);
			}else if(gridview.isFooterShown()){
		      	}
			adapter.notifyDataSetChanged();
			// 调用刷新完成
			gridview.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	/**
     * 解析商品信息
*/
	public void initGoods(final String url){
		new Thread(new Runnable()  
	    {  
	        @Override  
	        public void run()  
	        {  
				try {
					Log.e("获取数据线程开始", "***********************************************************初始化商品开始");
					Message msg = Message.obtain();
					msg.obj = getGood(url);
					arrayList = getGood(url);
					handler.sendMessage(msg);
					Log.e("获取数据线程开始", "***********************************************************初始化商品结束");
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
			JSONObject brand;
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

	/**
     * 解析分类信息
*/
	public void initMenu(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.e("test", "****************************************初始化菜单线程开始");
					JSONObject dataJson = new JSONObject(DataAnalysis.readParse(MenuURLpath.urlPath + "shop_id=" + shop_id));
					JSONArray data = dataJson.getJSONArray("data");
					Log.e("test", data.length()+ "");
					//判断是否为空 
					if(data.length()<1){
						isnull = true;
						return;
					}
					isnull = false;
					JSONArray _children, brands;
					JSONObject info1, info2,info3;
					menulist1 = new ArrayList<MenuList1>();
					for(int i = 0; i < data.length(); i ++){//大类
						MenuList1 menu1 = new MenuList1();
						menulist2 = new ArrayList<MenuList2>();
						info1 = data.getJSONObject(i);
						_children = info1.getJSONArray("_children");
						menu1.setId( info1.getInt("id"));
						menu1.setName( info1.getString("name"));
						menu1.setImg_s( info1.getString("img_s"));
						menu1.setImg(info1.getString("img"));
						for(int a = 0; a < _children.length(); a ++){//子类
							MenuList2 menu2 = new MenuList2();
//							brandlist = new ArrayList<Brand>();
							info2 = _children.getJSONObject(a);
//							brands = info2.getJSONArray("brands");
							menu2.setId(info2.getInt("id"));
							menu2.setName(info2.getString("name"));
							//品牌已去掉
//							for(int b = 0; b < brands.length(); b ++){
//								info3 = brands.getJSONObject(b);
//								Brand brand = new Brand();
//								brand.setId(info3.getInt("id"));
//								brand.setName(info3.getString("name"));
//								brandlist.add(brand);
//							}
//							menu2.setBrand(brandlist);
							menulist2.add(menu2);
						}
						menu1.setMenuList2(menulist2);
						menulist1.add(menu1);
					}
					Log.e("test", isnull + "****************************************初始化菜单线程结束");
				}  catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
   /**
    * 初始化PopupWindow
*/
   private void initPopup() {
		popupWindow = new PopupWindow(getActivity());
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_layout, null);
       leftLV = (ListView) view.findViewById(R.id.pop_listview_left);
       rightLV = (ListView) view.findViewById(R.id.pop_listview_right);
       popupWindow.setContentView(view);
       popupWindow.setBackgroundDrawable(new PaintDrawable());
       popupWindow.setFocusable(true);
       popupWindow.setOutsideTouchable(true);
       popupWindow.setHeight(ScreenUtils.getScreenH(getActivity())-170); //设置popup长宽  170为上方宽度
       popupWindow.setWidth(ScreenUtils.getScreenW(getActivity())*4 / 9);
       popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
           @Override
           public void onDismiss() {
        	// 背景变回初始
               darkView.startAnimation(animOut);
               darkView.setVisibility(View.GONE);
           }
       });
		
       //加载一级分类 
       final FirstClassAdapter firstAdapter = new FirstClassAdapter(getActivity(), cache,menulist1);
       leftLV.setAdapter(firstAdapter);
       leftLV.setBackgroundColor(getResources().getColor(R.color.gray));
       if(leftNum != 0){
    	   leftLV.setSelection(leftNum);
           firstAdapter.setSelectedPosition(leftNum);
           firstAdapter.notifyDataSetChanged();
       }
       //加载左侧第一行对应右侧二级分类
       menulist2 = new ArrayList<MenuList2>();
       menulist2.addAll(menulist1.get(leftNum).getMenuList2());
       final SecondClassAdapter secondAdapter = new SecondClassAdapter(getActivity(), menulist2);
       rightLV.setAdapter(secondAdapter);
       if(rightNum != -1){
           rightLV.setSelection(rightNum);
           secondAdapter.setSelectedPosition(rightNum);
           secondAdapter.notifyDataSetChanged();
       }
       //左侧ListView点击事件
       leftLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //二级数据
               List<MenuList2> list2 = menulist1.get(position).getMenuList2();
               //如果没有二级类，则直接跳转
               if (list2 == null || list2.size() == 0) {
                   popupWindow.dismiss();

                   int firstId = menulist1.get(position).getId();
                   String selectedName = menulist1.get(position).getName();
                   handleResult(firstId, -1, selectedName);
                   return;
               }

               FirstClassAdapter adapter = (FirstClassAdapter) (parent.getAdapter());
               //如果上次点击的就是这一个item，则不进行任何操作
               if (adapter.getSelectedPosition() == position){
            	   Log.e("test", "****************************************************不进行任何操作");
                   return;
               }
               menulist1.get(position).setLeftNum(leftNum);
               leftNum = position;//获取位置
               //根据左侧一级分类选中情况，更新背景色
               adapter.setSelectedPosition(position);
               adapter.notifyDataSetChanged();
               //显示右侧二级分类
               updateSecondListView(list2, secondAdapter);
           }
       });

       //右侧ListView点击事件
       rightLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               //关闭popupWindow，显示用户选择的分类
               popupWindow.dismiss();
               rightNum = position;//获取位置
               int firstPosition = firstAdapter.getSelectedPosition();
               int firstId = menulist1.get(firstPosition).getId();
               int secondId = menulist1.get(firstPosition).getMenuList2().get(position).getId();
               String selectedName = menulist1.get(firstPosition).getMenuList2().get(position).getName();
               handleResult(firstId, secondId, selectedName);
           }
       });
	}
   
	//刷新右侧ListView
   private void updateSecondListView(List<MenuList2> list2,
                                     SecondClassAdapter secondAdapter) {
	   	menulist2.clear();
	   	Log.e("test", "****************************************************1");
	   	menulist2.addAll(list2);
	   	rightLV.setSelection(0);
        secondAdapter.setSelectedPosition(-1);
       secondAdapter.notifyDataSetChanged();
   }
   
       
   /**
	 * 按默认排序
	 */
    public void sortDefault(){
    	rl_button_default.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isOnclick_default){
					initButton();
					rl_button_default.setBackgroundResource(R.color.button_onclick);
					
					String url = GoodsURLPath.urlPath + "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
					initGoods(url);
					isOnclick_default = false;
				}
		    	isOnclick_sales = true;
		    	status = 1;
		    	page = 2;
		    	mark = 1;
			}
		});
    }
   /**
	 * 按销量排序
	 */
	public void sortAaleAmount() {
		rl_button_sales.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isOnclick_sales){
					initButton();
					rl_button_sales.setBackgroundResource(R.color.button_onclick);
					tv_pic_sales.setBackgroundResource(R.drawable.filter_sales_focus);
					
					String url = GoodsURLPath.urlPath_sales + "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id;
					initGoods(url);
					isOnclick_sales = false;
				}
				isOnclick_default = true;
				status = 2;
				page = 2;
				mark = 1;
			}
		});
	}
	
	/**
	 * 按价格排序
	 */
	public void sortPrice() {
		rl_button_price.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initButton();
				rl_button_price.setBackgroundResource(R.color.button_onclick);
				if(mark == 1){
					tv_pic_ascprice.setBackgroundResource(R.drawable.filter_price_asc_focus);
				}else{
					tv_pic_descprice.setBackgroundResource(R.drawable.filter_price_desc_focus);
				}
				String url = mark == 1?(GoodsURLPath.urlPath_priceToHigh + "&category_id=" + categoryId+ "&brand_id=" + brandId + "&shop_id=" + shop_id): 
															(GoodsURLPath.urlPath_priceToLow+ "&category_id=" + categoryId + "&brand_id=" + brandId + "&shop_id=" + shop_id);
				initGoods(url);
				mark = mark == 1? 0: 1;
				isOnclick_default = true;
				isOnclick_sales = true;
				status = status ==3?4 : 3;
				page = 2;
			}
		});
		
	}
	/**
	 * 初始化排序按钮的一些信息
	 */
	public void initButton(){
		rl_button_sales.setBackgroundResource(R.drawable.textview_shape);
		rl_button_default.setBackgroundResource(R.drawable.textview_shape);
		rl_button_price.setBackgroundResource(R.drawable.textview_shape);
		tv_pic_sales.setBackgroundResource(R.drawable.filter_sales_unfocus);
		tv_pic_ascprice.setBackgroundResource(R.drawable.filter_price_asc_unfocus);
		tv_pic_descprice.setBackgroundResource(R.drawable.filter_price_desc_unfocus);
	}
	/**
	 * 菜单分类触发事件
	 * 
	 */
	public void menuOnclick(){
		   rl_Menulist = (RelativeLayout)view.findViewById(R.id.rl_Menulist);
		   tv_menu = (TextView)view.findViewById(R.id.tv_fenlei);
	       darkView = view.findViewById(R.id.detail_first_darkview);
	       animIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_anim);
	       animOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_anim);
	       if(isnull){
	    	   return;
	       }else{
		       rl_Menulist.setOnClickListener(new OnClickListener() {
				@Override
					public void onClick(View v) {
					    if(isnull){
				    	   return;
				        }
						initPopup();//初始化Popup
						if (popupWindow.isShowing()) {
				            popupWindow.dismiss();
				        } else {
				            popupWindow.showAsDropDown(view.findViewById(R.id.detail_first_div_line));
				            popupWindow.setAnimationStyle(-1);
				           // 背景变暗
				            darkView.startAnimation(animIn);
				            darkView.setVisibility(View.VISIBLE);
				        }
					}
		       });
	       }
	}
	
	/**
	 * 适配器
	 */
	public void gridAdapter(ArrayList<HashMap<String,Object>> list){
		adapter = new Adapter(getActivity(),list,R.layout.listview_own_item,
                new String[] { "img","name","price","originalPrice","saleAmount"},
                new int[] { R.id.iv_pic,R.id.tv_ownName,R.id.tv_pri,R.id.tv_oldpri,R.id.tv_sales},cache);
        gridview.setAdapter(adapter);
	}
	
    //处理菜单点击结果
    private void handleResult(int firstId, int secondId, String selectedName){
    	rl_backAllMenu.setVisibility(View.VISIBLE);
//    	List<Brand> brand = menulist1.get(leftNum).getMenuList2().get(rightNum).getBrand();//获取该分类下的所有品牌
    	
		String url = (GoodsURLPath.urlPath_menu + secondId + "&shop_id=" + shop_id);
		
		initGoods(url);//加载商品列表
		initBrands(url, secondId);//加载品牌选项
		
		brand_handler = new BrandHandler();
		
		categoryId = secondId;
		tv_menu.setText(selectedName);
        initButton();//重置排序
        rl_button_default.setBackgroundResource(R.color.button_onclick);//恢复默认
        //重置标记，ID
        mark = 1;
        status = 1;
        page = 2; 
        brandId = -1;
    }
    
    class BrandHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			List<Brand> brands = (List<Brand>)msg.obj;
			int secondId = msg.arg1;
			setBrand(brands, secondId);//生成品牌控件
	        
			super.handleMessage(msg);
		}
    }
    private void initBrands(final String url, final int secondId) {
    	new Thread(new Runnable()  
	    {  
	        @Override  
	        public void run()  
	        {  
				try {
					Log.e("获取数据线程开始", "***********************************************************加载品牌分类开始");
					JSONObject dataJson = new JSONObject(DataAnalysis.readParse(url));//获取json对象
					JSONObject data=dataJson.getJSONObject("data");
					JSONArray brand=data.getJSONArray("brands");
					JSONObject info;
					List<Brand> brands = new ArrayList<Brand>();
					for (int i = 0; i < brand.length(); i++) {
						Brand br = new Brand();
						info=brand.getJSONObject(i);
						br.setId(info.getInt("id")); 
						br.setName(info.getString("name")); 
						brands.add(br);
					}
					Message msg = Message.obtain();
					msg.obj = brands;
					msg.arg1 = secondId;
					brand_handler.sendMessage(msg);
					Log.e("获取数据线程开始", "***********************************************************加载品牌分类结束");
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }).start();
	}
	/**
	 * 生成品牌选项
	 */
	private void setBrand(List<Brand> brand, int secondId) {
		hs.setVisibility(View.VISIBLE);
		ll_menu_grands.removeAllViews();
		ll_menu_grands.addView(ll);
		ll.removeAllViews();
		final List<TextView> tvAll = new ArrayList<TextView>();
		for(int a = -1; a < brand.size(); a++){ //a = -1 , -1为第一栏默认的全部品牌
			final TextView tv = new TextView(getActivity());
	    	LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    	lp.topMargin = 12;
	    	lp.leftMargin = a == -1? 40: 60;
	    	if((a + 1) == brand.size()){
	    		lp.rightMargin = 40;
	    	}
	    	tv.setGravity(Gravity.CENTER);
	    	tv.setTextSize(25);
	    	tv.setTextColor(getResources().getColor(android.R.color.black));
			tv.setLayoutParams(lp); 
			if(a == -1){
				tv.setText("全部商品");
				tv.setTextColor(getResources().getColor(R.color.main_red));
			}else{
				tv.setText(brand.get(a).getName());
			}
	    	tv.setClickable(true);
	    	ll.addView(tv);
	    	tvAll.add(tv);
	    	final String url;
	    	final int brandID = a == -1? -1: (Integer)brand.get(a).getId();//品牌ID
	    	if(a == -1){
	    		url = (GoodsURLPath.urlPath_menu + secondId + "&shop_id=" + shop_id);
	    	}else{
	    		url = (GoodsURLPath.urlPath_menu + secondId + "&brand_id=" + brand.get(a).getId() + "&shop_id=" + shop_id);
	    	}
	    	tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					brandId = brandID;
					initButton();//重置排序
					for (TextView textView : tvAll) {
						textView.setTextColor(getResources().getColor(R.color.black));
					}
					tv.setTextColor(getResources().getColor(R.color.main_red));
			        rl_button_default.setBackgroundResource(R.color.button_onclick);//恢复默认
			        isOnclick_sales = true;
			        mark = 1;
			        status = 1;
			        page = 2; 
					initGoods(url);
				}
			});
		}
	}
}
