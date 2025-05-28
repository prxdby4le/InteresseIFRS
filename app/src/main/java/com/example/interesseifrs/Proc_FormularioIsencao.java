package com.example.interesseifrs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Proc_FormularioIsencao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proc_formulario_isencao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView textView = findViewById(R.id.desc_fazerinscricao);
        textView.setText(Html.fromHtml(getString(R.string.como_fazer_inscrição), Html.FROM_HTML_MODE_COMPACT));
    }
    public void voltar (View v) {
        Intent i = new Intent(this, ProcessoSeletivo.class);
        startActivity(i);
    }
    public void home (View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}