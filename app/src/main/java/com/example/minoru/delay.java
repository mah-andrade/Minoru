package com.example.minoru;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.minoru.forms.tela_login;
import com.example.minoru.forms.telacliente_layout.form_cliente;
import com.example.minoru.forms.telafunc_tablayout.tela_func;
import com.example.minoru.forms.telagerentel_layout.tela_gerente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class delay extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String gerente = "NaiD5lUTa8WVwXWQubfsfGUQYuf1";
    private DocumentSnapshot aux;
    private DocumentReference recuperarUser;
    private FirebaseUser userAtual = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if(userAtual != null){
                recuperarUser = db.collection("Funcionarios").document(userAtual.getUid());
                recuperarUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){return;}
                        if(value != null && value.exists()){aux = value;}}});
            }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userAtual != null) {
                    if (aux != null) {tela_principal_func();}
                    else if (userAtual.getUid().equals(gerente)){tela_principal_gerente();}
                    else{telamensalista_cliente();}
                }else{
                    startActivity(new Intent(getBaseContext(), tela_login.class));
                    finish();}
            }
        }, 800);
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
        Intent intent = new Intent(this, form_cliente.class);
        startActivity(intent);
        finish();
    }
}

