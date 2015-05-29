package com.example.xinkaishi.util;

/**
 * 获取字符串的真实长度
 * 中文字符长度为2 英文为1
 */
public class ValueLength {
	public static int String_length(String value) {
		  int valueLength = 0;
		  String chinese = "[\u4e00-\u9fa5]";
		  for (int i = 0; i < value.length(); i++) {
		   String temp = value.substring(i, i + 1);
		   if (temp.matches(chinese)) {
		    valueLength += 2;
		   } else {
		    valueLength += 1;
		   }
		  }
		  return valueLength;
		 }
}
