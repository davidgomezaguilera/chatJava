package com.example.practicafirebasechatjava.Modelos;

import java.util.ArrayList;

public class Usuario {

    private String email, nombre, uid;
    private ArrayList<Mensaje> mensajesRecibidos, mensajesEnviados;

    public Usuario() {
    }

    public Usuario(String email, String nombre, String uid) {
        this.email = email;
        this.nombre = nombre;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(ArrayList<Mensaje> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public ArrayList<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(ArrayList<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

}
