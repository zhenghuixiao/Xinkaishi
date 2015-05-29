package com.example.xinkaishi.adapter;

import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xinkaishi.R;
import com.example.xinkaishi.bean.MenuList2;
import com.example.xinkaishi.bean.SecondClassItem;


/**
 * 二级分类（即右侧菜单）的adapter
 * Created on 14-12-9.
 */
public class SecondClassAdapter extends BaseAdapter{
    private Context context;
    private List<MenuList2> list;

    public SecondClassAdapter(Context context, List<MenuList2> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_right_item, null);
            holder.nameTV = (TextView) convertView.findViewById(R.id.right_item_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == selectedPosition){
        	holder.nameTV.setTextColor(convertView.getResources().getColor(R.color.main_red));
        }else{
        	holder.nameTV.setTextColor(convertView.getResources().getColor(R.color.black));
        	convertView.setBackgroundResource(R.drawable.selector_right_normal);
        }
        holder.nameTV.setText(list.get(position).getName());
        
        return convertView;
    }
    private int selectedPosition = -1;
    
    public void setSelectedPosition(int selectedPosition) {
   	 this.selectedPosition = selectedPosition;
   }

   public int getSelectedPosition() {
       return selectedPosition;
   }
   
    private class ViewHolder{
        TextView nameTV;
    }

}
