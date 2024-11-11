package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import sample.assests.helper.Dbhandeler;

import java.sql.*;

public class Produit extends Dbhandeler {
    // Attributs
    private int id;
    private String libele;
    private int quantite;
    private Double prix;
    private String date; 
    private int id_cat; // Pour ajouter l'ID de catégorie à la base de données
    private String libele_cat; // Pour récupérer le libellé de la catégorie depuis la base de données

    // Constructeur
    public Produit() { }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLibele() { return libele; }
    public void setLibele(String libele) { this.libele = libele; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public int getId_cat() { return id_cat; }
    public void setId_cat(int id_cat) { this.id_cat = id_cat; }

    public String getLibele_cat() { return libele_cat; }
    public void setLibele_cat(String libele_cat) { this.libele_cat = libele_cat; }

    @Override
    public String toString() {
        return libele;  // Retourne le libellé du produit
    }

    // Méthodes pour interagir avec la base de données

    // Insérer un produit
    public void insert(Produit p) {
        this.exequery("INSERT INTO produit(Libele,Quantity,CategorieId,Prix) VALUES (?,?,?,?)",
                p.getLibele(), p.getQuantite(), p.getId_cat(), p.getPrix());
    }

    // Supprimer un produit
    public void SupprimerProd(int id) {
        this.exequery("DELETE FROM produit WHERE ProduitId=?", id);
    }

    // Mettre à jour un produit
    public void UPdate(int id, String lib, int qnt, Double prix, int idc) {
        this.exequery("UPDATE produit SET Libele=?, Quantity=?, Prix=?, CategorieId=? WHERE ProduitId=?",
                lib, qnt, prix, idc, id);
    }

    // Afficher tous les produits
    public ObservableList<Produit> ShowAllProduct() {
        ObservableList<Produit> prodList = FXCollections.observableArrayList();
        try (Connection con = this.Connect();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery("SELECT ProduitId, CategorieId, Libele, Quantity, LibeleCat, Prix " +
                     "FROM produit INNER JOIN Categorie ON produit.CategorieId = Categorie.CatId")) {

            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("ProduitId"));
                p.setLibele(rs.getString("Libele"));
                p.setQuantite(rs.getInt("Quantity"));
                p.setLibele_cat(rs.getString("LibeleCat"));
                p.setId_cat(rs.getInt("CategorieId"));
                p.setPrix(rs.getDouble("Prix"));
                prodList.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return prodList;
    }

    // Rechercher un produit par ID
    public Produit searchob(int id) {
        Produit p = new Produit();
        try (Connection con = this.Connect();
             PreparedStatement pstm = con.prepareStatement("SELECT ProduitId, CategorieId, Libele, Quantity, LibeleCat, Prix " +
                     "FROM produit INNER JOIN Categorie ON produit.CategorieId = Categorie.CatId WHERE ProduitId=?")) {

            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getInt("ProduitId"));
                    p.setLibele(rs.getString("Libele"));
                    p.setQuantite(rs.getInt("Quantity"));
                    p.setLibele_cat(rs.getString("LibeleCat"));
                    p.setId_cat(rs.getInt("CategorieId"));
                    p.setPrix(rs.getDouble("Prix"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return p;
    }

    // Méthode pour afficher les produits disponibles
    public ObservableList<Produit> ShowAllProductQ() {
        ObservableList<Produit> prodList = FXCollections.observableArrayList();
        try (Connection con = this.Connect();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery("SELECT ProduitId, CategorieId, Libele, Quantity, LibeleCat, Prix " +
                     "FROM produit INNER JOIN Categorie ON produit.CategorieId = Categorie.CatId WHERE produit.Quantity > 0")) {

            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("ProduitId"));
                p.setLibele(rs.getString("Libele"));
                p.setQuantite(rs.getInt("Quantity"));
                p.setLibele_cat(rs.getString("LibeleCat"));
                p.setId_cat(rs.getInt("CategorieId"));
                p.setPrix(rs.getDouble("Prix"));
                prodList.add(p);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return prodList;
    }
    
    public ObservableList<Produit> SearchMulti(String S){
        ObservableList<Produit>prod= FXCollections.observableArrayList();
        try{
            Connection con=this.Connect();
            Statement stm=con.createStatement();
            PreparedStatement pstm=con.prepareStatement("SELECT ProduitId,CategorieId,Libele,Quantity,LibeleCat,Prix from produit inner join Categorie on Categorie.CatId=produit.CategorieId where ProduitId=? or Libele=? or Quantity=? or LibeleCat=? or Prix=? ");
            int id;
            try {
                id=Integer.parseInt(S);

            }catch (NumberFormatException e){
                id=0;
            }
            pstm.setInt(1, id);
            pstm.setString(2, S);
            pstm.setString(3, S);
            pstm.setString(4, S);
            pstm.setString(5, S);
            ResultSet rs=pstm.executeQuery();
            while(rs.next()){
                Produit P =new Produit();
                P.setId(rs.getInt("ProduitId"));
                P.setLibele(rs.getString("Libele"));
                P.setQuantite(rs.getInt("Quantity"));
                P.setLibele_cat(rs.getString("LibeleCat"));
                P.setId_cat(rs.getInt("CategorieId"));
                P.setPrix(rs.getDouble("Prix"));
                prod.add(P);
            }
            stm.close();
            con.close();
        }catch (Exception e){ System.out.println(e.toString()); }

        return prod;
    }

    // Vérifier la quantité et mettre à jour après une commande
    public Boolean SelectQunt(int id, int quan) {
        int q = 0;
        boolean valid = false;
        try (Connection con = this.Connect();
             PreparedStatement pstm = con.prepareStatement("SELECT Quantity FROM produit WHERE ProduitId=?")) {

            pstm.setInt(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    q = rs.getInt("Quantity");
                }
            }

            if (quan <= q) {
                this.exequery("UPDATE produit SET Quantity=? WHERE ProduitId=?", (q - quan), id);
                valid = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return valid;
    }

    // Méthode pour générer des données de graphique en camembert (PieChart)
    public ObservableList<PieChart.Data> chrats() {
        ObservableList<PieChart.Data> piData = FXCollections.observableArrayList();
        try (Connection con = this.Connect();
             Statement stm = con.createStatement();
             PreparedStatement pstm = con.prepareStatement("SELECT DISTINCT Libele FROM commande " +
                     "INNER JOIN produit ON produit.ProduitId = commande.id_produit " +
                     "INNER JOIN client ON idclient = client.id_client")) {

            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                String lib = rs.getString("Libele");
                try (PreparedStatement pst = con.prepareStatement("SELECT SUM(quantite) FROM commande " +
                        "INNER JOIN produit ON produit.ProduitId = commande.id_produit " +
                        "WHERE Libele=?")) {

                    pst.setString(1, lib);
                    ResultSet rss = pst.executeQuery();
                    if (rss.next()) {
                        int val = rss.getInt("SUM(quantite)");
                        piData.add(new PieChart.Data(lib, val));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return piData;
    }
}
