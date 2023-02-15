package com.example.minoru.models;

public class veiculos {

    private String hora,vaga,nome,placa, veiculo ,VencimentoContrato,InicioContrato;


    public veiculos(){
        //CONSTRUCTOR


    }


    public veiculos(String hora, String vaga, String nome , String placa , String veiculo, String VencimentoContrato,String InicioContrato){
        this.hora = hora;
        this.vaga = vaga;
        this.nome = nome;
        this.placa = placa;
        this.veiculo = veiculo;
        this.VencimentoContrato = VencimentoContrato;
        this.InicioContrato = InicioContrato;

    }


    public String getHora() {
        return hora;
    }

    public String getVaga() {
        return vaga;
    }

    public String getNome() {
        return nome;
   }

    public String getPlaca() {
        return placa;
    }

    public String getVeiculo(){
        return veiculo;
    }

    public String getVencimentoContrato() {
        return VencimentoContrato;
    }

    public String getInicioContrato() {
        return InicioContrato;
    }
}
