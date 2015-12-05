package yan.girl.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import yan.girl.activity.R;
import yan.girl.database.SDCardDatabase;
import yan.girl.interfaces.DialogListener;
import yan.girl.metadata.DiaryMetadata;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class DiaryListAct extends Activity implements OnItemClickListener,OnItemLongClickListener{

	private ListView diaryListView;
	private List<Map<String,Object>> diaryDataList;
	
	/*private DiaryDBHelper dbHelper;*/	
	private SDCardDatabase sdCardDB = null;
	private Cursor dbCursor =null ;
	
	private  int mPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*dbHelper=new DiaryDBHelper(this, DiaryMetadata.DB_NAME);*/		
		sdCardDB = new SDCardDatabase();
		
		setContentView(R.layout.mainlist_diary);
		diaryListView = (ListView)findViewById(R.id.diarylist);
		diaryListView.setOnItemClickListener(this);
		diaryListView.setOnItemLongClickListener(this);
		
		registerForContextMenu(diaryListView);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("DiaryListAct--->onResume");
		
		refreshDiaryList();
		diaryListView.setSelection(mPosition);	
	}

	private void refreshDiaryList()
	{
		System.out.println("DiaryListAct--->refreshListItems");	
		
		diaryDataList = buildListForSimpleAdapter();		
		String[] from = new String[]{DiaryMetadata.TableColumn.TABLE_COLUMN_IMG,
									DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE,
									DiaryMetadata.TableColumn.TABLE_COLUMN_TIME};
		int[] to = new int[]{R.id.diary_image,R.id.title,R.id.time};
		
		SimpleAdapter adapter = new SimpleAdapter(this, diaryDataList, R.layout.listitem_diary, from, to);		
		diaryListView.setAdapter(adapter);
		
	}
	

	private List<Map<String,Object>> buildListForSimpleAdapter()
	{
		System.out.println("refreshListItems--->buildListForAdapter");	
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Cursor cur = requestDataFromDB();
		startManagingCursor(cur);
		
		while(cur.moveToNext())
		{
			Map<String,Object> map= new HashMap<String,Object>();
			
			String title = cur.getString(cur.getColumnIndex(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE));	
			String time = cur.getString(cur.getColumnIndex(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME));			
			String _id = cur.getString(cur.getColumnIndex(DiaryMetadata.TableColumn._ID));
			
			map.put(DiaryMetadata.TableColumn.TABLE_COLUMN_IMG, R.drawable.list_item_diary);			
			map.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE, title);
			map.put(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME, time);
			map.put(DiaryMetadata.TableColumn._ID, _id);
			
			list.add(map);
		}		
		return list;
		
	}
	
	private Cursor requestDataFromDB()
	{
		System.out.println("buildListForAdapter--->requestDataFromDB");	
		
		/*SQLiteDatabase db = dbHelper.getReadableDatabase();*/		
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sdCardDB.getDbfile(), null);
		dbCursor = db.query(DiaryMetadata.DB_TABLE_NAME, 
				new String[]{DiaryMetadata.TableColumn._ID,
							DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE,
							DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT,
							DiaryMetadata.TableColumn.TABLE_COLUMN_IMG,
							DiaryMetadata.TableColumn.TABLE_COLUMN_TIME},
				null, null, null, null, null);
		return dbCursor;
		
	}

	
	private static final int MENU_ITEM_NEW = Menu.FIRST;
	private static final int MENU_ITEM_SETTING = Menu.FIRST+1;
	private static final int MENU_ITEM_ABOUT = Menu.FIRST+2;
	private static final int MENU_ITEM_REFRESH= Menu.FIRST+3;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		System.out.println("onCreateOptionsMenu");	
		
		/*MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_new, menu);*/
		
		menu.clear();
		menu.add(0, MENU_ITEM_NEW, 0, "写日记").setIcon(R.drawable.write);
		menu.add(0, MENU_ITEM_REFRESH, 1, "刷新列表").setIcon(R.drawable.refresh);		
		menu.add(0, MENU_ITEM_SETTING, 2, "设置").setIcon(R.drawable.setting);
		menu.add(0, MENU_ITEM_ABOUT, 3, "关于").setIcon(R.drawable.menu_about);
		
		return super.onCreateOptionsMenu(menu);
	}

	public static final int REQUEST_CODE_NEW = 1 ;//跳转进入写日记界面
	public static final int REQUEST_FLAG_NEW = 101;
	
	public static final int REQUEST_CODE_DISP = 2;	//跳转进入日记显示界面
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		System.out.println("onMenuItemSelected :"+item.getItemId());
		
		Intent intent = null;
		switch(item.getItemId())
		{
		case MENU_ITEM_NEW:
			intent=new Intent(DiaryListAct.this,DiaryNewEditAct.class);
			intent.setFlags(REQUEST_FLAG_NEW);
			startActivityForResult(intent, REQUEST_CODE_NEW);
			break;
			
		case MENU_ITEM_SETTING:
			intent = new Intent(this,ConfigureAct.class);
			startActivity(intent);			
			break;		
			
		case MENU_ITEM_ABOUT:
//			finish();
			break;
			
		case MENU_ITEM_REFRESH:
			refreshDiaryList();
			break;
			
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	
	//----------------------------------------------------------------------------- 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);//可以尽量利用系统提供的参数
		System.out.println("onActivityResult");
		
		int pos = this.mPosition;
		
		if(intent!=null){
			int flag = intent.getFlags();
			switch(flag)
			{
			case DiaryNewEditAct.FLAG_SAVE:	
				this.mPosition = this.diaryDataList.size();//相当于指向当前list后一条
				System.out.println("new diary position "+this.mPosition);
				break;
			case DiaryNewEditAct.FLAG_UPDATE:
				pos = intent.getIntExtra("position", this.mPosition);//第二个参数时默认值
				this.mPosition = pos;	
				break;
			}
		}
		
		System.out.println("DiaryList result position-->"+this.mPosition);

		/*
		 * 在onActivityResult之后调用onResume,在这里设置diaryList.setSelection(position);则无效
		 * 需要设置成员变量保存position,然后在onResume中设置diaryList.setSelection(position);
		 */
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
		this.mPosition = position;
		
		this.toDispDiary(position);
				
	}

	private void toDispDiary(int position) {
		// TODO Auto-generated method stub
		
		Map<String, Object> map = diaryDataList.get(position);
		int _id = Integer.parseInt(map.get(DiaryMetadata.TableColumn._ID).toString());		
		String title = map.get(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE).toString();
		String time = map.get(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME).toString();
		System.out.println("onItemClick \n"+"positon: "+position+" title: "+title+" time: "+time+" _id: "+_id);
		
		/*
		 * 传数据方式一：直接把数据库数据一并传到下一个Activity,则不用再访问数据库数据
		 * 传数据方式二：只传_id,然后在下一个Activity中根据得到的_id搜索数据库再返回数据库值
		 *
		 * dbCursor.moveToPosition(_id);
		 * -1 <= position <= count
		 * 对position理解错误,以为这个函数是让游标移动到指定的_id,
		 * 其实是让游标指定到数据库中实际内容所在位置,刚好可以借用ListView的position
		 * 那么这里保存的_id可以用于操作数据库，如后面用来删除和更新日记
		 * */

		//采用第一种方式
		dbCursor.moveToPosition(position);		
		//可以考虑采用实体类对数据进行封装
		Bundle info = new Bundle();
		info.putInt(DiaryMetadata.TableColumn._ID, _id);
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE, title);
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT,
				dbCursor.getString(dbCursor.getColumnIndex(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT)));
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME, time);
		info.putInt("position", position);//用于数据更新后返回时锁定在该条目
				
		Intent intent = new Intent(DiaryListAct.this,DiaryDetailAct.class);
		intent.putExtra("diary.disp.data", info);	
		startActivityForResult(intent, REQUEST_CODE_DISP);
	}
	
	private static final int PROMPT_DIALOG_ID = 1;
	//----------------------------------------------------------------------------- 
	@SuppressWarnings("unchecked")
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		this.mPosition = position;
		
		Map<String,Object> map= (Map<String,Object>)parent.getItemAtPosition(position);
		int _id = Integer.parseInt(map.get(DiaryMetadata.TableColumn._ID).toString());
		
		//长按弹出删除选项、查看选项等
		/**
		 * 1.通过Activity的android:theme="@android:style/Theme.Dialog"实现
		 * 缺点：当多个Activity之间需要传值时则显得复杂，比如从A进入该‘对话框’，选择‘编辑’后进入B，
		 * 而B在更新后需要把数据返回给A则太困难,不采用
		 */
		/*Intent intent = new Intent(this,DiaryHandleAct.class);
		Bundle bundle = new Bundle();
		bundle.putInt("_id", _id);
		intent.putExtra("handle.data",bundle);
		startActivity(intent);*/
		
		/**
		 * 2.通过Activity的showDialog函数来调用onCreateDialog实现弹出对话框,在其中用AlertDialog来实现弹出列表对话框，
		 * dismissDialog来取消对话框，这种方式比较简单
		 */
		showDialog(PROMPT_DIALOG_ID);
		
		/**
		 * 3.通过自定义的Dialog+Interface做的Listener来共同实现传参，这样能在Activity与Dialog之间交换数据，
		 * 其中用到了接口函数来传值，这个方式比较复杂
		 */
		/*PromptDialog prmpDlg = new PromptDialog(this, digListener);
		prmpDlg.setCancelable(false);//可以控制不接收返回键的点击
		prmpDlg.show();*/
		
		/**
		 * 4.通过ContextMenu实现，这才是对List最好的方法，因为只是提示用户操作，而不存在数据交换
		 */

		//return false;
		/*
		 * 如果返回false则会继续调用onItemClick
		 */
		return true;
	}
	
	/**
	 * 用于第三种方式
	 */
	 private DialogListener digListener = new DialogListener(){

		@Override
		public void onItemClick(int pos) {
			// TODO Auto-generated method stub
			switch(pos){
			case 0:
				toDispDiary(mPosition);
				break;
		
			case 1:
				deletePromptDialog(mPosition);				
				break;
				
			}
		}
	};
	
	
	private void deletePromptDialog(int position) {
			Map<String, Object> map = diaryDataList.get(position);
			final int _id = Integer.parseInt(map.get(DiaryMetadata.TableColumn._ID).toString());
						
			AlertDialog.Builder builder = new AlertDialog.Builder(DiaryListAct.this);
			builder.setTitle("删除提示");
			builder.setMessage("是否删除这篇日记?");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sdCardDB.deleteDiary(_id);
					refreshDiaryList();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.create().show();
		}
	

	

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		System.out.println("Dialog onPrepareDialog");		
		super.onPrepareDialog(id, dialog);
		switch(id){
		case PROMPT_DIALOG_ID:
			setDialogSize(dialog);
			break;
		}
	}
	
	private void setDialogSize(Dialog dialog){
		
		WindowManager windowManager = getWindowManager(); 
		Display display = windowManager.getDefaultDisplay(); //获取屏幕宽、高 

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); //获取对话框当前的参数值 
		params.height = (int) (display.getHeight() * 0.8); 
//		params.width = (int) (display.getWidth() * 0.6); 

		params.alpha = 0.8f;//设置透明度
		dialog.getWindow().setAttributes(params);	
	}	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		System.out.println("Dialog onCreateDialog");
		return buildDialog(this, PROMPT_DIALOG_ID);
//		return super.onCreateDialog(id);
	}

	
	/**
	 * 用于第二种方式
	 */	
	private Dialog buildDialog(Context context,int id){

		ListView handleDlgListView = null;
		List<String>  handleDlgDataList = null;
		ArrayAdapter<String> arrdapter = null; 			
		
		
		handleDlgDataList = new ArrayList<String>();   
		handleDlgDataList.add("查看");
		handleDlgDataList.add("编辑");
		handleDlgDataList.add("删除");
		
		LayoutInflater inflater=this.getLayoutInflater();
		final View handleDlgView=inflater.inflate(R.layout.mainlist_diary_handle, null);
		handleDlgListView = (ListView) handleDlgView.findViewById(R.id.handle_list);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setView(handleDlgView);
		builder.setIcon(null);
		builder.setTitle("请选择：");
		
		arrdapter = new ArrayAdapter<String>(this,R.layout.listitem_handle, handleDlgDataList);    
		handleDlgListView.setAdapter(arrdapter);
		handleDlgListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// TODO Auto-generated method stub
				switch(pos){
				case 0:
					toDispDiary(mPosition);
					dismissDialog(PROMPT_DIALOG_ID);
					break;
				case 1:
					Toast.makeText(DiaryListAct.this, "Waiting..", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					deletePromptDialog(mPosition);
					dismissDialog(PROMPT_DIALOG_ID);
					break;
				}			
			}			
		});
		
		return builder.create();
	}

	
	private static final int CONTEXT_MENU_ITEM_DISP = Menu.FIRST;
	private static final int CONTEXT_MENU_ITEM_MODIFY = Menu.FIRST+1;
	private static final int CONTEXT_MENU_ITEM_DELETE = Menu.FIRST+2;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
//		 TODO Auto-generated method stub
		System.out.println("创建ContextMenu");
		menu.clear();
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_DISP, Menu.NONE, "查看");
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_MODIFY, Menu.NONE, "编辑");
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_DELETE, Menu.NONE, "删除");

		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}
	
	//----------------------------------------------------------------------------- 
/*	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("dispatchKeyEvent");
        switch(event.getKeyCode())  
        {  
            case KeyEvent.KEYCODE_BACK: {  
                 if(event.isLongPress())  //长按退出
                 {  
//                     this.stopService(intent);//关闭正在运行的相关服务
                	 System.out.println("LongPress");
                     System.exit(0);  
                     return true;  
                 }else  
                 {                      
//                     return false;//如果不截断消息的处理，继续往下执行，则会响应onKeyDown事件
                 }  
            }    
        }  
		return super.dispatchKeyEvent(event);
	}*/	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			System.out.println("ListView--->KEYCODE_BACK ："+keyCode);
			exitBy2Click();
			return false;			
		}

		return super.onKeyDown(keyCode, event);
	}


	private static Boolean isExit = false;
    /**
     * 在2秒内连续按下返回键则退出程序,函数需与静态变量isExit一起使用
     * 	private static Boolean isExit = false;
     */
	private void exitBy2Click()
	{		
	    Timer  exitTimer = null;
	    TimerTask task = new TimerTask() {            
	        @Override  
	        public void run() {  
	            isExit = false;  //取消准备退出的状态	            
	        }  
	    };
        if(isExit == false ) 
        {  
            isExit = true;  //进入准备准备退出状态
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
            exitTimer = new Timer(); //启动定时器
            exitTimer.schedule(task, 2000); //如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务 
        
        }else{                                                                           
            finish();  
            System.exit(0);         
        }	  		
	}



	
	
}
