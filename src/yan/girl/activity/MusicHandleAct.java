package yan.girl.activity;

import java.util.ArrayList;
import java.util.List;

import yan.girl.database.SDCardDatabase;
import yan.girl.interfaces.AppConstant;
import yan.girl.services.Mp3PlayerService;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MusicHandleAct  extends Activity implements OnItemClickListener{
	
	private ListView listView;
	private List<String>    list;
	private ArrayAdapter<String> arrdapter; 
	
	private String filename;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.mainlist_music_handle);
		
		setWindowSize();

		
		Intent intent = this.getIntent();
		filename =  intent.getStringExtra("filename");
		System.out.println("getIntent filename : "+filename);
		
		listView = (ListView) this.findViewById(R.id.handle_list);
		
		list = new ArrayList<String>(); 
        list.add("����");
        list.add("��ͣ");
        list.add("ֹͣ"); 		
        
        arrdapter = new ArrayAdapter<String>(this,R.layout.listitem_handle, list);    

        listView.setAdapter(arrdapter);
        listView.setOnItemClickListener(this);
	}
	
	private void setWindowSize(){
		
		WindowManager windowManager = getWindowManager(); 
		Display display = windowManager.getDefaultDisplay(); //Ϊ��ȡ��Ļ���� 

		WindowManager.LayoutParams params = getWindow().getAttributes(); //��ȡ�Ի���ǰ�Ĳ���ֵ 
//		params.height = (int) (display.getHeight() * 0.6); //�߶�����Ϊ��Ļ��0.6 
		params.width = (int) (display.getWidth() * 0.6); //�������Ϊ��Ļ��0.6 

		params.alpha = 0.8f;//����͸����
		this.getWindow().setAttributes( params);	
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		//ͬ�����⣺���һ�β��ţ��ڶ��ε��MediaPlayer�������³�ʼ����
		Intent service = new Intent(this,Mp3PlayerService.class);
		
		if(list.get(position).equals("����")){

			service.putExtra("msg", AppConstant.Mp3PlayerMsg.MSG_START);


		}else if(list.get(position).equals("��ͣ")){

			service.putExtra("msg", AppConstant.Mp3PlayerMsg.MSG_PAUSE);

		}else if(list.get(position).equals("ֹͣ")){

			service.putExtra("msg", AppConstant.Mp3PlayerMsg.MSG_STOP);			

		}
		
		service.putExtra("filename", filename);
		startService(service);
		
	}
	//���ֲ��Ż��ǲ�Ҫ�ڵ���ʽActivity�� ��ʵ�֣���Dialog������ʾ��Ȼ����Mp3ListAct������Service
	


}
