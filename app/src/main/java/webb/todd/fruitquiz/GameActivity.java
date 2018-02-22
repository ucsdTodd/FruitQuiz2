package webb.todd.fruitquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    static String TAG = "Game";

    static String[] ALL_FRUIT = new String[]{
            "apricot", "caimito", "pomegranate", "pomelo", "nectarine", "cherimoya",
            "blackberry", "gojiberry", "gooseberry", "elderberry", "kumquat", "lime",
            "mango", "papaya", "kiwi", "lychee", "passionfruit", "persimmon", "pitaya",
            "plum"
    };

    static int DEFAULT_QUESTIONS = 10;

    TextView questionNumberTextView;
    TextView answerTextView;
    Animation shakeAnimation;
    ImageView fruitImageView;
    Random random = new Random();
    MediaPlayer boingSound;
    MediaPlayer clappingSound;
    Button[] choiceButtons;

    List<String> questions;
    int questionIndex = 0;
    int totalGuesses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boingSound = MediaPlayer.create(this, R.raw.boing);
        clappingSound = MediaPlayer.create(this, R.raw.applause);

        // load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(5); // animation repeats 5 times

        fruitImageView = (ImageView) findViewById( R.id.fruitImageView );

        choiceButtons = new Button[4];
        choiceButtons[0] = (Button) findViewById(R.id.choiceButton1);
        choiceButtons[1] = (Button) findViewById(R.id.choiceButton2);
        choiceButtons[2] = (Button) findViewById(R.id.choiceButton3);
        choiceButtons[3] = (Button) findViewById(R.id.choiceButton4);
        for( Button button : choiceButtons ){
            button.setOnClickListener( this );
        }

        questionNumberTextView = (TextView) findViewById( R.id.questionTextView );
        answerTextView =
                (TextView) findViewById(R.id.answerTextView);

        resetQuiz();

    }

    private List<String> getQuestions( int numQuestions ){
        int ttlQuestions = Math.min( ALL_FRUIT.length, numQuestions );
        LinkedHashSet<String> questions = new LinkedHashSet<>( ttlQuestions );
        while( questions.size() < ttlQuestions ){
            // set should prevent duplicates
            questions.add( ALL_FRUIT[ random.nextInt(ALL_FRUIT.length) ] );
        }
        return new ArrayList<>( questions );
    }

    private void resetQuiz(){
        totalGuesses = 0;
        questions = getQuestions( DEFAULT_QUESTIONS );
        questionIndex = -1;
        loadNextQuestion();
    }

    @Override
    public void onClick(View v) {
        Button guessButton = ((Button) v);
        String guess = guessButton.getText().toString();
        totalGuesses++;
        if( guess.equals( questions.get( questionIndex ) ) ){
            // correct
            answerTextView.setText( guess + " is correct!");
            clappingSound.start();
            guessButton.startAnimation( shakeAnimation );
            if( questionIndex == questions.size() - 1 ){
                // handle quiz completion
                completeQuiz();
            }
            else {
                (new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread( new Runnable(){
                            @Override
                            public void run() {
                                loadNextQuestion();
                            }
                        });
                    }
                }, 900L); //fixme
            }
        }
        else{
            // NOT correct
            Toast.makeText( getApplicationContext(), "WRONG!", Toast.LENGTH_SHORT ).show();
            answerTextView.setText( "Try again" );
            boingSound.start();
            guessButton.setEnabled( false );
        }
    }

    // after the user guesses a correct flag, load the next flag
    private void loadNextQuestion() {
        String question = questions.get( ++questionIndex );

        questionNumberTextView.setText( "Question " + (questionIndex + 1) + " of " +questions.size() );
        try (InputStream stream =
                     getAssets().open( question + ".png")) {
            // load the asset as a Drawable and display on the flagImageView
            Drawable fruitDrawable = Drawable.createFromStream( stream, question ) ;
            fruitImageView.setImageDrawable( fruitDrawable );
        }
        catch (IOException exception) {
            Log.e( TAG, "Error loading " + question, exception );
        }
        List<String> usedChoices = new ArrayList<>(4);
        usedChoices.add( question );
        for( Button button : choiceButtons ){
            String choice;
            do{
                choice = ALL_FRUIT[ random.nextInt( ALL_FRUIT.length ) ];
            }
            while( usedChoices.contains(choice) );
            usedChoices.add( choice );
            button.setText( choice );
            button.setEnabled( true );
        }
        choiceButtons[ random.nextInt(choiceButtons.length)].setText( question );
        answerTextView.setText( "" );
    }

    private void completeQuiz(){
        // DialogFragment to display quiz stats and start new quiz
        DialogFragment quizResults =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setMessage( "Results: " +totalGuesses + " guesses (" +(1000d /totalGuesses) + "%)" );
                                //getString( R.string.results,
                                //        totalGuesses, " guesses (",
                                //        (1000d /totalGuesses), "%)" ));

                        // "Reset Quiz" Button
                        builder.setPositiveButton( R.string.playAgain,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        resetQuiz();
                                    }
                                }
                        );
                        builder.setNegativeButton( R.string.done,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        startActivity( new Intent( GameActivity.this, MainActivity.class ) );
                                    }
                                }
                          );

                        return builder.create(); // return the AlertDialog
                    }
                };

        // use FragmentManager to display the DialogFragment
        quizResults.setCancelable(false);
        quizResults.show( getFragmentManager(), "quiz results") ;

    }
}
