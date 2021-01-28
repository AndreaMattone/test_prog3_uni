package fxmlCreators;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;


public class Login extends AnchorPane{

    @FXML
    private ListView listViewLogin;
    @FXML
    private Button btn_loginFXML_OK;
    @FXML
    private Text textAreaLogin;

    public ListView getListViewLogin() {
        return listViewLogin;
    }
    public Button getBtn_loginFXML_OK() {
        return btn_loginFXML_OK;
    }
    public Text getTextAreaLogin() {
        return textAreaLogin;
    }

    public Login(){
        FXMLLoader fxmlLoaderTmp = new FXMLLoader(getClass().getResource("../sample/fxml/login.fxml"));
        fxmlLoaderTmp.setRoot(this);
        fxmlLoaderTmp.setController(this);
        try {
            fxmlLoaderTmp.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
