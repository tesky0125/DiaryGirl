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
	private TabSpec tabSpec=null;//�������Ա�֤��һ�����������ã������ڶ������������ı���
	private Intent intent=null;//ͬ��
	
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
        setTitle(" �����ʼǱ� ");
        setContentView(R.layout.main_tabhost);//����ô˺�������VIEW����ʾ����ô�����ļ�һ��Ҫ���չ涨������
        
        //��ϵͳ����ɨ��SD��ʱ������ֳ�������������Ӧ������������ʾ��
        Toast.makeText(this, "������ݿ���...", Toast.LENGTH_SHORT).show();
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
 
/*   //��һ�������RedioGroup�ļ������������Լ�����ÿһ��RadioButton��ʡȥ��ÿ��RadioButtonȥ������
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
    
    //�ڶ��������ÿ��RedioButton�ļ���,������ж�isChecked����ᵼ������RadioButton����ı䣬��Ϊ����Change����
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
			System.out.println("TabHost--->KEYCODE_BACK ��"+keyCode);
			//��TabHost�нػ�KEYCODE_BACK�����ε�
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	

}