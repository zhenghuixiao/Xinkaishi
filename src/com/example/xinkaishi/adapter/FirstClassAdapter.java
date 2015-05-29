package com.example.xinkaishi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.example.xinkaishi.R;
import com.example.xinkaishi.bean.MenuList1;
import com.example.xinkaishi.util.Cache;
import com.example.xinkaishi.util.LoadImg;
import com.example.xinkaishi.util.LoadImg.OnLoadImageListener;

/**
 * 一级分类（即左侧菜单）的adapter
 * Created  on 14-12-9.
 */
public class FirstClassAdapter extends BaseAdapter {
    private Context context;
    private List<MenuList1> list;
    private Cache cache;
    private int a = 0; 
    public FirstClassAdapter(Context context, Cache cache, List<MenuList1> list) {
        this.context = context;
        this.list = list;
        this.cache = cache;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView( final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (true) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_left_item, null);
            holder = new ViewHolder();
            holder.picTV = (ImageView)convertView.findViewById(R.id.left_item_pic);
            holder.nameTV = (TextView) convertView.findViewById(R.id.left_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //选中和没选中时，设置不同的颜色
        if (position == selectedPosition){
            convertView.setBackgroundResource(R.color.popup_right_bg);
            holder.nameTV.setTextColor(convertView.getResources().getColor(R.color.main_red));
            LoadImg.onLoadImage(list.get(position).getImg_s(), cache, new OnLoadImageListener() {  //异步加载小图标
                @Override  
                public void OnLoadImage(final Bitmap bitmap, String bitmapPath) {  
                    if(bitmap!=null){ 
                    	holder.picTV.setImageBitmap(bitmap);
                    }  
                }  
            });
            holder.picTV.setImageBitmap(cache.getBitmapFromMemCache(list.get(position).getImg_s()));
        }else{
            convertView.setBackgroundResource(R.drawable.selector_left_normal);
            LoadImg.onLoadImage(list.get(position).getImg(), cache, new OnLoadImageListener() {  //异步加载小图标
                @Override  
                public void OnLoadImage(final Bitmap bitmap, String bitmapPath) {  
                    if(bitmap!=null){ 
                    	holder.picTV.setImageBitmap(bitmap);
                    }  
                }  
            });
        	holder.picTV.setImageBitmap(cache.getBitmapFromMemCache(list.get(position).getImg()));
        }
        holder.nameTV.setText(list.get(position).getName());
        return convertView;
    }

    private int selectedPosition = 0;

    public void setSelectedPosition(int selectedPosition) {
    	 this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private class ViewHolder {
        TextView nameTV;
        ImageView picTV;
    }
}
