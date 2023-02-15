package com.example.minoru.forms.telagerentel_layout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minoru.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class form_precos extends AppCompatActivity {

    private Button btAltCarro , bt_alt_moto;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView horavalorcarro,diarovalorcarro,mensalvalorcarro, horavalormoto , diarovalormoto, mensalvalormoto;
    private ImageView voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_form_precos);
        iniciarComponent();



        //atualizando preco CARRO
        DocumentReference valoresgerenteCarro = db.collection("ValorEstacionamento")
                .document("Carro");
        valoresgerenteCarro.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                horavalorcarro.setText("R$"+formata_valores_br(value.getDouble("avulso")));
                diarovalorcarro.setText("R$"+formata_valores_br(value.getDouble("diario")));
                mensalvalorcarro.setText("R$"+formata_valores_br(value.getDouble("mensal")));
            }
        });

        //atualizando preco moto
        DocumentReference valoresgerenteMoto = db.collection("ValorEstacionamento")
                .document("Moto");

        valoresgerenteMoto.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                horavalormoto.setText("R$"+formata_valores_br(value.getDouble("avulso")));
                diarovalormoto.setText("R$"+formata_valores_br(value.getDouble("diario")));
                mensalvalormoto.setText("R$"+formata_valores_br(value.getDouble("mensal")));
            }
        });


        bt_alt_moto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAlterar(2);
            }
        });

        btAltCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAlterar(1);
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(form_precos.this, tela_gerente.class);
                startActivity(intent);
                finish();
            }
        });

    }





    void openDialogAlterar(int a){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_precocarro);
        dialog.show();
        frontEndMenu(dialog,a);
    }



    void frontEndMenu(Dialog b, int opcao){
        EditText horapreco = b.findViewById(R.id.edittext_hora);
        EditText diariopreco = b.findViewById(R.id.edittext_diario);
        EditText mensalpreco = b.findViewById(R.id.edittext_mensal);
        Button precoalt = b.findViewById(R.id.bt_alterar);
        Button cancel = b.findViewById(R.id.bt_cancela);
        ImageView imageViewpreco = b.findViewById(R.id.imageViewpreco);
        TextView txTexrto = b.findViewById(R.id.textView16);

        if(opcao == 2){
            // img de moto
            imageViewpreco.setImageResource(R.drawable.ic_moto);
            txTexrto.setText("Alterar valores para Moto");

            //bd moto
            DocumentReference valoresgerenteMoto = db.collection("ValorEstacionamento")
                    .document("Moto");

            valoresgerenteMoto.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    horapreco.setText(Double.toString(value.getDouble("avulso")));
                    diariopreco.setText(Double.toString(value.getDouble("diario")));
                    mensalpreco.setText(Double.toString(value.getDouble("mensal")));
                }
            });
        }

        if(opcao == 1){
            DocumentReference valoresgerenteCarro = db.collection("ValorEstacionamento")
                    .document("Carro");
            valoresgerenteCarro.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    horapreco.setText(Double.toString(value.getDouble("avulso")));
                    diariopreco.setText(Double.toString(value.getDouble("diario")));
                    mensalpreco.setText(Double.toString(value.getDouble("mensal")));
                }
            });


        }

        DocumentReference valoresgerenteMoto = db.collection("ValorEstacionamento")
                .document("Moto");

        DocumentReference valoresgerenteCarro = db.collection("ValorEstacionamento")
                .document("Carro");

        precoalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double hora = Double.parseDouble(horapreco.getText().toString());
                double diario = Double.parseDouble(diariopreco.getText().toString());
                double mensal = Double.parseDouble(mensalpreco.getText().toString());

                if(opcao == 2){
                    valoresgerenteMoto.update("avulso", hora);
                    valoresgerenteMoto.update("diario", diario);
                    valoresgerenteMoto.update("mensal", mensal);

                    Toast.makeText(form_precos.this, "Preços Atualizados com Sucesso", Toast.LENGTH_SHORT).show();
                    b.dismiss();

                }
                if(opcao == 1){
                    valoresgerenteCarro.update("avulso", hora);
                    valoresgerenteCarro.update("diario", diario);
                    valoresgerenteCarro.update("mensal", mensal);

                    Toast.makeText(form_precos.this, "Preços Atualizados com Sucesso", Toast.LENGTH_SHORT).show();
                    b.dismiss();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
    }

    String formata_valores_br (double a){

        DecimalFormatSymbols dfs= new DecimalFormatSymbols(new Locale("pt","Brazil"));
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator('.');

        String padrao = "###,###.##";
        DecimalFormat df = new DecimalFormat(padrao,dfs);
        return df.format(a);
    }

    String formata_valores_brEdit (String a){

        DecimalFormatSymbols dfs= new DecimalFormatSymbols(new Locale("pt","Brazil"));
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator('.');

        String padrao = "###,###.##";
        DecimalFormat df = new DecimalFormat(padrao,dfs);
        return df.format(a);
    }


    void iniciarComponent(){
        btAltCarro = findViewById(R.id.bt_alt_carro);
        horavalorcarro = findViewById(R.id.horavalorcarro);
        voltar = findViewById(R.id.voltar);
        mensalvalorcarro = findViewById(R.id.mensalvalorcarro);
        diarovalorcarro = findViewById(R.id.diarovalorcarro);
        bt_alt_moto = findViewById(R.id.bt_alt_moto);
        horavalormoto = findViewById(R.id.horavalormoto);
        mensalvalormoto = findViewById(R.id.mensalvalormoto);
        diarovalormoto = findViewById(R.id.diarovalormoto);
    }
}