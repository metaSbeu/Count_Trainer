package com.example.counttrainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Random;

public class QuizTextActivity extends AppCompatActivity {

    private TextView textViewScore;
    private TextView textViewRemainTime;
    private TextView textViewCorrectAnswer;
    private TextView textViewIncorrectAnswer;
    private TextView textViewQuestion;
    private TextView textViewQuestionLabel;
    private EditText editTextUserAnswer;
    private GridLayout gridLayoutNumberPad;

    private final Random random = new Random();
    private int score;
    private int correctAnswer;
    private int incorrectUserAnswers = 0;
    private boolean timeout = false;

    private static int TO_WIN;
    private static int TO_LOSE;
    private static int RANDOM_BOUND;
    private static int TIME;
    private static int DIFFICULTY;

    List<String> actions = List.of("+", "-", "*", "/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_text);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        TO_WIN = intent.getIntExtra("answersToWin", TO_WIN);
        TO_LOSE = intent.getIntExtra("mistakesToLose", TO_LOSE);
        RANDOM_BOUND = intent.getIntExtra("randomBound", RANDOM_BOUND);
        TIME = intent.getIntExtra("timeLimit", TIME);
        DIFFICULTY = intent.getIntExtra("difficulty", DIFFICULTY);

        Log.d("TAG", String.valueOf(TO_WIN));
        Log.d("TAG", String.valueOf(TO_LOSE));
        Log.d("TAG", String.valueOf(RANDOM_BOUND));
        Log.d("TAG", String.valueOf(TIME));
        Log.d("TAG", String.valueOf(DIFFICULTY));

        initViews();
        gridListener();
        generateQuestion();
        startTimer();
    }

    private void generateQuestion() {
        editTextUserAnswer.setText("");
        int number1 = random.nextInt(RANDOM_BOUND) + 1;
        int number2 = random.nextInt(RANDOM_BOUND) + 1;
        String action = actions.get(random.nextInt(DIFFICULTY));

        switch (action) {
            case "+":
                correctAnswer = number1 + number2;
                break;
            case "-":
                if (number1 >= number2) {
                    correctAnswer = number1 - number2;
                } else {
                    correctAnswer = number2 - number1;
                    int temp = number2;
                    number2 = number1;
                    number1 = temp;
                }
                break;
            case "*":
                correctAnswer = number1 * number2;
                break;
            case "/":
                correctAnswer = number1;
                number1 *= number2;
                break;
        }
        textViewQuestion.setText(String.format(getString(R.string.textQuestion), number1, action, number2));
    }

    private void startTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewRemainTime.setText(getString(R.string.remain_time, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                timeout = true;
                checkEndGame();
            }
        }.start();
    }

    private void hideViews() {
        gridLayoutNumberPad.setVisibility(View.INVISIBLE);
        textViewQuestion.setVisibility(View.INVISIBLE);
        editTextUserAnswer.setVisibility(View.INVISIBLE);
        textViewCorrectAnswer.setVisibility(View.INVISIBLE);
        textViewIncorrectAnswer.setVisibility(View.INVISIBLE);
    }

    private void checkAnswer() {
        try {
            int userAnswer = Integer.parseInt(editTextUserAnswer.getText().toString().trim());
            if (userAnswer == correctAnswer) {
                textViewCorrectAnswer.setVisibility(View.VISIBLE);
                textViewIncorrectAnswer.setVisibility(View.INVISIBLE);
                generateQuestion();
                textViewScore.setText(String.format(getString(R.string.score), ++score, TO_WIN));
            } else {
                textViewCorrectAnswer.setVisibility(View.INVISIBLE);
                textViewIncorrectAnswer.setText(String.format(getString(R.string.answer_incorrect), correctAnswer));
                textViewIncorrectAnswer.setVisibility(View.VISIBLE);
                generateQuestion();
                incorrectUserAnswers++;
            }
            checkEndGame();
        } catch (NumberFormatException e) {
            editTextUserAnswer.setError("");
        }
    }

    private void checkEndGame() {
        // Проверяем, проиграл ли пользователь (слишком много ошибок или время истекло)
        if (incorrectUserAnswers >= TO_LOSE || timeout) {
            textViewQuestionLabel.setText(R.string.lose_message);

            Log.d("TAG", String.valueOf(timeout));
            hideViews();
        }
        // Проверяем, выиграл ли пользователь (достиг нужного количества правильных ответов)
        else if (score >= TO_WIN) {
            textViewQuestionLabel.setText(R.string.win_message);
            hideViews();
        }
    }

    private void onNumberPadButtonClick(Button button) {
        String buttonText = button.getText().toString();

        if (buttonText.equals(getString(R.string.ok))) {
            checkAnswer();
        } else if (buttonText.equals(getString(R.string.del))) {
            String currentText = editTextUserAnswer.getText().toString();
            if (!currentText.isEmpty()) {
                editTextUserAnswer.setText(currentText.substring(0, currentText.length() - 1));
            }
        } else {
            String currentText = editTextUserAnswer.getText().toString();
            editTextUserAnswer.setText(String.format("%s%s", currentText, buttonText));
        }
    }

    private void gridListener() {
        for (int i = 0; i < gridLayoutNumberPad.getChildCount(); i++) {
            if (gridLayoutNumberPad.getChildAt(i) instanceof Button) {
                Button button = (Button) gridLayoutNumberPad.getChildAt(i);
                button.setOnClickListener(v -> onNumberPadButtonClick(button));
            }
        }
    }

    public static Intent newIntent(Context context, int answersToWin, int mistakesToLose, int timeLimit, int randomBound, int difficulty) {
        Intent intent = new Intent(context, QuizTextActivity.class);
        intent.putExtra("answersToWin", answersToWin);
        intent.putExtra("mistakesToLose", mistakesToLose);
        intent.putExtra("timeLimit", timeLimit);
        intent.putExtra("randomBound", randomBound);
        intent.putExtra("difficulty", difficulty);
        return intent;
    }

    private void initViews() {
        textViewScore = findViewById(R.id.textViewScore);
        textViewRemainTime = findViewById(R.id.textViewRemainTime);
        textViewCorrectAnswer = findViewById(R.id.textViewCorrectAnswer);
        textViewIncorrectAnswer = findViewById(R.id.textViewIncorrectAnswer);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewQuestionLabel = findViewById(R.id.textViewQuestionLabel);
        editTextUserAnswer = findViewById(R.id.editTextUserAnswer);
        gridLayoutNumberPad = findViewById(R.id.gridLayoutNumberPad);
        textViewScore.setText(String.format(getString(R.string.score), 0, TO_WIN));
    }
}
