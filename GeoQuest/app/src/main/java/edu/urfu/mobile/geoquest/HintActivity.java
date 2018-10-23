package edu.urfu.mobile.geoquest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HintActivity extends AppCompatActivity {
    public static final String EXTRA_TRUE_ANSWER = "edu.urfu.mobile.geoquest.true_answer";
    public static final String EXTRA_ANSWER_WAS_SHOWN = "edu.urfu.mobile.geoquest.answer_was_shown";
    public static boolean usedHint;
    private boolean trueAnswer;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);
        setResult(false);
        showAction();

        trueAnswer = getIntent().getBooleanExtra(EXTRA_TRUE_ANSWER, false);

        okButton = findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });
    }

    private void showAnswer() {
        int result = 0;
        if (trueAnswer)
            result = R.string.true_toast;
        else
            result = R.string.false_toast;
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        setResult(true);
        usedHint = true;
    }

    private void showAction() {
        Intent intent = new Intent("edu.urfu.mobile.geoquest.intent.action.CHEAT");
        String action = intent.getAction();
        Toast.makeText(this, action, Toast.LENGTH_SHORT).show();
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_WAS_SHOWN, false);
    }

    private void setResult(boolean wasAnswerShown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_WAS_SHOWN, wasAnswerShown);
        setResult(RESULT_OK, intent);
    }
}