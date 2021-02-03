package com.example.practicafirebasechatjava.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.practicafirebasechatjava.Controladores.adapterRecyclerRecibidos;
import com.example.practicafirebasechatjava.Modelos.Mensaje;
import com.example.practicafirebasechatjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MensajesRecibidos extends AppCompatActivity {

    RecyclerView recyclerRecibidos;
    DatabaseReference ref;
    FirebaseUser usuario;
    FirebaseAuth auth;
    ArrayList<Mensaje> listaMensajes;
    ArrayList<String> keyEmisor;
    adapterRecyclerRecibidos adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes_recibidos);
        auth = FirebaseAuth.getInstance();
        usuario = auth.getCurrentUser();
        recyclerRecibidos = (RecyclerView) findViewById(R.id.recyclerMensajesRecibidos);

        LinearLayoutManager lym = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerRecibidos.setLayoutManager(lym);

        listaMensajes = new ArrayList<>();
        keyEmisor = new ArrayList<>();



        ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario.getUid()).child("mensajesRecibidos");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes.add(new Mensaje("hola"));
                for(DataSnapshot key : snapshot.getChildren()){

                    if(key.getKey().equals("Mensaje")){
                        //listaMensajes.add(new Mensaje(key.getValue().toString()));
                        listaMensajes.add(new Mensaje("hola"));
                    }else{
                        keyEmisor.add(key.getValue().toString());
                    }

                }
                adapter = new adapterRecyclerRecibidos(MensajesRecibidos.this,listaMensajes,keyEmisor);
                recyclerRecibidos.setAdapter(adapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        ref.addValueEventListener(eventListener);
        System.out.println("HOla");




    }
}