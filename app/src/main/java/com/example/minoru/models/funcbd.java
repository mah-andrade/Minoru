package com.example.minoru.models;

public class funcbd {

    private String email,tel,nome;

    public funcbd(){

        //construtor
    }


    public funcbd ( String email, String tel , String nome){
        this.email = email;
        this.tel = tel;
        this.nome = nome;

    }

    public String getEmail() {
        return email;
    }

    public String getTel() {
        return tel;
    }

    public String getNome() {
        return nome;
    }
}
