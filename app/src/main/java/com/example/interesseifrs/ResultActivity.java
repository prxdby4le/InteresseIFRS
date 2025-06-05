package com.example.interesseifrs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.interesseifrs.QuizActivity;
import com.example.interesseifrs.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Verificação segura dos extras
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String description = intent.getStringExtra("description");

        if(result == null || description == null) {
            Toast.makeText(this, "Dados do resultado não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvResultTitle = findViewById(R.id.tvResultTitle);
        TextView tvResultDescription = findViewById(R.id.tvResultDescription);
        Button btnRestart = findViewById(R.id.btnRestart);

        tvResultTitle.setText(result);
        tvResultDescription.setText(description);

        btnRestart.setOnClickListener(v -> {
            startActivity(new Intent(this, QuizActivity.class));
            finish();
        });
    }
    public void voltar (View v) {
        Intent i = new Intent(this, QuizActivity.class);
        startActivity(i);
    }
    public void home (View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}