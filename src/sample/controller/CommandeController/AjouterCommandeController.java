package sample.controller.CommandeController;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Command;
import sample.model.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AjouterCommandeController {

    @FXML
    private JFXTextField clientName;  // Nouveau champ pour le nom du client

    @FXML
    private JFXComboBox<Produit> allproduit; // Pour la sélection des produits
    @FXML
    private JFXTextField quantity;
    @FXML
    private JFXDatePicker datech;
    @FXML
    private JFXTextField adresse;
    @FXML
    private JFXComboBox<String> status;

    @FXML
    private TableView<Command> tab_Panier;
    @FXML
    private TableColumn<Command, String> clientPanier_col;
    @FXML
    private TableColumn<Command, String> produitPanier_col;
    @FXML
    private TableColumn<Command, Integer> quantitePanier_col;
    @FXML
    private TableColumn<Command, String> adressPanier_col;
    @FXML
    private TableColumn<Command, String> statusPanier_col;
    @FXML
    private TableColumn<Command, Double> totalPanier_col;
    @FXML
    private TableColumn<Command, String> datePanier_col;

    @FXML
    private TableColumn<Command, Double> prixPanier_col;  // Nouvelle colonne pour le prix du produit



    @FXML
    private JFXTextField Total_col;

    private ObservableList<Command> panier = FXCollections.observableArrayList();
    
    public void SetStatus(String b){
        if(b.equals("En attend")){
            //System.out.println(b);
            status.getSelectionModel().select(0);
        }if(b.equals("Livree")){

            status.getSelectionModel().select(1);
        }if(b.equals("Annulee")) {
            status.getSelectionModel().select(2);
        }
    }

    @FXML
    public void initialize() {
    	Produit P=new Produit();
        allproduit.setItems(P.ShowAllProduct()); //! A consulter
        status.getItems().addAll("En attente", "Livree", "Annulee");
        // Initialiser les colonnes du tableau avec les valeurs de la commande
        clientPanier_col.setCellValueFactory(new PropertyValueFactory<>("client"));
        produitPanier_col.setCellValueFactory(new PropertyValueFactory<>("produit"));
        prixPanier_col.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantitePanier_col.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        adressPanier_col.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        statusPanier_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        datePanier_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalPanier_col.setCellValueFactory(new PropertyValueFactory<>("total"));
    }
    @FXML
    void AddToPanier(ActionEvent event) {
        // Vérifier que tous les champs sont remplis avant d'ajouter au panier
        if (clientName.getText().isEmpty() || allproduit.getValue() == null || quantity.getText().isEmpty() ||
            datech.getValue() == null || adresse.getText().isEmpty() || status.getValue() == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        try {
            String client = clientName.getText();  // Récupérer le nom du client
            Produit produit = allproduit.getValue();
            int quantite = Integer.parseInt(quantity.getText());
            double prix = produit.getPrix();  // Récupérer le prix du produit
            String addr = adresse.getText();
            String stat = status.getValue();
            LocalDate date = datech.getValue();
            double total = prix * quantite;  // Calculer le total basé sur le prix et la quantité

            // Créer une nouvelle commande et la remplir avec les valeurs des champs
            Command commande = new Command(client, produit.getLibele(), quantite, addr, stat, total, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), prix);
            
            // Ajouter la commande dans la liste du panier
            panier.add(commande);

            // Mettre à jour le tableau avec les nouvelles données du panier
            tab_Panier.setItems(panier);

            // Mettre à jour le total global
            updateTotal();
            
            clearFields(); // Réinitialiser les champs après l'ajout au panier

        } catch (NumberFormatException e) {
            showAlert("Quantité invalide.");
        }
    }

    @FXML
    void SavePanier(ActionEvent event) {
        for (Command cmd : panier) {
            // Ajouter la commande dans la base de données (logique d'insertion)
            cmd.save();  // Cette méthode devrait contenir la logique d'insertion dans la base
        }
        panier.clear(); // Vider le panier après enregistrement
        tab_Panier.setItems(panier);  // Réinitialiser le tableau après sauvegarde
        updateTotal(); // Réinitialiser le total
    }

    private void updateTotal() {
        double total = panier.stream().mapToDouble(Command::getTotal).sum();
        Total_col.setText(String.format("%.2f Ar", total));
    }

    private void clearFields() {
        clientName.clear();
        allproduit.setValue(null);
        quantity.clear();
        adresse.clear();
        status.setValue(null);
        datech.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
