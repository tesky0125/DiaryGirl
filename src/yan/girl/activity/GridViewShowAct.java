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
//		setTitle("ͼƬ���");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		setContentView(R.layout.main_gridview);
		
		Intent intent = this.getIntent();
		if(intent!=null)
			flag = intent.getFlags();
		
		gridView = (GridView) findViewById(R.id.gridview); 		
		
		Toast.makeText(this, "����ɨ��SDͼƬ...", Toast.LENGTH_SHORT).show();	
		
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
//		String pathname =listItems.get(position).get("pathname").toString();//�������THUMBNAIL��·��
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
		
		//��ʽһ��MediaStore.Images.Media �ʺϸ���DATA�ֶζ�ȡһ��ԭͼ
/*		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
        		MediaStore.Images.Media.DISPLAY_NAME,
        		MediaStore.Images.Media.DATA
        		};
        //���ʺ���ͼ�꣬��������ͼ��������Ľ�
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
		//��ʽ����MediaStore.Images.Thumbnails �ʺ϶�ȡһϵ������ͼ�������Ը���IMAGE_ID����URI��ȡԭͼ��,���ǰ��ļ��з��಻��
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
            //��������ظ�ʹ��.�ɻ���
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
        	//��path��ôȡͼ̫����Ӧֱ��������ͼ��ContentProvider
*/        	
            //��ʽһ����getviewʱ��������thumbnail  
			//��������⣬Ϊʲô�������ٻ�����View�ͱ��ˣ�
            String id = listItems.get(position).get("id").toString();
			String imgid = listItems.get(position).get("imgid").toString();
			String pathname =listItems.get(position).get("pathname").toString();
        	System.out.println("map :"+id+","+imgid+","+pathname);
        	
            View curView = mViewMaps.get(position);
            if(curView==null){        	           	
            	System.out.println("getView by listItems["+position+"].");

                picView = new ImageView(context);
                
//              ��һ�֣���������ͼ·����������ͼƬ·��
//                Bitmap bitmap = BitmapFactory.decodeFile(pathname,options);
                
//                ��һ�֣������ͼƬ·������������淽ʽ���ȶ�ȡͼƬ��Ϣ���ٸ�����Ϣ��ȡ������ͼ
//              BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inJustDecodeBounds = true; 	
//				//��ȡͼƬ������Ϣ
//              Bitmap bitmap = BitmapFactory.decodeFile(pathname,options);
//              options.inJustDecodeBounds = false;
//		        //�������ű�
//		        int ratio = (int)(options.outHeight / (float)200);
//		        if (ratio <= 0)
//		        	ratio = 1;
//		        options.inSampleSize = ratio;
//		        //���¶���ͼƬ��ע�����options.inJustDecodeBounds ��Ϊ false
//		        bitmap=BitmapFactory.decodeFile(pathname,options);
//
//	        
//              �ڶ��ָ���·����ȡԭʼͼƬ��Ȼ�����ԭʼͼƬ��ȡ������ͼ���ʺ�ƽ̨2.2
//              Bitmap originBitmap = BitmapFactory.decodeFile(pathname);
//              Bitmap bitmap = ThumbnailUtils.extractThumbnail(originBitmap,30,30) ;

		        
//				�����ָ���ͼƬ·���������ͼ
                BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true; // �����˴�����һ��Ҫ�ǵý�ֵ����Ϊfalse				
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
