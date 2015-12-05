package yan.girl.database;

import java.io.File;
import java.io.IOException;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class SDCardDatabase {

		private String sdPath = null;
		
		private static final String Def_Relative_Pathname = "DiaryGirl/";
		private static final String Def_Db_Filename = "diary.db";
		
		private File dbPath = null;
		private File dbFile = null;


		public SDCardDatabase() {
			sdPath = getSDPath();
			dbPath = new File(sdPath+Def_Relative_Pathname);
			dbFile = new File(sdPath+Def_Relative_Pathname+Def_Db_Filename);
		}
		
		/**
		 * ע��pathname��Ҫָ�������·������/sdcard/diary/
		 * @param pathname
		 * @param dbFilename
		 */
		public SDCardDatabase(String pathname,String dbFilename) {
			dbPath = new File(pathname);
			dbFile = new File(pathname+dbFilename);
		}		
		
		private String getSDPath()
		{
			return Environment.getExternalStorageDirectory().toString()+"/";
		}
		
		public File getDbPath() {
			return dbPath;
		}
		
		public File getDbfile() {
			return dbFile;
		}
		
		private boolean isSDMounted(){
			boolean res = false;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
			{ 
				 res = true;
			}else{ 
				 res = false;
			}
			return res; 
			
		}
		
		public boolean checkDBResources()
		{	
			boolean tmpFlag = false;
			boolean resFlag = true;
			
			//����Ӧ�ж�SD���Ƿ�����
			if(!isSDMounted()){
				System.exit(-1);//�����쳣�˳�
			}
			
			//���ݿ��ļ�·���Ƿ����
			if (!dbPath.exists()) {   
				tmpFlag = dbPath.mkdirs();
				System.out.println("createDB "+tmpFlag);	
				resFlag = resFlag && tmpFlag;
			}else{
				System.out.println("checkDBResource�����ݿ�Ŀ¼���ڣ�");
			}
			
			//���ݿ��ļ��Ƿ����
			if (!dbFile.exists()) {   
				try {   
					  tmpFlag = dbFile.createNewFile();//�����ļ�    
					  System.out.println("createDB "+tmpFlag);
					  resFlag = resFlag && tmpFlag;
				} catch (IOException e) {     
				      e.printStackTrace();   
				}   
			}else{    	
				System.out.println("checkDBResource�����ݿ��ļ����ڣ�");
			}
			
			return resFlag;	
		}
		
		public void initSDDbTable() {
			// TODO Auto-generated method stub
		     if(checkDBResources())//������ݿ�·�����ļ�������
		     {
			        SQLiteDatabase db=null;
			        try{
						db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
						if(!isTableExist("diary"))
							db.execSQL("CREATE TABLE diary (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
									"title VARCHAR(20) NOT NULL ," +
									"content TEXT NOT NULL ," +
									"img BLOB ," +
									"time VARCHAR(20) );");
						System.out.println("���ݿ��diary�Ѵ���.");
			        }catch(Exception e){
			        	e.printStackTrace();
			        }finally{
			        	db.close();
			        }
		     }
		}
		
		 private boolean isTableExist(String tabName){
		         boolean result = false;
		         if(tabName == null){
		                 return false;
		         }
		         SQLiteDatabase db = null;
		         Cursor cursor = null;
		         try {
		                 db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		                 String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
		                 cursor = db.rawQuery(sql, null);
		                 if(cursor.moveToNext()){
		                         int count = cursor.getInt(0);
		                         if(count>0){
		                                 result = true;
		                         }
		                 }                
		         } catch (Exception e) {
		                 e.printStackTrace();
		         } finally{
		        	 cursor.close();
		        	 db.close();
		         }
		         return result;
		 } 
		 
		public long updateDiary(long id,ContentValues values)
		{
			long res = -1 ;
			SQLiteDatabase db = null ;
			try{					
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);				
				res = db.update("diary", values, "_id=?", new String[]{String.valueOf(id)});
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.close();
			}
			return res;
		}
		 
		public void deleteDiary(long id)
		{
			SQLiteDatabase db = null ;
			try{					
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);				
				db.delete("diary", "_id=?", new String[]{String.valueOf(id)});
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.close();
			}
		}

		public long insertDiary(ContentValues values) {
			// TODO Auto-generated method stub
			long res = -1 ;
			SQLiteDatabase db = null ;
			try{					
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);				
				res = db.insert("diary", null, values);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.close();
			}
			return res;
		}		 
		 
	}


