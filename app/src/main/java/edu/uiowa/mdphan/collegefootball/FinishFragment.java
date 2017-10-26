package edu.uiowa.mdphan.collegefootball;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinishFragment extends DialogFragment {

    int totalGuesses = MainActivityFragment.totalGuesses;

    @Override
    public Dialog onCreateDialog (Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.results, totalGuesses, (1000 / (double) totalGuesses)));

        // "reset quiz" button
        builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                MainActivityFragment.resetQuiz();
            }
        });

        return builder.create();
    }

}
