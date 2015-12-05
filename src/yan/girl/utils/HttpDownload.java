package yan.girl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

public class HttpDownload {
	
//	public URL url;
	/*
	 * 根据URL从网络下载类文本文件(字符流传输，用来读取文件内容)，一定要注意在XML中注册访问网络的权限
	 */
	public String downtxtfile(String urlStr)
	{
		StringBuffer strBuffer = new StringBuffer();
		String tmpLine = null;
		InputStream input = null;
		BufferedReader bufferReader = null;
		try {
			/*URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStream input = urlConn.getInputStream();*/
			input = url2InputStream(urlStr);
			/*InputStreamReader inputReader = new InputStreamReader(input);
			BufferedReader bufferReader = new BufferedReader(inputReader);*/
			bufferReader = FileUtils.is2bufferReader(input);
			
			while ((tmpLine=bufferReader.readLine())!=null)
				strBuffer.append(tmpLine);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				input.close();
				bufferReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return strBuffer.toString();
	}
	
	/**
	 * 从网络下载文件，采用字节流方式下载，不适合阅读，其中dirname/filename并不是qualifyname
	 * @param urlStr
	 * @param dirname
	 * @param filename
	 * @return 下载成功返回 1,下载失败返回 -1 ,文件存在返回 0
	 */
	public int downfile(String urlStr,String dirname,String filename)
	{
		int result = -1;
		InputStream input = null;
		FileUtils fileUtil = new FileUtils();
		try{
			if(!fileUtil.isFileExist(dirname+filename)){
				input = this.url2InputStream(urlStr);
				File file = fileUtil.write2SDfromInput(dirname, filename, input);
				if(file == null)
					result = -1;
				else result = 1;
			}else{
				result = 0;
		}
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return result;
		
		
	}
	
	
	/**
	 * 把URL转换成为字节数据流
	 * @param urlStr
	 * @return
	 */
	public InputStream url2InputStream(String urlStr)
	{
		InputStream input = null;
		URL url = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			input = urlConn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return input;
	}
	

}
