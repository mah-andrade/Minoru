package com.example.minoru.forms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.minoru.R;
import com.example.minoru.adapter.adapterToken;
import com.example.minoru.forms.telacliente_layout.form_cliente;
import com.example.minoru.forms.telafunc_tablayout.tela_func;
import com.example.minoru.forms.telagerentel_layout.tela_gerente;

import com.example.minoru.models.funcionarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tela_login extends AppCompatActivity {

    private TextView esq_Senha;
    EditText email,senha;
    Button btlogar;
    ProgressBar pg;
    private FirebaseAuth mAuth;
    private final String gerente = "NaiD5lUTa8WVwXWQubfsfGUQYuf1";
    private FirebaseFirestore db;
    private CollectionReference funcionariosRef;
    private ArrayList<String> userFuncionarios = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        funcionariosRef = db.collection("Funcionarios");
        mAuth = FirebaseAuth.getInstance();
        iniciar_componentes();

        esq_Senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(tela_login.this, form_esqueceu_senha.class);
                startActivity(intent);
            }
        });

        btlogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String em = email.getText().toString().trim();
                String se = senha.getText().toString().trim();

                //Validacoes


                if(em.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view,"Campo Email Vazio",Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.show();
                    email.requestFocus();
                }else if(!isValidEmail(em)){
                    Snackbar snackbar = Snackbar.make(view,"Digite um Email Valido",Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.show();
                    email.requestFocus();
                }
                else if(se.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view,"Campo Senha Vazio",Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.show();
                    senha.requestFocus();
                }
                else if(!isValidPassword(se)){
                    Snackbar snackbar = Snackbar.make(view,"Digite uma senha com no minimo 6 Caracteres",Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.show();
                    senha.requestFocus();
                }else{
                    //AUTENTICAR
                    btlogar.setVisibility(View.GONE);
                    EscondeTeclado(view);
                    auth(view);


                }

            }
        });
    }
    private void auth(View view){

        String em = email.getText().toString().trim();
        String se = senha.getText().toString().trim();



        mAuth.signInWithEmailAndPassword(em,se).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    pg.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String userAtual = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            System.out.println("user atual"+userAtual);
                            adapterToken tok = new adapterToken();

                            DocumentReference recuperarUser = db.collection("Funcionarios").document(userAtual);

                            recuperarUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        tok.tk(se,userAtual);
                                        tela_principal_func();

                                    }else{
                                        if(userAtual.equals(gerente)){
                                            tela_principal_gerente();
                                        }else if(userAtual != null){
                                            telamensalista_cliente();
                                        }
                                    }
                                }
                            });
                        }
                    }, 1000);
                }else{
                    String erro;

                    try{
                        throw task.getException();

                    }catch (Exception e){
                        erro = "Email ou senha invalido";
                    }
                    btlogar.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar.make(view,erro,Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();



        // Auth Funcionario ja ta logado
        // id 2323

        FirebaseUser userAtual = FirebaseAuth.getInstance().getCurrentUser();
            if (userAtual != null){
                String usuario = userAtual.getUid();
                System.out.println("Usuario Logado ID : " + usuario);

                DocumentReference recuperarUser = db.collection("Funcionarios").document(usuario);

                recuperarUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            tela_principal_func();
                        }else{
                            if(usuario.equals(gerente)){
                                tela_principal_gerente();
                            }
                            else {
                                telamensalista_cliente();
                            }
                        }
                    }
                });
            }
            }



    void tela_principal_func(){
        Intent intent = new Intent(this, tela_func.class);
        startActivity(intent);
        finish();
    }

    void tela_principal_gerente(){
        Intent intent = new Intent(this, tela_gerente.class);
        startActivity(intent);
        finish();
    }
    void telamensalista_cliente(){
        Intent intent = new Intent(tela_login.this, form_cliente.class);
        startActivity(intent);
        finish();
    }

    void iniciar_componentes(){

        email = findViewById(R.id.Edit_email);
        senha = findViewById(R.id.Edit_senha);
        btlogar = findViewById(R.id.bt_logar);
        pg = findViewById(R.id.progressBar);
        esq_Senha=findViewById(R.id.esq_senha);

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = p.matcher(email);
        return matcher.matches();
    }


    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 7 && pass.length() <=14) {
            return true;
        }
        return false;
    }
    public void EscondeTeclado(View v){

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

    }



}