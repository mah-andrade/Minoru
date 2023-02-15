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
import com.example.minoru.models.veiculosF2;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class adapterVeiculoFragment2 extends FirestoreRecyclerAdapter<veiculosF2, adapterVeiculoFragment2.vecHolder> {
    private OnItemClickListener listener;




    public adapterVeiculoFragment2(@NonNull FirestoreRecyclerOptions<veiculosF2> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull vecHolder holder, int position, @NonNull veiculosF2 model) {
        holder.text_nome.setText(model.getNome());
        holder.text_cliente.setText(model.getCliente());
        holder.text_placa.setText(model.getPlaca());
        holder.text_valorpago.setText(formata_valores_br(model.getValor()));

        if(model.getVeiculo().equals("MOTO")){
            holder.imgvec.setImageResource(R.drawable.ic_moto);
        }
        if(model.getCliente().equals("Avulso")){
            holder.text_tempodecorrido.setText(String.valueOf((int)(model.getTempoMinutos())+" min"));
        }else{
            holder.text_tempodecorrido.setVisibility(View.INVISIBLE);
            holder.txttempo.setVisibility(View.INVISIBLE);
        }
    }


    @NonNull
    @Override
    public vecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiculo_item2,
                parent,false);

        return new vecHolder(v);
    }




    class vecHolder extends RecyclerView.ViewHolder{
        TextView text_placa;
        TextView text_cliente;
        TextView text_nome;
        ImageView imgvec;
        TextView text_valorpago;
        TextView text_tempodecorrido,txttempo;

        public vecHolder(@NonNull View itemView) {
            super(itemView);
            text_placa = itemView.findViewById(R.id.text_placa);
            text_cliente = itemView.findViewById(R.id.text_horario);
            text_nome = itemView.findViewById(R.id.text_nome);
            text_valorpago = itemView.findViewById(R.id.text_valopagar);
            imgvec = itemView.findViewById(R.id.imageView_veiculo);
            text_tempodecorrido = itemView.findViewById(R.id.txttempodecorrido);
            txttempo = itemView.findViewById(R.id.txttempo);


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

    String formata_valores_br (double a){

        DecimalFormatSymbols dfs= new DecimalFormatSymbols(new Locale("pt","Brazil"));
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator('.');

        String padrao = "###,###.##";
        DecimalFormat df = new DecimalFormat(padrao,dfs);
        return df.format(a);
    }


}
