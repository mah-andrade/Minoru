package com.example.minoru.adapter;

import android.media.Image;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class adapterVeiculo extends FirestoreRecyclerAdapter<veiculos, adapterVeiculo.vecHolder> {
    private OnItemClickListener listener;
    private int aux;
    public adapterVeiculo(@NonNull FirestoreRecyclerOptions<veiculos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull vecHolder holder, int position, @NonNull veiculos model) {
        holder.text_nome.setText(model.getNome());
        holder.text_hora.setText(model.getHora());
        holder.text_placa.setText(model.getPlaca());
        holder.text_vaga.setText(model.getVaga());


        String a = "2";

        if(a.equals(model.getVeiculo())){
            holder.imgvec.setImageResource(R.drawable.ic_moto);

        }else{

        }
    }

    @NonNull
    @Override
    public vecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiculo_item,
                parent,false);

        return new vecHolder(v);
    }




    class vecHolder extends RecyclerView.ViewHolder{
        TextView text_placa;
        TextView text_hora;
        TextView text_nome;
        TextView text_vaga;
        ImageView imgvec;

        public vecHolder(@NonNull View itemView) {
            super(itemView);
            text_placa = itemView.findViewById(R.id.text_placa);
            text_hora = itemView.findViewById(R.id.text_horario);
            text_nome = itemView.findViewById(R.id.text_nome);
            text_vaga = itemView.findViewById(R.id.text_vaga);
            imgvec = itemView.findViewById(R.id.imageView_veiculo);


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
