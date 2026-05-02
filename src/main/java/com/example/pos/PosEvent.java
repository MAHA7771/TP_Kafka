package com.example.pos;

import java.util.List;

public class PosEvent {
    private String type;
    private String idCaisse;
    private String ville;
    private String timestamp;
    private Double montant;
    private List<String> produits;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIdCaisse() { return idCaisse; }
    public void setIdCaisse(String idCaisse) { this.idCaisse = idCaisse; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }
    public List<String> getProduits() { return produits; }
    public void setProduits(List<String> produits) { this.produits = produits; }
}