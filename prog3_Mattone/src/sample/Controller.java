package sample;
import fxmlCreators.PostaInArrivo;
import fxmlCreators.PostaInviata;
import fxmlCreators.Cestino;
import fxmlCreators.ScriviMail;
import fxmlCreators.Login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import sample.utils.Email;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    private DataModel model;
    private Controller controller;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @FXML
    private SplitPane mySplitPane;
    @FXML
    private AnchorPane myAnchorPane1;
    @FXML
    private AnchorPane myAnchorPane2;
    @FXML
    private BorderPane myBorderPane;
    @FXML
    private Button btn_Login;
    @FXML
    private Button btn_Ricevute;
    @FXML
    private Button btn_Inviate;
    @FXML
    private Button btn_Cestino;
    @FXML
    private Button btn_Scrivi;


    /*Utilizzo questa funzione per ottenere gli items dei "sotto-pannelli"
    NON FUNZIONA*/
    /*public <T> T getElem(String name, Class<T> type){
        return (T)myBorderPane.lookup("#" + name);
    }*/


    /**#################################################*/
    /**Handler bottoni schermata principale a sinistra*/
    @FXML
    public void handlerBtnLogin(final ActionEvent e){
        //System.out.println("[Client] Login button premuto");
        Login myLogin = new Login();
        myBorderPane.setCenter(myLogin);
        scegliLogin(myLogin);
    }

    @FXML
    public void handlerBtnRicevute(final ActionEvent e){
        if(model.getCurrentUtente()!=null){
            //System.out.println("[Client] Mail ricevute button premuto");
            PostaInArrivo myPostaInArrivo = new PostaInArrivo();
            myBorderPane.setCenter(myPostaInArrivo);
            mailInArrivo(myPostaInArrivo);
        }
    }

    @FXML
    public void handlerBtnInviate(final ActionEvent e){
        if(model.getCurrentUtente()!=null) {
            //System.out.println("[Client] Mail inviate button premuto");
            PostaInviata myPostaInviata = new PostaInviata();
            myBorderPane.setCenter(myPostaInviata);
            mailInviate(myPostaInviata);
        }
    }

    @FXML
    public void handlerBtnCestino(final ActionEvent e){
        if(model.getCurrentUtente()!=null){
            //System.out.println("[Client] Mail cestino button premuto");
            Cestino myCestino = new Cestino();
            myBorderPane.setCenter(myCestino);
            mailCestino(myCestino);
        }

    }

    @FXML
    public void handlerBtnScrivi(final ActionEvent e){
        if(model.getCurrentUtente()!=null) {
            //System.out.println("[Client] Invia mail button premuto");
            ScriviMail myScriviMail = new ScriviMail();
            myBorderPane.setCenter(myScriviMail);
            scriviMail(myScriviMail);
        }
    }


    /**#################################################*/
    /**Funzioni principali*/

    private void scegliLogin(Login myLogin) {
        ListView ListViewLogin = myLogin.getListViewLogin();
        ListViewLogin.setItems(model.getUserList());
        myLogin.getBtn_loginFXML_OK().setOnAction(actionEvent -> {
            if(myLogin.getListViewLogin().getSelectionModel().getSelectedItem()!=null){
                model.setCurrentUtente(myLogin.getListViewLogin().getSelectionModel().getSelectedItem().toString());
                //System.out.println("[Client] Login effettuato con "+model.getCurrentUtente());
                model.getEmailListRicevute().clear();
                model.getEmailListInviate().clear();
                model.getEmailListCestino().clear();
                myLogin.getTextAreaLogin().setText("Loggato come "+ model.getCurrentUtente());
            }
        });
    }

    private void mailInArrivo(PostaInArrivo myPostaInArrivo) {

        ListView listViewPostaInArrivo = myPostaInArrivo.getListViewPostaInArrivo();
        TextArea textAreaPostaInArrivo = myPostaInArrivo.getTextAreaPostaInArrivo();
        Button btn_postaInArrivoFXML_Rispondi = myPostaInArrivo.getBtn_postaInArrivoFXML_Rispondi();
        Button btn_postaInArrivoFXML_RispondiATutti = myPostaInArrivo.getBtn_postaInArrivoFXML_RispondiATutti();
        Button btn_postaInArrivoFXML_Forward = myPostaInArrivo.getBtn_postaInArrivoFXML_Forward();
        Button btn_postaInArrivoFXML_Elimina = myPostaInArrivo.getBtn_postaInArrivoFXML_Elimina();

        myPostaInArrivo.getBtn_postaInArrivoFXML_Rispondi().setOnAction(actionEvent -> {
            if(model.getCurrentEmailRcv()!=null){
                String mailToResp = model.getCurrentEmailRcv().toString();
                rispondiMail(mailToResp);
            }
        });
        myPostaInArrivo.getBtn_postaInArrivoFXML_RispondiATutti().setOnAction(actionEvent -> {
            if(model.getCurrentEmailRcv()!=null){
                String mailToRespAll = model.getCurrentEmailRcv().toString();
                rispondiAtuttiMail(mailToRespAll);
            }
        });
        myPostaInArrivo.getBtn_postaInArrivoFXML_Forward().setOnAction(actionEvent -> {
            if(model.getCurrentEmailRcv()!=null){
                String mailToForward = model.getCurrentEmailRcv().toString();
                forwardMail(mailToForward);
            }
        });
        myPostaInArrivo.getBtn_postaInArrivoFXML_Elimina().setOnAction(actionEvent -> {
            if(model.getCurrentEmailRcv()!=null){
                //System.out.println("[Client] Tentativo di eliminazione di una mail dalla mail in arrivo");
                String current = model.getCurrentEmailRcv().toString();
                String parts[] = current.split(";");
                if(ClientNet.isServerUp()){
                    ClientNet.deleteMail(model,controller,Integer.parseInt(parts[0]));
                    ClientNet.refresh(model,controller);
                }else{
                    makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                }

            }
        });


        if(model.getCurrentUtente()!=null) {

            listViewPostaInArrivo.setItems(model.getEmailListRicevute());
            listViewPostaInArrivo.setCellFactory(lv -> new ListCell<Email>() {
                @Override
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText("Mittente: " + email.getMittente() + " Oggetto: " + email.getArgomento());
                    }
                }
            });

            listViewPostaInArrivo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                model.setCurrentEmailRcv((Email) newSelection);
                if(newSelection!=null){
                    Email mail = model.getCurrentEmailRcv();
                    String myMail[] = mail.toString().split(";");
                    int id = Integer.parseInt(myMail[0]);
                    String mittente = myMail[1];
                    String destinatario = myMail[2];
                    String argomento = myMail[3];
                    String testo = myMail[4];
                    String stato = myMail[5];

                    textAreaPostaInArrivo.setText(
                            "Mittente:       " + mittente + "\n" +
                                    "Destinatari:    " + destinatario + "\n" +
                                    "Argomento:      " + argomento + "\n\n" +
                                    "Testo:          " + testo
                    );
                }else{
                }
            });
            /*model.getEmailListRicevute().addListener((ListChangeListener<Email>) change -> {
                System.out.println("we fenomeni");
            });*/

            /*Visualizzo la mail corrente per poterla leggere*/
            /*model.currentEmailRcvProperty().addListener((obs, oldSelection, newSelection) -> {
            });*/

        }else{}




    }

    private void mailInviate(PostaInviata myPostaInviata) {
        ListView listViewPostaInviata = myPostaInviata.getListViewPostaInviata();
        TextArea textAreaPostaInviata = myPostaInviata.getTextAreaPostaInviata();
        Button btn_postaInviataFXML_Elimina = myPostaInviata.getBtn_postaInviataFXML_Elimina();

        myPostaInviata.getBtn_postaInviataFXML_Elimina().setOnAction(actionEvent -> {
            if(model.getCurrentEmailInv()!=null){
                //SPOSTARE NEL CESTINO
                String current = model.getCurrentEmailInv().toString();
                String parts[] = current.split(";");
                if(ClientNet.isServerUp()){
                    ClientNet.deleteMail(model,controller,Integer.parseInt(parts[0]));
                    ClientNet.refresh(model,controller);
                }else{
                    makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                }

            }
        });

        if(model.getCurrentUtente()!=null){

            listViewPostaInviata.setItems(model.getEmailListInviate());
            listViewPostaInviata.setCellFactory(lv -> new ListCell<Email>() {
                @Override
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    if(empty){
                        setText(null);
                    }else{
                        setText("Destinatario: " + email.getDestinatario()+" Oggetto: "+email.getArgomento());
                    }
                }
            });

            listViewPostaInviata.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->{
                model.setCurrentEmailInv((Email)newSelection);
                if(newSelection!=null){
                    Email mail = model.getCurrentEmailInv();
                    String myMail[] = mail.toString().split(";");
                    int id = Integer.parseInt(myMail[0]);
                    String mittente = myMail[1];
                    String destinatario = myMail[2];
                    String argomento = myMail[3];
                    String testo = myMail[4];
                    String stato = myMail[5];

                    textAreaPostaInviata.setText(
                            "Mittente:       " + mittente + "\n" +
                                    "Destinatari:    " + destinatario + "\n" +
                                    "Argomento:      " + argomento + "\n\n" +
                                    "Testo:          " + testo
                    );
                }else{
                }
            });

            /*Visualizzo la mail corrente per poterla leggere*/
            /*model.currentEmailInvProperty().addListener((obs, oldSelection, newSelection) -> {
            });*/


        }else{}


    }

    private void mailCestino(Cestino myCestino) {
        ListView listViewCestino = myCestino.getListViewCestino();
        TextArea textAreaCestino = myCestino.getTextAreaCestino();
        Button btn_cestinoFXML_Elimina = myCestino.getBtn_cestinoFXML_Elimina();

        myCestino.getBtn_cestinoFXML_Elimina().setOnAction(actionEvent -> {
            if(model.getCurrentEmailCes()!=null){
                String current = model.getCurrentEmailCes().toString();
                String parts[] = current.split(";");
                if(ClientNet.isServerUp()){
                    ClientNet.deleteMail(model,controller,Integer.parseInt(parts[0]));
                    ClientNet.refresh(model,controller);
                }else{
                    makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                }
            }
        });

        if(model.getCurrentUtente()!=null){

            listViewCestino.setItems(model.getEmailListCestino());
            listViewCestino.setCellFactory(lv -> new ListCell<Email>() {
                @Override
                public void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    if(empty){
                        setText(null);
                    }else{
                        setText("Mittente: " + email.getMittente()+ "Destinatario: " + email.getDestinatario() + " Oggetto: "+email.getArgomento());
                    }
                }
            });

            listViewCestino.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->{
                model.setCurrentEmailCes((Email)newSelection);
                if(newSelection!=null){
                    Email mail = model.getCurrentEmailCes();
                    String myMail[] = mail.toString().split(";");
                    int id = Integer.parseInt(myMail[0]);
                    String mittente = myMail[1];
                    String destinatario = myMail[2];
                    String argomento = myMail[3];
                    String testo = myMail[4];
                    String stato = myMail[5];

                    textAreaCestino.setText(
                            "Mittente:       " + mittente + "\n" +
                                    "Destinatari:    " + destinatario + "\n" +
                                    "Argomento:      " + argomento + "\n\n" +
                                    "Testo:          " + testo
                    );
                }else{
                }

            });

            /*Visualizzo la mail corrente per poterla leggere*/
            /*model.currentEmailCesProperty().addListener((obs, oldSelection, newSelection) -> {
            });*/

        }else{}
    }

    private void scriviMail(ScriviMail myScriviMail) {
        TextArea getTextAreascriviMailFXML_A = myScriviMail.getTextAreascriviMailFXML_A();
        TextArea getTextAreascriviMailFXML_Oggetto = myScriviMail.getTextAreascriviMailFXML_Oggetto();
        TextArea getTextAreascriviMailFXML_Testo = myScriviMail.getTextAreascriviMailFXML_Testo();
        Button getBtn_scriviMailFXML_Invia = myScriviMail.getBtn_scriviMailFXML_Invia();

        myScriviMail.getBtn_scriviMailFXML_Invia().setOnAction(actionEvent -> {
            String to = getTextAreascriviMailFXML_A.getText();
            String oggetto = getTextAreascriviMailFXML_Oggetto.getText();
            String testo = getTextAreascriviMailFXML_Testo.getText();

            if (model.getCurrentUtente() != null){
                /*verifico che i destinatari siano corretti dal punto di vista sintattico*/
                String parts[] = to.split(",");
                boolean check=true;
                for(int i=0;i< parts.length;i++){
                    if(check==true){
                        check = validate(parts[i]);
                    }
                }

                if(to!=null&&!to.isBlank()&&oggetto!=null&&!oggetto.isBlank()&&testo!=null&&!testo.isBlank()){
                    if(check==true){
                        //System.out.println("[Client] Tentativo di invio mail da parte di "+model.getCurrentUtente()+" verso "+to);
                        String mittente = model.getCurrentUtente();
                        to = getTextAreascriviMailFXML_A.getText();
                        oggetto = getTextAreascriviMailFXML_Oggetto.getText();
                        testo = getTextAreascriviMailFXML_Testo.getText();

                        if(ClientNet.isServerUp()){
                            ClientNet.sendMail(model,controller,to,oggetto,testo);
                            ClientNet.refresh(model,controller);
                        }else{
                            makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                        }
                    }else{
                        makeAlert("Errore in scrivi mail","Email non corretta dal punto di vista sintattico");
                    }
                }else{
                    makeAlert("Errore in scrivi mail","Riempi tutti i campi");
                }
            }else{
                makeAlert("Errore in scrivi mail","L'utente non si è ancora loggato");
            }

        });
    }


    /**#################################################*/
    /** Funzioni di supporto
     **/

    public void rispondiMail(String mailToResp){
        ScriviMail myScriviMail = new ScriviMail();
        myBorderPane.setCenter(myScriviMail);
        TextArea getTextAreascriviMailFXML_A = myScriviMail.getTextAreascriviMailFXML_A();
        TextArea getTextAreascriviMailFXML_Oggetto = myScriviMail.getTextAreascriviMailFXML_Oggetto();
        TextArea getTextAreascriviMailFXML_Testo = myScriviMail.getTextAreascriviMailFXML_Testo();
        Button getBtn_scriviMailFXML_Invia = myScriviMail.getBtn_scriviMailFXML_Invia();
        String mailParted[] = mailToResp.split(";");
        getTextAreascriviMailFXML_A.setText(mailParted[1]);
        getTextAreascriviMailFXML_A.setEditable(false);
        getTextAreascriviMailFXML_Oggetto.setText("Re: "+mailParted[3]);
        getTextAreascriviMailFXML_Oggetto.setEditable(false);

        myScriviMail.getBtn_scriviMailFXML_Invia().setOnAction(actionEvent -> {

            if (model.getCurrentUtente() != null){
                /*verifico che i destinatari siano corretti dal punto di vista sintattico*/
                String parts[] = mailParted[1].split(",");
                boolean check=true;
                for(int i=0;i< parts.length;i++){
                    if(check==true){
                        check = validate(parts[i]);
                    }
                }
                if(mailParted[1]!=null&&!mailParted[1].isBlank()&&mailParted[3]!=null&&!mailParted[3].isBlank()&&mailParted[4]!=null&&!mailParted[4].isBlank()){
                    if(check==true){
                        //System.out.println("[Client] Tentativo di invio mail da parte di "+model.getCurrentUtente()+" verso "+to);
                        if(ClientNet.isServerUp()){
                            ClientNet.sendMail(model,controller,mailParted[1],"Re: "+mailParted[3],mailParted[4]);
                            ClientNet.refresh(model,controller);
                        }else{
                            makeAlert("SERVER DOWN", "Il server è spento, attendere che venga riacceso");
                        }

                    }else{
                        makeAlert("Errore in scrivi mail","Email non corretta dal punto di vista sintattico");
                    }
                }else{
                    makeAlert("Errore in scrivi mail","Riempi tutti i campi");
                }
            }else{
                makeAlert("Errore in scrivi mail","L'utente non si è ancora loggato");
            }

        });


    }

    public void rispondiAtuttiMail(String mailToRespAll) {
        ScriviMail myScriviMail = new ScriviMail();
        myBorderPane.setCenter(myScriviMail);
        TextArea getTextAreascriviMailFXML_A = myScriviMail.getTextAreascriviMailFXML_A();
        TextArea getTextAreascriviMailFXML_Oggetto = myScriviMail.getTextAreascriviMailFXML_Oggetto();
        TextArea getTextAreascriviMailFXML_Testo = myScriviMail.getTextAreascriviMailFXML_Testo();
        Button getBtn_scriviMailFXML_Invia = myScriviMail.getBtn_scriviMailFXML_Invia();

        String mailParted[] = mailToRespAll.split(";");
        getTextAreascriviMailFXML_A.setText(mailParted[1]+","+mailParted[2]);
        getTextAreascriviMailFXML_A.setEditable(false);
        getTextAreascriviMailFXML_Oggetto.setText("Re: "+mailParted[3]);
        getTextAreascriviMailFXML_Oggetto.setEditable(false);

        myScriviMail.getBtn_scriviMailFXML_Invia().setOnAction(actionEvent -> {

            if (model.getCurrentUtente() != null){
                /*verifico che i destinatari siano corretti dal punto di vista sintattico*/
                String parts[] = mailParted[2].split(",");
                boolean check=true;
                for(int i=0;i< parts.length;i++){
                    if(check==true){
                        check = validate(parts[i]);
                    }
                }
                if(mailParted[2]!=null&&!mailParted[2].isBlank()&&mailParted[3]!=null&&!mailParted[3].isBlank()&&mailParted[4]!=null&&!mailParted[4].isBlank()){
                    if(check==true){
                        //System.out.println("[Client] Tentativo di invio mail da parte di "+model.getCurrentUtente()+" verso "+to);
                        if(ClientNet.isServerUp()){
                            ClientNet.sendMail(model,controller,mailParted[1]+","+mailParted[2],"Re: "+mailParted[3],mailParted[4]);
                            ClientNet.refresh(model,controller);
                        }else{
                            makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                        }

                    }else{
                        makeAlert("Errore in scrivi mail","Email non corretta dal punto di vista sintattico");
                    }
                }else{
                    makeAlert("Errore in scrivi mail","Riempi tutti i campi");
                }
            }else{
                makeAlert("Errore in scrivi mail","L'utente non si è ancora loggato");
            }

        });


    }

    public void forwardMail(String mailToForward){
        ScriviMail myScriviMail = new ScriviMail();
        myBorderPane.setCenter(myScriviMail);
        TextArea getTextAreascriviMailFXML_A = myScriviMail.getTextAreascriviMailFXML_A();
        TextArea getTextAreascriviMailFXML_Oggetto = myScriviMail.getTextAreascriviMailFXML_Oggetto();
        TextArea getTextAreascriviMailFXML_Testo = myScriviMail.getTextAreascriviMailFXML_Testo();
        Button getBtn_scriviMailFXML_Invia = myScriviMail.getBtn_scriviMailFXML_Invia();

        String mailParted[] = mailToForward.split(";");
        getTextAreascriviMailFXML_Oggetto.setText("Forward: "+mailParted[3]);
        getTextAreascriviMailFXML_Oggetto.setEditable(false);
        getTextAreascriviMailFXML_Testo.setText(mailParted[4]);
        getTextAreascriviMailFXML_Testo.setEditable(false);

        myScriviMail.getBtn_scriviMailFXML_Invia().setOnAction(actionEvent -> {
            String to = getTextAreascriviMailFXML_A.getText();

            if (model.getCurrentUtente() != null){
                /*verifico che i destinatari siano corretti dal punto di vista sintattico*/
                String parts[] = to.split(",");
                boolean check=true;
                for(int i=0;i< parts.length;i++){
                    if(check==true){
                        check = validate(parts[i]);
                    }
                }
                if(to!=null&&!to.isBlank()&&mailParted[3]!=null&&!mailParted[3].isBlank()&&mailParted[4]!=null&&!mailParted[4].isBlank()){
                    if(check==true){
                        //System.out.println("[Client] Tentativo di invio mail da parte di "+model.getCurrentUtente()+" verso "+to);
                        if(ClientNet.isServerUp()){
                            ClientNet.sendMail(model,controller,to,"Forward: "+mailParted[3],mailParted[4]);
                            ClientNet.refresh(model,controller);
                        }else{
                            makeAlert("SERVER DOWN", "Il server è spento, attendere che venga riacceso");
                        }

                    }else{
                        makeAlert("Errore in scrivi mail","Email non corretta dal punto di vista sintattico");
                    }
                }else{
                    makeAlert("Errore in scrivi mail","Riempi tutti i campi");
                }
            }else{
                makeAlert("Errore in scrivi mail","L'utente non si è ancora loggato");
            }

        });

    }

    public void makeAlert(String titolo,String testo){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(titolo);
                alert.setHeaderText(null);
                alert.setContentText(testo);
                alert.showAndWait();            }
        });
    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public void initModel(DataModel model,Controller controller){
        /*Mi assicuro che il model sia settato una sola volta*/
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.model = model;
        this.controller=controller;
        //this.model.loadUtenti();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO
        /*Ipotizziamo di avere un model, ovvero una classe
            private Persona p;
           */
        /*
        p = new Persona(params...);
        */
    }

}
