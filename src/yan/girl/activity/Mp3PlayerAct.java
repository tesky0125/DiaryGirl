package yan.girl.activity;

import java.io.IOException;

import yan.girl.activity.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Mp3PlayerAct extends Activity implements OnClickListener{

	private ImageButton btnStart,btnPause,btnStop;
	private String filename;
	private MediaPlayer mediaPlayer=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mp3player_ui);
		
		btnStart = (ImageButton) this.findViewById(R.id.btnstart);
		btnPause = (ImageButton) this.findViewById(R.id.btnpause);
		btnStop = (ImageButton) this.findViewById(R.id.btnstop);
		
		btnStart.setOnClickListener(this);
		btnPause.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		 
		Intent intent = getIntent();
		filename =  intent.getStringExtra("filename");
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btnstart:
			start();
			break;
		case R.id.btnpause:
			pause();
			break;
		case R.id.btnstop:
			stop();
			break;
		}
		
	}

	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isReleased = false;
	 
	private void start()
	{
		if(!isPlaying){
			mediaPlayer = MediaPlayer.create(this, Uri.parse("file://"+filename));
			mediaPlayer.setLooping(false);
			try {
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mediaPlayer.start();
			isPlaying = true;
			isPause = false;
			isReleased= false;
		}
	}
	
	private void pause()
	{
		if(mediaPlayer!=null){
			if(!isReleased){
				if(!isPause){
					mediaPlayer.pause();
					isPlaying = false;
					isPause = true;
				}else{
					mediaPlayer.start();
					isPause = false;
					isPlaying = true;
				}
			}
		}
	}
	
	private void stop()
	{
		if(mediaPlayer!=null){
			if(!isReleased){
				if(isPlaying){
					mediaPlayer.stop();
					mediaPlayer.release();
					isReleased = true;				
					isPlaying = false;
				}
			}
		}
	}
}
