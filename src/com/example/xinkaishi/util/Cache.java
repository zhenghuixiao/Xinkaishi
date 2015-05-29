package com.example.xinkaishi.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class Cache {
	  int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    int cacheSize = maxMemory / 8;

	     LruCache<String,Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	    
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	//当要显示图片时，要调用以下方法判断是否存在缓存中，如果不存在再去下载。
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
}
