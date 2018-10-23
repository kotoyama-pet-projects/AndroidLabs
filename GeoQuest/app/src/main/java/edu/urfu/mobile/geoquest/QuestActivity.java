package edu.urfu.mobile.geoquest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuestActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_HINT = 0;
    final String TAG = "QuestActivity: ";
    private TextView questionTextView;
    private int currentIndex = 0;
    private Button showAnswerButton;
    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
    private Button backButton;
    private Button saveButton;
    private Button loadButton;

    private static final String PREFS_NAME = "NAME";
    private static final String PREFS = "PREFS";
    private SharedPreferences prefs;
    private EditText nameTextView;

    private Question[] questionBank = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_america, true),
            new Question(R.string.question_asia, true)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        Log.d(TAG, "onCreate");
        showAction();

        questionTextView = findViewById(R.id.question_text_view);
        nameTextView = findViewById(R.id.name_edit_text);

        if (savedInstanceState != null) currentIndex = savedInstanceState.getInt("index", 0);

        trueButton = findViewById(R.id.true_button);
        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        falseButton = findViewById(R.id.false_button);
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % questionBank.length;
                updateQuestion();
            }
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex - 1) % questionBank.length;
                if (currentIndex < 0)
                    currentIndex = questionBank.length - 1;
                updateQuestion();
            }
        });

        showAnswerButton = findViewById(R.id.answer_button);
        showAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HintActivity.usedHint)
                    Toast.makeText(QuestActivity.this, R.string.refusing_toast, Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(QuestActivity.this, HintActivity.class);
                    boolean trueAnswer = questionBank[currentIndex].isAnswerTrue();
                    intent.putExtra(HintActivity.EXTRA_TRUE_ANSWER, trueAnswer);
                    startActivityForResult(intent, REQUEST_CODE_HINT);
                }
            }
        });

        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });

        loadButton = findViewById(R.id.load_button);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreferences();
            }
        });
        updateQuestion();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    private void updateQuestion() {
        int question = questionBank[currentIndex].getTextResId();
        questionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean trueAnswer = questionBank[currentIndex].isAnswerTrue();
        int messageResId = 0;

        if (userPressedTrue == trueAnswer)
            messageResId = R.string.correct_toast;
        else
            messageResId = R.string.incorrect_toast;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void showAction() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
    }

    private void savePreferences() {
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_NAME, nameTextView.getText().toString());
        editor.apply();
        Toast.makeText(this, R.string.save_toast, Toast.LENGTH_SHORT).show();
    }

    private void loadPreferences() {
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedName = prefs.getString(PREFS_NAME, "");
        nameTextView.setText(savedName);
        Toast.makeText(this, R.string.load_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putInt("index", currentIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CODE_HINT)
            if (data == null) return;
        HintActivity.usedHint = HintActivity.wasAnswerShown(data);
    }
}