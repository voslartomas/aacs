package cz.ssakhk.androidaudiocarsystem.activity;

import cz.ssakhk.androidaudiocarsystem.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 * @author 
 *
 */
public class PreferencesActivity extends PreferenceActivity{
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
