package com.example.xinkaishi.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.xinkaishi.bean.Check;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ZiceAdapter extends BaseAdapter{
	private Context context;
    private ArrayList<HashMap<String, Object>> list;
    private int layoutID;  
    private String flag[];  
    private int ItemIDs[];
    private Check check;
 // 标记用户当前选择的那一个作家
    private int index = -1;
	public ZiceAdapter(Context context, ArrayList<HashMap<String, Object>> list, int layoutID, String flag[], int ItemIDs[], Check check){
		this.context = context;
        this.list = list;  
		this.layoutID = layoutID;  
        this.flag = flag;  
        this.ItemIDs = ItemIDs; 
        this.check = check;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
    	if(true){
    		convertView = LayoutInflater.from(context).inflate(layoutID, null);  
    		holder = new ViewHolder();
    		holder.tv_zice =  (TextView) convertView.findViewById(ItemIDs[0]);
    		holder.group = (RadioGroup) convertView.findViewById(ItemIDs[1]);
    		holder.rb_always = (RadioButton) convertView.findViewById(ItemIDs[2]); 
    		holder.rb_ouer = (RadioButton) convertView.findViewById(ItemIDs[3]); 
    		holder.rb_little = (RadioButton) convertView.findViewById(ItemIDs[4]); 
            convertView.setTag(holder);
    	}else {
            holder = (ViewHolder) convertView.getTag(); 
        }
    	holder.tv_zice.setText(list.get(position).get(flag[0]) + "");
    	holder.rb_always.setText(list.get(position).get(flag[1]) + "");
    	holder.rb_ouer.setText( list.get(position).get(flag[2]) + "");  
    	holder.rb_little.setText( list.get(position).get(flag[3]) + "");  
    	holder.group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == ItemIDs[2]){
					check.getList().get(position).put("fenshu", 5);
					check.getHm().put(position + "", 5);
				}else if(checkedId == ItemIDs[3]){
					check.getList().get(position).put("fenshu", 3);
					check.getHm().put(position + "", 3);
				}else if(checkedId == ItemIDs[4]){
					check.getList().get(position).put("fenshu", 1);
					check.getHm().put(position + "", 1);
				}
				check.getList().get(position).put("count", 1);
				int num = 0, count = 0;
				for(int a = 0; a < 12; a ++){
					num = num + (Integer)check.getList().get(a).get("fenshu");
					count = count + (Integer)check.getList().get(a).get("count");
				}
				check.setNum(num);
				check.setCount(count);
			}
		});
    	switch((Integer)check.getHm().get(position + "")){
    		case 1:
    			holder.rb_always.setChecked(false);
    			holder.rb_ouer.setChecked(false);
    			holder.rb_little.setChecked(true);
    			break;
    		case 3:
    			holder.rb_always.setChecked(false);
    			holder.rb_ouer.setChecked(true);
    			holder.rb_little.setChecked(false);
    			break;
    		case 5:
    			holder.rb_always.setChecked(true);
    			holder.rb_ouer.setChecked(false);
    			holder.rb_little.setChecked(false);
    			break;
    		case 0:
    			holder.rb_always.setChecked(false);
    			holder.rb_ouer.setChecked(false);
    			holder.rb_little.setChecked(false);
    			break;
    			
    	}
		return convertView;
	}
	class ViewHolder {
		RadioGroup group;
		TextView tv_zice;
		RadioButton rb_always, rb_ouer, rb_little;
    }

}
