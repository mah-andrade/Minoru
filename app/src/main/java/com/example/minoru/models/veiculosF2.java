package com.example.minoru.models;

public class veiculosF2 {
    //NAO UTILIZAR
    private String nome,placa,cliente,veiculo;
    private double valor,tempoMinutos;

    public veiculosF2(){
        //CONSTRUCTOR


    }


    public veiculosF2(String nome , String placa ,String cliente, double valor, double tempoMinutos,String veiculo){
        this.nome = nome;
        this.placa = placa;
        this.cliente = cliente;
        this.valor = valor;
        this.tempoMinutos = tempoMinutos;
        this.veiculo = veiculo;
    }


    public String getNome() {
        return nome;
    }

    public String getPlaca() {
        return placa;
    }

    public String getCliente() {
        return cliente;
    }

    public double getValor() {
        return valor;
    }

    public double getTempoMinutos() {
        return tempoMinutos;
    }

    public String getVeiculo() {
        return veiculo;
    }
}
