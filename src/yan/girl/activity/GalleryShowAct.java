package yan.girl.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import yan.girl.activity.R;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;

public class GalleryShowAct extends Activity implements ViewFactory, OnItemSelectedListener {

	private List<Map<String, Object>> listItems ; 	
	private ImageSwitcher imgSwitcher;
	private Gallery gallery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gallery_show);
		
		listItems = buildListForAdapter();		
        
        System.out.println("OnCreate-->imgSwitcher");
        imgSwitcher = (ImageSwitcher) this.findViewById(R.id.switcher);
        imgSwitcher.setFactory(this);
        imgSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imgSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        //imgSwitcher的资源在gallery选中时设置
        //为imgSwitcher设置OnTouchListener等监听器，并传送到gallery上
        imgSwitcher.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gallery.onTouchEvent(event);
				return true;
			}
		});
        

        System.out.println("OnCreate-->gallery");
        gallery = (Gallery) this.findViewById(R.id.gallery);              
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setSelection(listItems.size()/2);
        gallery.setOnItemSelectedListener(this);		
	}
	
	
	//ImageSwitcher
	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		System.out.println("makeView");
		ImageView imgView = new ImageView(this);
		//setImageResource在Gallery的onItemSelected中执行
//		imgView.setBackgroundColor(0xFF000000);
//		imgView.setBackgroundResource(R.drawable.bg_body);
		imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imgView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return imgView;
	}

	//----------------------------------------------------------------------------- 
	//记录当前位置
	private int curPosition = -1;
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
			System.out.println("onItemSelected");
			//之前必须 imgSwitcher.setFactory(this);
		if(curPosition != position){
	//		imgSwitcher.setImageResource(mImageIds[position]);
			Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
					Long.parseLong(listItems.get(position%listItems.size()).get("imgid").toString()));   
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), 
						uri);
			} catch (Exception e) {
			}		
			imgSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
			imgSwitcher.setBackgroundResource(R.drawable.grid_frame);
			curPosition = position;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub		
	}

	//-------------------------------------------------------------------------------
	private List<Map<String, Object>> buildListForAdapter()
	{
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();   
			        
		//采用方式二：MediaStore.Images.Thumbnails 适合读取一系列缩略图，还可以根据IMAGE_ID构造URI提取原图
		String[] projection = new String[]{
				MediaStore.Images.Thumbnails.IMAGE_ID,
        		MediaStore.Images.Thumbnails.DATA
        		};

        final Cursor cursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, 
                null, null, MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);
  
       
        while(cursor.moveToNext()){

        	String imgid = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
        	String pathname = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
        	        	
        	System.out.println("cursor :"+imgid+","+pathname);
        	
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("imgid", imgid );
            map.put("pathname", pathname);
            list.add(map);					 			    
        }
        cursor.close();
 			
		return list;		
	}
	

	
	//-------------------------------------------------------------------------------------------------
	
	
	public class ImageAdapter extends BaseAdapter{	
		private Context mCtx;
		
		public ImageAdapter(Context mCtx) {
			this.mCtx = mCtx;
		}
	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE/*listItems.size()*/;
		}
	
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItems.get(position%listItems.size());//根据position转换为对应的object
		}
	
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position%listItems.size();
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imgView = null;
			
			imgView = new ImageView(mCtx);
			String pathname =listItems.get(position%listItems.size()).get("pathname").toString();
			
			Bitmap mBitmap = BitmapFactory.decodeFile(pathname);//
			
			imgView.setImageBitmap(mBitmap);
			imgView.setAdjustViewBounds(true);
			imgView.setLayoutParams(new Gallery.LayoutParams(50,50/*LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT*/));
			imgView.setBackgroundResource(R.drawable.grid_frame);

			
			return imgView;
		}
		
	}	//class ImageAdapter
	
	
	
	private static final int MENU_ITEM_SET = Menu.FIRST;
	private static final int MENU_ITEM_EXIT = Menu.FIRST+1;
	private static final int MENU_ITEM_THUMBNAILS = Menu.FIRST+2;	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		
		menu.add(0, MENU_ITEM_THUMBNAILS, 0, "缩略图浏览");		
		menu.add(0, MENU_ITEM_SET, 1, "设置");
		menu.add(0, MENU_ITEM_EXIT,2, "退出");
		
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(item.getItemId())
		{
		case MENU_ITEM_SET:
			intent = new Intent(this,ConfigureAct.class);
			startActivity(intent);			
			break;
		case MENU_ITEM_THUMBNAILS:
			intent = new Intent(this,/*GridViewShowAct*/PhotoViewerAct.class);
			startActivity(intent);	
			break;			
		case MENU_ITEM_EXIT:
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	//----------------------------------------------------------------------------- 

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		

	}


	//----------------------------------------------------------------------------- 
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

//这个自定义类最好单独在一个java文件中，一定不要包含在另一个类中
class DetailGallery extends android.widget.Gallery{

	public DetailGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return super.onFling(e1, e2, velocityX/5, velocityY);
	}
	
}
