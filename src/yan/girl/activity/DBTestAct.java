package yan.girl.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import yan.girl.activity.R;
import yan.girl.database.DiaryDBHelper;
import yan.girl.database.SDCardDatabase;
import yan.girl.metadata.DiaryMetadata;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DBTestAct extends Activity {
	
	private Button btnInsert,btnQuery,btnCreateSdDb,
					btnInsertSdDb,btnQuerySdDb,btnTrans,
					btnDropTable,btnUpdateSDTable;
	private DiaryDBHelper dbHelper;
	
	private SDCardDatabase sdCardDB = null;
	private File dbFile = null;	
	private List<Map<String,Object>> transDatas = null;
	
	private String flag = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setTitle("");
        dbHelper=new DiaryDBHelper(DBTestAct.this, DiaryMetadata.DB_NAME);
        sdCardDB = new SDCardDatabase();
        

        setContentView(R.layout.db_test);
    	//----------------------------------------------------------------------------- 
        btnInsert=(Button)findViewById(R.id.insert);
        btnInsert.setOnClickListener(new OnClickListener(){           
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("ListViewAct--->onCreate--->btnInsert");		
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE, "my first diary");
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT, 
						"老婆，这是我为你设计的第一个日记测试内容");
				
				/*
				 *  最终图标要保存到浏览器的内部数据库中，系统程序均保存为SQLite格式，
				 *  因为图片是二进制的所以使用字节数组存储数据库的    
				 */
				Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.list_item_diary);
				// 将Bitmap转换为BLOB类型存储存储
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_IMG, outputStream.toByteArray());
				
				SimpleDateFormat formatter = new  SimpleDateFormat("yy-MM-dd HH:mm");
				Date date =new Date();
				String time = formatter.format(date);
				System.out.println(time);
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME, time);
				
				db.insert(DiaryMetadata.DB_TABLE_NAME, null, values);
				System.out.println("Inserted!");
			}
        	
        });
    	//----------------------------------------------------------------------------- 
        btnQuery=(Button)findViewById(R.id.query);
        btnQuery.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("ListViewAct--->onCreate--->btnQuery");					
				
				SQLiteDatabase db = dbHelper.getReadableDatabase();				
				Cursor cur = db.query(DiaryMetadata.DB_TABLE_NAME, new String[]{"_id","title","content","img","time"},
						/*"_id = 1"*/null, null, null, null, null);
				startManagingCursor(cur);
				
				transDatas = new ArrayList<Map<String,Object>>();
				
				while(cur.moveToNext()){
					Map<String,Object> map = new HashMap<String,Object>();
					
					int id = cur.getInt(cur.getColumnIndex("_id"));
					String title = cur.getString(cur.getColumnIndex("title"));
					String content = cur.getString(cur.getColumnIndex("content"));
					
					byte[] imgarr = cur.getBlob(cur.getColumnIndex("img"));					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(imgarr);
					Drawable image = Drawable.createFromStream(inputStream, null/*"image"*/);//
					
					String time = cur.getString(cur.getColumnIndex("time"));
					
					System.out.println(id+title+content+time);
					
					map.put("_id", id);
					map.put("title",title);
					map.put("content", content);
					map.put("img", image);//
					map.put("time", time);
					
					transDatas.add(map);
					System.out.println("查询过程中保存数据成功！");
//					createDialog(image).show();
				}
//				btnQuery.setVisibility(View.INVISIBLE);
//				btnTrans.setVisibility(View.VISIBLE);
			}
        	
        });

    	//----------------------------------------------------------------------------- 
        btnCreateSdDb=(Button)findViewById(R.id.create_sd_db);
        btnCreateSdDb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("ButtonOnClick.");
				
		        if(sdCardDB.checkDBResources())
		        {
		        	dbFile = sdCardDB.getDbfile();
		   
			        System.out.println(dbFile.getPath());
			        System.out.println(dbFile.exists()+" ,"+sdCardDB.getDbPath().exists());
			        
					SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
					if(!tabIsExist("diary"))
						db.execSQL("CREATE TABLE diary (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
								"title VARCHAR(20) NOT NULL ," +
								"content TEXT NOT NULL ," +
								"img BLOB ," +
								"time VARCHAR(20) );");
		        }
			}
        	
        });
        
    	//----------------------------------------------------------------------------- 
        btnInsertSdDb=(Button)findViewById(R.id.insert_sd);
        btnInsertSdDb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("ButtonOnClick.");
				dbFile = sdCardDB.getDbfile();
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				
				ContentValues values = new ContentValues();
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE, "my first diary");
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT, 
						"老婆，这是我为你设计的第一个日记测试内容");				
				/*
				 *  最终图标要保存到浏览器的内部数据库中，系统程序均保存为SQLite格式，
				 *  因为图片是二进制的所以使用字节数组存储数据库的    
				 */
				Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.list_item_diary);
				// 将Bitmap转换为BLOB类型存储存储
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_IMG, outputStream.toByteArray());
				
				SimpleDateFormat formatter = new  SimpleDateFormat("yy-MM-dd HH:mm");
				Date date =new Date();
				String time = formatter.format(date);
				System.out.println(time);
				values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME, time);
				
				db.insert(DiaryMetadata.DB_TABLE_NAME, null, values);
				
				System.out.println("Inserted!");				
				
			}
        	
        }); 
    	//----------------------------------------------------------------------------- 
        btnQuerySdDb=(Button)findViewById(R.id.query_sd);
        btnQuerySdDb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dbFile = sdCardDB.getDbfile();
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				Cursor cur = db.query(DiaryMetadata.DB_TABLE_NAME, new String[]{"_id","title","content","img","time"},
						/*"_id = 1"*/null, null, null, null, null);
				startManagingCursor(cur);
				
				
				while(cur.moveToNext()){
					
					int id = cur.getInt(cur.getColumnIndex("_id"));
					String title = cur.getString(cur.getColumnIndex("title"));
					String content = cur.getString(cur.getColumnIndex("content"));
					
					byte[] imgarr = cur.getBlob(cur.getColumnIndex("img"));					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(imgarr);
					Drawable image = Drawable.createFromStream(inputStream, null/*"image"*/);//
					
					String time = cur.getString(cur.getColumnIndex("time"));
					
					System.out.println(id+title+content+time);

					createDialog(image).show();
				}	
			}
        	
        });
        
    	//----------------------------------------------------------------------------- 
        btnTrans = (Button)findViewById(R.id.trans);
        btnTrans.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("ButtonOnClick.");							
				dbFile = sdCardDB.getDbfile();
				SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				ContentValues values = new ContentValues();
				
				if(transDatas!=null){
					
					for (Iterator iterator = transDatas.iterator(); iterator.hasNext();) {
						Map<String,Object> map = (Map<String,Object>) iterator.next();
						String id = map.get("_id").toString();//
						String title = map.get("title").toString();
						String content = map.get("content").toString();
						String time = map.get("time").toString();	
						
						values.put("title", title);
						values.put("content", content);
						values.put("time", time);
						
						Drawable image = (Drawable) map.get("img");
						BitmapDrawable bd = (BitmapDrawable) image;
						Bitmap bmp = bd.getBitmap();
						// 将Bitmap转换为BLOB类型存储存储
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
						values.put(DiaryMetadata.TableColumn.TABLE_COLUMN_IMG, outputStream.toByteArray());					
						
						System.out.println(id+title+content+time);
						
						db.insert("diary", null, values);
						System.out.println("转存数据成功！");
					}	
//					btnTrans.setVisibility(View.INVISIBLE);
//					btnDropTable.setVisibility(View.VISIBLE);
				}				
			}
        	
        });
    	//----------------------------------------------------------------------------- 
        btnDropTable=(Button)findViewById(R.id.drop_table);
        btnDropTable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL("DROP TABLE IF EXISTS diary");
				DBTestAct.this.deleteDatabase("diary.db");//这个数据库名一定要与其Activity相关
				
				
//				btnDropTable.setVisibility(View.INVISIBLE);
//				flag = "tans ok.";
//				Intent intent = new Intent();
//				intent.putExtra("flag", flag);
//				setResult(RESULT_OK,intent);
//				finish();
			}
        	
        });
        
        btnUpdateSDTable = (Button) this.findViewById(R.id.update_sd_table);
        btnUpdateSDTable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        
    }
   
    
	//----------------------------------------------------------------------------- 
    
    /**
     * 
     * @param image
     * @return
     */
    public Dialog createDialog(Drawable image)
    {
    	AlertDialog.Builder builder= new AlertDialog.Builder(this);
		builder.setIcon(image);
		builder.setTitle("两个Button的对话框");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setTitle("单击了对话框上的确定按钮");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setTitle("单击了对话框上的取消按钮");
			}
		});
		
		return builder.create();   	
    }
    
	//----------------------------------------------------------------------------- 
    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(String tabName){
            boolean result = false;
            if(tabName == null){
                    return false;
            }
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                    db = /*this.getReadableDatabase();*/SQLiteDatabase.openOrCreateDatabase(dbFile, null);
                    String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
                    cursor = db.rawQuery(sql, null);
                    if(cursor.moveToNext()){
                            int count = cursor.getInt(0);
                            if(count>0){
                                    result = true;
                            }
                    }
                    
            } catch (Exception e) {
                    // TODO: handle exception
            }                
            return result;
    } 
    

    
}