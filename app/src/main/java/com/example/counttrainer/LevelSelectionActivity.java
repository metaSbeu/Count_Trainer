package com.example.counttrainer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectionActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private String type;

    // Константа для верхней границы генерации
    private static final int RANDOM_BOUND = 100;

    // Массивы с параметрами сложности
// Массивы с параметрами сложности для каждого уровня
    private final int[] answersToWinArray = {
            2, 3, 5, 5, 5, 10, 10, 10, 15, 15, 15, 20, 25, 30, 30, 50
    };

    private final int[] mistakesToLoseArray = {
            5, 5, 5, 5, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2
    };

    private final int[] randomBoundArray = {
            5, 5, 8, 8, 8, 8, 8, 10, 10, 10, 10, 15, 15, 15, 20, 30
    };

    private final int[] timeLimitArray = {
            15000, 15000, 20000, 15000, 10000, 60000, 30000, 20000, 60000, 40000, 30000, 60000, 45000, 45000, 45000, 100000
    };

    private final int[] difficultyArray = {
            1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4
    };

    private TextView textViewLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);

        type = getIntent().getStringExtra("type");

        gridLayout = findViewById(R.id.gridLayout);
        textViewLabel = findViewById(R.id.textViewLabel);

        if (type.equals("text")) {
            textViewLabel.setText("Текстовые уровни");
        } else if (type.equals("image")) {
            textViewLabel.setText("Уровни с изображениями:");
        } else if (type.equals("inequality")) {
            textViewLabel.setText("Уровни с неравенствами:");
        }

        setupButtons();
    }

    private void setupButtons() {
        int childCount = gridLayout.getChildCount();

        if (childCount != 16) {
            throw new IllegalStateException("GridLayout должен содержать 16 кнопок!");
        }

        for (int i = 0; i < childCount; i++) {
            View child = gridLayout.getChildAt(i);

            if (child instanceof Button) {
                Button button = (Button) child;
                button.setText(String.valueOf(i + 1));

                button.setOnClickListener(view -> {
                    int level = Integer.parseInt(button.getText().toString());
                    int answersToWin = answersToWinArray[level - 1];
                    int mistakesToLose = mistakesToLoseArray[level - 1];
                    int randomBound = randomBoundArray[level - 1];
                    int timeLimit = timeLimitArray[level - 1];

                    if ("text".equals(type)) {
                        onTextLevelSelected(level, answersToWin, mistakesToLose, timeLimit, randomBound, difficultyArray[level - 1]);
                    } else if ("image".equals(type)) {
                        onImageLevelSelected(level, answersToWin, mistakesToLose, timeLimit, randomBound);
                    } else if ("inequality".equals(type)) {
                        onInequalityLevelSelected(level, answersToWin, mistakesToLose, timeLimit, randomBound);
                    }
                });
            }
        }
    }

    private void onInequalityLevelSelected(int level, int answersToWin, int mistakesToLose, int timeLimit, int randomBound) {
        Toast.makeText(this, "Выбран уровень неравенств " + level, Toast.LENGTH_SHORT).show();
        startActivity(QuizInequalityActivity.newIntent(this, answersToWin, mistakesToLose, timeLimit, randomBound));
    }

    private void onTextLevelSelected(int level, int answersToWin, int mistakesToLose, int timeLimit, int randomBound, int difficulty) {
        // Действия для текстового задания
        Toast.makeText(this, "Выбран текстовый уровень " + level, Toast.LENGTH_SHORT).show();
        // Открываем активность с параметрами
        startActivity(QuizTextActivity.newIntent(this, answersToWin, mistakesToLose, timeLimit, randomBound, difficulty));
    }

    private void onImageLevelSelected(int level, int answersToWin, int mistakesToLose, int timeLimit, int randomBound) {
        // Действия для задания с картинками
        Toast.makeText(this, "Выбран уровень с картинками " + level, Toast.LENGTH_SHORT).show();
        // Открываем активность с параметрами
        startActivity(QuizImageActivity.newIntent(this, answersToWin, mistakesToLose, timeLimit, randomBound));
    }

    public static Intent newIntent(Context context, String type) {
        Intent intent = new Intent(context, LevelSelectionActivity.class);
        intent.putExtra("type", type);
        return intent;
    }
}
