package com.example.xinkaishi.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class LoadImg {
	private static ExecutorService executorService = Executors.newFixedThreadPool(5); 
	public static void onLoadImage(final String bitmapUrl,final Cache cache, final OnLoadImageListener onLoadImageListener){  
        final Handler handler = new Handler(){  
            public void handleMessage(Message msg){  
                onLoadImageListener.OnLoadImage((Bitmap) msg.obj, null);  
            }  
    };  
    
    executorService.submit(new Runnable(){  
    		Message msg = new Message(); 
            @Override  
            public void run() {  
            	URL imageUrl ;
            	if(cache.getBitmapFromMemCache(bitmapUrl) == null){
                    try {  
                    	imageUrl = new URL(bitmapUrl);  
                        HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();  
                        InputStream inputStream = conn.getInputStream();  
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);  
                        cache.addBitmapToMemoryCache(bitmapUrl, bitmap);
                        msg.obj = bitmap;  
                        handler.sendMessage(msg);  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
	            }else{
	            	msg.obj = cache.getBitmapFromMemCache(bitmapUrl);
	            	handler.sendMessage(msg);  
	            }  
            }  
              
        });  
  
    }  
    public interface OnLoadImageListener{  
        public void OnLoadImage(Bitmap bitmap,String bitmapPath);  
    } 
}
