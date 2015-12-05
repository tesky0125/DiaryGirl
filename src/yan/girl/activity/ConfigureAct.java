package yan.girl.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class ConfigureAct extends PreferenceActivity implements OnSharedPreferenceChangeListener {

//	private PreferenceScreen parent_screen_diary_pref;
	
	private ListPreference list_bg_pref;
	private String str_bg_pref;
	
	private ListPreference list_font_size_pref;
	private String str_font_size;
	private ListPreference list_font_color_pref;
	private String str_font_color;
	
	private CheckBoxPreference parent_checkbox_diary_pref;
//	private PreferenceScreen child_screen_diary_pref;
	private CheckBoxPreference next_screen_checkbox_preference1;
	private CheckBoxPreference next_screen_checkbox_preference2;
	
	private CheckBoxPreference parent_checkbox_preference;
	private CheckBoxPreference child_checkbox_preference;
	
	SharedPreferences prefs ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("设置");
		
        addPreferencesFromResource(R.xml.preferences);	
        
        findPreferences();               
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        readPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

	}

	private void findPreferences() {
//      parent_screen_diary_pref = (PreferenceScreen) findPreference("parent_screen_diary_pref");
		list_bg_pref = (ListPreference) findPreference("list_bg_pref");
        list_font_size_pref = (ListPreference) findPreference("list_font_size_pref");
        list_font_color_pref = (ListPreference) findPreference("list_font_color_pref");
        parent_checkbox_diary_pref = (CheckBoxPreference) findPreference("parent_checkbox_diary_pref");
//        child_screen_diary_pref = (PreferenceScreen) findPreference("child_screen_diary_pref");
        next_screen_checkbox_preference1 = (CheckBoxPreference) findPreference("next_screen_checkbox_preference1");
        next_screen_checkbox_preference2 = (CheckBoxPreference) findPreference("next_screen_checkbox_preference2");
        parent_checkbox_preference = (CheckBoxPreference) findPreference("parent_checkbox_preference");
        child_checkbox_preference = (CheckBoxPreference) findPreference("child_checkbox_preference");
	}

	private void readPreferences() {
		str_bg_pref = prefs.getString("list_bg_pref","");
		str_font_size  = prefs.getString("list_font_size_pref","");
		str_font_color  = prefs.getString("list_font_color_pref","");
		System.out.println(str_bg_pref+","+str_font_size+","+str_font_color);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		System.out.println("onSharedPreferenceChanged,Key:"+key);
        if (key.equals("list_bg_pref")){
            System.out.println(list_bg_pref.getValue());
            if(!list_bg_pref.getValue().equals(str_bg_pref)){
            	System.out.println("Preference has changed.");
//            	PreferenceActivity中的SharePreference是自动存储的，不需要手动存储，
//            	这就是下面注释代码无法执行的原因，只需要根据改变处理响应时间即可
//            	prefs.edit()
//            	.putString("list_bg_pref", list_bg_pref.getValue());
//            	.commit();
            }
        }
        if(key.equals("list_font_size_pref")){
        	System.out.println(list_font_size_pref.getValue());
            if(!list_font_size_pref.getValue().equals(str_font_size)){

            }
        }
        if(key.equals("list_font_color_pref")){
        	System.out.println(list_font_color_pref.getValue());
            if(!list_font_color_pref.getValue().equals(str_font_color)){

            }        	
        }

	}
		
	
}
