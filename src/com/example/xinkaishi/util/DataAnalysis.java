package com.example.xinkaishi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DataAnalysis {
	/**
     * 从指定的URL中获取JSON数据字符串
     * @param urlPath
     * @return
     * @throws Exception
     */
	 public static String readParse(String urlPath) throws Exception {  
         ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
         byte[] data = new byte[1024];  
          int len = 0;  
          URL url = new URL(urlPath);  
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
          InputStream inStream = conn.getInputStream();  
          while ((len = inStream.read(data)) != -1) {  
              outStream.write(data, 0, len);  
          }  
          inStream.close();  
          return new String(outStream.toByteArray());//通过out.Stream.toByteArray获取到写的数据  
      }
	 
	 /**
      * 访问数据库并返回JSON数据字符串
      * 
      * @param params 向服务器端传的参数
      * @param url
      * @return
      * @throws Exception
      */
     public static String doPost(List<NameValuePair> params, String url)
             throws Exception {
         String result = null;
         // 获取HttpClient对象
         HttpClient httpClient = new DefaultHttpClient();
         // 新建HttpPost对象
         HttpPost httpPost = new HttpPost(url);
         if (params != null) {
             // 设置字符集
             HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
             // 设置参数实体
             httpPost.setEntity(entity);
         }

         /*// 连接超时
         httpClient.getParams().setParameter(
                 CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
         // 请求超时
         httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                 3000);*/
         // 获取HttpResponse实例
         HttpResponse httpResp = httpClient.execute(httpPost);
         // 判断是够请求成功
         if (httpResp.getStatusLine().getStatusCode() == 200) {
             // 获取返回的数据
             result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
             Log.e("测试", result);
         } else {
        	 result = "0";
             Log.i("HttpPost", "HttpPost方式请求失败");
         }

         return result;
     }
}
