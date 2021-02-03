package com.example.practicafirebasechatjava.Controladores;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicafirebasechatjava.Modelos.Mensaje;
import com.example.practicafirebasechatjava.R;
import java.util.ArrayList;

public class adapterRecyclerRecibidos extends RecyclerView.Adapter<adapterRecyclerRecibidos.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvKeyEmisor, tvMensaje, tvTexto1, tvTexto2;


        ViewHolder(View itemView){
            super(itemView);
            tvKeyEmisor = itemView.findViewById(R.id.tvKeyEmisor);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvTexto1 = itemView.findViewById(R.id.tvTexto1);
            tvTexto2 = itemView.findViewById(R.id.tvTexto2);
        }

        public void asignarDatos(Mensaje mensaje, String key){

            tvKeyEmisor.setText(key);
            tvMensaje.setText(mensaje.getMensaje());

        }

    }
    private ArrayList<Mensaje> listaMensajes;
    private ArrayList<String> keysEmisor;
    private Context contexto;

    public adapterRecyclerRecibidos(Context contexto, ArrayList<Mensaje> listaMensajes, ArrayList<String> keysEmisor){
        this.contexto = contexto;
        this.listaMensajes = listaMensajes;
        this.keysEmisor = keysEmisor;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensajes_recibidos, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull adapterRecyclerRecibidos.ViewHolder holder, int position) {
        holder.asignarDatos(listaMensajes.get(position), keysEmisor.get(position));

    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
