package yan.girl.activity;

import java.util.ArrayList;
import java.util.List;

import yan.girl.interfaces.DialogListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PromptDialog extends Dialog implements OnItemClickListener{
	private Context m_context;
	private DialogListener m_listener;
	private ListView handleListView = null;
	private List<String>  handleDataList = null;
	private ArrayAdapter<String> arrdapter = null; 
	
	public PromptDialog(Context context) {
		super(context);
		m_context = context;
		// TODO Auto-generated constructor stub
	}
	
	public PromptDialog(Context context,DialogListener listener) {
		this(context);
		this.m_listener = listener;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainlist_diary_handle);	
		handleListView = (ListView) findViewById(R.id.handle_list);
		
		handleDataList = new ArrayList<String>();   
		handleDataList.add("²é¿´");
//		handleDataList.add("±à¼­");
		handleDataList.add("É¾³ý");
				
		arrdapter = new ArrayAdapter<String>(m_context, R.layout.listitem_handle,  handleDataList);    
		handleListView.setAdapter(arrdapter);	
		handleListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub

		m_listener.onItemClick(position);
		this.dismiss();
	}
	
	
}
