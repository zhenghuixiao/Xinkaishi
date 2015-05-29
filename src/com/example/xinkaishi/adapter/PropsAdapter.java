package com.example.xinkaishi.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PropsAdapter extends BaseAdapter{
	private LayoutInflater mInflater;  
    private ArrayList<HashMap<String, Object>> list;  
    private int layoutID;  
    private int ItemIDs[];  
    private TextView tv_name, tv_value;
    public PropsAdapter(Context context, ArrayList<HashMap<String, Object>> list,  
            int layoutID, String flag[], int ItemIDs[]) {  
        this.mInflater = LayoutInflater.from(context);  
        this.list = list;  
        this.layoutID = layoutID;  
        this.ItemIDs = ItemIDs;  
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
    	if(convertView==null){
    		convertView = mInflater.inflate(layoutID, null);  
    	}
    	tv_name = (TextView) convertView.findViewById(ItemIDs[0]); 
    	tv_value = (TextView) convertView.findViewById(ItemIDs[1]); 
    	tv_name.setText(list.get(position).get("name") + "");//品牌的显示
    	tv_value.setText(list.get(position).get("value") + "");  
        return convertView;  
    }  
}
