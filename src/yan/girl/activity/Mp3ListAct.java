package yan.girl.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import yan.girl.activity.R;
import yan.girl.services.Mp3PlayerService;
import yan.girl.utils.OpenFileIntent;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Mp3ListAct extends ListActivity{
	
	private String pathname = null;
	
	List<Map<String,Object>> dataList=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("����");
		setContentView(R.layout.mainlist_mp3);
		
        this.pathname = getSDPath();
        
        Toast.makeText(this, "���ڶ�ȡMP3��Ϣ...", Toast.LENGTH_LONG).show();
        
//		refreshMp3List(pathname);//�Լ��ֶ�����MP3�ļ�
        
        //����Media�ṩ��ContentProvider���Ѽ�MP3��Ϣ
        refreshMp3ListFromCP("");
	}
	
    private String getSDPath()
    {
    	return Environment.getExternalStorageDirectory().toString();
    }


	//----------------------------------------------------------------------------- 
    /**
     * �����û��������ļ�ȫ�����õ��ļ���Ϣ,����Ϊ��ʱ,���������ļ���Ϣ
     * @param mp3Qname
     */
    private void refreshMp3ListFromCP(String mp3Qname)
    {
    	dataList = getMp3Infos(mp3Qname);
    	String[] from = new String[]{"music_name","music_size","music_path"};
    	int[] to = new int[]{R.id.music_name,R.id.music_size,R.id.music_hintpath};
/*    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_mp3, cursor, from, to);*/
    	SimpleAdapter adapter = new SimpleAdapter(this, dataList,R.layout.listitem_mp3, from, to);
    	setListAdapter(adapter);
    	setSelection(0);   	
    }
  
//	private String mp3Title="";
	private String mp3DispName=""; 
	private String mp3Duration=""; 
	private String mp3Path="";
	
    public List<Map<String,Object>> getMp3Infos(String mp3Qname) 
    {
    	Cursor cursor = null;
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    	
	    try
	    { 
	    	String [] projection = {
	//	    MediaStore.Audio.Media._ID,
//		    MediaStore.Audio.Media.TITLE,
		    MediaStore.Audio.Media.DISPLAY_NAME, 		    
	//	    MediaStore.Audio.Media.ARTIST,
	//	    MediaStore.Audio.Media.ALBUM,
		    MediaStore.Audio.Media.DURATION,
	//	    MediaStore.Audio.Media.SIZE
		    MediaStore.Audio.Media.DATA, // --> Location
	    	};
	/*    	String selection = MediaStore.Audio.Media.DATA + " = ?"; // like
	    	String[] selectionArgs = {mp3Qname}; */
	    	
	    	String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";

	//    	cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
	    	cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
	
		    while(cursor.moveToNext())
		    {
		    	Map<String,Object> map= new HashMap<String,Object>();
//			    mp3Title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)).toString();
			    mp3DispName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)).toString();
			    mp3Duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toString();
			    mp3Path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)).toString();
			    
				map.put("music_name", mp3DispName);
				map.put("music_size", transDuration(mp3Duration));
				map.put("music_path", mp3Path);
				
				list.add(map);
				
			    System.out.println(mp3DispName+","+mp3Duration+","+mp3Path);
		    }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    } 
	    
    	if (!list.isEmpty()) {     
   	     Collections.sort(list, new Comparator<Map<String, Object>>() {
   	     	@Override
   	     	public int compare(Map<String, Object> object1,
   	     	Map<String, Object> object2) {
   			//�����ļ�������
   	          	return ((String)object1.get("music_name")).compareTo((String)object2.get("music_name")); 
   	     	}     
   	     });     
   	}
	    return list;
	}

    /**
     * �Ѱ�΢������ʱ��ת��Ϊ������ʱ����
     * @param duration
     * @return
     */
    private String transDuration(String duration){
    	int time = Integer.parseInt(duration)/1000;
    	int munite = time/60;
    	int second = time%60;
    	duration = (munite/10>0?munite:("0"+munite))+":"+(second/10>0?second:("0"+second));
		return duration;    	
    }
    
/*	����������date 2007-09-11 ת���� '20070911'
	Trim(str(date.Year())) + PadL(Trim(str(date.Month())),2) + PadL(Trim(str(date.Day())),2);
	*/

    
	//-----------------------------------------------------------------------------  
    /**
     * �����û�������·�����Զ������ļ�
     * @param pathname
     */
/*    private void refreshMp3List(String pathname)
    {
    	setTitle("MP3 > "+pathname);
    	dataList = setPath2searchFiles(pathname);
    	String[] from = new String[]{"music_name","music_size"};
    	int[] to = new int[]{R.id.music_name,R.id.music_size};
    	SimpleAdapter adapter = new SimpleAdapter(this, dataList,R.layout.list_mp3, from, to);
    	setListAdapter(adapter);
    	setSelection(0);
    }*/	
    
	//�����Ϣ��ɲ���ʵ����
	private String musicName;
	private String musicSize;
	/**
	 * �������õ�·��ȥ������Ӧ���ļ�������ӵ�list��
	 * @param pathname
	 * @return
	 */
/*	private List<Map<String,Object>> setPath2searchFiles(String pathname) 
	{
	    File[] files = new File(pathname).listFiles();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		searchFiles(files,list);
	    return list; 
	}
	 
	private void searchFiles(final File[] files,final List<Map<String,Object>> list) 
	{
	    for(int i=0 ; files!= null && i<files.length ;i++) 
	    {
		    if(files[i].isFile() && files[i].getName().endsWith(".mp3")){
		    	System.out.println("mp3 "+files[i].getName());
		    	musicName = files[i].getName();
		    	musicSize = files[i].getPath();
		    	Map<String,Object> mp3map = new HashMap<String,Object>();
		    	mp3map.put("music_name", musicName);
		    	mp3map.put("music_size", musicSize);
		    	list.add(mp3map);
		    }else if(files[i].isDirectory()) {
		    	  
			    final File[] childFiles = new File(files[i].getAbsolutePath()).listFiles();
//			    searchFiles(childFiles,list);
				new Thread(new Runnable() {
	                    public void run() {
	                        searchFiles(childFiles,list);
	                    }
	                }).start();
			}
	    }
    }*/


/*	//��ȡSD��������ͼƬ
	List<String> fileList = new ArrayList<String>();
	private String PATH = getSDPath();
    public void getImgFromSDcard()
    {
            File file = new File(PATH);
                File[] files = file.listFiles();
                
                for(int i=0 ;i<files.length ;i++)
                {
                        if(files[i].isFile())
                        {
                                String filename = files[i].getName();
                                //��ȡbmp,jpg,png��ʽ��ͼƬ
                                if(filename.endsWith(".jpg")||filename.endsWith(".png")||filename.endsWith(".bmp"))
                                {
                                        String filePath = files[i].getAbsolutePath();
                                        fileList.add(filePath);
                                }
                        }else if(files[i].isDirectory()){
                                PATH = files[i].getAbsolutePath();
                                getImgFromSDcard();
                        }
                }
    }*/
	
	//-------------------------------------------------------------------------------------------

	@Override
	protected void onListItemClick(ListView list, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(list, v, position, id);
		Map<String,Object> map= (Map<String,Object>)list.getItemAtPosition(position);
		
		String mp3Path = map.get("music_path").toString();
		System.out.println(mp3Path);
		
		/*Intent intent = OpenFileIntent.openFile(mp3Path);
		startActivity(intent);*/
		
		//�����ļ�ʹ���Լ�д�Ĳ����������ţ�����ʹ��service��ʵ�֣�����ر�activityʱ����ֹͣ
		/*Intent intent = new Intent(Mp3ListAct.this,Mp3PlayerService.class);
		intent.putExtra("filename", mp3Path);
		startService(intent);*/
		
		Intent intent = new Intent(Mp3ListAct.this,/*Mp3PlayerAct*/MusicHandleAct.class);
		intent.putExtra("filename", mp3Path);
		startActivity(intent);
	}

	
	
	//---------------------------------------------------------------------------------------
	private static final int MENU_ITEM_FILESCAN = Menu.FIRST ;	
	private static final int MENU_ITEM_SEARCH = Menu.FIRST+1;
	private static final int MENU_ITEM_SET = Menu.FIRST+2 ;	
	private static final int MENU_ITEM_EXIT = Menu.FIRST+3 ;

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		
		menu.add(0,	MENU_ITEM_SEARCH, 0, "����MP3");
		menu.add(0, MENU_ITEM_FILESCAN, 1, "�ļ����");		
		menu.add(0, MENU_ITEM_SET, 2, "����");
		menu.add(0, MENU_ITEM_EXIT, 3, "�˳�");
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(item.getItemId())
		{
		case MENU_ITEM_FILESCAN:
			intent = new Intent(this,FileScanAct.class);
			startActivity(intent);
			break;
		case MENU_ITEM_SEARCH:
			/*refreshMp3List(pathname);*/
			refreshMp3ListFromCP("");
			break;
		case MENU_ITEM_SET:
			intent = new Intent(this,ConfigureAct.class);
			startActivity(intent);			
			break;
		case MENU_ITEM_EXIT:
			finish();
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	//-----------------------------------------------------------------------------------------
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			System.out.println("ListView--->KEYCODE_BACK ��"+keyCode);
			exitByDoubleClick();
			return false;			
		}

		return super.onKeyDown(keyCode, event);
	}


	private static Boolean isExitPrepared = false;
    /**
     * ��2�����������·��ؼ����˳�����,�������뾲̬����isExitһ��ʹ��
     * 	private static Boolean isExitPrepared = false;
     */
	private void exitByDoubleClick()
	{		
	    Timer  exitTimer = null;
	    TimerTask task = new TimerTask() {            
	        @Override  
	        public void run() {  
	            isExitPrepared = false;  //ȡ��׼���˳���״̬	            
	        }  
	    };
        if(isExitPrepared == false ) 
        {  
            isExitPrepared = true;  //����׼��׼���˳�״̬
            Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
            exitTimer = new Timer(); //������ʱ��
            exitTimer.schedule(task, 2000); //���2������û�а��·��ؼ�����������ʱ��
        
        }else{                                                                           
            finish();  
            System.exit(0);         
        }	  		
	}	
	
	//------------------------------------------------------------------------------------------
	
}
