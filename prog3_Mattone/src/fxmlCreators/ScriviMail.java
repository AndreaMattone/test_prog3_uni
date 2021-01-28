package fxmlCreators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ScriviMail extends AnchorPane {
    @FXML
    private TextArea TextAreascriviMailFXML_A;
    @FXML
    private TextArea TextAreascriviMailFXML_Oggetto;
    @FXML
    private TextArea TextAreascriviMailFXML_Testo;
    @FXML
    private Button btn_scriviMailFXML_Invia;

    public Button getBtn_scriviMailFXML_Invia() {
        return btn_scriviMailFXML_Invia;
    }
    public TextArea getTextAreascriviMailFXML_A() {
        return TextAreascriviMailFXML_A;
    }
    public TextArea getTextAreascriviMailFXML_Oggetto() {
        return TextAreascriviMailFXML_Oggetto;
    }
    public TextArea getTextAreascriviMailFXML_Testo() {
        return TextAreascriviMailFXML_Testo;
    }

    public ScriviMail(){
        FXMLLoader fxmlLoaderTmp = new FXMLLoader(getClass().getResource("../sample/fxml/scriviMail.fxml"));
        fxmlLoaderTmp.setRoot(this);
        fxmlLoaderTmp.setController(this);
        try {
            fxmlLoaderTmp.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
