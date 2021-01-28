package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.utils.Email;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class serverMethods{

    /**Manda la lista delle mail al client*/
    public static void sendMailsToClient(String utente, Socket incoming){

        /*Leggo le mail dal file TXT*/
        String path = "src/sample/files/mailbox_"+utente+".txt";
        ArrayList<String> mailList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader((new FileReader(path)));
            String line = reader.readLine();
            mailList.add(line);
            while(line!=null){
                line= reader.readLine();
                mailList.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*Creo l'oggetto ArrayList<Email> con le mail dell'utente*/
        int len = mailList.size()-2;/*Come ultimo campo in mail list c'è "null", non voglio prendere quel campo*/
        ArrayList<Email> myEmails = new ArrayList<>();
        for(int i = 0; i<=len;i++){
            String parts[] = mailList.get(i).split(";");
            int id = Integer.parseInt(parts[0]);
            String mittente = parts[1];
            String destinatari = parts[2];
            String oggetto = parts[3];
            String testo = parts[4];
            int stato = Integer.parseInt(parts[5]);
            myEmails.add(new Email(id,mittente,destinatari,oggetto,testo,stato));
        }
        System.out.println(myEmails);

        //Mando l'elenco di Email all'utente
        try {
            ObjectOutputStream myOutputStream = new ObjectOutputStream(incoming.getOutputStream());
            myOutputStream.writeObject(myEmails);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**Manda la lista dei logins al client*/
    public static void getLogins(Socket incoming){
        /*Leggo i logins dal file TXT*/
        String path = "src/sample/files/login.txt";
        ArrayList<String> logins = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader((new FileReader(path)));
            String line = reader.readLine();
            logins.add(line);
            while(line!=null){
                line= reader.readLine();
                logins.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(logins);
        /*System.out.println(logins.get(logins.size()-2));*/
        logins.remove(logins.size()-1);

        try {
            ObjectOutputStream myOutputStream = new ObjectOutputStream(incoming.getOutputStream());
            myOutputStream.writeObject(logins);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**Gestisce la scrittura sulle mailbox, quindi invio (reply,forward ecc) e cancellazione delle mail dalle mailbox
     * Accede in mutua esclusione alle mailbox degli utenti
     * */
    public static synchronized void mailboxWork(String user, int action, int ifNeeded_ID, String ifNeeded_Destinatari, String ifNeeded_Oggetto, String ifNeeded_testo, Socket incoming){
        switch(action){
            case 2:
                /*Devo gestire l'invio di una mail*/
                String mittente = user;
                String destinatari = ifNeeded_Destinatari;
                String oggetto = ifNeeded_Oggetto;
                String testo = ifNeeded_testo;

                /*Verifico che i destinatari esistano*/
                String pathLogin = "src/sample/files/login.txt";
                ArrayList<String> userList = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader((new FileReader(pathLogin)));
                    String line = reader.readLine();
                    userList.add(line);
                    while(line!=null){
                        line= reader.readLine();
                        userList.add(line);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*In userList ho l'elenco dei destinatari*/
                /*userList da i=0 a size()-2*/
                boolean check = true;
                String dests[] = destinatari.split(",");
                for(String s:dests){
                    if(!userList.contains(s)){
                        check=false;
                    }
                }
                if(check==true){
                    //Tutti i destinatari sono accettabili

                    /**
                     * Scrivo la mail nella casella del mittente
                     *
                     *
                     * */
                    /*Cerco prima di tutto l'ultimo id presente nella mailbox dell'utente così da assegare
                    * alla mail che inserisco l'id+1*/
                    String path = "src/sample/files/mailbox_"+mittente+".txt";
                    ArrayList<String> mailList = new ArrayList<>();
                    try {
                        BufferedReader reader = new BufferedReader((new FileReader(path)));
                        String line = reader.readLine();
                        mailList.add(line);
                        while(line!=null){
                            line= reader.readLine();
                            mailList.add(line);
                        }
                        reader.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int lastId = 0;
                    if(mailList.size()==1){ //non ci sono mail, c'è la mail null
                        lastId=0;
                    }else{
                        String lastMail = mailList.get(mailList.size()-2);
                        String temp[] = lastMail.split(";");
                        lastId = Integer.parseInt(temp[0]);
                    }
                    Email emailDelMittente = new Email(lastId+1,mittente,destinatari,oggetto,testo,2);
                    String toInsertInMittenteMailBox = emailDelMittente.toString();
                    /*Procedo all'inserimento nel txt della mailbox del mittente*/
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(
                                new FileWriter("src/sample/files/mailbox_"+mittente+".txt", true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        /*Se è la prima mail non metto \n*/
                        if(lastId+1==1){
                            writer.write(toInsertInMittenteMailBox);
                        }else{
                            writer.write("\n"+toInsertInMittenteMailBox);
                        }
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    /**
                     * Scrivo la mail in ogni casella dei destinatari
                     *
                     *
                     * */
                    /*Per ogni destinatario d cerco l'id dell'ultima mail e aggiungo la mail ricevuta con id+1*/
                    for(String d:dests){
                        String pathDest = "src/sample/files/mailbox_"+d+".txt";
                        ArrayList<String> mailListDest = new ArrayList<>();
                        try{
                            BufferedReader readerDest =new BufferedReader((new FileReader(pathDest)));
                            String lineDest = readerDest.readLine();
                            mailListDest.add(lineDest);
                            while(lineDest!=null){
                                lineDest=readerDest.readLine();
                                mailListDest.add(lineDest);
                            }
                            readerDest.close();
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int lastIdDest = 0;
                        if(mailListDest.size()==1){
                            lastIdDest=0;
                        }else{
                            String lastMailDest = mailListDest.get(mailListDest.size()-2);
                            String tempDest[] = lastMailDest.split(";");
                            lastIdDest=Integer.parseInt(tempDest[0]);
                        }
                        Email emailDelDest = new Email(lastIdDest+1,mittente,destinatari,oggetto,testo,1);
                        String toInsertInDestMailBox = emailDelDest.toString();



                        /*Inserisco ora nella mail box del destinatario attuale
                        * */
                        BufferedWriter writerDest = null;
                        try {
                            writerDest = new BufferedWriter(
                                    new FileWriter("src/sample/files/mailbox_"+d+".txt", true));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            /*Se è la prima mail non metto \n*/
                            if(lastIdDest+1==1){
                                writerDest.write(toInsertInDestMailBox);
                            }else{
                                writerDest.write("\n"+toInsertInDestMailBox);
                            }
                            writerDest.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //Invio il risultato dell'avvenuto invio corretto delle mail
                    OutputStream outStream = null;
                    try {
                        outStream = incoming.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                    out.println("ok");

                }else{
                    //almeno uno dei destinatari non è accettabile
                    OutputStream outStream = null;
                    try {
                        outStream = incoming.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                    out.println("ko");
                }

                break;


            case 3:
                /*Devo gestire la cancellazione di una mail*/
                String userName = user;
                int idToDelete = ifNeeded_ID;

                /*Cerco nella mailbox dell'utente la mail indicata, se ha stato 1 o 2, ovvero se è una mail inviata
                * o ricevutam le cambio lo stato a 3, così che vada nel cestino, se invece ha stato 3, ovvero
                * è nel cestino, le metto stato 4, quindi non potrà piu essere visualizzata ma rimarra nel "database"
                * */

                /*Recupero la mail box dell'utente*/
                String path = "src/sample/files/mailbox_"+userName+".txt";
                ArrayList<String> mailList = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader((new FileReader(path)));
                    String line = reader.readLine();
                    mailList.add(line);
                    while(line!=null){
                        line= reader.readLine();
                        mailList.add(line);
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Creo l'oggetto ArrayList<Email> con le mail dell'utente*/
                int len = mailList.size()-2;/*Come ultimo campo in mail list c'è "null", non voglio prendere quel campo*/
                ArrayList<Email> myEmails = new ArrayList<>();
                for(int i = 0; i<=len;i++){
                    String parts[] = mailList.get(i).split(";");
                    int id = Integer.parseInt(parts[0]);
                    String mitt = parts[1];
                    String dest = parts[2];
                    String ogg = parts[3];
                    String tes = parts[4];
                    int st = Integer.parseInt(parts[5]);
                    /*Se la mail è inviata o ricevuta e ne è richiesta la cancellazione la metto nel cestino*/
                    if(id==idToDelete && (st==1 || st==2)){
                        myEmails.add(new Email(id,mitt,dest,ogg,tes,3));
                    }else if(id==idToDelete && st==3){/*Se la mail è nel cestino e ne è richiesta la cancellazione la "cancello" mettendo stato 4*/
                        myEmails.add(new Email(id,mitt,dest,ogg,tes,4));
                    }else{
                        myEmails.add(new Email(id,mitt,dest,ogg,tes,st));
                    }
                }
                //System.out.println(myEmails);

                /*Pulisco il file*/
                try {
                    BufferedWriter writer1 = new BufferedWriter(
                            new FileWriter("src/sample/files/mailbox_"+userName+".txt", false));
                    writer1.write("");
                    writer1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*Riscrivo il file con le modifiche*/
                try {
                    System.out.println(myEmails);
                    BufferedWriter writer1 = new BufferedWriter(
                            new FileWriter("src/sample/files/mailbox_"+userName+".txt", true));
                    for(int i =0 ; i<myEmails.size();i++){
                        /*La scrittura delle mail è riga per riga, con un a capo alla fine
                        * ECCETTO PER L'ULTIMA MAIL, CHE NON HA \n al fondo*/
                        if(i!=myEmails.size()-1){
                            writer1.write(myEmails.get(i).toString()+"\n");
                            System.out.println(myEmails.get(i).toString());
                        }else{
                            writer1.write(myEmails.get(i).toString());
                            System.out.println(myEmails.get(i).toString());
                        }
                    }
                    writer1.close();


                    /*Invio al client il messaggio di avvenuta cancellazione*/
                    OutputStream outStream = null;
                    try {
                        outStream = incoming.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                    out.println("ok");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}


class ThreadedEchoHandler implements Runnable {

    private Socket incoming;
    private Parent root;

    public ThreadedEchoHandler(Socket in,Parent root) {
        incoming = in;
        this.root=root;
    }

    public void run() {

        try {
            try {
                /*Leggo dati che ricevo dal client*/
                InputStream inStream = incoming.getInputStream();
                Scanner in = new Scanner(inStream);
                String userLogin = in.nextLine(); // attenzione: se il client non scrive nulla questo resta in attesa...
                String action = in.nextLine();
                System.out.println(action);

                switch(action){
                    /*Il client richiede l'aggiornamento della mailbox*/
                    case "1":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per aggiornamento mailbox\n");
                        serverMethods.sendMailsToClient(userLogin,incoming);
                        break;

                    /*Utente cerca di inviare una mail*/
                    case "2":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per invio mail\n");
                        String destinatari = in.nextLine();
                        String oggetto = in.nextLine();
                        String testo = in.nextLine();
                        serverMethods.mailboxWork(userLogin,2,-1,destinatari,oggetto,testo,incoming);
                        break;

                    /*Utente richiede la cancellazione di una mail*/
                    case "3":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per eliminazione mail\n");
                        String idToDelete = in.nextLine();
                        serverMethods.mailboxWork(userLogin,3,Integer.parseInt(idToDelete),null,null,null,incoming);
                        break;

                    case "4":
                        ((javafx.scene.control.TextArea)root.lookup("#TextAreaServer")).appendText("Connessione con "+ incoming.getRemoteSocketAddress().toString() + " per aggiornamento logins\n");
                        serverMethods.getLogins(incoming);
                        break;
                    default:
                        //nothing
                }

            }
            finally {
                incoming.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}


    }


}


public class Server extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("fxml/server.fxml"));
        Parent root = myLoader.load();
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();

        Thread mainThreadServer = new Thread(() -> {
            try {
                ServerSocket s = new ServerSocket(8189);
                while (true) {
                    Socket actualSocket = s.accept(); // si mette in attesa di richiesta di connessione e la apre
                    Runnable r = new ThreadedEchoHandler(actualSocket,root);
                    new Thread(r).start();
                }
            }
            catch (IOException e) {e.printStackTrace();}
        });
        mainThreadServer.start();

    }

    public static void main(String[] args ) {
        launch(args);
    }


}
