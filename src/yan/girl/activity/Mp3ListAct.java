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
		setTitle("音乐");
		setContentView(R.layout.mainlist_mp3);
		
        this.pathname = getSDPath();
        
        Toast.makeText(this, "正在读取MP3信息...", Toast.LENGTH_LONG).show();
        
//		refreshMp3List(pathname);//自己手动搜索MP3文件
        
        //根据Media提供的ContentProvider来搜集MP3信息
        refreshMp3ListFromCP("");
	}
	
    private String getSDPath()
    {
    	return Environment.getExternalStorageDirectory().toString();
    }


	//----------------------------------------------------------------------------- 
    /**
     * 根据用户给出的文件全名来得到文件信息,参数为空时,搜索所有文件信息
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
   			//根据文件名排序
   	          	return ((String)object1.get("music_name")).compareTo((String)object2.get("music_name")); 
   	     	}     
   	     });     
   	}
	    return list;
	}

    /**
     * 把按微秒计算的时长转换为正常及时方法
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
    
/*	将日期型如date 2007-09-11 转化成 '20070911'
	Trim(str(date.Year())) + PadL(Trim(str(date.Month())),2) + PadL(Trim(str(date.Day())),2);
	*/

    
	//-----------------------------------------------------------------------------  
    /**
     * 根据用户给出的路径来自动搜索文件
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
    
	//如果信息多可采用实体类
	private String musicName;
	private String musicSize;
	/**
	 * 根据设置的路径去查找响应的文件，并添加到list中
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


/*	//获取SD卡上所有图片
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
                                //获取bmp,jpg,png格式的图片
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
		
		//音乐文件使用自己写的播放器来播放，可以使用service来实现，避免关闭activity时音乐停止
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
		
		menu.add(0,	MENU_ITEM_SEARCH, 0, "搜索MP3");
		menu.add(0, MENU_ITEM_FILESCAN, 1, "文件浏览");		
		menu.add(0, MENU_ITEM_SET, 2, "设置");
		menu.add(0, MENU_ITEM_EXIT, 3, "退出");
		
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
			System.out.println("ListView--->KEYCODE_BACK ："+keyCode);
			exitByDoubleClick();
			return false;			
		}

		return super.onKeyDown(keyCode, event);
	}


	private static Boolean isExitPrepared = false;
    /**
     * 在2秒内连续按下返回键则退出程序,函数需与静态变量isExit一起使用
     * 	private static Boolean isExitPrepared = false;
     */
	private void exitByDoubleClick()
	{		
	    Timer  exitTimer = null;
	    TimerTask task = new TimerTask() {            
	        @Override  
	        public void run() {  
	            isExitPrepared = false;  //取消准备退出的状态	            
	        }  
	    };
        if(isExitPrepared == false ) 
        {  
            isExitPrepared = true;  //进入准备准备退出状态
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
            exitTimer = new Timer(); //启动定时器
            exitTimer.schedule(task, 2000); //如果2秒钟内没有按下返回键，则启动定时器
        
        }else{                                                                           
            finish();  
            System.exit(0);         
        }	  		
	}	
	
	//------------------------------------------------------------------------------------------
	
}
