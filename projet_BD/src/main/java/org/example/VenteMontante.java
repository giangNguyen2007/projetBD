package org.example;

import java.time.LocalDateTime;

public class VenteMontante extends Vente{

    public int currentPrixMax;
    public VenteMontante(int idVente, int prixDepart, Boolean revocabilite, int qteLot, int idSalleDeVente, int idProduit, LocalDateTime dateDepot) {
        super(idVente, prixDepart, revocabilite, qteLot, idSalleDeVente, idProduit, dateDepot);

        this.categorie = VenteCategorie.MONTANTE;
    }


}
