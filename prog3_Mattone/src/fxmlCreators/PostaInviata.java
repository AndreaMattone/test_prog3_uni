package fxmlCreators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PostaInviata extends AnchorPane {

    @FXML
    private ListView listViewPostaInviata;
    @FXML
    private TextArea textAreaPostaInviata;
    @FXML
    private Button btn_postaInviataFXML_Elimina;

    public ListView getListViewPostaInviata() {
        return listViewPostaInviata;
    }
    public Button getBtn_postaInviataFXML_Elimina() {
        return btn_postaInviataFXML_Elimina;
    }
    public TextArea getTextAreaPostaInviata() {
        return textAreaPostaInviata;
    }

    public PostaInviata(){
        FXMLLoader fxmlLoaderTmp = new FXMLLoader(getClass().getResource("../sample/fxml/postaInviata.fxml"));
        fxmlLoaderTmp.setRoot(this);
        fxmlLoaderTmp.setController(this);
        try {
            fxmlLoaderTmp.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
