package com.example.counttrainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class QuizInequalityActivity extends AppCompatActivity {

    private int TO_WIN;
    private int TO_LOSE;
    private int TIME;
    private int RANDOM_BOUND;

    private int correctAnswers = 0;
    private int mistakes = 0;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewRemainTime;
    private TextView textViewCorrectAnswer;
    private TextView textViewIncorrectAnswer;
    private Button buttonYes;
    private Button buttonNo;

    private Random random = new Random();
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_inequality);

        TO_WIN = getIntent().getIntExtra("answersToWin", 5);
        TO_LOSE = getIntent().getIntExtra("mistakesToLose", 3);
        TIME = getIntent().getIntExtra("timeLimit", 30000);
        RANDOM_BOUND = getIntent().getIntExtra("randomBound", 10);

        initViews();
        startTimer();
        generateNewInequality();
    }

    private void initViews() {
        textViewQuestion = findViewById(R.id.textViewQuestion);
        buttonYes = findViewById(R.id.buttonYes);
        buttonNo = findViewById(R.id.buttonNo);

        textViewScore = findViewById(R.id.textViewScore);
        textViewRemainTime = findViewById(R.id.textViewRemainTime);
        textViewCorrectAnswer = findViewById(R.id.textViewCorrectAnswer);
        textViewIncorrectAnswer = findViewById(R.id.textViewIncorrectAnswer);
        textViewScore.setText(String.format(getString(R.string.score), 0, TO_WIN));

        buttonYes.setOnClickListener(view -> checkAnswer(true));
        buttonNo.setOnClickListener(view -> checkAnswer(false));
    }

    private void generateNewInequality() {
        int number1 = random.nextInt(RANDOM_BOUND) + 1;
        int number2 = random.nextInt(RANDOM_BOUND) + 1;
        int number3 = random.nextInt(RANDOM_BOUND) + 1;

        String operator = randomOperator();
        String inequalitySign = randomInequalitySign();

        String correctExpression = calculateExpression(number1, number2, operator, inequalitySign, number3);
        textViewQuestion.setTag(correctExpression);
        textViewQuestion.setText(String.format("%d %s %d %s %d", number1, operator, number2, inequalitySign, number3));
    }

    private String randomOperator() {
        String[] operators = {"+", "-", "*", "/"};
        return operators[random.nextInt(operators.length)];
    }

    private String randomInequalitySign() {
        String[] signs = {"<", ">", "<=", ">="};
        return signs[random.nextInt(signs.length)];
    }

    private String calculateExpression(int number1, int number2, String operator, String inequalitySign, int number3) {
        double result = 0;
        switch (operator) {
            case "+":
                result = number1 + number2;
                break;
            case "-":
                result = number1 - number2;
                break;
            case "*":
                result = number1 * number2;
                break;
            case "/":
                result = (number2 != 0) ? (double) number1 / number2 : 0;
                break;
        }
        return evaluateInequality(result, inequalitySign, number3) ? "true" : "false";
    }

    private boolean evaluateInequality(double result, String sign, int number3) {
        switch (sign) {
            case "<":
                return result < number3;
            case ">":
                return result > number3;
            case "<=":
                return result <= number3;
            case ">=":
                return result >= number3;
            default:
                return false;
        }
    }

    private void checkAnswer(boolean userAnswer) {
        String correctAnswer = textViewQuestion.getTag().toString();
        if ((userAnswer && correctAnswer.equals("true")) || (!userAnswer && correctAnswer.equals("false"))) {
            textViewCorrectAnswer.setVisibility(TextView.VISIBLE);
            textViewIncorrectAnswer.setVisibility(TextView.INVISIBLE);
            correctAnswers++;
            textViewScore.setText(getString(R.string.score, correctAnswers, TO_WIN));

            if (correctAnswers >= TO_WIN) {
                endGame(true);
            } else {
                generateNewInequality();
            }
        } else {
            textViewCorrectAnswer.setVisibility(TextView.INVISIBLE);
            textViewIncorrectAnswer.setText(R.string.answer_incorrect);
            textViewIncorrectAnswer.setVisibility(TextView.VISIBLE);
            mistakes++;

            if (mistakes >= TO_LOSE) {
                endGame(false);
            } else {
                generateNewInequality();
            }
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewRemainTime.setText(getString(R.string.remain_time, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                endGame(false);
            }
        }.start();
    }

    private void endGame(boolean isWin) {
        countDownTimer.cancel();
        if (isWin) {
            Toast.makeText(this, R.string.win_message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.lose_message, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public static Intent newIntent(Context context, int answersToWin, int mistakesToLose, int timeLimit, int randomBound) {
        Intent intent = new Intent(context, QuizInequalityActivity.class);
        intent.putExtra("answersToWin", answersToWin);
        intent.putExtra("mistakesToLose", mistakesToLose);
        intent.putExtra("timeLimit", timeLimit);
        intent.putExtra("randomBound", randomBound);
        return intent;
    }
}
