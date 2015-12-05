package yan.girl.activity;

import yan.girl.activity.R;
import yan.girl.database.SDCardDatabase;
import yan.girl.metadata.DiaryMetadata;
import yan.girl.utils.ImageUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DiaryDetailAct extends Activity implements android.view.View.OnClickListener {

	private TextView viewTitle,viewContent,viewTime;
	private ImageView imgView;
	
	private int _id;
	private String title;
	private String content;
	private String time;
	private int mPosition;
	private Bitmap mBitmap=null;
	
	/*private DiaryDBHelper dbHelper;*/
	
	private SDCardDatabase sdCardDB = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        
		/*dbHelper=new DiaryDBHelper(DiaryDetailAct.this, DiaryMetadata.DB_NAME);*/

		sdCardDB = new SDCardDatabase();
				
		setTitle("日记内容");
		setContentView(R.layout.diary_detail);	
		
		initWidget();
		
		extractData2Show();
		
	}

	private void initWidget() {
		imgView=(ImageView) this.findViewById(R.id.disp_image);
		imgView.setOnClickListener(this);
		
		viewTitle=(TextView)findViewById(R.id.disp_title);
		viewContent=(TextView)findViewById(R.id.disp_content);
		viewContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		viewTime=(TextView)findViewById(R.id.disp_time);
	}
	
	private void extractData2Show() {
		Intent intent = getIntent();
		if(intent != null){
			Bundle info = intent.getBundleExtra("diary.disp.data");
			_id = info.getInt(DiaryMetadata.TableColumn._ID);
			title = info.getString(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE);
			content = info.getString(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT);
			time = info.getString(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME);			
			mPosition = info.getInt("position");
			
			viewTitle.setText(title);
			viewContent.setText(content);
			viewTime.setText(time);		
			
			SQLiteDatabase db = null;
			try{
				db = SQLiteDatabase.openOrCreateDatabase(sdCardDB.getDbfile(), null);
				Cursor cur = db.query(DiaryMetadata.DB_TABLE_NAME, 
						new String[]{DiaryMetadata.TableColumn.TABLE_COLUMN_IMG},
						"_id = ?", 
						new String[]{String.valueOf(_id)}, null, null, null);
				cur.moveToNext();				
				Bitmap bmp = ImageUtils.bytes2Bimap(cur.getBlob(cur.getColumnIndex("img")));											
				imgView.setImageBitmap(bmp);				
				mBitmap = bmp;
			}catch(Exception e){				
			}finally{
				db.close();
			}			
		}
	}

	
	public static final int GRIDVIEW_FLAG_VIEW = 1;	
	
	//imgView
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("ImgView Show");
		Intent intent = new Intent(this,ImageShowAct.class);
		intent.putExtra("bitmap", mBitmap);
		intent.setFlags(GRIDVIEW_FLAG_VIEW);		
		this.startActivity(intent);
	}
	

	private static final int MENU_ITEM_MODIFY = Menu.FIRST;
	private static final int MENU_ITEM_DELETE = MENU_ITEM_MODIFY+1;	
	private static final int MENU_ITEM_BACK = MENU_ITEM_DELETE+1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		menu.add(2, MENU_ITEM_MODIFY, 0, "修改");
		menu.add(2, MENU_ITEM_DELETE, 1, "删除");
		menu.add(2, MENU_ITEM_BACK, 2, "取消");
		return super.onCreateOptionsMenu(menu);

	}

	public static final int REQUEST_CODE_DISP_MODIFY = 3 ;		
	public static final int REQUEST_FLAG_DISP_MODIFY = 301;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case MENU_ITEM_MODIFY:
			Intent intent = new Intent(DiaryDetailAct.this,DiaryNewEditAct.class);			
			Bundle info = setBundle();		
			intent.putExtra("diary.disp.data.modify", info);
			intent.setFlags(REQUEST_FLAG_DISP_MODIFY);
			startActivityForResult(intent, REQUEST_CODE_DISP_MODIFY);
					
			break;
		case MENU_ITEM_DELETE:
			//deleteDiary();
			deleteAffirmDialog().show();

			break;
		case MENU_ITEM_BACK:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private Bundle setBundle() {
		Bundle info = new Bundle();
		info.putInt("_id", _id);
		info.putString("title", title);
		info.putString("content", content);
		info.putString("time", time);
		info.putInt("position", mPosition);//
		info.putParcelable("bitmap", mBitmap);
		return info;
	}
	


	private Dialog deleteAffirmDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确认");
		builder.setMessage("您确定要删除这篇日记吗？");
		builder.setPositiveButton("确定", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				sdCardDB.deleteDiary(_id);
				Intent intent =new Intent();
				intent.putExtra("position", mPosition-1);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub				
			}
		});
		return builder.create();
	}
	
	//----------------------------------------------------------------------------- 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		int position = mPosition;
		if(intent!=null){
			position = intent.getIntExtra("position", mPosition);
			int flag = intent.getFlags();
			intent.setFlags(flag);					
			System.out.println("DiaryDetail position-->"+position);
			intent.putExtra("position", position);
			setResult(RESULT_OK, intent);
		}
		finish();//
	}
}
