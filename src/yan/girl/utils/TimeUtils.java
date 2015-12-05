package yan.girl.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {


	public static String createDateTime() {
		Date date=new Date();
		SimpleDateFormat formatter=new SimpleDateFormat("yy-MM-dd HH:mm");
		String time=formatter.format(date);
		return time;
	}
	
	public static int[] createHMS(){
		Calendar calendar=Calendar.getInstance();       
        long time=System.currentTimeMillis();
        calendar.setTimeInMillis(time);
        int hour=calendar.get(Calendar.HOUR);
        int minutes=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        return new int[]{hour,minutes,second};
	}
}
