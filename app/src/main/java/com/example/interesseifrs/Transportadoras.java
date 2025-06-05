package com.example.interesseifrs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Transportadoras extends AppCompatActivity {

    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportadoras); // Substitua pelo nome do seu layout

        // Inicializar o ClipboardManager
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // Configurar listeners para os botões de copiar
        setupCopyButtons();
    }

    private void setupCopyButtons() {
        // Botão copiar Citral
        ImageButton btnCopyCitral = findViewById(R.id.btn_copy_citral);
        btnCopyCitral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 9 9454-6869", "Citral");
            }
        });

        // Botão copiar Maroto
        ImageButton btnCopyMaroto = findViewById(R.id.btn_copy_maroto);
        btnCopyMaroto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 99958-4720", "Maroto");
            }
        });

        // Botão copiar Transporte SS
        ImageButton btnCopySs = findViewById(R.id.btn_copy_ss);
        btnCopySs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 3547 2291", "Transporte SS");
            }
        });

        // Botão copiar MW Turismo
        ImageButton btnCopyMw = findViewById(R.id.btn_copy_mw);
        btnCopyMw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 3547-1594", "MW Turismo");
            }
        });

        // Botão copiar Tchê Leva
        ImageButton btnCopyTche = findViewById(R.id.btn_copy_tche);
        btnCopyTche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 9 9707-2829", "Tchê Leva Turismo");
            }
        });

        // Botão copiar DC Turismo
        ImageButton btnCopyDc = findViewById(R.id.btn_copy_dc);
        btnCopyDc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard("(51) 9 9646-6420", "DC Turismo");
            }
        });
    }

    private void copyToClipboard(String phoneNumber, String companyName) {
        ClipData clip = ClipData.newPlainText("Telefone " + companyName, phoneNumber);
        clipboardManager.setPrimaryClip(clip);

        // Mostrar toast de confirmação
        Toast.makeText(this, "Número da " + companyName + " copiado: " + phoneNumber,
                Toast.LENGTH_SHORT).show();
    }
    public void voltar (View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void home (View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}