package com.example.xinkaishi.custom;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class MyLayout extends ViewGroup {  
	
    public MyLayout(Context context) {  
        super(context);  
    }  
    /** 
     * 控制子控件的换行 
     */  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
    	View childView1= getChildAt(0);  
    	int wid = childView1.getMeasuredWidth();  
        int columns = (r - l) / wid;  
        if (columns < 0) {  
            columns = 1;  
        }  
        int x = 0;  
        int y = 0;  
        int i = 0;  
        int count = getChildCount();  
        for (int j = 0; j < count; j++) {  
            final View childView = getChildAt(j);  
            // 获取子控件Child的宽高  
            int w = childView.getMeasuredWidth();  
            int h = childView.getMeasuredHeight(); 
            // 计算子控件的顶点坐标  
            int left = x ;  
            int top = y ;  
            // 布局子控件  
            childView.layout(left, top, left + w, top + h);  
  
            if (i >= (columns - 1)) {  
            	i = 0;  
                x = 0;  
                y += h;  
            } else {  
                i++;  
                x += w;  
            }  
        }  
    }  
  
    /** 
     * 计算控件及子控件所占区域 
     */  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
    	View child = getChildAt(0);  
        // 获取子控件Child的宽高  
        int w = child.getMeasuredWidth();  
        int h = child.getMeasuredHeight(); 
        // 创建测量参数  
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.UNSPECIFIED);  
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.UNSPECIFIED);  
        // 记录ViewGroup中Child的总个数  
        int count = getChildCount();  
        // 设置子空间Child的宽高  
        for (int i = 0; i < count; i++) {  
            View childView = getChildAt(i);  
            childView.measure(cellWidthSpec, cellHeightSpec);  
        }  
        // 设置容器控件所占区域大小  
        // 注意setMeasuredDimension和resolveSize的用法  
        int a = count/3 + (count%3 >0? 1 : 0);
        setMeasuredDimension(resolveSize(w * count, widthMeasureSpec),  
                resolveSize(h * a, heightMeasureSpec));  
    }  
}  
