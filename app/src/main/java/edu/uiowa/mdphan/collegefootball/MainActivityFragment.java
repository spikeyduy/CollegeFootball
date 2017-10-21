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

import java.io.IOException;
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
    private List<String> quizSchoolsList; // schools in quiz
    private Set<String> conferenceSet; // conferences in quiz
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
        quizSchoolsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        // load the shake animaton
        // loadAnimation's takes in the context, getActivity will inherit whatever activity this fragment is called from
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); // animation repeats 3 times

        // get GUI components
        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        schoolImageView = view.findViewById(R.id.schoolImageView);
        guessLinearLayouts = new LinearLayout[3];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        answerTextView = view.findViewById(R.id.answerTextView);

        // configure listeners for the guess buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                // // TODO: 10/19/17 create listener for guess buttons 
//                button.setOnClickListener(guessButtonListener);
            }
        }

        // set questionNumberTextView's text
        // arguments are: the text that it is set, and the two placeholders that the text needs. 1 and then the amount of questions in the quiz
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
    public void resetQuiz() {
        // use AssetManager to get image filenames
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); // empty list of image files

        try {
            // get all of the school's images
            // THIS MAY OR MAY NOT WORK, NEED TO WORK ON THIS
            // // TODO: 10/19/17 conferenceSet is null
            for (String conference : conferenceSet) {
                String[] paths = assets.list(conference);

                for (String path : paths) {
                    // TODO: filenamelist is not being populated
                    fileNameList.add(path.replace(".png",""));
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }

        correctAnswers = 0; // reset number of correct answers made
        totalGuesses = 0; // reset total number of guesses made
        quizSchoolsList.clear(); // clear prior list of quiz schools

        int schoolCounter = 1;
        int numberOfConferences = 11;

        // add SCHOOLS_IN_QUIZ random file names to quizSchoolList
        while (schoolCounter <= SCHOOLS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfConferences);

            // get random file name
            // TODO: indexError, filenamelist is not populated? 
            String fileName = fileNameList.get(randomIndex);

            // add schools
            // may be able to just add all the schools to the quizSchoolsList
            if (!quizSchoolsList.contains(fileName)) {
                quizSchoolsList.add(fileName);
                ++schoolCounter;
            }
        }

//        loadNextFlag();
    }
}
