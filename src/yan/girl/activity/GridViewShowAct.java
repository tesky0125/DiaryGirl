package yan.girl.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GridViewShowAct extends Activity implements OnItemClickListener{
	private GridView gridView;  
	private List<Map<String, Object>> listItems ;   
	
	private int flag ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setTitle("图片浏览");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		setContentView(R.layout.main_gridview);
		
		Intent intent = this.getIntent();
		if(intent!=null)
			flag = intent.getFlags();
		
		gridView = (GridView) findViewById(R.id.gridview); 		
		
		Toast.makeText(this, "正在扫描SD图片...", Toast.LENGTH_SHORT).show();	
		
		listItems = buildListForAdapter();
						
		ImageAdapter adapter = new ImageAdapter(this);
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(this);
	}	

	public static final int GRIDVIEW_FLAG_VIEW = 2;
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		System.out.println("position "+position);
//		String pathname =listItems.get(position).get("pathname").toString();//传入的是THUMBNAIL的路径
		String imgid = listItems.get(position).get("imgid").toString();
		Intent intent = new Intent(this,ImageShowAct.class);
//		intent.putExtra("pathname", pathname);
		intent.putExtra("imgid", imgid);
		if(flag == DiaryNewEditAct.GRIDVIEW_FLAG_TOSAVE){
			intent.setFlags(flag);
		}else{
			intent.setFlags(GRIDVIEW_FLAG_VIEW);
		}
		this.startActivityForResult(intent, 0);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		Bitmap bitmap = null;
		if(data!=null){
			bitmap = data.getParcelableExtra("bitmap");		
			System.out.println("before add image...");
			Intent intent = new Intent();
			intent.putExtra("bitmap", bitmap);
			setResult(RESULT_OK, intent);
			System.out.println("before add image...");
			finish();
		}
	}

	private List<Map<String, Object>> buildListForAdapter()
	{
		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();   
		
		//方式一：MediaStore.Images.Media 适合根据DATA字段读取一张原图
/*		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
        		MediaStore.Images.Media.DISPLAY_NAME,
        		MediaStore.Images.Media.DATA
        		};
        //不适合做图标，查找缩略图方法还需改进
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, 
                null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

       
        while(cursor.moveToNext()){

        	String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        	String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        	String pathname = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        	
        	
        	System.out.println("cursor :"+id+","+title+","+pathname);
        	
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("id", id );
            map.put("title", title);
            map.put("pathname", pathname);
            list.add(map);					
 			    

        }
        cursor.close();
 */		        
		//方式二：MediaStore.Images.Thumbnails 适合读取一系列缩略图，还可以根据IMAGE_ID构造URI提取原图，,但是按文件夹分类不易
		String[] projection = new String[]{
				MediaStore.Images.Thumbnails._ID,
				MediaStore.Images.Thumbnails.IMAGE_ID,
        		MediaStore.Images.Thumbnails.DATA
        		};

        final Cursor cursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, 
                null, null, MediaStore.Images.Thumbnails.DEFAULT_SORT_ORDER);
  

        while(cursor.moveToNext()){

        	String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID));
        	String imgid = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
        	String pathname = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
        	
        	System.out.println("cursor :"+id+","+imgid+","+pathname);        
        	     	
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("id", id );
            map.put("imgid", imgid );
            map.put("pathname", pathname);
            list.add(map);					
 			    

        }
        cursor.close();
 			
		return list;		
	}
	
	

	
	/**
     * Adapter for our image files.
     */
	private class ImageAdapter extends BaseAdapter {

        private final Context context; 
        private final Map<Integer,View> mViewMaps;
        private int mItemBackground;
        
        public ImageAdapter(Context context) {
            this.context = context;
            mViewMaps = new HashMap<Integer,View> (listItems.size());
            TypedArray a = obtainStyledAttributes(R.styleable.ItemBackground);
            mItemBackground = a.getResourceId(R.styleable.ItemBackground_android_galleryItemBackground, 0);
            //定义可以重复使用.可回收
            a.recycle();
        }
        public int getCount() {
            return listItems.size();
        }

        public Object getItem(int position) {
            return listItems.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView picView = null;

/*            String id = listItems.get(position).get("id").toString();
        	String title = listItems.get(position).get("title").toString();
        	String pathname =listItems.get(position).get("pathname").toString();
        	//从path那么取图太慢，应直接用缩略图的ContentProvider
*/        	
            //方式一：在getview时，解析出thumbnail  
			//问题出在这，为什么滑动后再滑回来View就变了？
            String id = listItems.get(position).get("id").toString();
			String imgid = listItems.get(position).get("imgid").toString();
			String pathname =listItems.get(position).get("pathname").toString();
        	System.out.println("map :"+id+","+imgid+","+pathname);
        	
            View curView = mViewMaps.get(position);
            if(curView==null){        	           	
            	System.out.println("getView by listItems["+position+"].");

                picView = new ImageView(context);
                
//              第一种，根据缩略图路径，而不用图片路径
//                Bitmap bitmap = BitmapFactory.decodeFile(pathname,options);
                
//                第一种，如果是图片路径，则采用下面方式，先读取图片信息，再根据信息读取出缩略图
//              BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inJustDecodeBounds = true; 	
//				//获取图片宽、高信息
//              Bitmap bitmap = BitmapFactory.decodeFile(pathname,options);
//              options.inJustDecodeBounds = false;
//		        //计算缩放比
//		        int ratio = (int)(options.outHeight / (float)200);
//		        if (ratio <= 0)
//		        	ratio = 1;
//		        options.inSampleSize = ratio;
//		        //重新读入图片，注意这次options.inJustDecodeBounds 设为 false
//		        bitmap=BitmapFactory.decodeFile(pathname,options);
//
//	        
//              第二种根据路径提取原始图片，然后根据原始图片提取出缩略图，适合平台2.2
//              Bitmap originBitmap = BitmapFactory.decodeFile(pathname);
//              Bitmap bitmap = ThumbnailUtils.extractThumbnail(originBitmap,30,30) ;

		        
//				第三种根据图片路径获得缩略图
                BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false				
				Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                		getContentResolver(), Integer.parseInt(imgid), MediaStore.Images.Thumbnails.MICRO_KIND,
                		options); 
				options.inJustDecodeBounds = false;

				 
                picView.setImageBitmap(bitmap);
                picView.setBackgroundResource(mItemBackground);
                picView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picView.setPadding(6, 6, 6, 6);
                picView.setLayoutParams(new GridView.LayoutParams(60,60));
                mViewMaps.put(position, picView);

            }else{
            	System.out.println("getView by mViewMaps["+position+"].");
            	picView = (ImageView) curView;
            }
                                
            return picView;
        }
    }





}
