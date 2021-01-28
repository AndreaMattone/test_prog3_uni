package fxmlCreators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PostaInArrivo extends AnchorPane {

    @FXML
    private ListView listViewPostaInArrivo;
    @FXML
    private TextArea textAreaPostaInArrivo;
    @FXML
    private Button btn_postaInArrivoFXML_Rispondi;
    @FXML
    private Button btn_postaInArrivoFXML_RispondiATutti;
    @FXML
    private Button btn_postaInArrivoFXML_Forward;
    @FXML
    private Button btn_postaInArrivoFXML_Elimina;

    public ListView getListViewPostaInArrivo() {
        return listViewPostaInArrivo;
    }
    public TextArea getTextAreaPostaInArrivo() {
        return textAreaPostaInArrivo;
    }
    public Button getBtn_postaInArrivoFXML_Rispondi() {
        return btn_postaInArrivoFXML_Rispondi;
    }
    public Button getBtn_postaInArrivoFXML_RispondiATutti() {
        return btn_postaInArrivoFXML_RispondiATutti;
    }
    public Button getBtn_postaInArrivoFXML_Forward() {
        return btn_postaInArrivoFXML_Forward;
    }
    public Button getBtn_postaInArrivoFXML_Elimina() {
        return btn_postaInArrivoFXML_Elimina;
    }

    public PostaInArrivo(){
        FXMLLoader fxmlLoaderTmp = new FXMLLoader(getClass().getResource("../sample/fxml/postaInArrivo.fxml"));
        fxmlLoaderTmp.setRoot(this);
        fxmlLoaderTmp.setController(this);
        try {
           fxmlLoaderTmp.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
