package com.example.practicafirebasechatjava.Vistas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practicafirebasechatjava.MainActivity;
import com.example.practicafirebasechatjava.Modelos.Mensaje;
import com.example.practicafirebasechatjava.Modelos.Usuario;
import com.example.practicafirebasechatjava.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity {

    private Button btGoogle, btInicio, btRegistro;
    private EditText etEmail, etPass;
    private FirebaseAuth auth;
    private FirebaseUser usuario;
    private DatabaseReference database;
    private String email, pass;
    private GoogleSignInClient googleSignIn;
    private int SIGN_IN_GOOGLE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Inicio de Sesión");
        btGoogle = (Button) findViewById(R.id.btGoogle);
        btInicio = (Button) findViewById(R.id.btLogin);
        btRegistro = (Button) findViewById(R.id.btRegistrar);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPassword);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mostrarRegistro = new Intent(loginActivity.this,registroActivity.class);
                startActivity(mostrarRegistro);
            }
        });

        btInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                if(!email.isEmpty() && !pass.isEmpty()){

                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                usuario = auth.getCurrentUser();

                                Toast.makeText(loginActivity.this, "Inicio de sesion correctamente, bienvenido "+ usuario.getEmail(), Toast.LENGTH_SHORT).show();

                                Map<String, Object> map = new HashMap<>();
                                map.put("email", usuario.getEmail());

                                database.child("Usuarios_online").child(usuario.getUid()).setValue(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(loginActivity.this, MainActivity.class));
                                    }
                                });


                            }else{
                                Toast.makeText(loginActivity.this, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }else{
                    Toast.makeText(loginActivity.this, "Rellene todos los campos por favor.", Toast.LENGTH_SHORT).show();
                }

            }
        });




        btGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build();

                googleSignIn = GoogleSignIn.getClient(loginActivity.this,googleOptions);
                Intent signInIntent = googleSignIn.getSignInIntent();// aqui me daba un error porque el googleSignIn era nulo, tenia que crear la linea anterior.
                googleSignIn.signOut();
                startActivityForResult(signInIntent, SIGN_IN_GOOGLE);

            }
        });

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            usuario = auth.getCurrentUser();

                            String email = usuario.getEmail();
                            String nombre = usuario.getDisplayName();
                            ArrayList<Mensaje> mensajesRecibidos = new ArrayList<>();
                            ArrayList<Mensaje> mensajesEnviados = new ArrayList<>();
                            mensajesRecibidos.add(new Mensaje(""));
                            mensajesEnviados.add(new Mensaje(""));
                            String key = usuario.getUid();

                            Usuario registroGoogle = new Usuario(email,nombre,key);
                            registroGoogle.setMensajesRecibidos(mensajesRecibidos);
                            registroGoogle.setMensajesEnviados(mensajesEnviados);

                            Map<String, Object> map = new HashMap<>();
                            map.put("email",email);
                            map.put("nombre", nombre);
                            map.put("mensajesRecibidos",mensajesRecibidos);
                            map.put("mensajesEnviados", mensajesEnviados);

                            database.child("Usuarios").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {
                                    if(task2.isSuccessful()){

                                        database.child("Usuarios_online").child(key).setValue(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task3) {

                                                if(task3.isSuccessful()){
                                                    Toast.makeText(loginActivity.this, "Sesion iniciada correctamente, Bienvenido "+ nombre, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });


                                    }
                                }
                            });

                            startActivity(new Intent(loginActivity.this,MainActivity.class));
                        } else {

                        }

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

            }
        }
    }
}