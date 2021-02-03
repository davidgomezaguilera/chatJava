package com.example.practicafirebasechatjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.practicafirebasechatjava.Controladores.adapterRecyclerRecibidos;
import com.example.practicafirebasechatjava.Modelos.Mensaje;
import com.example.practicafirebasechatjava.Vistas.MensajesRecibidos;
import com.example.practicafirebasechatjava.Vistas.loginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseUser usuario;
    DatabaseReference ref,ref2;
    private Button btCerrarSesion, btEnviarMensaje, btMensajesRecibidos, btMensajesEnviados;
    private EditText etMensaje;
    private Spinner usuarios;
    private String keyUsuarioSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        usuario = fAuth.getCurrentUser();
        btCerrarSesion = (Button) findViewById(R.id.btCerrarSesion);
        btEnviarMensaje = (Button) findViewById(R.id.btEnviarMensaje);
        btMensajesEnviados = (Button) findViewById(R.id.btMensajesEnviados);
        btMensajesRecibidos = (Button) findViewById(R.id.btMensajesRecibidos);
        etMensaje = (EditText) findViewById(R.id.etMensaje);
        usuarios = (Spinner) findViewById(R.id.spinner);
        setTitle("Página principial.");

        //recargamos el spinner con los usuarios que se encuentran online.
        recargarSpinner();//no se realmente si poner esto fuera del onClick o dentro
        obtenerKeySpinnerSeleccionado();
        System.out.println("Key usuario seleccionado dentro del boton de enviar "+keyUsuarioSpinner);
        btEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //aqui tenemos que controlar que, cuando le demos al boton, cojamos el texto que se encuentra
                //en etMensaje y creemos un objecto Mensaje con él.
                //una vez tenemos el Mensaje y el usuario que lo ha enviado, tenemos que encontrar el usuario al que se lo queremos enviar
                //ya con el Mensaje, el emisor y el receptor, tenemos que buscar en la base de datos Firebase la key del usuario que esta conectado
                //es decir, el emisor, entrar dentro de sus mensajes enviados y añadirle el Mensaje.
                //a su vez tambien tenemos que entrar dentro del usuario al que queremos enviar el mensajes es decir, el receptor
                //entrar dentro de sus mensajesRecibidos y añadirle el Mensaje

                if(!etMensaje.getText().toString().isEmpty()){
                    Mensaje mensajeNuevo = new Mensaje(etMensaje.getText().toString());
                    System.out.println(keyUsuarioSpinner);


                    ref = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(keyUsuarioSpinner).child("mensajesRecibidos");
                    String mensajeAñadido = ref.push().getKey();
                    Map<String, Object> map = new HashMap<>();
                    map.put("Emisor",usuario.getUid());
                    map.put("Mensaje",mensajeNuevo.getMensaje());
                    ref.child(mensajeAñadido).setValue(map);

                    ref2 = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(usuario.getUid()).child("mensajesEnviados");
                    String mensajeEnviadoAñadido = ref2.push().getKey();
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("Receptor",keyUsuarioSpinner);
                    map2.put("Mensaje",mensajeNuevo.getMensaje());
                    ref2.child(mensajeEnviadoAñadido).setValue(map2);

                    Toast.makeText(MainActivity.this, "Mensaje enviado correctamente." , Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(MainActivity.this, "Por favor, escriba algo en el mensaje.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btMensajesRecibidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, MensajesRecibidos.class);
                startActivity(i);
            }
        });



        btCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fAuth.signOut();

                String key = usuario.getUid();
                ref = FirebaseDatabase.getInstance().getReference("Usuarios_online");
                ref.child(key).setValue(null);
                startActivity(new Intent(MainActivity.this,loginActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fAuth.signOut();
    }

    private void recargarSpinner(){
//aqui debemos de decidir si queremos que salgan todos los usuarios que se han regsitrado en la app o solo los que se encuentran online.
        //imagino que ya que he creado el nodo de Usuarios_online mostraremos los usuario sque se encuentren online.

        ArrayList<String> emailUsuarios = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emailUsuarios.clear();
                for(DataSnapshot key : snapshot.getChildren()){
                    System.out.println("Dentro del for");
                    System.out.println("key del for onDataChange "+ key.getKey());

                    if(usuario.getUid().equals(key.getKey())){

                    }else{
                        //he intentado añadir al Spinner el email del usuario para que se vea mas claro, pero luego a la hora de buscar
                        //dicho usuario me complica mucho las cosas, entonces guardare mejor la key del usuario y yasta, aunque quede mas feo, no tengo mucho tiempo
                        //emailUsuarios.add(""+key.getValue());
                        emailUsuarios.add(key.getKey());
                    }
                }
                usuarios.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.support_simple_spinner_dropdown_item,emailUsuarios));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ref = FirebaseDatabase.getInstance().getReference().child("Usuarios_online");
        ref.addValueEventListener(eventListener);

    }
//esta funcion devolverá el email del usuario al que queremos enviarle un mensaje.
    public void obtenerKeySpinnerSeleccionado(){
        usuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//aqui se supone que guardamos el email que hemos seleccionado, es decir, al usuario al que queremos enviarle el email.
                keyUsuarioSpinner = (String) usuarios.getSelectedItem();
                System.out.println("LA KEY SELECCIONADA DEL SPINNER ES: "+keyUsuarioSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Eliga una opción por favor.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}