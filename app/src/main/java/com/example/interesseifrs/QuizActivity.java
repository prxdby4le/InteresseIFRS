package com.example.interesseifrs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion;
    private RadioButton rbOptionA, rbOptionB, rbOptionC;
    private Button btnNext;
    private ProgressBar progressBar;

    private int currentQuestion = 0;
    private String[] answers = new String[10]; // Armazena diretamente o curso escolhido
    private int agroCount = 0, adminCount = 0, infoCount = 0;

    // Constantes para os cursos
    private static final String AGRO = "AGRO";
    private static final String INFO = "INFO";
    private static final String ADMIN = "ADMIN";

    private String[] questions = {
            "1. Em um projeto em grupo, qual função você prefere desempenhar?",
            "2. Qual dessas atividades você mais se identifica?",
            "3. O que mais te atrai em um dia de trabalho?",
            "4. Quando surge um problema, como você costuma agir?",
            "5. Qual destas ferramentas você mais gostaria de dominar?",
            "6. O que te motiva mais em um ambiente profissional?",
            "7. Qual destes desafios você prefere encarar?",
            "8. Em qual dessas situações você se sentiria mais confortável?",
            "9. Qual destes tópicos você gostaria de aprender mais?",
            "10. Como você se imagina daqui a alguns anos?"
    };

    private String[][] options = {
            {
                    "A) Coordenar as tarefas e garantir que tudo esteja nos prazos.",
                    "B) Resolver problemas técnicos e buscar soluções criativas.",
                    "C) Trabalhar ao ar livre, cuidando da execução prática das atividades."
            },
            {
                    "A) Cuidar de plantas, animais ou realizar atividades em contato com a natureza.",
                    "B) Elaborar estratégias para melhorar os resultados de um negócio",
                    "C) Criar e testar programas e sistemas para facilitar processos."
            },
            {
                    "A) Ficar concentrado, programando ou mexendo em equipamentos eletrônicos.",
                    "B) Estar em campo, realizando atividades práticas e manuais.",
                    "C) Analisar relatórios e pensar em formas de otimizar processos."
            },
            {
                    "A) Analisa a situação e busca uma solução que beneficie o grupo.",
                    "B) Coloca a mão na massa e experimenta soluções na prática.",
                    "C) Pesquisa ferramentas ou técnicas para resolver de forma eficiente."
            },
            {
                    "A) Linguagens de programação e softwares.",
                    "B) Planilhas e gráficos para tomada de decisões.",
                    "C) Equipamentos agrícolas e técnicas de manejo."
            },
            {
                    "A) Trabalhar diretamente com a terra ou com animais.",
                    "B) Aprender sobre novas tecnologias e criar soluções inovadoras.",
                    "C) Liderar equipes e ver os resultados acontecerem."
            },
            {
                    "A) Fazer um plano de negócios para uma nova empresa.",
                    "B) Desenvolver um aplicativo para resolver um problema.",
                    "C) Cuidar de uma plantação ou criação de animais até a colheita ou o abate."
            },
            {
                    "A) Ficar horas concentrado em frente ao computador, resolvendo problemas técnicos.",
                    "B) Passar o dia realizando atividades físicas ao ar livre.",
                    "C) Participar de reuniões para definir metas e estratégias."
            },
            {
                    "A) Como abrir e gerenciar uma empresa.",
                    "B) Como aumentar a produtividade no cultivo ou na criação de animais.",
                    "C) Como criar sistemas, aplicativos ou sites."
            },
            {
                    "A) Atuando no campo, produzindo alimentos e cuidando do meio ambiente.",
                    "B) Trabalhando com tecnologia, sempre aprendendo coisas novas.",
                    "C) Gerenciando uma equipe ou um negócio próprio."
            }
    };

    // Mapeamento direto: cada posição corresponde ao curso que a resposta representa
    // Para cada pergunta: [Curso da Opção A, Curso da Opção B, Curso da Opção C]
    private String[][] optionMapping = {
            {ADMIN, INFO, AGRO},    // Pergunta 0
            {AGRO, ADMIN, INFO},    // Pergunta 1
            {INFO, AGRO, ADMIN},    // Pergunta 2
            {ADMIN, AGRO, INFO},    // Pergunta 3
            {INFO, ADMIN, AGRO},    // Pergunta 4
            {AGRO, INFO, ADMIN},    // Pergunta 5
            {ADMIN, INFO, AGRO},    // Pergunta 6
            {INFO, AGRO, ADMIN},    // Pergunta 7
            {ADMIN, AGRO, INFO},    // Pergunta 8
            {AGRO, INFO, ADMIN}     // Pergunta 9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvQuestion = findViewById(R.id.tvQuestion);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        btnNext = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setMax(questions.length);
        loadQuestion();

        btnNext.setOnClickListener(v -> {
            if (validateSelection()) {
                saveAnswer();
                currentQuestion++;

                if (currentQuestion < questions.length) {
                    loadQuestion();
                } else {
                    calculateResult();
                    showResult();
                }
            } else {
                Toast.makeText(this, "Selecione uma opção", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestion() {
        tvQuestion.setText(questions[currentQuestion]);
        rbOptionA.setText(options[currentQuestion][0]);
        rbOptionB.setText(options[currentQuestion][1]);
        rbOptionC.setText(options[currentQuestion][2]);

        // Limpa seleções anteriores
        rbOptionA.setChecked(false);
        rbOptionB.setChecked(false);
        rbOptionC.setChecked(false);

        progressBar.setProgress(currentQuestion + 1);

        if (currentQuestion == questions.length - 1) {
            btnNext.setText("Finalizar");
        }
    }

    private boolean validateSelection() {
        return rbOptionA.isChecked() || rbOptionB.isChecked() || rbOptionC.isChecked();
    }

    private void saveAnswer() {
        // Salva diretamente o curso correspondente à opção selecionada
        if (rbOptionA.isChecked()) {
            answers[currentQuestion] = optionMapping[currentQuestion][0];
        } else if (rbOptionB.isChecked()) {
            answers[currentQuestion] = optionMapping[currentQuestion][1];
        } else if (rbOptionC.isChecked()) {
            answers[currentQuestion] = optionMapping[currentQuestion][2];
        }
    }

    private void calculateResult() {
        // Reseta contadores
        agroCount = 0;
        adminCount = 0;
        infoCount = 0;

        // Conta as respostas por curso
        for (String answer : answers) {
            if (answer != null) {
                switch (answer) {
                    case AGRO:
                        agroCount++;
                        break;
                    case INFO:
                        infoCount++;
                        break;
                    case ADMIN:
                        adminCount++;
                        break;
                }
            }
        }

        // Log para debug
        Log.d("QuizResult", "Agro: " + agroCount + ", Info: " + infoCount + ", Admin: " + adminCount);
    }

    private void showResult() {
        try {
            Intent intent = new Intent(this, ResultActivity.class);

            // Determina o resultado baseado na maior pontuação
            if (agroCount == infoCount && infoCount == adminCount) {
                intent.putExtra("result", "Perfil Multifacetado");
                intent.putExtra("description", "Você demonstra aptidões equilibradas para várias áreas!");
            }
            else if (agroCount > infoCount && agroCount > adminCount) {
                intent.putExtra("result", "Técnico em Agropecuária");
                intent.putExtra("description", "Seu perfil demonstra forte conexão com atividades práticas e sustentáveis relacionadas ao meio ambiente e agropecuária.");
            }
            else if (infoCount > adminCount) {
                intent.putExtra("result", "Técnico em Informática");
                intent.putExtra("description", "Seu interesse por tecnologia e resolução de problemas digitais indica grande afinidade com a área de informática.");
            }
            else {
                intent.putExtra("result", "Técnico em Administração");
                intent.putExtra("description", "Sua habilidade em organização, planejamento e gestão demonstra aptidão para a área administrativa.");
            }

            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao mostrar resultado", Toast.LENGTH_SHORT).show();
            Log.e("QuizError", "Erro: " + e.getMessage());
        }
    }
}