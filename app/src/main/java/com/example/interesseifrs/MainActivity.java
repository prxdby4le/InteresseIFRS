package com.example.interesseifrs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.Html;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        TextView textView = findViewById(R.id.desc_text);
        textView.setText(Html.fromHtml(getString(R.string.text_telainicial), Html.FROM_HTML_MODE_COMPACT));
    }
    public void precesso_seletivo_view (View v) {
        Intent i = new Intent(MainActivity.this, ProcessoSeletivo.class);
        startActivity(i);
    }
    public void guia_vocacao_view (View v) {
        Intent i = new Intent(MainActivity.this, GuiaVocacao.class);
        startActivity(i);
    }
    public void sobre_cursos_view (View v) {
        Intent i = new Intent(MainActivity.this, SobreCursos.class);
        startActivity(i);
    }
    public void inscricoes_view(View v) {
        Uri site = Uri.parse("https://ifrs.edu.br");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void chamadas_view(View v) {
        Uri site = Uri.parse("https://ifrs.edu.br");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void custom_text(View v) {
        Uri site = Uri.parse("https://ingresso.ifrs.edu.br/2025-2/sobre-o-ifrs/");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void desc_text(View v) {
        Uri site = Uri.parse("https://ingresso.ifrs.edu.br/2025-2/");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
}