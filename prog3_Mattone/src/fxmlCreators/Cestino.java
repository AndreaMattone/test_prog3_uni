package fxmlCreators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Cestino extends AnchorPane {

    @FXML
    private ListView listViewCestino;
    @FXML
    private TextArea textAreaCestino;
    @FXML
    private Button btn_cestinoFXML_Elimina;

    public ListView getListViewCestino() {
        return listViewCestino;
    }
    public Button getBtn_cestinoFXML_Elimina() {
        return btn_cestinoFXML_Elimina;
    }
    public TextArea getTextAreaCestino() {
        return textAreaCestino;
    }

    public Cestino(){
        FXMLLoader fxmlLoaderTmp = new FXMLLoader(getClass().getResource("../sample/fxml/cestino.fxml"));
        fxmlLoaderTmp.setRoot(this);
        fxmlLoaderTmp.setController(this);
        try {
            fxmlLoaderTmp.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
