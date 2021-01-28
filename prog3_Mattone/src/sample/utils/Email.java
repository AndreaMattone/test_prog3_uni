package sample.utils;

import java.io.Serializable;

public class Email extends Object implements Serializable {

    private int id;
    private String mittente;
    private String destinatario;
    private String argomento;
    private String testo;
    private int stato;/*Inviata=1 Ricevuta=2 Cestino=3 Eliminato=4*/


    public Email(){ }
    public Email(int id, String mittente, String destinatario, String argomento, String testo,int stato){
        this.id=id;
        this.mittente=mittente;
        this.destinatario=destinatario;
        this.argomento=argomento;
        this.testo=testo;
        this.stato=stato;
    }

    public int getId(){
        return this.id;
    }
    public String getMittente(){
        return this.mittente;
    }
    public String getDestinatario() {
        return this.destinatario;
    }
    public String getArgomento(){ return this.argomento;}
    public String getTesto(){ return this.testo;}
    public int getStato() {
        return stato;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    public void setArgomento(String argomento) {
        this.argomento = argomento;
    }
    public void setTesto(String testo) {
        this.testo = testo;
    }
    public void setStato(int stato) {
        this.stato = stato;
    }

    @Override
    public String toString(){
        return "" + getId() + ";" + getMittente() + ";" + getDestinatario() + ";" + getArgomento() + ";" + getTesto()+";"+getStato();
    }

}


