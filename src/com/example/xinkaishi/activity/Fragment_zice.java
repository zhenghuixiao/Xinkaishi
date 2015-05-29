package com.example.xinkaishi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinkaishi.R;
import com.example.xinkaishi.adapter.ZiceAdapter;
import com.example.xinkaishi.bean.Check;

public class Fragment_zice extends DialogFragment {
	private View view;
	private ListView listview;
	private ArrayList<HashMap<String,Object>> list_group;
	private Check check;
	private TextView tv_enter, tv_qs_title, tv_qs_subtitle;
	private TextView tv_yes, tv_tips, tv_testResult, tv_title;
	private String question[];
	private String tips[];
	private String message;
	private View blackview;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_fragment_ziche, container, false);
		
		initView();
		LVadapter();
		initMain();
		
		ViewGroup p = (ViewGroup) view.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
		
		return view;
	}
	private void initView() {
		tv_enter = (TextView)view.findViewById(R.id.tv_enter);
		listview = (ListView)view.findViewById(R.id.listview);
		tv_qs_title = (TextView)view.findViewById(R.id.tv_qs_title);
		tv_qs_subtitle = (TextView)view.findViewById(R.id.tv_qs_subtitle);
		blackview = view.findViewById(R.id.blackview);
		list_group = new ArrayList<HashMap<String,Object>>();
		check = new Check();
		for(int a = 0; a < 12; a ++){
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("fenshu", 0);
			hm.put("count", 0);
			check.getList().add(hm);
			
			check.getHm().put(a + "", 0);
		}
		question = new String[12];
		question[0] = "1、经常打哈欠吗？";
		question[1] = "2、会失眠吗？";
		question[2] = "3、喜欢把腿放在高处吗？";
		question[3] = "4、星期天的晚上会有上班恐惧吗？";
		question[4] = "5、愿意和上级和熟人见面。";
		question[5] = "6、晚上能睡多晚就多晚。";
		question[6] = "7、常坐着发愣发呆。";
		question[7] = "8、楼梯时时常绊到脚。";
		question[8] = "9、不是很渴就想不到去喝水。";
		question[9] = "10、也想不起朋友的名字，或者到嘴边的地名。";
		question[10] = "11、忘了体重突然上升或下降，觉得无所谓。";
		question[11] = "12、一有风吹草动，肚子就不舒服。";
		
		tips = new String[3];
		tips[0] = "早春时期，为冬春交换之时，气温仍然寒冷，消耗的热量较多，宜进食偏于温热的食物。饮食选择热量较高的主食，并补充足够的蛋白质。";
		tips[1] = "春季中期，为天气变化较大之时，气温骤冷骤热，变化较大，可以参照早春时期的饮食进行。在气温较高时可增加青菜的食量，减少肉类的食用。";
		tips[2] = "春季晚期，为春夏交换之时，气温偏热，所以宜于进食清淡的食物。饮食原则为选择清淡的食物，并注意补充足够维生素，如饮食中应适当增加青菜。";

		//		春季饮食宜忌生冷油(油食品)腻之品，传统医学还认为春季为肝气旺盛之时，不宜多食酸味食品，以免使肝气过盛而损害脾胃。";
		
		for(int a = 0; a < 12; a ++){
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("title", question[a]);
			hm.put("always", "经常");
			hm.put("ouer", "偶尔");
			hm.put("little", "很少");
			list_group.add(hm);
		}
	}

	private void LVadapter() {
		ZiceAdapter adapter = new ZiceAdapter(getActivity(), list_group, R.layout.listview_zice, new String[]{"title", "always", "ouer", "little"}, new int[]{R.id.tv_zice, R.id.rg_zice, R.id.rb_always, R.id.rb_ouer, R.id.rb_little}, check);
		listview.setAdapter(adapter);
	}
	

	private void initMain() { 
		tv_qs_title.setText("亚健康测试题");
		tv_qs_subtitle.setText("关于健康状况的调查，" +
				"发现中国有六成的人正处于亚健康。在现今这种充满压力的社会，" +
				"太容易造成心理负担了！下面一套测试题，不妨测一测，" +
				"看看自己亚健康状态到了什么程度，需不需要检查治疗？");
		tv_enter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(check.getCount()<12){
//					showPoint();
					Toast toast = Toast.makeText(getActivity(),
						     "请选择完剩余题目！", Toast.LENGTH_SHORT);
						   toast.setGravity(Gravity.CENTER, -290, -50);
						   toast.show();
				}else{
					showPopupWindow();
				}
			}
		});
	}
	public void showPoint(){
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
        final View vPopWindow=inflater.inflate(R.layout.popup_zice, null, false);  
        //宽300 高300       
        final PopupWindow popWindow = new PopupWindow(vPopWindow,300,200,true);
        tv_yes = (TextView)vPopWindow.findViewById(R.id.tv_yes);
        tv_testResult = (TextView)vPopWindow.findViewById(R.id.tv_testResult);
        tv_tips = (TextView)vPopWindow.findViewById(R.id.tv_tips);
        tv_title =(TextView)vPopWindow.findViewById(R.id.tv_title);
        tv_tips.setText("");
        tv_title.setText("答题未完");
        tv_testResult.setText("部分题目未选择!");
        tv_yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popWindow.dismiss();
				blackview.setVisibility(View.GONE);
			}
		});
        popWindow.showAtLocation(vPopWindow, Gravity.CENTER, -290, -50);
        blackview.setVisibility(View.VISIBLE);//暗色背景
	}
	
	public void showPopupWindow(){
//	        	42-60分你的健康状态比较糟糕，建议马上去医院查查。
//		　　24-41分你的健康状况开始令人担忧，以后要注意了（我已经是这个了）。
//		　　23-12分你的健康状况良好，继续保持。
    	LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);     
        final View vPopWindow=inflater.inflate(R.layout.popup_zice, null, false);  
        //宽300 高300       
        final PopupWindow popWindow = new PopupWindow(vPopWindow,450,300,true);
        tv_yes = (TextView)vPopWindow.findViewById(R.id.tv_yes);
        tv_testResult = (TextView)vPopWindow.findViewById(R.id.tv_testResult);
        tv_tips = (TextView)vPopWindow.findViewById(R.id.tv_tips);
        Random random = new Random();
        tv_tips.setText("小贴士: " + tips[random.nextInt(3)]);
        
        message = null;
		if(check.getNum() < 24){
			message = "你的健康状况相对良好，请继续保持。";
			tv_testResult.setTextColor(getResources().getColor(R.color.tv_green));
		}else if (check.getNum() < 42){
			message = "你的健康状况开始令人担忧，以后要注意了。";
			tv_testResult.setTextColor(getResources().getColor(R.color.tv_yellow));
		}else{
			message = "你的健康状态比较糟糕，建议马上去医院查查。";
			tv_testResult.setTextColor(getResources().getColor(R.color.main_red));
		}
		
        tv_testResult.setText(message);
        
        
        tv_yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popWindow.dismiss();
				blackview.setVisibility(View.GONE);
				for(int a = 0; a < 12; a ++){
	    			check.getList().get(a).put("fenshu", 0);
	    			check.getList().get(a).put("count", 0);
	    			check.getHm().put(a + "", 0);
	    		}
	            check.setCount(0);
	            LVadapter();
			}
		});
        popWindow.showAtLocation(vPopWindow, Gravity.CENTER, -300, -50);
        blackview.setVisibility(View.VISIBLE);//暗色背景
	}
}
