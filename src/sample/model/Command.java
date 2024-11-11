package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.assests.helper.Dbhandeler;

import java.sql.*;

public class Command extends Dbhandeler {
    // Attributs de la classe Command
    private int id;
    private String client;
    private String produit;
    private String adresse;
    private String status;
    private double prix;
    private int quantite;
    private double total;
    private int id_prod;
    private int id_client;
    private String date;

    // Constructeur avec paramètres
    public Command(String client, String produit, int quantite, String adresse, String statut, double total, String date, double prix) {
        this.client = client;
        this.produit = produit;
        this.quantite = quantite;
        this.adresse = adresse;
        this.status = statut;
        this.total = total;
        this.date = date;
        this.prix = prix;
    }

    // Constructeur par défaut
    public Command() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getId_prod() {
        return id_prod;
    }

    public void setId_prod(int id_prod) {
        this.id_prod = id_prod;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Méthode toString pour afficher les informations de la commande
    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", client='" + client + '\'' +
                ", produit='" + produit + '\'' +
                ", adresse='" + adresse + '\'' +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", total=" + total +
                ", id_prod=" + id_prod +
                ", id_client=" + id_client +
                '}';
    }

    // Insérer une nouvelle commande dans la base de données
    public void insert(Command C) {
        this.exequery("INSERT INTO commande(id_produit,idclient,adresse,quantite,statut,date_commande) VALUES(?,?,?,?,?,?)",
                C.getId_prod(), C.getId_client(), C.getAdresse(), C.getQuantite(), C.getStatus(), C.getDate());
    }

    // Afficher toutes les commandes
    public ObservableList<Command> ShowAllcommand() {
        ObservableList<Command> comm = FXCollections.observableArrayList();
        try (Connection con = this.Connect();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery("SELECT id_commande,prenom,nom,ProduitId,Libele,adresse,quantite,statut,date_commande,Prix FROM commande INNER JOIN produit ON ProduitId=id_produit INNER JOIN client ON idclient=id_client;")) {
             
            while (rs.next()) {
                Command C = new Command();
                C.setId(rs.getInt("id_commande"));
                C.setProduit(rs.getString("Libele"));
                C.setAdresse(rs.getString("adresse"));
                C.setQuantite(rs.getInt("quantite"));
                C.setStatus(rs.getString("statut"));
                C.setDate(rs.getString("date_commande"));
                C.setPrix(rs.getDouble("Prix"));
                C.setClient(rs.getString("nom") + " " + rs.getString("prenom"));
                C.setId_prod(rs.getInt("ProduitId"));
                C.setTotal(rs.getDouble("Prix") * rs.getInt("quantite"));
                comm.add(C);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return comm;
    }

    // Rechercher des commandes selon différents critères
    public ObservableList<Command> search(String S) {
        ObservableList<Command> comm = FXCollections.observableArrayList();
        try (Connection con = this.Connect();
             PreparedStatement pstm = con.prepareStatement("SELECT id_commande,c.prenom,c.nom,ProduitId,Libele,adresse,quantite,statut,date_commande,Prix FROM commande INNER JOIN produit ON produit.ProduitId=commande.id_produit INNER JOIN client AS c ON idclient=c.id_client WHERE id_commande = ? OR c.prenom=? OR c.nom=? OR date_commande=? OR Libele=? OR statut=? OR Prix=? OR quantite=?;")) {
             
            int id = 0, qnt = 0;
            double Prix = 0;

            try {
                id = Integer.parseInt(S);
                qnt = Integer.parseInt(S);
                Prix = Double.parseDouble(S);
            } catch (NumberFormatException e) {
                // Ignorer les exceptions de conversion
            }

            pstm.setInt(1, id);
            pstm.setString(2, S);
            pstm.setString(3, S);
            pstm.setString(4, S);
            pstm.setString(5, S);
            pstm.setString(6, S);
            pstm.setDouble(7, Prix);
            pstm.setInt(8, qnt);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()) {
                Command C = new Command();
                C.setId(rs.getInt("id_commande"));
                C.setProduit(rs.getString("Libele"));
                C.setAdresse(rs.getString("adresse"));
                C.setQuantite(rs.getInt("quantite"));
                C.setStatus(rs.getString("statut"));
                C.setDate(rs.getString("date_commande"));
                C.setPrix(rs.getDouble("Prix"));
                C.setClient(rs.getString("nom") + " " + rs.getString("prenom"));
                C.setId_prod(rs.getInt("ProduitId"));
                C.setTotal(rs.getDouble("Prix") * rs.getDouble("quantite"));
                comm.add(C);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return comm;
    }

    // Supprimer une commande par ID
    public void SupprimerComm(int id) {
        try (Connection con = this.Connect();
             PreparedStatement pstm = con.prepareStatement("DELETE FROM commande WHERE id_commande=?")) {
             
            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Enregistrer une nouvelle commande dans la base de données
    public void save() {
        try (Connection con = this.Connect()) {
            String query = "INSERT INTO commande(id_produit, idclient, adresse, quantite, statut, date_commande) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstm = con.prepareStatement(query)) {
                pstm.setInt(1, this.id_prod);
                pstm.setInt(2, this.id_client);
                pstm.setString(3, this.adresse);
                pstm.setInt(4, this.quantite);
                pstm.setString(5, this.status);
                pstm.setString(6, this.date);
                pstm.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Rechercher une commande par ID
    public Command searchob(String S) {
        Command C = new Command();
        try (Connection con = this.Connect();
             PreparedStatement pstm = con.prepareStatement("SELECT id_commande,c.prenom,c.nom,idclient,ProduitId,Libele,adresse,quantite,statut,date_commande,Prix FROM commande INNER JOIN produit ON produit.ProduitId=commande.id_produit INNER JOIN client AS c ON idclient=c.id_client WHERE id_commande = ?;")) {
             
            int id = 0;
            try {
                id = Integer.parseInt(S);
            } catch (NumberFormatException e) {
                id = 0;
            }

            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                C.setId(rs.getInt("id_commande"));
                C.setProduit(rs.getString("Libele"));
                C.setAdresse(rs.getString("adresse"));
                C.setQuantite(rs.getInt("quantite"));
                C.setStatus(rs.getString("statut"));
                C.setDate(rs.getString("date_commande"));
                C.setPrix(rs.getDouble("Prix"));
                C.setClient(rs.getString("nom") + " " + rs.getString("prenom"));
                C.setId_prod(rs.getInt("ProduitId"));
                C.setTotal(rs.getDouble("Prix") * rs.getDouble("quantite"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return C;
    }
}
