package com.example.practicafirebasechatjava.Vistas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.practicafirebasechatjava.Modelos.Mensaje;
import com.example.practicafirebasechatjava.Modelos.Usuario;
import com.example.practicafirebasechatjava.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registroActivity extends AppCompatActivity {

    private Button btregistroFinal;
    private EditText etEmailRegistro, etPassRegistro, etNombreRegistro;
    FirebaseAuth fAuth;
    DatabaseReference fDatabase;
    ArrayList<Usuario> listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        setTitle("Pagina de registro de usuarios.");
        listaUsuarios = new ArrayList<>();
        btregistroFinal = (Button) findViewById(R.id.btRegistroFinal);
        etEmailRegistro = (EditText) findViewById(R.id.etEmailRegistro);
        etPassRegistro = (EditText) findViewById(R.id.etPassRegistro);
        etNombreRegistro = (EditText) findViewById(R.id.etNombre);
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance().getReference();
        btregistroFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmailRegistro.getText().toString();
                String pass = etPassRegistro.getText().toString();
                String nombre = etNombreRegistro.getText().toString();

                if(!email.isEmpty() && !pass.isEmpty() && !nombre.isEmpty()){

                    if(pass.length() >= 6){
                        registrarUsuario(email, pass, nombre);
                    }else{
                        Toast.makeText(registroActivity.this, "Escriba una contraseña con mas de 5 caracteres por favor.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(registroActivity.this, "Porfavor rellene todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void registrarUsuario(String email, String pass, String nombre){
        fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String id = fAuth.getCurrentUser().getUid();
                    ArrayList<Mensaje> mensajesRecibidos = new ArrayList<Mensaje>();
                    mensajesRecibidos.add(new Mensaje(""));
                    ArrayList<Mensaje> mensajesEnviados = new ArrayList<>();
                    mensajesEnviados.add(new Mensaje(""));
                    Usuario nuevoRegistro = new Usuario(email,nombre,id);
                    nuevoRegistro.setMensajesEnviados(mensajesEnviados);
                    nuevoRegistro.setMensajesRecibidos(mensajesRecibidos);
                    Map<String, Object> map = new HashMap<>();
                    map.put("email",email);
                    map.put("nombre", nombre);
                    map.put("mensajesRecibidos",mensajesRecibidos);
                    map.put("mensajesEnviados", mensajesEnviados);



                    fDatabase.child("Usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {

                            if(task2.isSuccessful()){


                                listaUsuarios.add(nuevoRegistro);
                                Toast.makeText(registroActivity.this, "Usuario "+nombre+ " registrado con éxito.", Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                                Intent i = new Intent(registroActivity.this,loginActivity.class);
                                startActivity(i);

                            }else{
                                Toast.makeText(registroActivity.this, "Error al añadir los datos a la base de datos", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }else{
                    Toast.makeText(registroActivity.this, "Error al crear el usuario, intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}