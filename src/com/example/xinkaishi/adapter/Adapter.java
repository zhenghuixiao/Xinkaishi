package com.example.xinkaishi.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xinkaishi.R;
import com.example.xinkaishi.activity.Fragment_goods;
import com.example.xinkaishi.util.Cache;
import com.example.xinkaishi.util.LoadImg;
import com.example.xinkaishi.util.LoadImg.OnLoadImageListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class Adapter extends BaseAdapter {  
    private Context context;
    private ArrayList<HashMap<String, Object>> list;  
    private int layoutID;  
    private String flag[];  
    private int ItemIDs[];  
    private Cache cache;
    public Adapter(Context mainActivity, ArrayList<HashMap<String, Object>> list,  
            int layoutID, String flag[], int ItemIDs[],Cache cache) {  
        this.context = mainActivity;
        this.list = list;  
        this.layoutID = layoutID;  
        this.flag = flag;  
        this.ItemIDs = ItemIDs;  
        this.cache = cache;
    }  
    @Override  
    public int getCount() {  
        return list.size();  
    }  
    @Override  
    public Object getItem(int arg0) {  
        return 0;  
    }  
    @Override  
    public long getItemId(int arg0) {  
        return 0;  
    }  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
    	final ViewHolder holder;
    	if(convertView==null){
    		convertView = LayoutInflater.from(context).inflate(layoutID, null);  
    		holder = new ViewHolder();
    		holder.imgView = (ImageView) convertView.findViewById(ItemIDs[0]);
    		holder.tv_name = (TextView) convertView.findViewById(ItemIDs[1]); 
    		holder.tv_pri = (TextView) convertView.findViewById(ItemIDs[2]); 
    		holder.tv_oldpri = (TextView) convertView.findViewById(ItemIDs[3]); 
    		holder.tv_sales = (TextView) convertView.findViewById(ItemIDs[4]); 
            convertView.setTag(holder);
    	}else {
            holder = (ViewHolder) convertView.getTag(); 
        }
    	holder.tv_name.setText(list.get(position).get(flag[1]) + "");
    	holder.tv_pri.setText( String.format("%.2f", (list.get(position).get(flag[2]))));  
    	holder.tv_oldpri.setText( String.format("%.2f", (list.get(position).get(flag[3]))));  //两位小数
    	holder.tv_oldpri.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);  
    	holder.tv_sales.setText( list.get(position).get(flag[4]).toString());  
    	
		LoadImg.onLoadImage((String)list.get(position).get(flag[0]), cache, new OnLoadImageListener() {  //异步加载
            @Override  
            public void OnLoadImage(final Bitmap bitmap, String bitmapPath) {  
                if(bitmap!=null){  
                	holder.imgView.setImageBitmap(bitmap);
                }  
            }  
        });
        return convertView;  
    }  
    class ViewHolder {
    	ImageView imgView;
    	TextView tv_name, tv_pri, tv_oldpri, tv_sales;
    }
}
