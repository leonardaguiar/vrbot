package br.edu.ifba.modelos;

/**
 * Created by Léo on 19/10/2017.
 */
public class Configuracao {
    private int id;
    private String nome;
    private String endereco;
    private String porta_dados;
    private String porta_video;

    public Configuracao() {
    }

    public String getPorta_dados() {
        return porta_dados;
    }

    public void setPorta_dados(String porta_dados) {
        this.porta_dados = porta_dados;
    }

    public String getPorta_video() {
        return porta_video;
    }

    public void setPorta_video(String porta_video) {
        this.porta_video = porta_video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getPortaDados() {
        return porta_dados;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
