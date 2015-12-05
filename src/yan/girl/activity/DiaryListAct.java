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
		menu.add(0, MENU_ITEM_NEW, 0, "д�ռ�").setIcon(R.drawable.write);
		menu.add(0, MENU_ITEM_REFRESH, 1, "ˢ���б�").setIcon(R.drawable.refresh);		
		menu.add(0, MENU_ITEM_SETTING, 2, "����").setIcon(R.drawable.setting);
		menu.add(0, MENU_ITEM_ABOUT, 3, "����").setIcon(R.drawable.menu_about);
		
		return super.onCreateOptionsMenu(menu);
	}

	public static final int REQUEST_CODE_NEW = 1 ;//��ת����д�ռǽ���
	public static final int REQUEST_FLAG_NEW = 101;
	
	public static final int REQUEST_CODE_DISP = 2;	//��ת�����ռ���ʾ����
	
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
		super.onActivityResult(requestCode, resultCode, intent);//���Ծ�������ϵͳ�ṩ�Ĳ���
		System.out.println("onActivityResult");
		
		int pos = this.mPosition;
		
		if(intent!=null){
			int flag = intent.getFlags();
			switch(flag)
			{
			case DiaryNewEditAct.FLAG_SAVE:	
				this.mPosition = this.diaryDataList.size();//�൱��ָ��ǰlist��һ��
				System.out.println("new diary position "+this.mPosition);
				break;
			case DiaryNewEditAct.FLAG_UPDATE:
				pos = intent.getIntExtra("position", this.mPosition);//�ڶ�������ʱĬ��ֵ
				this.mPosition = pos;	
				break;
			}
		}
		
		System.out.println("DiaryList result position-->"+this.mPosition);

		/*
		 * ��onActivityResult֮�����onResume,����������diaryList.setSelection(position);����Ч
		 * ��Ҫ���ó�Ա��������position,Ȼ����onResume������diaryList.setSelection(position);
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
		 * �����ݷ�ʽһ��ֱ�Ӱ����ݿ�����һ��������һ��Activity,�����ٷ������ݿ�����
		 * �����ݷ�ʽ����ֻ��_id,Ȼ������һ��Activity�и��ݵõ���_id�������ݿ��ٷ������ݿ�ֵ
		 *
		 * dbCursor.moveToPosition(_id);
		 * -1 <= position <= count
		 * ��position������,��Ϊ������������α��ƶ���ָ����_id,
		 * ��ʵ�����α�ָ�������ݿ���ʵ����������λ��,�պÿ��Խ���ListView��position
		 * ��ô���ﱣ���_id�������ڲ������ݿ⣬���������ɾ���͸����ռ�
		 * */

		//���õ�һ�ַ�ʽ
		dbCursor.moveToPosition(position);		
		//���Կ��ǲ���ʵ��������ݽ��з�װ
		Bundle info = new Bundle();
		info.putInt(DiaryMetadata.TableColumn._ID, _id);
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_TITLE, title);
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT,
				dbCursor.getString(dbCursor.getColumnIndex(DiaryMetadata.TableColumn.TABLE_COLUMN_CONTENT)));
		info.putString(DiaryMetadata.TableColumn.TABLE_COLUMN_TIME, time);
		info.putInt("position", position);//�������ݸ��º󷵻�ʱ�����ڸ���Ŀ
				
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
		
		//��������ɾ��ѡ��鿴ѡ���
		/**
		 * 1.ͨ��Activity��android:theme="@android:style/Theme.Dialog"ʵ��
		 * ȱ�㣺�����Activity֮����Ҫ��ֵʱ���Եø��ӣ������A����á��Ի��򡯣�ѡ�񡮱༭�������B��
		 * ��B�ڸ��º���Ҫ�����ݷ��ظ�A��̫����,������
		 */
		/*Intent intent = new Intent(this,DiaryHandleAct.class);
		Bundle bundle = new Bundle();
		bundle.putInt("_id", _id);
		intent.putExtra("handle.data",bundle);
		startActivity(intent);*/
		
		/**
		 * 2.ͨ��Activity��showDialog����������onCreateDialogʵ�ֵ����Ի���,��������AlertDialog��ʵ�ֵ����б�Ի���
		 * dismissDialog��ȡ���Ի������ַ�ʽ�Ƚϼ�
		 */
		showDialog(PROMPT_DIALOG_ID);
		
		/**
		 * 3.ͨ���Զ����Dialog+Interface����Listener����ͬʵ�ִ��Σ���������Activity��Dialog֮�佻�����ݣ�
		 * �����õ��˽ӿں�������ֵ�������ʽ�Ƚϸ���
		 */
		/*PromptDialog prmpDlg = new PromptDialog(this, digListener);
		prmpDlg.setCancelable(false);//���Կ��Ʋ����շ��ؼ��ĵ��
		prmpDlg.show();*/
		
		/**
		 * 4.ͨ��ContextMenuʵ�֣�����Ƕ�List��õķ�������Ϊֻ����ʾ�û������������������ݽ���
		 */

		//return false;
		/*
		 * �������false����������onItemClick
		 */
		return true;
	}
	
	/**
	 * ���ڵ����ַ�ʽ
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
			builder.setTitle("ɾ����ʾ");
			builder.setMessage("�Ƿ�ɾ����ƪ�ռ�?");
			builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sdCardDB.deleteDiary(_id);
					refreshDiaryList();
				}
			});
			builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {				
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
		Display display = windowManager.getDefaultDisplay(); //��ȡ��Ļ���� 

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); //��ȡ�Ի���ǰ�Ĳ���ֵ 
		params.height = (int) (display.getHeight() * 0.8); 
//		params.width = (int) (display.getWidth() * 0.6); 

		params.alpha = 0.8f;//����͸����
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
	 * ���ڵڶ��ַ�ʽ
	 */	
	private Dialog buildDialog(Context context,int id){

		ListView handleDlgListView = null;
		List<String>  handleDlgDataList = null;
		ArrayAdapter<String> arrdapter = null; 			
		
		
		handleDlgDataList = new ArrayList<String>();   
		handleDlgDataList.add("�鿴");
		handleDlgDataList.add("�༭");
		handleDlgDataList.add("ɾ��");
		
		LayoutInflater inflater=this.getLayoutInflater();
		final View handleDlgView=inflater.inflate(R.layout.mainlist_diary_handle, null);
		handleDlgListView = (ListView) handleDlgView.findViewById(R.id.handle_list);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setView(handleDlgView);
		builder.setIcon(null);
		builder.setTitle("��ѡ��");
		
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
		System.out.println("����ContextMenu");
		menu.clear();
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_DISP, Menu.NONE, "�鿴");
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_MODIFY, Menu.NONE, "�༭");
        menu.add(Menu.NONE, CONTEXT_MENU_ITEM_DELETE, Menu.NONE, "ɾ��");

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
                 if(event.isLongPress())  //�����˳�
                 {  
//                     this.stopService(intent);//�ر��������е���ط���
                	 System.out.println("LongPress");
                     System.exit(0);  
                     return true;  
                 }else  
                 {                      
//                     return false;//������ض���Ϣ�Ĵ�����������ִ�У������ӦonKeyDown�¼�
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
			System.out.println("ListView--->KEYCODE_BACK ��"+keyCode);
			exitBy2Click();
			return false;			
		}

		return super.onKeyDown(keyCode, event);
	}


	private static Boolean isExit = false;
    /**
     * ��2�����������·��ؼ����˳�����,�������뾲̬����isExitһ��ʹ��
     * 	private static Boolean isExit = false;
     */
	private void exitBy2Click()
	{		
	    Timer  exitTimer = null;
	    TimerTask task = new TimerTask() {            
	        @Override  
	        public void run() {  
	            isExit = false;  //ȡ��׼���˳���״̬	            
	        }  
	    };
        if(isExit == false ) 
        {  
            isExit = true;  //����׼��׼���˳�״̬
            Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
            exitTimer = new Timer(); //������ʱ��
            exitTimer.schedule(task, 2000); //���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е����� 
        
        }else{                                                                           
            finish();  
            System.exit(0);         
        }	  		
	}



	
	
}
