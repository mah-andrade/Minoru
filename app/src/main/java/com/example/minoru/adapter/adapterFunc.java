package com.example.minoru.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minoru.R;
import com.example.minoru.models.funcbd;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class adapterFunc extends FirestoreRecyclerAdapter<funcbd, adapterFunc.funcHolder> {
    private OnItemClickListener listener;


    public adapterFunc(@NonNull FirestoreRecyclerOptions<funcbd> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull funcHolder holder, int position, @NonNull funcbd model) {
        holder.text_nome.setText(model.getNome());
        holder.text_telefone.setText(model.getTel());
        holder.text_email.setText(model.getEmail());

    }

    @NonNull
    @Override
    public funcHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.funcionario_item_item,
                parent,false);

        return new funcHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();

    }


    class funcHolder extends RecyclerView.ViewHolder{
        TextView text_telefone,text_nome,text_email;


        public funcHolder(@NonNull View itemView) {
            super(itemView);

            text_nome = itemView.findViewById(R.id.text_nome);
            text_telefone = itemView.findViewById(R.id.text_telefone);
            text_email = itemView.findViewById(R.id.text_email);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position );
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}

