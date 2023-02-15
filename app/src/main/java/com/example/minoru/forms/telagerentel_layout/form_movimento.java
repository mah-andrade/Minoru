package com.example.minoru.forms.telagerentel_layout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minoru.R;
import com.example.minoru.adapter.adapterMensalistas;
import com.example.minoru.adapter.adapterVeiculoFragment2;
import com.example.minoru.forms.telafunc_tablayout.tela_func;
import com.example.minoru.models.veiculos;
import com.example.minoru.models.veiculosF2;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class form_movimento extends AppCompatActivity {
    private ImageView voltar;
    private TextView datamov;
    private adapterVeiculoFragment2 adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id,bd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_form_movimento);
        IniciarComponentes();
        iniciarRecyclerView();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate ld = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            datamov.setText(ld.format(formatter));
        }



        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(form_movimento.this, tela_gerente.class);
                startActivity(intent);
                finish();
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
        CollectionReference dadosFina = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("FINALIZADOS");

        Query query = dadosFina.orderBy("nome",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<veiculosF2> options = new FirestoreRecyclerOptions.Builder<veiculosF2>()
                .setQuery(query,veiculosF2.class)
                .build();

        adapter = new adapterVeiculoFragment2(options);
        RecyclerView recyclerView = findViewById(R.id.recy_vec_mov);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public  String dateCapter() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int month = calendar.get(calendar.MONTH) + 1;
        int year = calendar.get(calendar.YEAR);

        if (day >= 10) {
            if (month >= 10) {
                String date = day + "." + month + "." + year;
                return date;
            } else {
                String date = day + ".0" + month + "." + year;
                return date;
            }
        } else {
            if (month >= 10) {
                String date = "0" + day + "." + month + "." + year;
                return date;
            } else {
                String date = "0" + day + ".0" + month + "." + year;
                return date;
            }
        }
    }


    void IniciarComponentes(){

        voltar = findViewById(R.id.voltar);
        datamov = findViewById(R.id.datamov);
    }
}
