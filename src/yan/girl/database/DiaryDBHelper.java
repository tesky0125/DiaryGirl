package yan.girl.database;

import yan.girl.metadata.DiaryMetadata;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DiaryDBHelper extends SQLiteOpenHelper{

	public DiaryDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DiaryDBHelper(Context context, String name, int version) {
		this(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	
	public DiaryDBHelper(Context context, String name) {
		this(context, name, DiaryMetadata.DB_VERSION);
		// TODO Auto-generated constructor stub
	}	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("DiaryDBHelper--->onCreate");
		db.execSQL("CREATE TABLE diary (_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
										"title VARCHAR(20) NOT NULL ," +
										"content TEXT NOT NULL ," +
										"img BLOB ," +
										"time VARCHAR(20) );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("DiaryDBHelper--->onUpgrade");

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		System.out.println("DiaryDBHelper--->onOpen");
	}

	@Override
	public synchronized void close() {
		// TODO Auto-generated method stub
		super.close();
		System.out.println("DiaryDBHelper--->close");	
	}



}
