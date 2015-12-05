package yan.girl.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import yan.girl.activity.R;
import yan.girl.database.DiaryDBHelper;
import yan.girl.database.SDCardDatabase;
import yan.girl.metadata.DiaryMetadata;
import yan.girl.utils.ImageUtils;
import yan.girl.utils.TimeUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class DiaryNewEditAct extends Activity{

	private EditText editTitle,editContent;
	private String title,content,time;
	private int id;
	private Bitmap mBitmap = null;
	private int mPosition;
	private DiaryDBHelper dbHelper;//--
	private SDCardDatabase sdCardDB = null;
	private File dbFile = null;	
	
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("写日记");
		setContentView(R.layout.diary_new_edit);
		
        dbHelper=new DiaryDBHelper(DiaryNewEditAct.this, DiaryMetadata.DB_NAME);//--
        sdCardDB = new SDCardDatabase();
		
		editTitle=(EditText)findViewById(R.id.newedittitle);
		editContent=(EditText)findViewById(R.id.newcontent);

		
        intent = getIntent();
        if(intent !=null && intent.getFlags()==DiaryDetailAct.REQUEST_FLAG_DISP_MODIFY){
        	Bundle info = intent.getBundleExtra("diary.disp.data.modify");
        	id = info.getInt("_id");
        	title = info.getString("title");
        	content = info.getString("content");
        	time = info.getString("time");
        	mPosition = info.getInt("position");
        	mBitmap = info.getParcelable("bitmap");
        	System.out.println("new get position-->"+mPosition);

    		editTitle.setText(title);
    		editContent.setText(content);
        }	
        
        
	}

	public static final int MENU_ITEM_SAVE = Menu.FIRST;
	public static final int MENU_ITEM_ADD_IMG = MENU_ITEM_SAVE+1;
	public static final int MENU_ITEM_BACK = MENU_ITEM_ADD_IMG+1;
	
	public static final int MENU_ITEM_UPDATE = MENU_ITEM_BACK+1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		if(intent.getFlags() == DiaryListAct.REQUEST_FLAG_NEW){
			menu.add(0, MENU_ITEM_ADD_IMG, 0, "添加照片");
			menu.add(1, MENU_ITEM_SAVE, 1, "保存");
			menu.add(1, MENU_ITEM_BACK, 2, "取消");	
		}else if(intent.getFlags() == DiaryDetailAct.REQUEST_FLAG_DISP_MODIFY){
			menu.add(0, MENU_ITEM_ADD_IMG, 0, "添加照片");
			menu.add(2, MENU_ITEM_UPDATE, 1, "确定修改");
			menu.add(2, MENU_ITEM_BACK, 2, "取消");				
		}
		return super.onCreateOptionsMenu(menu);
	}

	public static final int FLAG_SAVE = 1;	
	public static final int FLAG_UPDATE = FLAG_SAVE+1;	
	
	//进入大图显示的三个路径：1,日记显示 2,图片浏览 3,日记添加图片时图片浏览 ,以GRIDVIEW_FLAG_ 开头
	public static final int GRIDVIEW_FLAG_TOSAVE = 3;
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case MENU_ITEM_ADD_IMG:
			Intent intent = new Intent(DiaryNewEditAct.this,/*GridViewShowAct*/PhotoViewerAct.class);
			intent.addFlags(GRIDVIEW_FLAG_TOSAVE);
			DiaryNewEditAct.this.startActivityForResult(intent, 0);			
			break;
		case MENU_ITEM_SAVE:
			HandleDiaryAsynTask diarySave = new HandleDiaryAsynTask(this);
			diarySave.execute(MENU_ITEM_SAVE);//难道保存延迟那么大?每次回到主界面，新日记还没有出来...
										
			Intent intentInsert=new Intent();
			intentInsert.setFlags(FLAG_SAVE);
			setResult(RESULT_OK, intentInsert);			
			finish();
			break;
		case MENU_ITEM_UPDATE:
			HandleDiaryAsynTask diaryUpdate = new HandleDiaryAsynTask(this);
			diaryUpdate.execute(MENU_ITEM_UPDATE);
			
			Intent intentUpdate=new Intent();
			intentUpdate.putExtra("position", mPosition);
			intentUpdate.setFlags(FLAG_UPDATE);
			System.out.println("new save position-->"+mPosition);
			setResult(RESULT_OK, intentUpdate);
			finish();			
			break;
		case MENU_ITEM_BACK:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if(data!=null){
			mBitmap = data.getParcelableExtra("bitmap");
		}		
	}


	private class HandleDiaryAsynTask extends AsyncTask<Integer, Integer, Long>{
		ProgressDialog pdialog;
		Context context;
		public HandleDiaryAsynTask(Context context){
			
			this.context = context;

        }
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
            pdialog = new ProgressDialog(context);   
            pdialog.setTitle("请稍等");
            pdialog.setMessage("正在保存...");
            pdialog.setButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	dialog.cancel();
            	}
            });
            pdialog.setCancelable(true);
//            pdialog.setMax(100);
//            pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdialog.show();
		}		
		
		@Override
		protected Long doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			long rowId = -1;
			if(params[0] == MENU_ITEM_SAVE){
				rowId= prepareData2insertDiary();
			 }else if(params[0] == MENU_ITEM_UPDATE){
				rowId= prepareData2updateDiary();
			 }
			return rowId;
			
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("rowId:"+result);
//			pdialog.dismiss();
		}



		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			 
		}				
		
	}


	//
	private long prepareData2insertDiary()
	{
		title=editTitle.getText().toString();
		content=editContent.getText().toString();
		System.out.println(title+","+content);
				
		ContentValues values=new ContentValues();	
		
		long rowId =-1;
		if(!title.equals("") && !content.equals(""))
		{
			values.put("title", title);
			values.put("content", content);
			
			Bitmap bmp=null;
			if(mBitmap!=null){
				bmp = mBitmap;
				
			}else{
				bmp = BitmapFactory.decodeResource(getResources(), R.drawable.list_item_diary);
			}			
			byte[] blob = ImageUtils.bitmap2Bytes(bmp);						
			values.put("img", blob);		
			
			String time = TimeUtils.createDateTime();
			values.put("time", time);
	
			rowId = sdCardDB.insertDiary(values);
			

		}
		return rowId;
		
	}
	
	private long prepareData2updateDiary()
	{
		title=editTitle.getText().toString();
		content=editContent.getText().toString();
		System.out.println(title+content);
		
		ContentValues values=new ContentValues();
		values.put("title", title);
		values.put("content", content);
		
		Bitmap bmp=null;
		if(mBitmap!=null){
			bmp = mBitmap;
			
		}else{
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.list_item_diary);
		}
		byte[] blob = ImageUtils.bitmap2Bytes(bmp);						
		values.put("img", blob);		
		
		String time = TimeUtils.createDateTime();		
		values.put("time", time);
	
		long result = sdCardDB.updateDiary(id, values);
		return result;
	}


}
