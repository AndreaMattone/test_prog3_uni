package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private static Controller myController;
    private static DataModel myModel;

    public static Controller getMyController() {
        return myController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("fxml/sample.fxml"));
        Parent root = myLoader.load();

        primaryStage.setTitle("MyClient");
        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.show();
        myController = myLoader.getController();
        myModel = new DataModel();
        myController.initModel(myModel,myController);

        /*Ogni 5 secondi aggiorna i dati della mailBox*/
        Thread mainThreadClient = new Thread(() -> {
            boolean exit=false;
            while(exit==false){
                if(ClientNet.isServerUp()){
                    ClientNet.getLogins(myModel,myController);
                    exit=true;
                    try { Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
                }else{
                    myController.makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                }
                try { Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
            }
            while(true) {
                if(ClientNet.isServerUp()){
                    if(myModel.getCurrentUtente()!=null){
                        ClientNet.refresh(myModel,myController);
                    }
                }else{
                    myController.makeAlert("SERVER DOWN","Il server è spento, attendere che venga riacceso");
                }
                /*Ogni 10 secondi richiedo un aggiornamento della mail box
                * */
                try { Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        mainThreadClient.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
