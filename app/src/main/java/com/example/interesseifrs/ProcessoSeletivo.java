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

public class ProcessoSeletivo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_processo_seletivo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtendo o TextView e aplicando a formatação HTML ao conteúdo
        TextView textView = findViewById(R.id.desc_processo);
        textView.setText(Html.fromHtml(getString(R.string.processo_desc), Html.FROM_HTML_MODE_COMPACT));
    }
    public void txt_processo(View v) {
        Uri site = Uri.parse("https://estude.ifrs.edu.br/");
        Intent i = new Intent(Intent.ACTION_VIEW, site);
        startActivity(i);
    }
    public void proc_form_e_isencao (View v) {
        Intent i = new Intent(this, Proc_FormularioIsencao.class);
        startActivity(i);
    }
    public void proc_documentos (View v) {
        Intent i = new Intent(this, Proc_DocsPraLevar.class);
        startActivity(i);
    }
    public void proc_trajeto (View v) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
    public void proc_datahora (View v) {
        Intent i = new Intent(this, Proc_DataHora.class);
        startActivity(i);
    }
    public void proc_abertura (View v) {
        Intent i = new Intent(this, Proc_Status.class);
        startActivity(i);
    }
}