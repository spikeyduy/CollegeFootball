package edu.uiowa.mdphan.collegefootball;

import android.preference.PreferenceFragment;
import android.os.Bundle;

public class SettingsActivityFragment extends PreferenceFragment {
    // creates preference GUI from preference.xml
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load xml
    }

}
