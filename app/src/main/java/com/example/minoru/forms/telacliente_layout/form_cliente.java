package com.example.minoru.forms.telacliente_layout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minoru.R;
import com.example.minoru.adapter.adapterContrato;
import com.example.minoru.adapter.adapterMensalistas;
import com.example.minoru.forms.tela_login;
import com.example.minoru.models.veiculos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class form_cliente extends AppCompatActivity {
    ImageView sair;
    private adapterContrato adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String id;
    private TextView nomeCl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cliente);
        mAuth = FirebaseAuth.getInstance();
        iniciar_componentes();
        getSupportActionBar().hide();
        iniciarRecyclerView();

        db.collection("Mensalistas").whereEqualTo("email",mAuth.getCurrentUser().getEmail()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()){
                                        nomeCl.setText(document.getString("nome"));
                                    }
                                }
                            }
                        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(form_cliente.this, tela_login.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void iniciarRecyclerView() {
        CollectionReference dadosF = db.collection("Mensalistas");
        Query query = dadosF.whereEqualTo("email",mAuth.getCurrentUser().getEmail());
        FirestoreRecyclerOptions<veiculos> options = new FirestoreRecyclerOptions.Builder<veiculos>()
                .setQuery(query,veiculos.class)
                .build();

        adapter = new adapterContrato(options);
        RecyclerView recyclerView = findViewById(R.id.recyC);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new adapterContrato.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                documentSnapshot.toObject(veiculos.class);
                id = documentSnapshot.getId();
                Intent intent = new Intent(form_cliente.this,form_contrato.class);
                intent.putExtra("Id",id);
                startActivity(intent);
                finish();
            }
        });
    }

    public void iniciar_componentes(){
        sair = findViewById(R.id.sair);
        nomeCl = findViewById(R.id.cliente_id);
    }
}