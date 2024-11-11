package sample.controller.UtulisateurController;

import animatefx.animation.*;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.assests.helper.Helper;
import sample.controller.MasterpageController;
import sample.model.Utulisateur;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Utulisateur implements Initializable  {

    @FXML
    private JFXTextField email;

    @FXML
    private JFXPasswordField pass;


    @FXML
    void login(ActionEvent event) throws IOException {
        String msg = "";
        email.resetValidation();
        pass.resetValidation();
        pass.validate();
        email.validate();

        if (!pass.getText().isEmpty() && !email.getText().isEmpty()) {
            int rep = verfierLogin(email.getText(), pass.getText());
            switch (rep) {
                case 1:
                    msg = "Mot de passe Incorrect";
                    pass.clear();
                    break;
                case -1:
                    msg = "Mot de passe et email/nom d'utilisateur Incorrect";
                    pass.clear();
                    email.clear();
                    break;
                case 2:
                    // Créer une instance d'FXMLLoader
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/views/Masterpage.fxml"));
                    Parent root = loader.load();  // Charge le fichier FXML

                    // Obtenir le contrôleur de la Masterpage
                    MasterpageController m = loader.getController();
                    m.setname(email.getText().trim());

                    // Obtenir le stage actuel et changer la scène
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1237, 592);
                    currentStage.setTitle("Gestion Des Commandes");
                    currentStage.setScene(scene);
                    currentStage.show();
                    break;
            }
        }

        if (!msg.isEmpty()) {
            Helper.Alert(msg);
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Helper h=new Helper();
        h.validator(email,"Champ Email Obilgatore");
        h.validator(pass,"Champ Pass Obilgatore");
        email.setStyle("-fx-text-inner-color:white;-fx-prompt-text-fill:white;");
        pass.setStyle("-fx-text-inner-color:white;-fx-prompt-text-fill:white;");

    }


    @FXML
    void entre(KeyEvent event) throws Exception {
    }

}
