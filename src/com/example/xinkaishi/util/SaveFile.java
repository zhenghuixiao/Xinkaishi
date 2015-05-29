package com.example.xinkaishi.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SaveFile {
	/**
	* 保存文件
	* @param toSaveString
	* @param filePath
	*/
	public void save(String toSaveString, String filePath){
		try{
			File saveFile = new File(filePath);
		if (!saveFile.exists()){
			File dir = new File(saveFile.getParent());
			dir.mkdirs();
			saveFile.createNewFile();
		 }
	
		FileOutputStream outStream = new FileOutputStream(saveFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outStream));
		bw.write(toSaveString);
		bw.close();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
