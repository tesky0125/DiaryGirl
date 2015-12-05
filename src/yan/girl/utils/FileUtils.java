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
		 * ��Ȼһ���ֻ�SD��Ŀ¼��/mnt/sdcard,��Ϊ�˱�����Щ���Ͳ�һ�£��������ַ�ʽ���ϸ�
		 */
		this.sdPath = Environment.getExternalStorageDirectory().toString();
	}


	/**
	 * ��ȡsdcard·��
	 */
	public String getSdPath() {
		return sdPath;
	}
	
	/**
	 * ����·��,ע��������ҪSD����Ȩ��
	 * @param dirName
	 * @return
	 */
	public boolean createDir(String dirname)
	{
		File dir=new File(sdPath+dirname);
		return	dir.mkdir();			
	}
	
	/**
	 * �����ļ��������ļ���ע�������fileQualifyName���ļ���ȫ�ƣ�������·��
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
	 * �����ļ��������ļ���ע�������fileQualifyName���ļ���ȫ�ƣ�������·��
	 * @param fileQualifyName
	 * @return
	 */	
	public boolean isFileExist(String fileQualifyName)
	{
		File file = new File(sdPath+fileQualifyName); 
		return file.exists();
	}
	
	/**
	 * ����������ȡ���ݣ���д�뵽SD�У�����ʵ�ֺ����ĸ���
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
	 * ���ֽ������ת�����ַ�����д����
	 */
	public static BufferedReader is2bufferReader(InputStream is)
	{
		return new BufferedReader(new InputStreamReader(is));
	}
	
}
