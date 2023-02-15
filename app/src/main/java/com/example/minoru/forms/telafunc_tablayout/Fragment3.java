package com.example.minoru.forms.telafunc_tablayout;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minoru.R;
import com.example.minoru.adapter.adapterMensalistas;
import com.example.minoru.adapter.adapterVeiculo;
import com.example.minoru.models.veiculos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment3 extends Fragment {

    private View groupFragmentView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private adapterMensalistas adapter;
    private String id,veiculomensal,VencimentoFormatado,InicioContrato;
    private LocalDate localDate;
    private double valorfinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_3, container,false);
        iniciarRecyclerView();

        return groupFragmentView;
    }

    private void iniciarRecyclerView() {
        CollectionReference dadosF = db.collection("Mensalistas");
        Query query = dadosF.orderBy("nome",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<veiculos> options = new FirestoreRecyclerOptions.Builder<veiculos>()
                .setQuery(query,veiculos.class)
                .build();

        adapter = new adapterMensalistas(options);
        RecyclerView recyclerView = groupFragmentView.findViewById(R.id.recytab2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(groupFragmentView.getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new adapterMensalistas.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                documentSnapshot.toObject(veiculos.class);
                id = documentSnapshot.getId();
                openDialogFinish();
            }
        });
    }

    private void openDialogFinish() {
        Dialog dialog = new Dialog(getContext(),R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_infomensal);
        dialog.show();
        frontDialog(dialog);

    }

    public void frontDialog(Dialog dialog){
        TextView tvNome = dialog.findViewById(R.id.txtnome);
        TextView tvTel = dialog.findViewById(R.id.txttel);
        TextView tvCliente = dialog.findViewById(R.id.txtcliente);
        TextView tvDatacontrato = dialog.findViewById(R.id.txtdataentrada);
        TextView tvVencientocontrato = dialog.findViewById(R.id.txtdatavencimento);
        TextView tvVeiculo = dialog.findViewById(R.id.txtvec);
        TextView tvModelo = dialog.findViewById(R.id.txtmodelovec);
        TextView tvPlaca = dialog.findViewById(R.id.txtplaca);
        TextView tvVaga = dialog.findViewById(R.id.txtvaga2);
        Button renovar = dialog.findViewById(R.id.bt_inf_renova);
        Button fechar = dialog.findViewById(R.id.bt_inf_cancelar);

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        db.collection("Mensalistas").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot dc) {
                tvNome.setText(dc.getString("nome"));
                tvTel.setText(dc.getString("tel"));
                tvCliente.setText("Mensalista");
                tvDatacontrato.setText(dc.getString("InicioContrato"));
                tvVencientocontrato.setText(dc.getString("VencimentoContrato"));
                //PEGANDO VEC
                if(dc.getString("veiculo").equals("1")){tvVeiculo.setText("Carro");}
                else{tvVeiculo.setText("Moto");}
                tvModelo.setText(dc.getString("modelo"));
                tvPlaca.setText(dc.getString("placa"));
                tvVaga.setText(dc.getString("vaga"));
                veiculomensal = tvVeiculo.getText().toString().trim();
                String Vencimento = tvVencientocontrato.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    localDate = LocalDate.parse(Vencimento,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
            }
        });

        renovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMensalPagamento();
                dialog.dismiss();
            }
        });

    }
    public void openDialogMensalPagamento(){
        Dialog dialog = new Dialog(getContext(),R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_pagamento);
        dialog.show();
        frontDialogmensalPagamento(dialog);

    }

    private void frontDialogmensalPagamento(Dialog dialog) {

        TextView txtvalpag = dialog.findViewById(R.id.txtvalpag);
        Button bt_sim = dialog.findViewById(R.id.bt_sim);
        Button bt_nao = dialog.findViewById(R.id.bt_nao);
        TextView id_vencimento = dialog.findViewById(R.id.id_vencimento);

        String carro = "1";
        if (carro.equals(veiculomensal)){
            db.collection("ValorEstacionamento").document("Carro").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    valorfinal = value.getDouble("mensal");
                    txtvalpag.setText(String.valueOf(valorfinal));
                }
            });
        }else{
            db.collection("ValorEstacionamento").document("Moto").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    valorfinal = value.getDouble("mensal");
                    txtvalpag.setText(String.valueOf(valorfinal));
                }
            });
        }

        // somente api android 8 acima
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            InicioContrato = localDate.format(formatter);
            localDate = localDate.plusDays(30);
            VencimentoFormatado = localDate.format(formatter);

        }
        id_vencimento.setText(VencimentoFormatado);

        bt_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Mensalistas").document(id).update("VencimentoContrato",VencimentoFormatado);
                DocumentReference valoresMonth =  db.collection("Mensal").document(month());

                valoresMonth.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                double auxR = document.getDouble("valorRendimento");
                                auxR = auxR + valorfinal;
                                valoresMonth.update("valorRendimento",auxR);
                            }else{}
                        }else{
                            System.out.println("falha ao recuperar por que : "+ task.getException());
                        }
                    }
                });
                Toast.makeText(getContext(), "Mensalidade Renovada com Sucesso" , Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        bt_nao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    String month(){
        Calendar calendar = Calendar.getInstance();
        int b = calendar.get(Calendar.MONTH)+1;
        int a = calendar.get(Calendar.YEAR);
        String date = (Integer.toString(b))+(Integer.toString(a));
        return date ;
    }

    public  String dateCapter() {

        String Format;
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