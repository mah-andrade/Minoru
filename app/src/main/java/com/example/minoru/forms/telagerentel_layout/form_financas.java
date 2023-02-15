package com.example.minoru.forms.telagerentel_layout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.minoru.R;
import com.example.minoru.forms.tela_login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Year;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class form_financas extends AppCompatActivity {
    private ImageView voltar;
    private TextView nredimentos,nclientes,nmensalistas,vsalarios,nlucro;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference valores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_form_financas);
        iniciarcompo();

        // Declaração do Spinner

        final List<String> meses = Arrays.asList("","Janeiro","Fevereiro","Março", "Abril","Maio","Junho","Julho", "Agosto", "Setembro","Outubro","Novembro","Dezembro");
        final Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter adapter =new ArrayAdapter(getApplicationContext(), R.layout.spinner_itemlayout, meses);
        adapter.setDropDownViewResource(R.layout.spinner_itemlayout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                valores = db.collection("Mensal").document(i+year());
                valores.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            System.out.println("erro");
                        }
                        if(value !=null && value.exists()){
                            System.out.println("Aqui");
                            nredimentos.setText("R$ "+formata_valores_br(value.getDouble("valorRendimento")));
                            nclientes.setText(String.valueOf(value.getDouble("totalClientes").intValue()));
                            nmensalistas.setText(String.valueOf(value.getDouble("mensalistas").intValue()));
                            vsalarios.setText("R$ "+formata_valores_br(value.getDouble("salariosFunc")));
                            double salariofunc = value.getDouble("salariosFunc");
                            double rend = value.getDouble("valorRendimento");
                            double lucro = rend - salariofunc;
                            nlucro.setText("R$ "+formata_valores_br(lucro));
                        }
                        else{
                            nredimentos.setText("R$ 0");
                            vsalarios.setText("R$ 0");
                            nlucro.setText("R$ 0");
                            nclientes.setText("0");
                            nmensalistas.setText("0");
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(form_financas.this, tela_gerente.class);
                startActivity(intent);
                finish();
            }
        });

    }



    void iniciarcompo(){
        nredimentos = findViewById(R.id.nredimentos);
        voltar = findViewById(R.id.voltar);
        nclientes = findViewById(R.id.nclientes);
        nmensalistas = findViewById(R.id.nmensalistas);
        vsalarios = findViewById(R.id.vsalarios);
        nlucro = findViewById(R.id.nlucro);
    }



    String year(){
        Calendar calendar = Calendar.getInstance();

        int a = calendar.get(Calendar.YEAR);

        return Integer.toString(a) ;
    }

    String formata_valores_br (double a){

        DecimalFormatSymbols dfs= new DecimalFormatSymbols(new Locale("pt","Brazil"));
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator('.');

        String padrao = "###,###.##";
        DecimalFormat df = new DecimalFormat(padrao,dfs);
        return df.format(a);
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
}