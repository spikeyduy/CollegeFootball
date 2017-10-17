package edu.uiowa.mdphan.collegefootball;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String CONFERENCES = "pref_conferencesToInclude";

    private boolean phoneDevice = true; // used to force portrait mode
    private boolean preferencesChanged = true; // reload the app if the preferences have changed.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set default values in the app's SharedPreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // register listener for sharedPreferences changes
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferencesChangeListener);

        // determine screen size
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is a tablet, set phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false; // tablet size
        }

        // if running on phone-sized device, allow only portrait orientation
        if (phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // only want to show the menu in portrait mode
        // get device orientation
        int orientation = getResources().getConfiguration().orientation;

        // if portrait
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // inflate menu
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // should send you to the SettingsActivity
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
            // now that the default preferences have been set,
            // initalize MainActivityFragment and start quiz
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);
//            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
//            quizFragment.updateConferences(PreferenceManager.getDefaultSharedPreferences(this));
//            quizFragment.resetQuiz();
//            preferencesChanged = false;
        }
    }

    // listener for changes to the app's SharePreferences
    private OnSharedPreferenceChangeListener preferencesChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // called when user changes settings
            preferencesChanged = true;

            // reset the quizFragment
            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizFragment);

            if (key.equals(CHOICES)) { // number of choices
//                quizFragment.updateGuessRows(sharedPreferences);
//                quizFragment.resetQuiz();
            } else if (key.equals(CONFERENCES)) { // conferences to include
                // may not need this
                Set<String> conferences = sharedPreferences.getStringSet(CONFERENCES, null);

                if (conferences != null && conferences.size() > 0) {
//                    quizFragment.updateConferences(sharedPreferences);
//                    quizFragment.resetQuiz();
                } else {
                    // must select one conference-- set BIG10
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    conferences.add(getString(R.string.default_conference_message));
                    editor.putStringSet(CONFERENCES, conferences);
                    editor.apply();

                    // "applying default conference
                    Toast.makeText(MainActivity.this, R.string.default_conference_message, Toast.LENGTH_SHORT).show();
                }
            }

            // toast to show that the quiz is restarting
            Toast.makeText(MainActivity.this, R.string.restarting_quiz,Toast.LENGTH_SHORT).show();
        }
    };
}

