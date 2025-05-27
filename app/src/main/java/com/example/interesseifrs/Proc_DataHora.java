package com.example.interesseifrs;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Proc_DataHora extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proc_data_hora);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView datahora = findViewById(R.id.txt_seja_pontual);
        startCountdown(datahora);
    }

    private void startCountdown(TextView countdownText) {
        // Configura o fuso horário para Brasília
        TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");

        // Define a data de abertura das inscrições (1/9/2025 às 00:00 no horário de Brasília)
        Calendar inscricoesCalendar = Calendar.getInstance(timeZone);
        inscricoesCalendar.set(2025, Calendar.SEPTEMBER, 1, 0, 0, 0);
        long inscricoesTime = inscricoesCalendar.getTimeInMillis();

        // Obtém a data atual com o mesmo fuso horário
        Calendar nowCalendar = Calendar.getInstance(timeZone);
        long nowTime = nowCalendar.getTimeInMillis();

        // Verifica se já passou da data das inscrições
        if (nowTime >= inscricoesTime) {
            countdownText.setText("As inscrições do IFRS estão abertas!");
            return;
        }

        // Cria um CountDownTimer que atualiza a cada segundo
        new CountDownTimer(inscricoesTime - nowTime, 1000) {
            public void onTick(long millisUntilFinished) {
                // Calcula meses, dias, horas, minutos e segundos restantes
                Calendar currentCalendar = Calendar.getInstance(timeZone);
                Calendar targetCalendar = (Calendar) inscricoesCalendar.clone();

                long months = monthsBetween(currentCalendar, targetCalendar);

                // Ajusta para mostrar dias restantes no mês atual
                targetCalendar.add(Calendar.MONTH, (int) -months);
                long days = daysBetween(currentCalendar, targetCalendar);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                // Formata a string de contagem regressiva
                String countdownString;
                if (months > 0) {
                    countdownString = String.format(Locale.getDefault(),
                            "Faltam:\n%d meses\n%d dias\n%02d horas\n%02d minutos\n%02d segundos\n\npara as inscrições do IFRS",
                            months, days, hours, minutes, seconds);
                } else {
                    countdownString = String.format(Locale.getDefault(),
                            "Faltam:\n%d dias\n%02d horas\n%02d minutos\n%02d segundos\n\npara as inscrições do IFRS",
                            days, hours, minutes, seconds);
                }

                // Atualiza o TextView
                countdownText.setText(countdownString);
            }

            public void onFinish() {
                countdownText.setText("As inscrições do IFRS estão abertas!");
            }
        }.start();
    }

    // Calcula meses entre duas datas
    private static long monthsBetween(Calendar startDate, Calendar endDate) {
        return (endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)) * 12
                + (endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH));
    }

    // Calcula dias entre duas datas (desconsiderando meses e anos)
    private static long daysBetween(Calendar startDate, Calendar endDate) {
        return endDate.get(Calendar.DAY_OF_MONTH) - startDate.get(Calendar.DAY_OF_MONTH);
    }
}