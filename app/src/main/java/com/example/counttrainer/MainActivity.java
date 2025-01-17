package com.example.counttrainer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button buttonTextQuiz;
    private Button buttonImageQuiz;
    private Button buttonInequality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        buttonTextQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLevelSelection("text");
            }
        });

        buttonImageQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLevelSelection("image");
            }
        });

        buttonInequality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLevelSelection("inequality");
            }
        });
    }

    private void launchLevelSelection(String type) {
        Intent intent = LevelSelectionActivity.newIntent(this, type);
        startActivity(intent);
    }

    private void initViews() {
        buttonTextQuiz = findViewById(R.id.buttonTextQuiz);
        buttonImageQuiz = findViewById(R.id.buttonImageQuiz);
        buttonInequality = findViewById(R.id.buttonInequality);
    }
}