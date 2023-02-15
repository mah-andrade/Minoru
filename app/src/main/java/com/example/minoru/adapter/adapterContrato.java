package com.example.minoru.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minoru.R;
import com.example.minoru.models.veiculos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class adapterContrato extends FirestoreRecyclerAdapter<veiculos, adapterContrato.contratoHolder> {
    private OnItemClickListener listener;

    public adapterContrato(@NonNull FirestoreRecyclerOptions<veiculos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull contratoHolder holder, int position, @NonNull veiculos model) {
        holder.text_nome.setText(model.getNome());
        holder.text_datain.setText(model.getInicioContrato());
        holder.text_datav.setText(model.getVencimentoContrato());

    }

    @NonNull
    @Override
    public contratoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contrato_item,
                parent,false);

        return new contratoHolder(v);
    }




    class contratoHolder extends RecyclerView.ViewHolder{
        TextView text_datav;
        TextView text_datain;
        TextView text_nome;


        public contratoHolder(@NonNull View itemView) {
            super(itemView);
            text_datav = itemView.findViewById(R.id.text_dtfim);
            text_datain = itemView.findViewById(R.id.text_telefone);
            text_nome = itemView.findViewById(R.id.text_nome);

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


