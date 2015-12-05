package yan.girl.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yan.girl.database.SDCardDatabase;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DiaryHandleAct extends Activity implements OnItemClickListener{
	
	private ListView listView;
	private List<String>    list;
	private ArrayAdapter<String> arrdapter; 

	private SDCardDatabase sdCardDB = null;

	
	private int _id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.mainlist_diary_handle);
		
		setWindowSize();

		sdCardDB = new SDCardDatabase();
		
		Intent intent = this.getIntent();
		_id = intent.getBundleExtra("handle.data").getInt("_id");
		System.out.println("getIntent rowId : "+_id);
		
		listView = (ListView) this.findViewById(R.id.handle_list);
		
		list = new ArrayList<String>(); 
        list.add("查看");
        list.add("编辑");
        list.add("删除"); 		
        
        arrdapter = new ArrayAdapter<String>(this,R.layout.listitem_handle, list);    

        listView.setAdapter(arrdapter);
        listView.setOnItemClickListener(this);
	}

	
	private void setWindowSize(){
		
		WindowManager windowManager = getWindowManager(); 
		Display display = windowManager.getDefaultDisplay(); //为获取屏幕宽、高 

		WindowManager.LayoutParams params = getWindow().getAttributes(); //获取对话框当前的参数值 
//		params.height = (int) (display.getHeight() * 0.6); //高度设置为屏幕的0.6 
		params.width = (int) (display.getWidth() * 0.6); //宽度设置为屏幕的0.6 

		params.alpha = 0.8f;//设置透明度
		this.getWindow().setAttributes( params);	
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		if(list.get(position).equals("删除")){
			System.out.println("deleting..");
			sdCardDB.deleteDiary(_id);
			finish();
		}else{
			Toast.makeText(this, "Sorry.", Toast.LENGTH_SHORT).show();
		}
	}
	

}
