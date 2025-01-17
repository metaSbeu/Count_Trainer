package com.example.counttrainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizImageActivity extends AppCompatActivity {

    private TextView textViewScore;
    private TextView textViewRemainTime;
    private TextView textViewCorrectAnswer;
    private TextView textViewIncorrectAnswer;
    private FlexboxLayout flexboxLayoutQuestion;
    private TextView textViewQuestionLabel;
    private EditText editTextUserAnswer;
    private GridLayout gridLayoutNumberPad;

    private final Random random = new Random();
    private int score;
    private int correctAnswer;
    private int incorrectUserAnswers = 0;
    private boolean timeout = false;

    private static final int RANDOM_BOUND = 4;
    private static int TO_WIN;
    private static int TO_LOSE;
    private static int TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_image);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        TO_WIN = intent.getIntExtra("answersToWin", TO_WIN);
        TO_LOSE = intent.getIntExtra("mistakesToLose", TO_LOSE);
        TIME = intent.getIntExtra("timeLimit", TIME);

        initViews();
        gridListener();
        generateQuestion();
        startTimer();
    }


    private void generateQuestion() {
        // Очищаем ввод пользователя
        editTextUserAnswer.setText("");

        // Генерация чисел для каждого типа фрукта
        int number1 = random.nextInt(RANDOM_BOUND) + 1; // Количество яблок
        int number2 = random.nextInt(RANDOM_BOUND) + 1; // Количество груш
        int number3 = random.nextInt(RANDOM_BOUND) + 1; // Количество бананов
        int number4 = random.nextInt(RANDOM_BOUND) + 1; // Количество апельсинов

        // Массив данных о фруктах
        int[][] fruitData = {
                {R.drawable.apple, number1, R.string.apple},
                {R.drawable.pear, number2, R.string.pear},
                {R.drawable.banana, number3, R.string.banana},
                {R.drawable.orange, number4, R.string.orange}
        };

        // Случайный выбор двух фруктов для подсчёта
        List<int[]> fruitList = Arrays.asList(fruitData);
        Collections.shuffle(fruitList);

        int[] firstFruit = fruitList.get(0);
        int[] secondFruit = fruitList.get(1);

        // Правильный ответ только для выбранных фруктов
        correctAnswer = firstFruit[1] + secondFruit[1];

        // Очищаем FlexboxLayout
        flexboxLayoutQuestion.removeAllViews();

        // Собираем все изображения в список
        List<Integer> allImages = new ArrayList<>();
        for (int[] fruit : fruitData) {
            for (int i = 0; i < fruit[1]; i++) {
                allImages.add(fruit[0]);
            }
        }

        // Перемешиваем список изображений
        Collections.shuffle(allImages);

        // Добавляем изображения в FlexboxLayout
        for (int drawableId : allImages) {
            addImageToFlexbox(drawableId);
        }

        // Формируем текст вопроса для выбранных фруктов
        String question = String.format(
                getString(R.string.question_how_many_two_fruits),
                getString(firstFruit[2]),
                getString(secondFruit[2])
        );

        textViewQuestionLabel.setText(question);
    }

    // Метод для добавления одного изображения в FlexboxLayout
    private void addImageToFlexbox(int drawableId) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(drawableId);

        // Устанавливаем параметры для ImageView
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                170, // ширина изображения
                170  // высота изображения
        );
        params.setMargins(8, 8, 8, 8);
        imageView.setLayoutParams(params);

        // Добавляем ImageView в FlexboxLayout
        flexboxLayoutQuestion.addView(imageView);
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

    private void onNumberPadButtonClick(Button button) {
        String buttonText = button.getText().toString();

        if (buttonText.equals(getString(R.string.ok))) {
            // Обработка нажатия на кнопку "OK" (например, отправка ответа)
            checkAnswer();
        } else if (buttonText.equals(getString(R.string.del))) {
            // Обработка нажатия на кнопку "DEL" (удаление последнего символа)
            String currentText = editTextUserAnswer.getText().toString();
            if (!currentText.isEmpty()) {
                editTextUserAnswer.setText(currentText.substring(0, currentText.length() - 1));
            }
        } else {
            // Добавление текста кнопки к ответу пользователя
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

    private void checkEndGame() {
        if (incorrectUserAnswers >= TO_LOSE || timeout) {
            textViewQuestionLabel.setText(R.string.lose_message);
            //imageViewLose.setVisibility(View.VISIBLE);
            hideViews();
        } else if (score >= TO_WIN) {
            textViewQuestionLabel.setText(R.string.win_message);
            //imageViewWin.setVisibility(View.VISIBLE);
            hideViews();
        }
    }

    private void hideViews() {
        gridLayoutNumberPad.setVisibility(View.INVISIBLE);
        flexboxLayoutQuestion.setVisibility(View.INVISIBLE);
        editTextUserAnswer.setVisibility(View.INVISIBLE);
        textViewCorrectAnswer.setVisibility(View.INVISIBLE);
        textViewIncorrectAnswer.setVisibility(View.INVISIBLE);
    }

    private void initViews() {
        textViewScore = findViewById(R.id.textViewScore);
        textViewRemainTime = findViewById(R.id.textViewRemainTime);
        textViewCorrectAnswer = findViewById(R.id.textViewCorrectAnswer);
        textViewIncorrectAnswer = findViewById(R.id.textViewIncorrectAnswer);
        flexboxLayoutQuestion = findViewById(R.id.flexboxLayoutQuestion);
        textViewQuestionLabel = findViewById(R.id.textViewQuestionLabel);
        editTextUserAnswer = findViewById(R.id.editTextUserAnswer);
        gridLayoutNumberPad = findViewById(R.id.gridLayoutNumberPad);
        textViewScore.setText(String.format(getString(R.string.score), 0, TO_WIN));
    }

    public static Intent newIntent(Context context, int answersToWin, int mistakesToLose, int timeLimit, int randomBound) {
        Intent intent = new Intent(context, QuizImageActivity.class);
        intent.putExtra("answersToWin", answersToWin);
        intent.putExtra("mistakesToLose", mistakesToLose);
        intent.putExtra("timeLimit", timeLimit);
        intent.putExtra("randomBound", randomBound);
        return intent;
    }
}