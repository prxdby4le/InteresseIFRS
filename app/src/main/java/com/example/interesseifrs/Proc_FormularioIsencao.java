package com.example.interesseifrs;

import android.content.Intent;
import android.net.Uri;
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

    public void form_inscricao_site (View v) {
        Uri site = Uri.parse("https://ifrs.edu.br/rolante/inscricoes-para-o-processo-seletivo-de-estudantes-2025-01-abrem-nesta-quinta/");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void form_acompanhamento (View v) {
        Uri site = Uri.parse("https://ingresso.ifrs.edu.br/2025-2/");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void acesse_isencao (View v) {
        Uri site = Uri.parse("https://ingresso.ifrs.edu.br/2025/publicacoes/resultados-das-solicitacoes-de-isencao-da-taxa-de-inscricao/?_gl=1*1pzbqct*_ga*MTYyNjQyMDEzOS4xNzAxMTkzNjAz*_ga_F5GDL7KJVK*MTcyNzM3MTE2NS4zNS4xLjE3MjczNzExNzMuMC4wLjA.");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
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