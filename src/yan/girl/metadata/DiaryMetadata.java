package yan.girl.metadata;

import android.provider.BaseColumns;

public class DiaryMetadata {
	public static final String APP_NAME = "DiaryGirl";

	public static final String DB_NAME = "diary.db";
	public static int DB_VERSION = 1;
	public static final String DB_TABLE_NAME = "diary";


	public static final class TableColumn implements BaseColumns{
		//_ID
		public static final String TABLE_COLUMN_TITLE = "title";
		public static final String TABLE_COLUMN_CONTENT = "content";
		public static final String TABLE_COLUMN_IMG = "img";
		public static final String TABLE_COLUMN_TIME = "time";	
	}
}
