package com.example.minoru.forms.telafunc_tablayout;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.minoru.R;
import com.example.minoru.adapter.adapterVeiculo;
import com.example.minoru.adapter.adapterVeiculoFragment2;
import com.example.minoru.models.veiculos;
import com.example.minoru.models.veiculosF2;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class Fragment2 extends Fragment {
    private View groupFragmentView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private adapterVeiculo adapter;
    private String id, clienteFinal,veiculoFinal,timeFinal;
    private Dialog dialogResumo, dialogFinal;
    private double valorDiario_c, valorAvulso_c,valorfinal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_2, container,false);
        iniciarRecyclerView();

        return groupFragmentView;
    }
    private void iniciarRecyclerView() {
        CollectionReference dadosF = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario");
        Query query = dadosF.orderBy("nome",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<veiculos> options = new FirestoreRecyclerOptions.Builder<veiculos>()
                .setQuery(query,veiculos.class)
                .build();
        adapter = new adapterVeiculo(options);
        RecyclerView recyclerView = groupFragmentView.findViewById(R.id.recytab2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(groupFragmentView.getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new adapterVeiculo.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                documentSnapshot.toObject(veiculos.class);
                id = documentSnapshot.getId();

                openDialogFinish();
            }
        });
    }
    private void openDialogFinish() {
        dialogResumo = new Dialog(getContext(),R.style.CustomAlertDialog);
        dialogResumo.setContentView(R.layout.dialog_finishcliente);
        dialogResumo.show();
        frontDialog(dialogResumo);
    }
    private void frontDialog(Dialog dialog) {
        TextView txtName = dialog.findViewById(R.id.txtnome);
        TextView txthorario = dialog.findViewById(R.id.txthoraentrada);
        TextView txtTelefone = dialog.findViewById(R.id.txttel);
        Button bt_finalizar = dialog.findViewById(R.id.btfinalizar);
        TextView txtVaga = dialog.findViewById(R.id.txtvaga2);
        TextView txtPlaca = dialog.findViewById(R.id.txtplaca);
        TextView txtVeiculo = dialog.findViewById(R.id.txtvec);
        TextView txtCliente = dialog.findViewById(R.id.txtcliente);
        Button cancel = dialog.findViewById(R.id.bt_inf_cancelar);
        TextView txtModel = dialog.findViewById(R.id.txtmodelovec);
        DocumentReference recuperarDados = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario").document(id);
        recuperarDados.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            txtName.setText(documentSnapshot.getString("nome"));
                            txthorario.setText(documentSnapshot.getString("hora"));
                            txtTelefone.setText(documentSnapshot.getString("tel"));
                            txtVaga.setText(documentSnapshot.getString("vaga"));
                            txtPlaca.setText(documentSnapshot.getString("placa"));
                            txtModel.setText(documentSnapshot.getString("modelo"));
                            // identificando e mostrando o cliente
                            double cliente;
                            cliente = documentSnapshot.getDouble("cliente");
                            int c = (int) cliente;
                            if(c == 2){
                                txtCliente.setText("Avulso");
                                clienteFinal = "Avulso";
                            }else if(c == 1){
                                txtCliente.setText("Diario");
                                clienteFinal = "Diario";
                            }
                            // FAZER MENSAL DEPOIS MENSAL NUMBER 3
                            //identificando e mostrando para o cliente
                            String veiculo = documentSnapshot.getString("veiculo");
                            int v = Integer.parseInt(veiculo);
                            System.out.println("veiculo eh :"+v);
                            if(v == 1){
                                txtVeiculo.setText("CARRO");
                                veiculoFinal = "CARRO";
                            }else if(v == 2){
                                txtVeiculo.setText("MOTO");
                                veiculoFinal = "MOTO";
                            }
                        }
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        bt_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfinis();
            }
        });
    }
    private void openfinis() {
        dialogFinal = new Dialog(getContext(),R.style.CustomAlertDialog);
        dialogFinal.setContentView(R.layout.dialog_finishclientefinal);
        dialogFinal.show();
        dialogcp(dialogFinal);
    }
    private void dialogcp(Dialog dialog) {
        TextView txtnome = dialog.findViewById(R.id.txt_nome);
        TextView txtplaca = dialog.findViewById(R.id.txt_placa);
        TextView txtHe = dialog.findViewById(R.id.txt_he);
        TextView txtHs = dialog.findViewById(R.id.txt_hs);
        TextView txtRs = dialog.findViewById(R.id.txt_rs);
        Button btcp = dialog.findViewById(R.id.bt_cp);
        Button cpCancelar = dialog.findViewById(R.id.cp_cancelar);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar2);
        DocumentReference dadosRef = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario").document(id);
        dadosRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // VERDADEIRO ACHAR O DOCUMENTO
                txtnome.setText(documentSnapshot.getString("nome"));
                txtplaca.setText(documentSnapshot.getString("placa"));
                txtHe.setText(documentSnapshot.getString("hora"));
                // valor da entrada em min
                String[] a = txtHe.getText().toString().split(":");
                int tMinInicio = calcularValor(a[0], a[1]);
                //pegando o valor da saida
                Calendar c = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("kk:mm");
                String time = format.format(c.getTime());
                timeFinal = time;
                txtHs.setText(time);
                String[] b = time.split(":");
                int tMinFinal = calcularValor(b[0], b[1]);
                double resMin = tMinFinal - tMinInicio;
                System.out.println("VALOR min:" + resMin);
                //identificando o cliente
                double cliente = documentSnapshot.getDouble("cliente");
                int clienteInt = (int) cliente;
                //identificando o veiculo {e chamando a funcao calcular preco para nao ter delay}
                //arrumar banco de dados carro 1 moto 2
                String veicy = documentSnapshot.getString("veiculo");
                int veiculo = Integer.parseInt(veicy);
                if (veiculo == 1) {
                    DocumentReference valoresgerente = db.collection("ValorEstacionamento")
                            .document("Carro");
                    valoresgerente.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            valorAvulso_c = value.getDouble("avulso");
                            valorDiario_c = value.getDouble("diario");
                            // Calcular preco
                            double bs;
                            valorfinal = bs = calcularPreco(resMin, valorAvulso_c, valorDiario_c, clienteInt);
                            txtRs.setText("R$: " + String.valueOf(bs));
                        }
                    });
                } else if (veiculo == 2) {
                    DocumentReference valoresgerente = db.collection("ValorEstacionamento")
                            .document("Moto");
                    valoresgerente.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            valorAvulso_c = value.getDouble("avulso");
                            valorDiario_c = value.getDouble("diario");
                            // Calcular preco
                            double bs;
                            valorfinal = bs = calcularPreco(resMin, valorAvulso_c, valorDiario_c, clienteInt);
                            txtRs.setText("R$: " + String.valueOf(bs));
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        cpCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResumo.dismiss();
                DocumentReference docRef = db.collection("Veiculo").document("DIAS")
                        .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario").document(id);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nome = documentSnapshot.getString("nome");
                        String placa = documentSnapshot.getString("placa");
                        String tel = documentSnapshot.getString("tel");
                        String vaga = documentSnapshot.getString("vaga");
                        String hora = documentSnapshot.getString("hora");
                        String hs = timeFinal;
                        // format
                        String[] formatAux = txtRs.getText().toString().split(" ");
                        double valors = Double.parseDouble(formatAux[1]);
                        //cliente format
                        Map<String, Object> dadosF = new HashMap<>();
                        dadosF.put("nome", nome);
                        dadosF.put("placa", placa);
                        dadosF.put("tel", tel);
                        dadosF.put("vaga", vaga);
                        dadosF.put("valor", valors);
                        dadosF.put("cliente", clienteFinal);
                        dadosF.put("veiculo", veiculoFinal);
                        dadosF.put("horaEntrada", hora);
                        dadosF.put("horaSaida", hs);
                        DocumentReference dadosFina = db.collection("Veiculo").document("DIAS")
                                .collection(dateCapter()).document("MOVIMENTACAO").collection("FINALIZADOS")
                                .document();
                        dadosFina.set(dadosF).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "CONCLUIDO", Toast.LENGTH_SHORT).show();
                                //DELAY
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        docRef.delete();
                                        loadQtrem();
                                        dialog.dismiss();
                                    }
                                },800);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                // AVULSO 2
                // DIARIO 1
                // MENSAL 3
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
                            }else{
                            }
                        }else{
                            System.out.println("falha ao recuperar por que : "+ task.getException());
                        }
                    }
                });
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
    public double calcularPreco(double min, double valorHORA, double valorDiario , int cliente ){
        //avulso 2 diario 1
        if(cliente == 2){
            double h = min/60;
            // 2.1 3 int
            double a = Math.ceil(h);
            double b =  a*valorHORA;
            return b;
        }else if(cliente == 1){
            return valorDiario;
        }else return 0;
    }
    public int calcularValor(String a, String b){
        int hor = Integer.parseInt(a);
        int min = Integer.parseInt(b);
        int res = (hor*60) + min;
        return res;
    }
    String month(){
        Calendar calendar = Calendar.getInstance();
        int b = calendar.get(Calendar.MONTH)+1;
        int a = calendar.get(Calendar.YEAR);
        String date = (Integer.toString(b))+(Integer.toString(a));
        return date ;
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
    public void loadQtrem(){
        DocumentReference odio = db.collection("Vagas").document(dateCapter());
        odio.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                double aux = documentSnapshot.getDouble("vagasqtd");
                int a = (int) aux;
                a--;
                odio.update("vagasqtd",a);
            }
        });
    }
}