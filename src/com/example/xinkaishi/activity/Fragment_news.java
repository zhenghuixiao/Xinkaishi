package com.example.xinkaishi.activity;

import com.example.xinkaishi.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Fragment_news extends Fragment {
	private View view;
	public static WebView webview_news;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_fragment_news, container, false);
		
		webview_news = (WebView)view.findViewById(R.id.wv_neirong);
		webview_news.getSettings().setJavaScriptEnabled(true);
		webview_news.loadUrl("http://jiankang.baidu.com/?qq-pf-to=pcqq.c2c");
		webview_news.setWebViewClient(new HelloWebViewClient ()); 
		
		
		ViewGroup p = (ViewGroup) view.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
		return view;
	}
	
	//Web视图  
    private class HelloWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
    } 
}
