package edu.uiowa.mdphan.collegefootball;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends Fragment {

    // set up the radio buttons
    private static RadioGroup radioG;
    private static RadioButton radioButt;

    public SettingsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        radioG = view.findViewById(R.id.choicesGroup);
        

        return view;
    }


}
