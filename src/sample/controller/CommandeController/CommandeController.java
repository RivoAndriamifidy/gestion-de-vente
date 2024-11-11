package sample.controller.CommandeController;

import com.jfoenix.controls.JFXTextField;
import com.opencsv.CSVWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.assests.helper.Helper;
import sample.model.Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommandeController implements Initializable {
    private Command C = new Command();

    @FXML
    private TableView<Command> tab_Commandes;

    @FXML
    private TableColumn<Command, Integer> id_commande;

    @FXML
    private TableColumn<Command, String> client_col;

    @FXML
    private TableColumn<Command, String> produit_col;

    @FXML
    private TableColumn<Command, Double> prix_col;

    @FXML
    private TableColumn<Command, String> adress_col;

    @FXML
    private TableColumn<Command, Integer> quantite_col;

    @FXML
    private TableColumn<Command, String> status_col;

    @FXML
    private TableColumn<Command, Double> total_col;

    @FXML
    private TableColumn<Command, String> Date_col;
    
    @FXML
    private TableColumn<Command, Double> subtotal_col;


    @FXML
    private JFXTextField search;

    @FXML
    private Label Total;

    @FXML
    void exportcsv(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Csv files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(Total.getScene().getWindow());

        if (file != null) {
            try (FileWriter outputfile = new FileWriter(file);
                 CSVWriter writer = new CSVWriter(outputfile)) {

                String[] header = {"IDCommande", "ClientName", "Produit", "Prix", "Adress", "Quantite", "Status", "Total", "Date"};
                writer.writeNext(header);

                ObservableList<Command> list = C.ShowAllcommand();
                list.forEach(data -> {
                    String[] donnes = {data.getId() + "", data.getClient(), data.getProduit(), data.getPrix() + "", data.getAdresse(), data.getQuantite() + "", data.getStatus(), data.getTotal() + "", data.getDate()};
                    writer.writeNext(donnes);
                });

                Helper.Alert("Fichier Csv Sauvegardé avec succès");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void modicomm(ActionEvent event) throws IOException {
        
    }

    @FXML
    void Refresh(ActionEvent event) {
        updateTable();
        search.clear();
    }

    @FXML
    void searchB(ActionEvent event) {
    	Command C1 = new Command();
        search.resetValidation();
        if (search.getText().isEmpty()) {
            search.validate();
        } else {
            ObservableList<Command> searchResults = C1.search(search.getText());
            if (!searchResults.isEmpty()) {
                tab_Commandes.setItems(searchResults);
                Total.setText("Total Commandes : " + total(searchResults) + " Ariary");
            } else {
                Helper.Alert("Aucun résultat trouvé.");
            }
            search.clear();
        }

    }

    @FXML
    void supcom(ActionEvent event) {
    	 if (tab_Commandes.getSelectionModel().isEmpty()) {
             Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
             alert1.setHeaderText(null);
             alert1.setContentText("Veuillez sélectionner la commande à supprimer !!");
             alert1.showAndWait();
         } else {
             Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
             alert.setContentText("Voulez-vous vraiment supprimer ??");
             Optional<ButtonType> result = alert.showAndWait();
             Command c1 = new Command();

             if (result.isPresent() && result.get() == ButtonType.OK) {
                 ArrayList<Command> l = new ArrayList<>(tab_Commandes.getSelectionModel().getSelectedItems());
                 for (Command res : l) {
                     c1.SupprimerComm(res.getId());
                 }
                 tab_Commandes.setItems(C.ShowAllcommand());
                 Total.setText("Total Commandes : " + total(C.ShowAllcommand()) + " Ariary");
             }
         }

    }

    @FXML
    void newcommandes(ActionEvent event) throws IOException {
    	Stage master = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../../views/CommandeView/AjoutCommand.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root, 800, 550);
        master.setTitle("Nouveau commande");
        master.setScene(scene);
        master.setResizable(false);
        master.show();
    }
    
    private TableCell<Command, String> createBoldCell() {
        return new TableCell<Command, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if ("Sous-total".equals(item)) {
                        setStyle("-fx-font-weight: bold;"); // Mettre en gras
                    } else {
                        setStyle(""); // Réinitialiser le style
                    }
                }
            }
        };
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tab_Commandes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        id_commande.setCellValueFactory(new PropertyValueFactory<>("id"));
        client_col.setCellValueFactory(new PropertyValueFactory<>("client"));
        client_col.setCellFactory(col -> createBoldCell()); // Appliquer la cellule personnalisée
        produit_col.setCellValueFactory(new PropertyValueFactory<>("produit"));
        prix_col.setCellValueFactory(new PropertyValueFactory<>("prix"));
        adress_col.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        quantite_col.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        Date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        total_col.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        updateTable();
    }


    private void updateTable() {
        // Obtenir toutes les commandes
        ObservableList<Command> allCommands = C.ShowAllcommand();
        
        // Grouper par date
        Map<String, List<Command>> groupedCommands = new HashMap<>();
        for (Command command : allCommands) {
            String date = command.getDate(); // Supposons que la date est au format String
            groupedCommands.putIfAbsent(date, new ArrayList<>());
            groupedCommands.get(date).add(command);
        }

        // Remplir le tableau avec les commandes groupées et sous-totaux
        ObservableList<Command> displayedCommands = FXCollections.observableArrayList();
        for (Map.Entry<String, List<Command>> entry : groupedCommands.entrySet()) {
            String date = entry.getKey();
            List<Command> commandsOnDate = entry.getValue();
            
            double dailyTotal = 0;

            // Ajouter les commandes de la date
            for (Command command : commandsOnDate) {
                displayedCommands.add(command);
                dailyTotal += command.getTotal();
            }

            // Ajouter une ligne pour le sous-total
            Command subtotalCommand = new Command(); // Créer une nouvelle commande pour le sous-total
            subtotalCommand.setDate(date);
            subtotalCommand.setTotal(dailyTotal);
            subtotalCommand.setClient("Sous-total"); // Indique qu'il s'agit d'un sous-total
            subtotalCommand.setPrix(0.0); // Ou toute autre valeur pertinente
            subtotalCommand.setQuantite(0); // Ou toute autre valeur pertinente
            subtotalCommand.setStatus(""); // Pour les sous-totaux, vous pouvez laisser vide ou mettre une valeur

            displayedCommands.add(subtotalCommand);
        }

        // Remplir le tableau avec les commandes affichées
        tab_Commandes.setItems(displayedCommands);
        
        // Mettre à jour le total des commandes
        Total.setText("Total Commandes : " + total(displayedCommands) + " Ariary");
    }


    private String total(ObservableList<Command> list) {
        AtomicReference<Double> t = new AtomicReference<>(0.0);
        list.forEach(tab -> t.updateAndGet(v -> v + tab.getTotal()));
        return t + "";
    }
}
