package com.example.minoru.adapter;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class adapterToken {


        public  void tk(String se,String ID){

            //NAO 6 > <= 14

            String seToken;

            int valor[] = new int[2];
            valor = token(se);
            String token[] = new String[3];

            if(valor[1] == 0){
                //3

                token[0] = se.substring(0,valor[0]);
                token[1] = se.substring(valor[0], valor[0]+valor[0]);
                token[2] = se.substring(valor[0]+valor[0], valor[0]+valor[0]+valor[0]);

                seToken =token[0]+token[1]+token[2];
                System.out.println(seToken);
                System.out.println(se);
            }else{

                token[0] = se.substring(0,valor[0]);
                token[1] = se.substring(valor[0], valor[0]+valor[0]);

                if(valor[1] == 1){
                    token[2] = se.substring(valor[0]+valor[0], valor[0]+valor[0]+1);
                }
                if(valor[1] == 2){
                    token[2] = se.substring(valor[0]+valor[0], valor[0]+valor[0]+2);
                }
                if(valor[1] == 3){
                    token[2] = se.substring(valor[0]+valor[0], valor[0]+valor[0]+3);
                }
                seToken =token[0]+token[1]+token[2];
                System.out.println(seToken);
                System.out.println(se);
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference func = db.collection("Funcionarios").document(ID);

            Map<String,Object>privacidade = new HashMap<>();
            privacidade.put("token1",token[0]);
            privacidade.put("Id",token[1]);
            privacidade.put("FuncID",token[2]);



            func.update(privacidade).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                    }
                }
            });
        }



        public  int[] token(String token){

            int b[] = new int[2];
            int tam = token.length();
            int string[] = new int[3];


            if((tam !=6)&&(tam !=9)&&(tam !=12)&&(tam !=10)&&(tam !=11)){
                int aux = tam/3;
                int totalmut = aux*3;
                string[0] = totalmut/2;
                string[1] = tam -totalmut;

                return string;

            }else if(tam == 10){
                int aux = tam/3;
                int totalmut = aux*3;
                string[0] = totalmut/2;
                string[1] = (tam+1) -totalmut;
                return string;
            }else if(tam == 11){
                int aux = tam/3;
                int totalmut = aux*3;
                string[0] = totalmut/2;
                string[1] = (tam+1) -totalmut;
                return string;
            }else{
                b[0] = tam/3;
                return b;
            }
        }
    }

