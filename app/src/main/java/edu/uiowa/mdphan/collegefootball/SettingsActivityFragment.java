package edu.uiowa.mdphan.collegefootball;

import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.preference.PreferenceManager;

public class SettingsActivityFragment extends PreferenceFragment {
    // creates preference GUI from preference.xml
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.preferences); // load xml
    }

}
