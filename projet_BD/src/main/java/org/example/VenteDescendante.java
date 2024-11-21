package org.example;

import java.time.LocalDateTime;

public class VenteDescendante extends Vente{

    public VenteDescendante(int idVente, int prixDepart, Boolean revocabilite, int qteLot, int idSalleDeVente, int idProduit, LocalDateTime dateDepot) {
        super(idVente, prixDepart, revocabilite, qteLot, idSalleDeVente, idProduit, dateDepot);
        this.categorie = VenteCategorie.DESCENDANTE;
    }
}
