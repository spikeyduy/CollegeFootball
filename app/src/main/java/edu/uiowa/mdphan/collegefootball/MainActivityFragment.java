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
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    // String used to log error messages
    private static final String TAG = "SchoolQuiz Activity";

    private static final int SCHOOLS_IN_QUIZ = 22;

    private List<String> fileNameList; // school file names
    private List<String> quizSchoolsList; // schools in quiz
    private String[] conferenceSet; // conferences in quiz
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
                button.setOnClickListener(guessButtonListener);
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

        conferenceSet = MainActivity.CONFERENCES;

        try {
            // get all of the school's images
            // THIS MAY OR MAY NOT WORK, NEED TO WORK ON THIS
            for (String conference : conferenceSet) {
                String[] paths = assets.list(conference);

                for (String path : paths) {
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
            // MAY NOT NEED TO DO THIS BECAUSE WE SHUFFLE EVERYTIME WE LOAD A NEW SCHOOL, MAYBE JUST POPULATE IT!!!!
            String fileName = fileNameList.get(randomIndex);

            // add schools
            // may be able to just add all the schools to the quizSchoolsList
            if (!quizSchoolsList.contains(fileName)) {
                quizSchoolsList.add(fileName);
                ++schoolCounter;
            }
        }

        loadNextSchool();
    }

    // after the user guesses a correct Conference, load next school
    private void loadNextSchool() {
        // get file name of the next school and remove it from the list
        String nextImage = quizSchoolsList.remove(0);
        correctAnswer = nextImage; // update correct answer
        answerTextView.setText(""); // clear answerTextView

        // display the current question number
        // also update the progress of the quiz
        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), SCHOOLS_IN_QUIZ));

        // extract the conference from the next image's name
        // names in quizSchoolsList should be in "conference-schoolName" setup
        // name may actually just be the nextImage because of pulling string straight from array
        String conference = nextImage.substring(0, nextImage.indexOf('-'));

        // use the assetManager to load next image from assets folder and try to use the InputStream
        AssetManager assets = getActivity().getAssets();
        try (InputStream stream = assets.open(conference + "/" + nextImage + ".png")) {
            // load the asset as a drawable and display on the schoolImageView
            Drawable school = Drawable.createFromStream(stream, nextImage);
            schoolImageView.setImageDrawable(school);

            animate(false); // animate the flag onto the screen
        } catch (IOException e) {
            Log.e(TAG, "Error loading "  + nextImage, e);
        }

        Collections.shuffle(fileNameList); // shuffle the file names

        // put the correct answer at the end of hte fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        // add 2, 4, 6 guess buttons based on value of guessRows
        for (int row = 0; row < guessRows; row++) {
            // place the buttons in currentTableRow
            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                // get reference to button to configure
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                // get conference name and set as button
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getSchoolName(filename));
            }
        }

        // randomly replace one of the buttons with the correct answer
        int row = random.nextInt(guessRows); // pick a random row
        int column = random.nextInt(2); // pick a random column
        LinearLayout randomRow = guessLinearLayouts[row]; // get the row
        String schoolName = getSchoolName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(schoolName);
    }

    // parses the school flag file name and returns the school name
    private String getSchoolName(String name) {
        // AGAIN, MAY NOT NEED THIS BECAUSE OF DIRECT ACCESS TO THE STRING ARRAY
        // MAY HAVE TO CREATE A UNIT TEST THAT MAKES SURE THE SCHOOLLIST IS CONFERENCE-SCHOOLNAME OR JUST SCHOOLNAME
        return name.substring(name.indexOf('-') + 1).replace('-',' ');
    }

    // animate the entire quizLinearLayout on or off screen
    // EXTRA
    private void animate(Boolean animateOut) {
        // prevent animation into the UI for the first flag
        if (correctAnswers == 0) {
            return;
        }

        // calculate the center x and center y
        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        // calculate animation radius
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());
        Animator animator;

        // if quizLinearLayout should animate out rather than in
        if (animateOut) {
            // create circular reveal animation?
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loadNextSchool();
                        }
                    }
            );
        } else { // if quizLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(500); // set animation duration to 500 ms
        animator.start();
    }

    // called when a guess button is touched
    private OnClickListener guessButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getSchoolName(correctAnswer);
            ++totalGuesses; // increment # of guesses

            if (guess.equals(answer)) {
                ++correctAnswers; // if correct answer, increment # of correct guesses

                // display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                // disable all guessbuttons
                disableButtons();

                // if user has correctly identified 22 schools
                if (correctAnswers == SCHOOLS_IN_QUIZ) {
                    // DialogFragment to display quiz stats and start new quiz
                    DialogFragment quizResults = new DialogFragment() {
                        // create an alertDialog and return it
                        @Override
                        public Dialog onCreateDialog(Bundle bundle) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.results, totalGuesses, (1000 / (double) totalGuesses)));

                            // "reset quiz" button
                            builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    resetQuiz();
                                }
                            });

                            return builder.create(); // return the alert dialog
                        }
                    };

                    // use fragmentManager to display the dialogfragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                } else { // have not finished the quiz
                    // load the next flag after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true); // animate the flag off the screen
                                }
                            }, 2000); // 2000 ms for 2-sec delay
                }
            } else { // answer was incorrect
                schoolImageView.startAnimation(shakeAnimation); // play shake

                // displays "incorrect" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(false); // disable incorrect answer
            }
        }
    };

    // utility method that disables all guess buttons
    private void disableButtons() {
        for (int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for (int i = 0; i < guessRow.getChildCount(); i++) {
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }
}
