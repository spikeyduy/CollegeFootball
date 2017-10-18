package edu.uiowa.mdphan.collegefootball;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.os.Handler;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    // String used to log error messages
    private static final String TAG = "SchoolQuiz Activity";

    private static final int SCHOOLS_IN_QUIZ = 22;

    private List<String> fileNameList; // school file names
    private List<String> quizConferencesList; // conferences in quiz
    private String correctAnswer;
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct answers
    private int guessRows; // number of rows displaying guess buttons
    private SecureRandom random; // used to randomize quiz
    private Handler handler; // used to delay loading next school
    private Animation shakeAnimation; // animation for incorrect guess
    private LinearLayout quizLinearLayout; // layout that contains quiz
    private TextView questionNumberTextView; // shows current question number
    private ImageView schoolImageView; // displays school
    private LinearLayout[] guessLinearLayouts; // rows of guess buttons
    private TextView answerTextView; // displays correct answer
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // inflate the view and assign it to a constant so that we can reuse it
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // stores the school images
        fileNameList = new ArrayList<>();
        quizConferencesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // load the shake animaton
        // loadAnimation's takes in the context, getActivity will inherit whatever activity this fragment is called from
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // animation repeats 3 times

        // get GUI components
        quizLinearLayout = (LinearLayout) view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        schoolImageView = (ImageView) view.findViewById(R.id.schoolImageView);
        guessLinearLayouts = new LinearLayout[3];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row3LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        // configure listeners for the guess buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
//                button.setOnClickListener(guessButtonListener);
            }
        }

        // set questionNumberTextView's text
        // arguments are the text that it is set, and the two placeholders that the text needs. 1 and then the amount of questions in the quiz
        questionNumberTextView.setText(getString(R.string.question, 1, SCHOOLS_IN_QUIZ));
        return view; // return the fragment's view for display
    }

    // update guessRows based on value in SharedPreferences
    public void updateGuessRows(SharedPreferences sharedPreferences) {
        // get number of guess buttons that should be displayed
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;
         // hide all guess buttons
        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        // display the correct amount of guess rows
        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    // resets the quiz and starts it again
    
}
