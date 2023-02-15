package com.example.minoru.forms.telagerentel_layout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.minoru.R;
import com.example.minoru.forms.tela_login;
import com.example.minoru.forms.telafunc_tablayout.tela_func;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class tela_gerente extends AppCompatActivity {

    private Button movimento, funcionarios, precos,financas;
    private ImageView logout,ajuda;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tela_gerente);
        iniciar_component();
        ajuda = findViewById(R.id.ajuda);
        ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(tela_gerente.this,R.style.CustomAlertDialog);
                dialog.setContentView(R.layout.dialog_ajudagerente);
                dialog.setCancelable(false);
                dialog.show();
                Button a = dialog.findViewById(R.id.bt_inf_cancelar);
                a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        DocumentReference MensalistaCad = db.collection("Mensal").document(month());
        MensalistaCad.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                }else{
                    Map<Object,Integer> tClientes = new HashMap<>();
                    tClientes.put("totalClientes",0);
                    tClientes.put("valorRendimento",0);
                    tClientes.put("salariosFunc",0);
                    tClientes.put("mensalistas",0);
                    MensalistaCad.set(tClientes);
                }
            }
        });

        DocumentReference valorCarro = db.collection("ValorEstacionamento")
                .document("Carro");
        valorCarro.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){}
                        else{
                            Map<Object, Double> tPreco = new HashMap<>();
                            tPreco.put("avulso",0.0);
                            tPreco.put("diario",0.0);
                            tPreco.put("mensal",0.0);
                            valorCarro.set(tPreco);

                        }
                    }
                });

        DocumentReference valorMoto = db.collection("ValorEstacionamento").document("Moto");

        valorMoto.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                }else{
                    Map<Object, Double> tPreco = new HashMap<>();
                    tPreco.put("avulso",0.0);
                    tPreco.put("diario",0.0);
                    tPreco.put("mensal",0.0);
                    valorMoto.set(tPreco);
                }
            }
        });

        financas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(tela_gerente.this, form_financas.class);
                startActivity(intent);
            }
        });

        movimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(tela_gerente.this, form_movimento.class);
                startActivity(intent);
            }
        });

        precos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tela_gerente.this, form_precos.class);
                startActivity(intent);
            }
        });

        funcionarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(tela_gerente.this, form_funcionarios.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(tela_gerente.this,tela_login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    String month(){
        Calendar calendar = Calendar.getInstance();
        int b = calendar.get(Calendar.MONTH)+1;
        int a = calendar.get(Calendar.YEAR);
        String date = (Integer.toString(b))+(Integer.toString(a));
        return date ;
    }





    void iniciar_component(){
        financas = findViewById(R.id.ic_financas);
        movimento = findViewById(R.id.ic_movimento);
        precos = findViewById(R.id.ic_preços);
        funcionarios = findViewById(R.id.ic_Funcionarios);
        logout = findViewById(R.id.sair);
    }
}