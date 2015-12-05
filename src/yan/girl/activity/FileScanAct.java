package yan.girl.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yan.girl.utils.OpenFileIntent;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FileScanAct extends ListActivity{
    
	private String pathname = null;//定义广义路径变量
    
	List<Map<String,Object>> dataList=null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("文件浏览器");
        setContentView(R.layout.mainlist_filescan);
        pathname = getSDPath();
        System.out.println("SD Path: "+pathname);
        refreshList(pathname);
    }	

    private String getSDPath()
    {
    	return Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    }
 
    private void refreshList(String pathname)
    {
    	setTitle("文件查看 > "+pathname);
    	dataList=buildListForSimpleAdapter(pathname);
    	String[] from = new String[]{"img","filename","pathname"};
    	int[] to = new int[]{R.id.file_img,R.id.filename,R.id.pathname};
    	SimpleAdapter adapter = new SimpleAdapter(this, dataList,R.layout.listitem_filescan, from, to);
    	setListAdapter(adapter);
    	setSelection(0);
    }
    
    private List<Map<String,Object>> buildListForSimpleAdapter(String pathname)
    {
    	String sdState = Environment.getExternalStorageState();  
        if (sdState.equals(Environment.MEDIA_MOUNTED)) { 
    	
	    	File[] files = new File(pathname).listFiles();
	    	
	    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	    	Map<String,Object> rootmap = new HashMap<String,Object>();
	    	rootmap.put("img", R.drawable.file_root);
	    	rootmap.put("filename", "/sdcard");
	    	rootmap.put("pathname", "Go to SD card");
	    	list.add(rootmap);
	    	
	    	Map<String,Object> parentmap = new HashMap<String,Object>();
	    	parentmap.put("img", R.drawable.file_paranet);
	    	parentmap.put("filename", "..");
	    	parentmap.put("pathname", "Go to parent");
	    	list.add(parentmap);
	    	
	    	for (File file:files) 
	    	{
	    		Map<String,Object> filemap = new HashMap<String,Object>();
				if(file.isDirectory())
					filemap.put("img", R.drawable.file_directory);
				else 
					//这里判断文件按类型，如MP3，则指定MP3图标 fileExtName(file).equals.("mp3")
					filemap.put("img", R.drawable.file_doc);
				
				filemap.put("filename", file.getName());
				filemap.put("pathname", file.getPath());//这里的path包含文件名
				list.add(filemap);
			}
	    	
	    	if (!list.isEmpty()) {     
	    	     Collections.sort(list, new Comparator<Map<String, Object>>() {
	    	     	@Override
	    	     	public int compare(Map<String, Object> object1,
	    	     	Map<String, Object> object2) {
	    			//根据文件名排序
	    	          	return ((String)object1.get("filename")).compareTo((String)object2.get("filename")); 
	    	     	}     
	    	     });     
	    	}

	    	return list; 
		}
		return null;
    }   
    
    private String fileExtName(File file)
    {
    	String extName=file.getName().substring( file.getName().lastIndexOf(".") + 1,file.getName().length() ).toLowerCase();
		return extName;   
    }
    
	//----------------------------------------------------------------------------- 
	@Override
	protected void onListItemClick(ListView list, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(list, v, position, id);
		Map<String,Object> map= (Map<String,Object>)list.getItemAtPosition(position);
		
		
		if(position == 0){
			pathname = getSDPath();
			refreshList(pathname);
		}else if(position == 1){
				try {
					goToParent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}else{
			pathname =  map.get("pathname").toString(); 
			File file = new File(pathname);
			if(file.isDirectory())
				refreshList(pathname);
			else {
				System.out.println("Open file : "+pathname);
				
				Intent fielIntent = OpenFileIntent.openFile(pathname);
				startActivity(fielIntent);	
				
			}
		}
	}
	
	private void goToParent() throws IOException {
		// TODO Auto-generated method stub
		File file = new File(pathname);
		File file_parent = file.getParentFile();
		//当需要遍历到所有文件夹时(包括系统文件夹),则会用到父目录,而为了阻止用户访问系统文件夹，
		//即当前目录为/sdcard时,不再向上回转到父目录
		
		/*System.out.println("file_parent getName : "+file_parent.getName());
		System.out.println("file_parent getPath : "+file_parent.getPath());
		System.out.println("file_parent getAbsolutePath : "+file_parent.getAbsolutePath());
		System.out.println("file_parent getCanonicalPath : "+file_parent.getCanonicalPath());*/
		
		//第二个注释是因为SDK不一致，用父目录来判断可能会导致达不到效果，如有些手机父目录是/mnt,并不是/,所以用当前目录判断
		if(/*file_parent == null*//*file_parent.getPath().equals("/")*/file.getPath().equals(getSDPath()/*"/sdcard"*/)){
			Toast.makeText(this, /*"This is root"*/"This is sd card", Toast.LENGTH_SHORT).show();
			refreshList(pathname);
		}else{
			pathname = file_parent.getAbsolutePath();
			refreshList(pathname);
		}
		
	}

	//----------------------------------------------------------------------------- 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		File file = new File(pathname);
//		File file_parent = file.getParentFile();
		
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {  
        	if(/*file_parent != null*/!file.getPath().equals(getSDPath())){
        		try {
					goToParent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}else{
        		finish();
        	}
        	return false;  	
        }                          
        
		return super.onKeyDown(keyCode, event);
	}


	
}