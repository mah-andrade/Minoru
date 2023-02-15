package com.example.minoru.forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.minoru.R;
import com.example.minoru.forms.telagerentel_layout.form_financas;
import com.example.minoru.forms.telagerentel_layout.tela_gerente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class form_esqueceu_senha extends AppCompatActivity {
    private Button esqsenha;
    private EditText esq_email;
    private ImageView voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_form_esqueceu_senha);
        IniciarCoponents();
        esqsenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = esq_email.getText().toString();

                recuperarSenha();


            }
        });


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(form_esqueceu_senha.this, tela_login.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void IniciarCoponents() {
        esqsenha = findViewById(R.id.bt_esq);
        esq_email= findViewById(R.id.Edit_email_esq);
        voltar= findViewById(R.id.voltar);
    }


    private void recuperarSenha() {

        String email = esq_email.getText().toString().trim();
        if (email.equals("minorugerente@gmail.com")){
            Toast.makeText(getBaseContext(), "Gerente nao pode ser alterado",
                    Toast.LENGTH_SHORT).show();
        }else if (email.isEmpty()) {
            Toast.makeText(getBaseContext(), "Insira seu email",
                    Toast.LENGTH_SHORT).show();

        } else {

            enviarEmail(email);
        }
    }

    private void enviarEmail(String email) {


        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(getBaseContext(), "Enviamos um messagem para o seu email para redefinir sua senha",
                        Toast.LENGTH_SHORT).show();

                Intent envesq = new Intent(form_esqueceu_senha.this, tela_login.class);
                startActivity(envesq);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getBaseContext(), "E-mail invalido ou não cadastrado",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }
}