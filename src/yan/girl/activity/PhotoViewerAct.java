package yan.girl.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoViewerAct extends Activity {
    /** Called when the activity is first created. */
	private GridView gridView;  
	private List<Map<String, Object>> listBuckets ;   //���ڴ���ļ����б���Ϣ,�����ļ�����������ͼƬ����
	private List<Map<String,Object>> listPicItems;//���ڴ�Ÿ����ļ����е�ͼƬ��Ϣ,����·��������
	
	private static int UI_STATE = -1;
	
	private int flag ;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_gridview);
        
		Intent intent = this.getIntent();
		if(intent!=null)
			flag = intent.getFlags();
		
        gridView = (GridView) findViewById(R.id.gridview); 	
        
        buildListForBucketAdapter();
        
        ImageBucketAdapter adapter = new ImageBucketAdapter(PhotoViewerAct.this);
		gridView.setAdapter(adapter);		
		gridView.setOnItemClickListener(new OnBucketItemClickListener()); 
		UI_STATE = 0;
    }
    
    private class OnBucketItemClickListener implements OnItemClickListener{
    	@Override
    	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    		// TODO Auto-generated method stub
    		System.out.println("position "+position);

    		//�ڵ���ļ���ʱ��������activity��ת�������ø���GridView��adapter��Ȼ��ˢ��
    		//����1.����������֮ǰ����list����ø����ļ����µ�����ͼƬ��Ϣ������List<List<Map<String,Object>>>
    		//����2.�������ַ�ʽ���ף���Ӧ��������ļ�һ��������bucket��·����ʱ��ȡ���µ�����ͼƬ��Ϣ��ˢ��GridView
    		
    		Map<String, Object> map = listBuckets.get(position);	
    		String bucketpath = map.get("pathname").toString();
    		buildListForPicsAdapter(bucketpath);
    		//��ȡ�����ļ����е�ͼƬ����Ϣ��Ȼ����GridView������ʾ...
    		ImagePicsAdapter adapter = new ImagePicsAdapter(PhotoViewerAct.this);
    		gridView.setAdapter(adapter);
    		
    		/*Animation anim = AnimationUtils.loadAnimation(PhotoViewerAct.this, R.anim.alpha);
			LayoutAnimationController lac = new LayoutAnimationController(anim);
			lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
			gridView.setLayoutAnimation(lac);*/
			
    		gridView.setOnItemClickListener(new OnPicsItemClickListener());
    		UI_STATE = 1;
    	}
    }

	public static final int GRIDVIEW_FLAG_VIEW = 2;
	
    private class OnPicsItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos,
				long id) {
			// TODO Auto-generated method stub
			System.out.println("position "+pos);
			String pathname = listPicItems.get(pos).get("pathname").toString();
			Intent intent = new Intent(PhotoViewerAct.this,ImageShowAct.class);
			intent.putExtra("pathname", pathname);
			if(flag == DiaryNewEditAct.GRIDVIEW_FLAG_TOSAVE){
				intent.setFlags(flag);
			}else{
				intent.setFlags(GRIDVIEW_FLAG_VIEW);
			}	
			PhotoViewerAct.this.startActivityForResult(intent, 0);
		}
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
	
	
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(UI_STATE == 1){
			if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				System.out.println("KEYCODE_BACK ��"+keyCode);
				//�ڴ��ж�Activity���ڽ���,��������Ӧ����
				ImageBucketAdapter adapter = new ImageBucketAdapter(PhotoViewerAct.this);
				gridView.setAdapter(adapter);		
				gridView.setOnItemClickListener(new OnBucketItemClickListener()); 
				UI_STATE = 0;
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void buildListForPicsAdapter(String bucketpath)
    {
	    	File[] files = new File(bucketpath).listFiles(new ImageFilter());
	    	
	    	listPicItems = new ArrayList<Map<String,Object>>();
	    	
	    	for (File file:files) 
	    	{
	    		Map<String,Object> filemap = new HashMap<String,Object>();
				filemap.put("pathname", file.getPath());
				listPicItems.add(filemap);
			}

    }  
	
	private void buildListForBucketAdapter()
	{
		final List<Map<String, Object>> listPics = new ArrayList<Map<String, Object>>();   
		
		//��ʽһ��MediaStore.Images.Media �ʺϸ���DATA�ֶζ�ȡһ��ԭͼ
		String[] projection = new String[]{
				MediaStore.Images.Media._ID,
        		MediaStore.Images.Media.DISPLAY_NAME,
        		MediaStore.Images.Media.DATA,
        		MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        		};
        
        final Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, 
                null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

       
        while(cursor.moveToNext()){

        	String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
        	String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        	String pathname = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        	String bucket = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
        	
//        	System.out.println("cursor :"+id+","+title+","+pathname+","+bucket);
        	
            Map<String, Object> map = new HashMap<String,Object>();
            map.put("id", id );
            map.put("title", title);
            map.put("pathname", pathname);
            map.put("bucket", bucket);
            listPics.add(map);					
 			    

        }
        cursor.close();
        
        //��ȡ��ͼƬ�ļ�����Ϣ�б� 			
        listBuckets = new ArrayList<Map<String,Object>>();
        List<String> arrBucketsName = new ArrayList<String>();

        for (Iterator<Map<String, Object>> iterator = listPics.iterator(); iterator.hasNext();) {
			Map<String, Object> map = (Map<String, Object>) iterator.next();
			String bucketname = map.get("bucket").toString();
			String bucketpath = new File(map.get("pathname").toString()).getParent();
			if(!arrBucketsName.contains(bucketname)){
				arrBucketsName.add(bucketname);
				Map<String, Object> bucketinfo = new HashMap<String,Object>();
				
				bucketinfo.put("bucket", bucketname);
				bucketinfo.put("pathname", bucketpath);
				int cnt = cntOfImgBucket(bucketpath);
				bucketinfo.put("count", cnt);
				
				listBuckets.add(bucketinfo);
//				System.out.println("bucket info :"+bucketname+","+bucketpath+","+cnt);
			}
		}

        

	}   
    
	private int cntOfImgBucket (String bucketpath){

		File[] files = new File(bucketpath).listFiles(new ImageFilter());
		
		return files.length;
	}

	/**
	 * ���ڹ���ͼƬ�ļ�
	 * @author Administrator
	 *
	 */
	public class ImageFilter  implements FilenameFilter{
		
		public boolean isGif(String file){
			if (file.toLowerCase().endsWith(".gif")){
				return true;
			}else{
				return false;
			}
		}

		public boolean isJpg(String file){
		    if (file.toLowerCase().endsWith(".jpg")){
		    	return true;
		    }else{
		    	return false;
		    }
		}

		public boolean isPng(String file){
			if (file.toLowerCase().endsWith(".png")){
				return true;
			}else{
				return false;
			}
		}

		public boolean accept(File dir,String fname){
			return (isGif(fname) || isJpg(fname) || isPng(fname));
		}
		
	}

	/**
	 * 
	 * @author Administrator
	 *
	 */
	class ViewHolder {
		ImageView imgView ;
		TextView cntView;
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	private class ImageBucketAdapter extends BaseAdapter {

        private final Context context; 
        private int mItemBackground;
        
        public ImageBucketAdapter(Context context) {
            this.context = context;
            TypedArray a = obtainStyledAttributes(R.styleable.ItemBackground);
            mItemBackground = a.getResourceId(R.styleable.ItemBackground_android_galleryItemBackground, 0);
            //��������ظ�ʹ��.�ɻ���
            a.recycle();
        }
        public int getCount() {
            return listBuckets.size();
        }

        public Object getItem(int position) {
            return listBuckets.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
        	
            ViewHolder viewHolder = null;
            
            if(convertView == null){       	           	
                viewHolder = new ViewHolder();
                
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(R.layout.gridview_item, null);
	            
                viewHolder.imgView = (ImageView) convertView.findViewById(R.id.item_image);
    			viewHolder.cntView = (TextView) convertView.findViewById(R.id.item_text);
    			
    			
    			convertView.setTag(viewHolder);    
    			
            }else{
    			viewHolder = (ViewHolder) convertView.getTag();
    		}
            
            viewHolder.imgView.setImageResource(R.drawable.default_background);
			viewHolder.imgView.setBackgroundResource(R.drawable.grid_frame);
			viewHolder.imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			viewHolder.cntView.setText(listBuckets.get(position).get("bucket").toString()+
					"("+listBuckets.get(position).get("count").toString()+")");
    			    			   		
			convertView.setPadding(5, 5, 5, 5);
//			convertView.setLayoutParams(new GridView.LayoutParams(80, 80));
			
            return convertView;
        }
    }

	/**
	 * 
	 * @author Administrator
	 *
	 */
	private class ImagePicsAdapter extends BaseAdapter {

        private final Context context; 
        private final Map<Integer,View> mViewMaps;
        private int mItemBackground;
        
        public ImagePicsAdapter(Context context) {
            this.context = context;
            mViewMaps = new HashMap<Integer,View> (listPicItems.size());
            TypedArray a = obtainStyledAttributes(R.styleable.ItemBackground);
            mItemBackground = a.getResourceId(R.styleable.ItemBackground_android_galleryItemBackground, 0);
            //��������ظ�ʹ��.�ɻ���
            a.recycle();
        }
        public int getCount() {
            return listPicItems.size();
        }

        public Object getItem(int position) {
            return listPicItems.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView picView = null;
      				
			String pathname =listPicItems.get(position).get("pathname").toString();
        	
            View curView = mViewMaps.get(position);
            if(curView==null){        	           	
            	System.out.println("getView by listPicItems["+position+"].");

                picView = new ImageView(context);
                
//              ��һ�֣������ͼƬ·������������淽ʽ���ȶ�ȡͼƬ��Ϣ���ٸ�����Ϣ��ȡ������ͼ
                BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true; 	
				//��ȡͼƬ������Ϣ
				Bitmap bitmap = BitmapFactory.decodeFile(pathname,options);
				options.inJustDecodeBounds = false;
		        //�������ű�
		        int ratio = (int)(options.outHeight / (float)200);
		        if (ratio <= 0)
		        	ratio = 1;
		        options.inSampleSize = ratio;
		        //���¶���ͼƬ��ע�����options.inJustDecodeBounds ��Ϊ false
		        bitmap=BitmapFactory.decodeFile(pathname,options);
				 
                picView.setImageBitmap(bitmap);
//                picView.setBackgroundResource(mItemBackground);
                picView.setBackgroundResource(R.drawable.grid_frame);
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
