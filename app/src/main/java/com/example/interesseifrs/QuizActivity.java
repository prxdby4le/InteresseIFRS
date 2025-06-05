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
            "Qual ambiente de trabalho você imagina como o mais inspirador?",
            "Quando pensa em colocar um projeto em prática, o que mais te motiva?",
            "Qual atividade você se vê realizando com mais prazer no seu tempo livre?",
            "Ao decidir sua carreira, qual aspecto é mais importante para você?",
            "Que tipo de desafio profissional te atrai mais?",
            "Imagine seu dia ideal de trabalho. O que não pode faltar?",
            "Qual dessas atitudes você considera mais gratificante?",
            "Na resolução de um problema, qual abordagem te parece mais atraente?",
            "Como seria o dia a dia ideal para você?",
            "Qual aspecto do seu futuro profissional mais te entusiasma?"
    };

    private String[][] options = {
            {
                    "Um espaço onde o contato com a natureza e atividades práticas fazem parte do dia.",
                    "Um local que permite a exploração de tecnologias e desafios digitais.",
                    "Um ambiente dinâmico que valoriza a organização, o planejamento e o trabalho em equipe."
            },
            {
                    "Contribuir com iniciativas que envolvem sustentabilidade e a promoção do meio ambiente.",
                    "Enfrentar problemas por meio da lógica e da utilização de ferramentas inovadoras.",
                    "Planejar e coordenar estratégias que transformem ideias em resultados concretos."
            },
            {
                    "Explorar espaços ao ar livre e aprender sobre métodos práticos de cultivo e manejo.",
                    "Investigar novas tecnologias, experimentando softwares e soluções digitais.",
                    "Organizar encontros ou eventos, cuidando dos detalhes de uma rotina estruturada."
            },
            {
                    "Ter uma rotina que permita um contato direto com processos práticos e naturais.",
                    "Utilizar o raciocínio crítico e a inovação tecnológica para solucionar desafios.",
                    "Desenvolver habilidades de liderança e trabalhar na criação de estratégias eficientes"
            },
            {
                    "Envolver-se em atividades ligadas à utilização prática de recursos e métodos naturais.",
                    "Resolver problemas por meio de sistemas digitais e desenvolvimentos tecnológicos.",
                    "Otimizar processos e gerir equipes para alcançar metas e resultados planejados."
            },
            {
                    "Momentos que incluam trabalho prático em ambientes com elementos naturais.",
                    "Desafios que instiguem o uso de novas linguagens e ferramentas tecnológicas",
                    "Uma rotina marcada por reuniões, planejamento e a coordenação de projetos."
            },
            {
                    "Fazer parte de iniciativas que promovam práticas sustentáveis e o cuidado com o ambiente.",
                    "Desvendar códigos, explorar recursos digitais e solucionar problemas com criatividade.",
                    "Administrar recursos, conduzir equipes e estruturar projetos com visão de futuro."
            },
            {
                    "Experimentar soluções práticas e colocar a mão na massa, observando os resultados diretamente.",
                    "Analisar a situação usando ferramentas digitais e métodos de raciocínio lógico.",
                    "Organizar as informações e articular um plano com contribuições de diferentes pessoas."
            },
            {
                    "Um dia repleto de atividades que mesclam trabalho prático e contato com a natureza.",
                    "Um dia onde a utilização intensa de tecnologias e a resolução de desafios digitais prevaleçam.",
                    "Um dia estruturado, onde a coordenação de tarefas e o planejamento estratégico sejam essenciais."
            },
            {
                    "Contribuir para projetos que valorizem métodos sustentáveis e o método prático de trabalhar.",
                    "Mergulhar em um universo de inovações tecnológicas e aprendizado contínuo em ambientes digitais.",
                    "Ser o responsável por coordenar estratégias e impulsionar o crescimento por meio da gestão e organização."
            }
    };

    // Mapeamento direto: cada posição corresponde ao curso que a resposta representa
    // Para cada pergunta: [Curso da Opção A, Curso da Opção B, Curso da Opção C]
    private String[][] optionMapping = {
            {AGRO, INFO, ADMIN},    // Pergunta 0
            {AGRO, INFO, ADMIN},    // Pergunta 1
            {AGRO, INFO, ADMIN},    // Pergunta 2
            {AGRO, INFO, ADMIN},    // Pergunta 3
            {AGRO, INFO, ADMIN},    // Pergunta 4
            {AGRO, INFO, ADMIN},    // Pergunta 5
            {AGRO, INFO, ADMIN},    // Pergunta 6
            {AGRO, INFO, ADMIN},    // Pergunta 7
            {AGRO, INFO, ADMIN},    // Pergunta 8
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