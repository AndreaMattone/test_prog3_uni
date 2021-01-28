package sample;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.utils.Email;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataModel {

    /**########################## MAIL RICEVUTE 1 ###########################################*/
    /**Observable email list ricevute
     * */
    List<Email> emlrc = new ArrayList<Email>();
    ObservableList<Email> emailListRicevute = FXCollections.observableArrayList(emlrc);
    /*public final ObservableList<Email> emailList = FXCollections.observableArrayList(email -> {
        return new Observable[]{};
    });*/

    public void setEmailListRicevute(ObservableList<Email> emailListRicevute) {
        this.emailListRicevute = emailListRicevute;
    }
    public ObservableList<Email> getEmailListRicevute(){
        return emailListRicevute;
    }

    /**Current email ricevute
     * La current email ricevute è una property così posso osservarla e gestire i cambiamenti*/
    public ObjectProperty<Email> currentEmailRcv = new SimpleObjectProperty<>(null);
    public ObjectProperty<Email> currentEmailRcvProperty(){
        return currentEmailRcv;
    }
    public Email getCurrentEmailRcv(){
        return currentEmailRcvProperty().get();
    }
    public void setCurrentEmailRcv(Email email){
        currentEmailRcvProperty().set(email);
    }





    /**########################## MAIL INVIATE  2  ################################################*/
    /**Observable email list inviate
     * */
    List<Email> emlin = new ArrayList<Email>();
    ObservableList<Email> emailListInviate = FXCollections.observableArrayList(emlin);

    public void setEmailListInviate(ObservableList<Email> emailListInviate) {
        this.emailListInviate = emailListInviate;
    }
    public ObservableList<Email> getEmailListInviate(){
        return emailListInviate;
    }

    /**Current email inviate
     * La current email ricevute è una property così posso osservarla e gestire i cambiamenti*/
    public ObjectProperty<Email> currentEmailInv = new SimpleObjectProperty<>(null);
    public ObjectProperty<Email> currentEmailInvProperty(){
        return currentEmailInv;
    }
    public Email getCurrentEmailInv(){
        return currentEmailInvProperty().get();
    }
    public void setCurrentEmailInv(Email email){
        currentEmailInvProperty().set(email);
    }




    /**########################## MAIL CESTINO  3 ################################################*/
    /**Observable email list cestino
     * */
    List<Email> emlce = new ArrayList<Email>();
    ObservableList<Email> emailListCestino = FXCollections.observableArrayList(emlce);

    public void setEmailListCestino(ObservableList<Email> emailListCestino) {
        this.emailListCestino = emailListCestino;
    }
    public ObservableList<Email> getEmailListCestino(){
        return emailListCestino;
    }

    /**Current email cestino
     * La current email cestino è una property così posso osservarla e gestire i cambiamenti*/
    public ObjectProperty<Email> currentEmailCes = new SimpleObjectProperty<>(null);
    public ObjectProperty<Email> currentEmailCesProperty(){
        return currentEmailCes;
    }
    public Email getCurrentEmailCes(){
        return currentEmailCesProperty().get();
    }
    public void setCurrentEmailCes(Email email){
        currentEmailCesProperty().set(email);
    }





    /**################################# UTENTE ########################################*/
    /**
     * Utenti registrati, tengo l'elenco qua in quanto non è richiesto di gestire la
     * registrazione e ho degli utenti prefissati
     * */
    List<String> usr = new ArrayList<>();
    ObservableList<String> userList = FXCollections.observableArrayList(usr);
    public ObservableList<String> getUserList(){ return userList;}

    /**Current utente
     * */
    public ObjectProperty<String> currentUtente = new SimpleObjectProperty<>(null);
    public ObjectProperty<String> currentUtenteProperty(){
        return currentUtente;
    }
    public String getCurrentUtente(){
        return currentUtenteProperty().get();
    }
    public void setCurrentUtente(String utente){
        currentUtenteProperty().set(utente);
    }



    public void loadUtenti() {
        try {
            Scanner sc = new Scanner(new FileReader("src/sample/files/login.txt"));
            while(sc.hasNext()){
                userList.add(sc.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


}
