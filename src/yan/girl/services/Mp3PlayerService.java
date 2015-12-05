package yan.girl.services;

import yan.girl.interfaces.AppConstant;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class Mp3PlayerService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent!=null){
			int msg = intent.getIntExtra("msg", 0);
			filename =  intent.getStringExtra("filename");
			System.out.println("Service : "+filename+","+msg);
			if(!filename.equals("")){
				switch(msg){
				case AppConstant.Mp3PlayerMsg.MSG_START:
					startMp3();
					break;
				case AppConstant.Mp3PlayerMsg.MSG_PAUSE:
					pauseMp3();
					break;
				case AppConstant.Mp3PlayerMsg.MSG_STOP:
					stopMp3();
					break;
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private String filename;
	
	private MediaPlayer mediaPlayer=null;	
	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isReleased = false;
	
	private void startMp3()
	{
		System.out.println("start");
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
	
	private void pauseMp3()
	{
		System.out.println("pause");
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
	
	private void stopMp3()
	{
		System.out.println("stop");
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
