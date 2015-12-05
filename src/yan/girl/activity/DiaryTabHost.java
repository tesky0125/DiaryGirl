package yan.girl.activity;

import yan.girl.activity.R;
import yan.girl.database.SDCardDatabase;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class DiaryTabHost extends TabActivity{

	private SDCardDatabase sdCardDB = null;

	
	private TabHost tabHost=null;
	private TabSpec tabSpec=null;//这样可以保证对一个变量的重用，不至于定义三个或更多的变量
	private Intent intent=null;//同上
	
	private static final String TAB_TAG_DIARY = "diary";
	private static final String TAB_TAG_MUSIC = "music";
	private static final String TAB_TAG_GALLERY = "gallery";
	
	private RadioGroup radioTab=null;
	private RadioButton radioBtnDiary=null;
	private RadioButton radioBtnMusic=null;
	private RadioButton radioBtnGallery=null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);		
        
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setTitle(" 恋恋笔记本 ");
        setContentView(R.layout.main_tabhost);//如果用此函数设置VIEW的显示，那么布局文件一定要按照规定来设置
        
        //当系统正在扫描SD卡时，会出现程序启动缓慢，应给出进度条提示。
        Toast.makeText(this, "检查数据库中...", Toast.LENGTH_SHORT).show();
        sdCardDB = new SDCardDatabase();
        sdCardDB.initSDDbTable();                
            
        initWidgetListener();

        createTabItems();
      
    }


	private void initWidgetListener() {
		
		//        radioTab=(RadioGroup)findViewById(R.id.radio_tab);
		//        radioTab.setOnCheckedChangeListener(radioListener);
		        
		        radioBtnDiary=(RadioButton)findViewById(R.id.tab1);
		        radioBtnMusic=(RadioButton)findViewById(R.id.tab2);
		        radioBtnGallery=(RadioButton)findViewById(R.id.tab3);
		        radioBtnDiary.setOnCheckedChangeListener(radioItemListener);
		        radioBtnMusic.setOnCheckedChangeListener(radioItemListener);
		        radioBtnGallery.setOnCheckedChangeListener(radioItemListener);
	}


	private void createTabItems() {
		
		tabHost=this.getTabHost();
        
        tabSpec=tabHost.newTabSpec(TAB_TAG_DIARY);
        tabSpec.setIndicator(TAB_TAG_DIARY);
        intent=new Intent(DiaryTabHost.this,DiaryListAct.class);
        tabSpec.setContent(intent/*R.id.widget_layout_blue*/);
        tabHost.addTab(tabSpec);
        
        tabSpec=tabHost.newTabSpec(TAB_TAG_MUSIC);
        tabSpec.setIndicator(TAB_TAG_MUSIC);
        intent=new Intent(DiaryTabHost.this,Mp3ListAct.class);
        tabSpec.setContent(intent/*R.id.widget_layout_blue*/);
        tabHost.addTab(tabSpec);
        
        tabSpec=tabHost.newTabSpec(TAB_TAG_GALLERY);
        tabSpec.setIndicator(TAB_TAG_GALLERY);
        intent=new Intent(DiaryTabHost.this,GalleryShowAct.class);
        tabSpec.setContent(intent/*R.id.widget_layout_blue*/);
        tabHost.addTab(tabSpec);
	}
    

 
	//----------------------------------------------------------------------------- 
 
/*   //第一种是针对RedioGroup的监听，这样可以监听到每一个RadioButton，省去用每个RadioButton去监听。
    RadioGroup.OnCheckedChangeListener radioListener =new RadioGroup.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			switch(checkedId)
			{
			case R.id.tab1:
				Log.v("TabGroup", "radioListener_tab1");
				tabHost.setCurrentTabByTag(TAB_TAG_1);
				break;
			case R.id.tab2:
				Log.v("TabGroup", "radioListener_tab2");
				tabHost.setCurrentTabByTag(TAB_TAG_2);
				break;
			case R.id.tab3:
				Log.v("TabGroup", "radioListener_tab3");
				tabHost.setCurrentTabByTag(TAB_TAG_3);
				break;
			default :
					break;
			}
		}
    	
    };*/
    
    //第二种是针对每个RedioButton的监听,如果不判断isChecked，则会导致两个RadioButton都会改变，因为都被Change过。
    CompoundButton.OnCheckedChangeListener radioItemListener=new CompoundButton.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			
			if(isChecked)
			{
				switch(buttonView.getId())
				{
				case R.id.tab1:
					Log.v("TabGroup", "radioItemListener_tab1");
					tabHost.setCurrentTabByTag(TAB_TAG_DIARY);

					break;
				case R.id.tab2:
					Log.v("TabGroup", "radioItemListener_tab2");
					tabHost.setCurrentTabByTag(TAB_TAG_MUSIC);

					break;
				case R.id.tab3:
					Log.v("TabGroup", "radioItemListener_tab3");
					tabHost.setCurrentTabByTag(TAB_TAG_GALLERY);

					break;
				default :
						break;
				}
			}
		}
    	
    };

    
	//----------------------------------------------------------------------------- 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			System.out.println("TabHost--->KEYCODE_BACK ："+keyCode);
			//在TabHost中截获KEYCODE_BACK并屏蔽掉
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	

}