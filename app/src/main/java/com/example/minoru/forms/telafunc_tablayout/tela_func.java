package com.example.minoru.forms.telafunc_tablayout;

import static java.lang.Boolean.FALSE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minoru.R;
import com.example.minoru.adapter.adapterTablayout;
import com.example.minoru.forms.tela_login;
import com.example.minoru.models.funcionarios;
import com.example.minoru.models.validacoes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.santalu.maskara.widget.MaskEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tela_func extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button addVec;
    private String valorRadio = "1";
    private ImageView imgajuda;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView bt_sair;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Map<Object, String> dadosMensalista = new HashMap<>();
    private String userEmail , userId,VencimentoFormatado,valorRadMensal,InicioContrato;
    private int qtdVagas = 0;
    private TextView  vagasGaragem,Textviefunc;
    private ArrayList<String> vagasOcupadas = new ArrayList<>();
    private double valorfinalMensalita;
    private Map<String, Object> dadosM = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tela_func);
        iniciar_component();
        loadQtd();

        imgajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(tela_func.this,R.style.CustomAlertDialog);
                dialog.setContentView(R.layout.dialog_ajudafunc);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Map<Object, Integer> dadosQtd = new HashMap<>();
                dadosQtd.put("vagasqtd",qtdVagas);
                DocumentReference odio = db.collection("Vagas").document(dateCapter());
                odio.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        qtdVagas = 0;
                        loadQtd();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(value.exists()){
                                    odio.update("vagasqtd",qtdVagas);
                                }else {
                                    odio.set(dadosQtd);
                                }
                            }
                        },2000);
                    }
                });
            }
        },2000);

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

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userEmail = user.getEmail();
        userId = user.getUid();
        DocumentReference Func = db.collection("Funcionarios").document(userId);

        Func.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Textviefunc.setText(documentSnapshot.getString("nome"));
            }
        });

        System.out.println("email user:"+userEmail);

        tabLayout.setupWithViewPager(viewPager);

        adapterTablayout adapterTab = new adapterTablayout(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapterTab.addFragment(new Fragment1(),"AVULSO");
        adapterTab.addFragment(new Fragment2(),"DIÁRIO");
        adapterTab.addFragment(new Fragment3(),"MENSAL");
        viewPager.setAdapter(adapterTab);


        addVec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openmenu();
            }
        });



        bt_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(tela_func.this, tela_login.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference odio = db.collection("Vagas").document(dateCapter());

        odio.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    System.out.println("NAO EXISTE");
                   return;
                }
                if(value !=null && value.exists()){
                    double aux = value.getDouble("vagasqtd");
                    int aux1 = (int)aux;
                    vagasGaragem.setText(aux1+"/45");
                }else{
                    System.out.println("Null");
                }
            }
        });
    }

    public void openmenu(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_menu);
        dialog.show();
        frontMenu(dialog);
    }

    private void frontMenu(Dialog dialog) {
        TextView txtDia = dialog.findViewById(R.id.txt_Dia);
        TextView txtMensal = dialog.findViewById(R.id.txt_Mensal);
        TextView txtAvuls = dialog.findViewById(R.id.txt_Avuls);
        Button btCancel = dialog.findViewById(R.id.bt_cancel);
        txtDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //abrindo como 1 Diário
                openDialog(1);
                dialog.dismiss();
                loadVagas();
            }
        });

        txtAvuls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //abrindo como 2 Avulso
                openDialog(2);
                dialog.dismiss();
                loadVagas();
            }
        });

        txtMensal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mensalista
                openMensal();
                dialog.dismiss();

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


    //DIALOG ADD CLIENTES , CARROS AVULSOS

    public void openDialog(int i){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_addcliente);
        dialog.show();
        frontDialog(dialog, i);
    }


    public void frontDialog(Dialog dialog, int i) {


        Button addCliente = dialog.findViewById(R.id.bt_add);
        Button cancel = dialog.findViewById(R.id.bt_add_cancel);
        EditText etNome = dialog.findViewById(R.id.dialog_nome);
        EditText etVaga = dialog.findViewById(R.id.dialog_VAGA);
        MaskEditText etTelefone = dialog.findViewById(R.id.dialog_telefone);
        MaskEditText edPlaca = dialog.findViewById(R.id.dialog_placa);
        TextView tvHora = dialog.findViewById(R.id.txt_horario);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioV);
        EditText edModelo = dialog.findViewById(R.id.dialog_modelovec);



        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        tvHora.setText(sdf.format(c.getTime()));


        valorRadio = "1";
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioCarro:
                        valorRadio = "1";
                        break;
                    case R.id.radioMoto:
                        valorRadio = "2";
                        break;
                }
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EscondeTeclado(view);
                int auxVaga =0;

                String nome = etNome.getText().toString().trim();
                String vaga = etVaga.getText().toString().trim();
                String tel = etTelefone.getUnMasked().trim();
                String placa = edPlaca.getMasked().trim();
                String time = tvHora.getText().toString().trim();
                String modelo = edModelo.getText().toString().trim();

                if(!vaga.isEmpty()){
                    auxVaga = Integer.parseInt(vaga);
                }

                if (nome.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo nome vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etNome.requestFocus();
                } else if (!isValidName(nome)) {
                    Snackbar snackbar = Snackbar.make(view, "Digite um Nome Valido", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etNome.requestFocus();
                } else if (tel.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo telefone vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etTelefone.requestFocus();
                } else if (tel.length() != 11) {
                    Snackbar snackbar = Snackbar.make(view, "Digite o numero de telefone completo!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etTelefone.requestFocus();
                }else if(modelo.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo Modelo vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    edModelo.requestFocus();
                }else if (placa.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo placa vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    edPlaca.requestFocus();
                } else if (placa.length() != 8) {
                    Snackbar snackbar = Snackbar.make(view, "Digite corretamente a placa!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    edPlaca.requestFocus();
                } else if (!isValidPlaca(placa)) {
                    Snackbar snackbar = Snackbar.make(view, "Digite a placa no PADRAO BRASILEIRO MERCO-SUL OU ANTIGO", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    edPlaca.requestFocus();

                }else if (vaga.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo vaga vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etVaga.requestFocus();
                }else if(!((auxVaga > 0) && (auxVaga <= 45))){
                    Snackbar snackbar = Snackbar.make(view, "Escolha uma vaga entre 1-45", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etVaga.requestFocus();
                }else if(!Vagaexist(auxVaga)){
                    Snackbar snackbar = Snackbar.make(view, "Vaga ja utilizada por outro veiculo", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    etVaga.requestFocus();
                } else {
                    Map<String, Object> dados = new HashMap<>();
                    dados.put("nome", nome);
                    dados.put("vaga", vaga);
                    dados.put("tel", etTelefone.getMasked());
                    dados.put("placa", edPlaca.getMasked());
                    dados.put("hora", time);
                    dados.put("cliente", i);
                    dados.put("veiculo", valorRadio);
                    dados.put("modelo",modelo);

                    DocumentReference refDad = null;

                    if (i == 2) {
                        refDad = db.collection("Veiculo").document("DIAS").collection(dateCapter()).document("MOVIMENTACAO")
                                .collection("Avulso").document();
                    } else if (i == 1) {
                        refDad = db.collection("Veiculo").document("DIAS").collection(dateCapter()).document("MOVIMENTACAO")
                                .collection("Diario").document();
                    }
                    DocumentReference MensalistaCad = db.collection("Mensal").document(month());
                    MensalistaCad.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                double aux = documentSnapshot.getDouble("totalClientes");
                                int a = (int)aux;
                                a =a +1;
                                MensalistaCad.update("totalClientes",a);
                            }else{}
                        }
                    });

                    refDad.set(dados).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("db", "Sucesso ao salvar os dados");
                            loadQtdadd();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("db_error", "Erro ao salvar os dados" + e.toString());
                        }
                    });
                    Toast.makeText(tela_func.this, "ADICIONADO COM SUCESSO", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void  openMensal(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_addclientemensal);
        dialog.show();
        frontDialogMensal(dialog);
    }

    public void frontDialogMensal(Dialog dialog){

        EditText nomeM = dialog.findViewById(R.id.dialog_nomeM);
        MaskEditText cpfM = dialog.findViewById(R.id.dialog_cpfM);
        MaskEditText telM = dialog.findViewById(R.id.dialog_telefoneM);
        TextView dateM = dialog.findViewById(R.id.edit_data_nasc);
        EditText endM = dialog.findViewById(R.id.dialog_endM);
        Button addM = dialog.findViewById(R.id.bt_addM);
        Button cancelM = dialog.findViewById(R.id.bt_add_cancelM);

        cancelM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //datepickerLogger
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EscondeTeclado(view);
                DatePickerDialog datePickerDialog = new DatePickerDialog(tela_func.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        if (day >= 10) {
                            if (month >= 10) {
                                String date = day + "/" + month + "/" + year;
                                dateM.setText(date);
                            } else {
                                String date = day + "/0" + month + "/" + year;
                                dateM.setText(date);
                            }
                        } else {
                            if (month >= 10) {
                                String date = "0" + day + "/" + month + "/" + year;
                                dateM.setText(date);
                            } else {
                                String date = "0" + day + "/0" + month + "/" + year;
                                dateM.setText(date);
                            }
                        }
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        addM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EscondeTeclado(view);
                String nome = nomeM.getText().toString().trim();
                String cpf = cpfM.getUnMasked().trim();
                String tel = telM.getUnMasked().trim();
                String date = dateM.getText().toString().trim();
                String end = endM.getText().toString().trim();
                char[] myChars = date.toCharArray();
                int resultadofinal = 0;
                validacoes valid = new validacoes();
                //Validar Campos
                if (nome.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo nome vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    nomeM.requestFocus();
                }else if(!isValidName(nome)){
                    Snackbar snackbar = Snackbar.make(view, "Digite um Nome Valido!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    nomeM.requestFocus();
                }else if(cpf.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo cpf vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    cpfM.requestFocus();
                }else if(!cpfM.isDone()){
                    Snackbar snackbar = Snackbar.make(view, "Preencha todo o campo CPF!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    cpfM.requestFocus();
                }else if(!valid.isCPF(cpf)){
                    Snackbar snackbar = Snackbar.make(view, "CPF INVALIDO!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    cpfM.requestFocus();
                }else if(tel.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo telefone vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    telM.requestFocus();
                }else if(!telM.isDone()){
                    Snackbar snackbar = Snackbar.make(view, "Preencha todo o campo telefone!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    telM.requestFocus();
                }else if(end.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo Endereço vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    endM.requestFocus();}
                else if(date.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo data de nascimento vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    dateM.requestFocus();
                }else if(date.length() == 10){
                    int x1 = myChars[6] - '0';
                    x1 = x1 * 1000;
                    int x2 = myChars[7] - '0';
                    x2 = x2 * 100;
                    int x3 = myChars[8] - '0';
                    x3 = x3 * 10;
                    int x4 = myChars[9] - '0';
                    x4 = x4 * 1;
                    int rsd = x1+x2+x3+x4;
                    resultadofinal = rsd;

                    if(resultadofinal > Calendar.getInstance().get(Calendar.YEAR)) {
                        Snackbar snackbar = Snackbar.make(view, "Ano Superior ", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }else if((calcularIdade(date)) < 18){
                        Snackbar snackbar = Snackbar.make(view, "Menor de idade", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }else{
                        dadosMensalista.put("nome",nome);
                        dadosMensalista.put("cpf",cpfM.getMasked());
                        dadosMensalista.put("tel",telM.getMasked());
                        dadosMensalista.put("dataNascimento",date);
                        dadosMensalista.put("endereco",end);

                        dadosM.put("nome", nome);

                        openDialogMensalvec();
                        dialog.dismiss();
                    }
                }
                else{
                    dadosMensalista.put("nome",nome);
                    dadosMensalista.put("cpf",cpfM.getMasked());
                    dadosMensalista.put("tel",telM.getMasked());
                    dadosMensalista.put("dataNascimento",date);
                    dadosMensalista.put("endereco",end);
                    dadosM.put("nome", nome);

                    openDialogMensalvec();
                    dialog.dismiss();

                }
            }
        });
    }

    public void openDialogMensalvec(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_addclientemensalveiculo);
        dialog.setCancelable(false);
        dialog.show();
        frontDialogVeiculoMensal(dialog);
    }

    public void frontDialogVeiculoMensal(Dialog dialog){
        loadVagas();
        MaskEditText placaF = dialog.findViewById(R.id.dialog_placaM);
        RadioGroup radio = dialog.findViewById(R.id.radioVM);
        Button buttonCp = dialog.findViewById(R.id.bt_add2);
        Button cancelF = dialog.findViewById(R.id.bt_add_cancel2);
        EditText EditVaga = dialog.findViewById(R.id.dialog_VAGA);
        EditText EditModelo = dialog.findViewById(R.id.dialog_modelovec);

        String[]   valorrad = {"1"};
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioCarro:
                        valorrad[0] = "1";
                        break;
                    case R.id.radioMoto:
                        valorrad[0] = "2";
                        break;
                }
            }
        });

        cancelF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonCp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EscondeTeclado(view);
                int auxVaga = 0;
                String placa = placaF.getUnMasked();
                String modelo = EditModelo.getText().toString();
                String vaga = EditVaga.getText().toString();

                if(!vaga.isEmpty()){
                    auxVaga = Integer.parseInt(vaga);
                }
                validacoes valid = new validacoes();

                if(modelo.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo modelo vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    EditModelo.requestFocus();
                }else if(placa.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo placa vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    placaF.requestFocus();
                }else if(!placaF.isDone()) {
                    Snackbar snackbar = Snackbar.make(view, "Complete o campo placa !", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    placaF.requestFocus();
                }else if(!valid.isValidPlaca(placaF.getMasked())){
                    Snackbar snackbar = Snackbar.make(view, "Digite a placa no PADRAO BRASILEIRO MERCO-SUL OU ANTIGO", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    placaF.requestFocus();
                }else if(vaga.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Campo vaga vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    EditVaga.requestFocus();
                }else if(!((auxVaga > 0) && (auxVaga <= 45))){
                    Snackbar snackbar = Snackbar.make(view, "Escolha uma vaga entre 1-45", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    EditVaga.requestFocus();
                }else if(!Vagaexist(auxVaga)){
                    Snackbar snackbar = Snackbar.make(view, "Vaga ja utilizada por outro veiculo", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    EditVaga.requestFocus();
                }else{
                    dadosMensalista.put("modelo",modelo);
                    dadosMensalista.put("placa",placaF.getMasked());
                    dadosMensalista.put("vaga",vaga);
                    dadosMensalista.put("veiculo",valorrad[0]);


                    dadosM.put("placa", placaF.getMasked());
                    dadosM.put("cliente", "Mensalista");
                    if(valorrad[0].equals("1")){
                        dadosM.put("veiculo","CARRO");
                    }else{
                        dadosM.put("veiculo","MOTO");
                    }
                    valorRadMensal = valorrad[0];
                    openEscolhaMensal();
                    dialog.dismiss();
                }
            }
        });
    }

    public void openEscolhaMensal(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_confirmacao);
        dialog.setCancelable(false);
        dialog.show();
        frontDialogswitchMENSAL(dialog);
    }

    private void frontDialogswitchMENSAL(Dialog dialog) {

        Button bt_nao = dialog.findViewById(R.id.bt_nao);
        Button bt_sim = dialog.findViewById(R.id.bt_sim);


        bt_nao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMensalPagamento();
                dialog.dismiss();
            }
        });

        bt_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMensalUser();
                dialog.dismiss();
            }
        });


    }


    public void openDialogMensalUser(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_addclientemensalusuario);
        dialog.setCancelable(false);
        dialog.show();
        frontDialogUser(dialog);

    }



    public void frontDialogUser(Dialog dialog){

        EditText Editemail = dialog.findViewById(R.id.editTextTextEmailAddress2);
        EditText Editsenha = dialog.findViewById(R.id.editTextTextPassword2);
        Button confir = dialog.findViewById(R.id.bt_add21);
        Button cancel = dialog.findViewById(R.id.bt_add_cancel21);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EscondeTeclado(view);
                //email e senha ;

                validacoes valid = new validacoes();
                String email = Editemail.getText().toString().trim();
                String senha = Editsenha.getText().toString().trim();

                if (email.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo email vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    Editemail.requestFocus();
                } else if (!valid.isValidEmail(email)) {
                    Snackbar snackbar = Snackbar.make(view, "Digite um email valido!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    Editemail.requestFocus();
                } else if (senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Campo senhna vazio!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    Editsenha.requestFocus();
                } else if (!valid.validSenhaRegex(senha)) {
                    Snackbar snackbar = Snackbar.make(view, "Senha tem que estar entre 7 a 14 caracterer, e nao caracterer especial", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    Editsenha.requestFocus();
                } else
                {
                dadosMensalista.put("email",email);

                FirebaseUser abc = mAuth.getCurrentUser();
                String iduserAtual = abc.getUid();
                System.out.println("Usuario Logado eh :" + iduserAtual);

                mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            user = mAuth.getCurrentUser();
                            String userID = user.getUid();
                            System.out.println("Usuario Logado eh :" + userID);
                            System.out.println("---------------------------------------");
                            System.out.println("user logado :" + user);

                            db.collection("Funcionarios").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String token = documentSnapshot.getString("token1");
                                    String Id = documentSnapshot.getString("Id");
                                    String FuncID = documentSnapshot.getString("FuncID");
                                    String cod = token + Id + FuncID;

                                    mAuth.signOut();
                                    mAuth.signInWithEmailAndPassword(userEmail, cod)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        System.out.println("Sucesso");

                                                    }
                                                }
                                            });
                                }
                            });
                        }else{

                            String erro = null;

                            try {
                                throw task.getException();

                            }catch (FirebaseAuthUserCollisionException e) {
                                erro = "Esse email ja se encontra cadastrado";
                            }catch (Exception e){
                                erro = "Erro ao cadastrar Mensalista";
                            }
                            Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                        }
                    }
                });
                    openDialogMensalPagamento();
                    dialog.dismiss();
            }
            }
        });

    }


    public void openDialogMensalPagamento(){
        Dialog dialog = new Dialog(this,R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.dialog_pagamento);
        dialog.setCancelable(false);
        dialog.show();
        frontDialogmensalPagamento(dialog);

    }

    private void frontDialogmensalPagamento(Dialog dialog) {

    TextView txtvalpag = dialog.findViewById(R.id.txtvalpag);
    Button bt_sim = dialog.findViewById(R.id.bt_sim);
    Button bt_nao = dialog.findViewById(R.id.bt_nao);
    TextView id_vencimento = dialog.findViewById(R.id.id_vencimento);

    String carro = "1";
    if (carro.equals(valorRadMensal)){
        db.collection("ValorEstacionamento").document("Carro").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                valorfinalMensalita = value.getDouble("mensal");
                txtvalpag.setText(String.valueOf(valorfinalMensalita));
            }
        });
    }else{
        db.collection("ValorEstacionamento").document("Moto").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                valorfinalMensalita = value.getDouble("mensal");
                txtvalpag.setText(String.valueOf(valorfinalMensalita));
            }
        });
    }

        // somente api android 8 acima
        LocalDate localDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            InicioContrato = localDate.format(formatter);
            localDate = localDate.plusDays(30);

            VencimentoFormatado = localDate.format(formatter);

        }

        id_vencimento.setText(VencimentoFormatado);

        bt_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EscondeTeclado(view);
                dadosMensalista.put("InicioContrato",InicioContrato);
                dadosMensalista.put("VencimentoContrato",VencimentoFormatado);
                dadosMensalista.put("contrato","ATIVO");

                db.collection("Mensalistas").document().set(dadosMensalista).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            dadosM.put("valor", valorfinalMensalita);

                            DocumentReference dadosFina = db.collection("Veiculo").document("DIAS")
                                    .collection(dateCapter()).document("MOVIMENTACAO").collection("FINALIZADOS")
                                    .document();

                            dadosFina.set(dadosM);

                            DocumentReference MensalistaCad = db.collection("Mensal").document(month());
                            MensalistaCad.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        double auxR = documentSnapshot.getDouble("valorRendimento");
                                        double aux = documentSnapshot.getDouble("mensalistas");
                                        int a = (int)aux;
                                        a =a +1;


                                        auxR = auxR + valorfinalMensalita;
                                        MensalistaCad.update("mensalistas",a);
                                        MensalistaCad.update("valorRendimento",auxR);
                                    }else{}
                                }
                            });
                           loadQtdadd();
                            Toast.makeText(tela_func.this, "Mensalista Adicionado Com Sucesso", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        bt_nao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public  String dateCapter() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int month = calendar.get(calendar.MONTH) + 1;
        int year = calendar.get(calendar.YEAR);
        String date;

        if (day >= 10) {

            if (month >= 10) {
                date = day + "." + month + "." + year;
            }else {
                date = day + ".0" + month + "." + year;
            }
            return date;
        } else {
            if (month >= 10) {
                date = "0" + day + "." + month + "." + year;
            } else {
                date = "0" + day + ".0" + month + "." + year;
            }
            return date;
        }
    }

    public void EscondeTeclado(View v){

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

    }


    public void loadQtdadd() {
        qtdVagas++;
        DocumentReference odio = db.collection("Vagas").document(dateCapter());
        odio.update("vagasqtd",qtdVagas);
    }

    public void loadVagas(){
        vagasOcupadas.clear();
        CollectionReference mensa = db.collection("Mensalistas");
        CollectionReference avulso = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Avulso");
        CollectionReference diario = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario");

        mensa.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    vagasOcupadas.add(func.getVaga());
                }
            }
        });
        avulso.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    vagasOcupadas.add(func.getVaga());
                }
            }
        });
        diario.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    vagasOcupadas.add(func.getVaga());
                }
            }
        });
    }

    public boolean Vagaexist(int numD){
         boolean result = true;
        String num = Integer.toString(numD);

        for(int i = 0; i < vagasOcupadas.size(); i++){
            if(vagasOcupadas.get(i).equals(num)){
                System.out.println("Existe");
                result = false;
                i = vagasOcupadas.size()+1;
            }else{
                System.out.println("nao existe");
            }
        }
        return result;
    }

    public void loadQtd(){

        CollectionReference mensa = db.collection("Mensalistas");

        mensa.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    qtdVagas++;
                }
            }
        });

        CollectionReference avulso = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Avulso");

        avulso.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    qtdVagas++;
                }
            }
        });

        CollectionReference diario = db.collection("Veiculo").document("DIAS")
                .collection(dateCapter()).document("MOVIMENTACAO").collection("Diario");

        diario.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    funcionarios func = documentSnapshot.toObject(funcionarios.class);
                    func.setDocumentId(documentSnapshot.getId());
                    qtdVagas++;
                }
            }
        });
    }

    public static boolean isValidName(String name){

        String regex = "^(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+(?:\\-(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+)*(?: (?:(?:e|y|de(?:(?: la| las| lo| los))?|do|dos|da|das|del|van|von|bin|le) )?(?:(?:(?:d'|D'|O'|Mc|Mac|al\\-))?(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+|(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+(?:\\-(?:[\\p{Lu}&&[\\p{IsLatin}]])(?:(?:')?(?:[\\p{Ll}&&[\\p{IsLatin}]]))+)*))+(?: (?:Jr\\.|II|III|IV))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean isValidPlaca (String placa){
        String regex = "[a-zA-Z][a-zA-Z][a-zA-Z][-][0-9]\\w[0-9][0-9]";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(placa);
        return matcher.matches();
    }

    String month(){
        Calendar calendar = Calendar.getInstance();
        int b = calendar.get(Calendar.MONTH)+1;
        int a = calendar.get(Calendar.YEAR);
        String date = (Integer.toString(b))+(Integer.toString(a));
        return date ;
    }

    public int calcularIdade(String date1){

        // Pegando a data de hoje
        GregorianCalendar calendario = new GregorianCalendar();
        int dia = calendario.get(calendario.DAY_OF_MONTH);
        int mes = calendario.get(calendario.MONTH)+1;
        int ano = calendario.get(calendario.YEAR);

        //formatando a data recebida
        int diaU = Integer.valueOf(date1.substring(0,2));
        int mesU = Integer.valueOf(date1.substring(3,5));
        int anoU = Integer.valueOf(date1.substring(6,10));

        int idade;

        if(mesU < mes || (mesU==mes && diaU <=dia)){
            idade = ano-anoU;
        }
        else{
            idade = (ano- anoU)-1;
        }
        return idade;
    }

    void iniciar_component(){
        addVec = findViewById(R.id.button_add);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        imgajuda = findViewById(R.id.img_ajuda);
        bt_sair = findViewById(R.id.sair);
        vagasGaragem = findViewById(R.id.n_vagas);
        Textviefunc = findViewById(R.id.func_id);
    }
}