package yan.girl.activity;

import yan.girl.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ImageShowAct extends Activity{
	private ImageView imageView;
	private Bitmap mBitmap;
	private int flag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		this.setTitle("大图浏览");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);			
		this.setContentView(R.layout.iamge_show);
		
		imageView = (ImageView) this.findViewById(R.id.disp_image);
		
		
		Intent intent = getIntent();
		flag = intent.getFlags();
		
		System.out.println("imageview flag "+flag);
		if(flag == PhotoViewerAct.GRIDVIEW_FLAG_VIEW || flag == DiaryNewEditAct.GRIDVIEW_FLAG_TOSAVE){
			if(intent.getExtras()!=null){
				String pathname = intent.getStringExtra("pathname");
				System.out.println("intent get pathname "+pathname);
		        mBitmap = BitmapFactory.decodeFile(pathname);
		        //第二种方式，采用imgid来获取图片
				/*String imgid = intent.getStringExtra("imgid");
				Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
						Long.parseLong(imgid));   

				try {
					mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), 
							uri);
				} catch (Exception e) {
				}*/
				
		        imageView.setImageBitmap(mBitmap);
		        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);			
				
			}
		}else if(flag == DiaryDetailAct.GRIDVIEW_FLAG_VIEW){
			if(intent.getExtras()!=null){
				mBitmap = intent.getParcelableExtra("bitmap");

		        imageView.setImageBitmap(mBitmap);
		        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);			
				
			}		
		}
	
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		imageView.startAnimation(animation);
		
	}
	
	private static final int MENU_ITEM_ACQUIRE = Menu.FIRST;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		if(flag == DiaryNewEditAct.GRIDVIEW_FLAG_TOSAVE)
			menu.add(0, MENU_ITEM_ACQUIRE, 0, "添加到日记本");		
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case MENU_ITEM_ACQUIRE:
			//将图片数据传入写日记的Activity中
			System.out.println("before add image");
			Intent intent = new Intent();
			intent.putExtra("bitmap", mBitmap);
			setResult(RESULT_OK, intent);
			System.out.println("after add image");
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
