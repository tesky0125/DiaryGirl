package yan.girl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {

	private String sdPath;

	public FileUtils() {
		super();
		/*
		 * 虽然一般手机SD卡目录是/mnt/sdcard,但为了避免有些机型不一致，采用这种方式更严格
		 */
		this.sdPath = Environment.getExternalStorageDirectory().toString();
	}


	/**
	 * 获取sdcard路径
	 */
	public String getSdPath() {
		return sdPath;
	}
	
	/**
	 * 创建路径,注意这里需要SD访问权限
	 * @param dirName
	 * @return
	 */
	public boolean createDir(String dirname)
	{
		File dir=new File(sdPath+dirname);
		return	dir.mkdir();			
	}
	
	/**
	 * 根据文件名创建文件，注意这里的fileQualifyName是文件的全称，包括其路径
	 * @param fileQualifyName
	 * @return
	 */
	public File createFile(String fileQualifyName)
	{
		File file = new File(sdPath+fileQualifyName);
		try{
			file.createNewFile();			
		}catch(Exception e){
			e.printStackTrace();
		}	
		return file;
	}
	
	/**
	 * 根据文件名查找文件，注意这里的fileQualifyName是文件的全称，包括其路径
	 * @param fileQualifyName
	 * @return
	 */	
	public boolean isFileExist(String fileQualifyName)
	{
		File file = new File(sdPath+fileQualifyName); 
		return file.exists();
	}
	
	/**
	 * 从输入流读取数据，并写入到SD中，这样实现函数的复用
	 * @return
	 */
	public File write2SDfromInput(String dirname,String filename,InputStream input)
	{
		File file = null;
		OutputStream output = null;
		try{
			createDir(dirname);
			file = createFile(dirname+filename);
			output = new FileOutputStream(file);
			int tmpLen = 0;
			byte[] buffer = new byte[4*1024];
			while((tmpLen=input.read(buffer))!=-1)
				output.write(buffer, 0, tmpLen);
			output.flush();
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}
	

	/**
	 * InputSteram to BufferedReader
	 * 把字节流间接转换成字符流读写缓冲
	 */
	public static BufferedReader is2bufferReader(InputStream is)
	{
		return new BufferedReader(new InputStreamReader(is));
	}
	
}
