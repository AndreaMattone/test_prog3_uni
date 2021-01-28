package sample;

import javafx.application.Platform;
import sample.utils.Email;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientNet {

    public static void getLogins(DataModel myModel,Controller myController){
        try {
            System.out.println("[Client] Richiedo i login al server");
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8189);

            /*Mando al server la richiesta*/
            OutputStream outStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outStream, true);
            out.println("null");
            out.println("4");

            /*Ricevo dal server la mailbox*/
            ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
            ArrayList<String> logins = new ArrayList<>();

            try {
                logins = ((ArrayList<String>)inStream.readObject());
                System.out.println(logins);
                ArrayList<String> finalLogins = logins;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        myModel.getUserList().setAll(finalLogins);
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                s.close();
            }



        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isServerUp(){
        try {
            System.out.println("[Client] Is server Up");
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8189);

            /*Mando al server una richiesta sentinella*/
            OutputStream outStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outStream, true);
            out.println("null");
            out.println("null");

            return true;
        } catch (IOException e) {
            System.out.println("Server down");
            return false;
        }
    }

    public static void refresh(DataModel myModel,Controller myController){
        //Download delle mail e inserisco nel mio datamodel
        ArrayList<Email> emailsFromServer = new ArrayList<>();
        try {
            System.out.println("[Client] Aggiorno i dati dal server");
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), 8189);

            /*Mando al server la richiesta*/
            OutputStream outStream = s.getOutputStream();
            PrintWriter out = new PrintWriter(outStream, true);
            System.out.println(myModel.getCurrentUtente());
            out.println(myModel.getCurrentUtente()); /*Specifico l'utente che la richiede*/
            out.println("1"); /*E il tipo di richiesta, in questo caso scaricare la mailbox*/

            /*Ricevo dal server la mailbox*/
            ObjectInputStream inStream = new ObjectInputStream(s.getInputStream());
            emailsFromServer = new ArrayList<>();

            try {
                emailsFromServer = ((ArrayList<Email>)inStream.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            finally {
                s.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        /*Ora estraggo dall'elenco di mail quelle ricevute, quelle inviate e quelle cancellate*/
        ArrayList<Email> emailRicevute = new ArrayList<>();
        ArrayList<Email> emailInviate = new ArrayList<>();
        ArrayList<Email> emailCestino = new ArrayList<>();
        for(Email e: emailsFromServer){
            int stato = e.getStato();
            switch(stato){
                case 1:
                    emailRicevute.add(e);
                    break;
                case 2:
                    emailInviate.add(e);
                    break;
                case 3:
                    emailCestino.add(e);
                    break;
            }
        }

        /*Quindi qua devo controllare se emailRicevute ha delle nuove mail e mandare di conseguenza la notifica
        * */
        int lenNewEmailRicevute = emailRicevute.size();
        int lenOldEmailRicevute = myModel.getEmailListRicevute().size();

        System.out.println(emailRicevute);
        if(lenNewEmailRicevute!=0){ /*Se il file è vuoto non faccio nessun controllo*/
            if(lenNewEmailRicevute>lenOldEmailRicevute){
                //ci sono nuove mail ricevute di sicuro
                myController.makeAlert("MAIL RICEVUTA", "Sono arrivate nuove mail");
            }else if(lenNewEmailRicevute<lenOldEmailRicevute){
                //potrebbero non esserci nuove mail ricevute, però devo controllare se le ultime
                //mail tra quelle nuove sono diverse a quelle vecchie nella stessa posizione,
                //in quanto potrebbero essere state cancellate diverse mail e poi arrivate di nuove
                /*devo cercare se l'ultima mail tra quelle nuove è presente tra quelle vecchie
                 * se si allora non ci sono nuove mail
                 * */
                /*
                 * [A,B,D]
                 * [A,B,C,D,E]
                 * */
                System.out.println(emailRicevute);
                String lastMailNew = emailRicevute.get(lenNewEmailRicevute-1).toString();
                boolean newM = true;
                for(Email e: myModel.getEmailListRicevute()){
                    if((e.toString().equals(lastMailNew)) ){
                        newM=false;
                    }
                }
                if(newM==true){
                    myController.makeAlert("MAIL RICEVUTA", "Sono arrivate nuove mail 1");
                }
            }else if(lenNewEmailRicevute==lenOldEmailRicevute){
                //potrebbe non essere cambiato nulla, ma devo controllare se l'ultima mail ricevuta è
                //nuova perchè porebbe essere stata cancellata una mail e arrivata una nuova,
                //o cancellate due mail e arrivate due nuove ecc.
                if(! (emailRicevute.get(lenNewEmailRicevute-1).toString().equals(myModel.getEmailListRicevute().get(lenNewEmailRicevute-1).toString()))){
                    //l'ultima mail ricevuta è nuova
                    myController.makeAlert("MAIL RICEVUTA", "Sono arrivate nuove mail 2");
                }else{
                    //Non ci sono nuove mail
                }

            }
        }


         /*
         * NB IL RUNLATER FA SI CHE QUESTE AGGIUNTE AVVENGANO ALLA FINE DI TUTTE LE ISTRUZIONI PRESENTI
         * IN QUESTA FUNZIONE*/
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                myModel.getEmailListRicevute().setAll(emailRicevute);
                myModel.getEmailListInviate().setAll(emailInviate);
                myModel.getEmailListCestino().setAll(emailCestino);
            }
        });


    }

    public static void sendMail(DataModel myModel,Controller myController,String destinatari, String oggetto,String testo){
        try {
            System.out.println("[Client] Invio mail");
            String nomeHost = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(nomeHost, 8189); //System.out.println("Ho aperto il socket verso il server");

            try {
                /*Invio la richiesta*/
                OutputStream outStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outStream, true);
                out.println(myModel.getCurrentUtente());
                out.println("2");
                out.println(destinatari);
                out.println(oggetto);
                out.println(testo);

                /*Leggo il risultato*/
                InputStream inStream = s.getInputStream();
                Scanner in = new Scanner(inStream);
                String received = in.nextLine(); // attenzione: se il server non scrive nulla questo resta in attesa...
                //System.out.println(received);
                if(received.equals("ok")){
                    myController.makeAlert("SUCCESSO", "Mail inviata con successo a tutti i destinatari");
                }else{
                    myController.makeAlert("ERRORE", "Errore nell'invio della mail");
                }

            }
            finally {
                s.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMail(DataModel myModel,Controller myController,int idToDelete){
        try {
            System.out.println("[Client] Cancellazione mail");
            String nomeHost = InetAddress.getLocalHost().getHostName();
            Socket s = new Socket(nomeHost, 8189); //System.out.println("Ho aperto il socket verso il server");

            try {
                /*Invio la richiesta*/
                OutputStream outStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outStream, true);
                out.println(myModel.getCurrentUtente());
                out.println("3");
                out.println(idToDelete);

                /*Leggo il risultato*/
                InputStream inStream = s.getInputStream();
                Scanner in = new Scanner(inStream);
                String received = in.nextLine(); // attenzione: se il server non scrive nulla questo resta in attesa...
                if(received.equals("ok")){
                    myController.makeAlert("SUCCESSO", "Mail eliminata con successo");
                }else{
                    myController.makeAlert("ERRORE", "Errore nell'eliminazione della mail");
                }

            }
            finally {
                s.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
