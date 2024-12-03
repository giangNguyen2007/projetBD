package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class MyInterface {

    public static String email;

    private static String[] commands;

    public MyInterface() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(" Entrez votre email utilisateur : ");

        MyInterface.email = scanner.nextLine();
        System.out.printf("Connecte en tant que %s \n", MyInterface.email);
//        System.out.println(" entrez une commande : ");
//        input = scanner.nextLine();
//        String[] parties = input.trim().split("\\s+");
//        String command = parties[0].toLowerCase();

        String input = "";
        String first_command = "";
        String[] parties = new String[0];

        while (!first_command.equals("quit")) {

            switch (first_command) {

                case "afficher_toutes_ventes":
                    if (parties.length == 1) {
                        try {
                            Utility.afficheToutesVentes();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "afficher_mes_produits":
                    if (parties.length == 1) {
                        try {
                            Utility.getProduitsParUser(MyInterface.email);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "nouveau_produit":
                    if (parties.length == 5) {

                        try {
                            String nomProduit = parties[1];
                            int prixRevient = Integer.parseInt(parties[2]);
                            int stock = Integer.parseInt(parties[3]);
                            String categorie = parties[4];

                            Utility.insertNewProduit(MyInterface.email, nomProduit, prixRevient, stock, categorie);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "creer_salle":
                    if (parties.length == 2) {
                        try {

                            String nomCategorie = parties[1];

                            Utility.insertNewSalle(nomCategorie);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "creer_categorie":
                    if (parties.length == 3) {
                        try {

                            String nomCategorie = parties[1];
                            String description = parties[2];

                            Utility.insertNewCategorie(nomCategorie, description);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "afficher_salles":
                    if (parties.length == 1) {
                        try {

                            Utility.afficherSalles();

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "mettre_produit_en_vente":
                    if (parties.length == 8) {
                        try {
                            int idProduit = Integer.parseInt(parties[1]);
                            int prixDepart = Integer.parseInt(parties[2]);
                            int qteLot = Integer.parseInt(parties[3]);
                            int sallVente = Integer.parseInt(parties[4]);
                            String revocabilite = parties[5];
                            String nbOffre = parties[6];
                            String type_vente = parties[7];


//                            if (revocabilite != "Y" && revocabilite != "N"){ throw new CustomInputException("revocabilite doit etre Y ou N");}
//                            if (nbOffre != "M" && nbOffre != "U"){ throw new CustomInputException("nbOffre doit etre U ou M");}
//                            if (type_vente != "montante" && type_vente != "descendante"){ throw new CustomInputException("typeVente doit etre montante ou descendante");};

                            Boolean venteMontante = Boolean.TRUE;
                            if (Objects.equals(type_vente, "descenante")){ venteMontante = Boolean.FALSE ;}

                            int stock = Utility.getProduitStock(idProduit);
                            int current_lot_en_vente = Utility.findTotalQuteEnVenteProduit(idProduit);
                            if (current_lot_en_vente + qteLot > stock){
                                throw new CustomInputException("La total des lots mis en vente ne doit pas depasser le stock du produit");
                            }

                            Utility.checkAuteur(MyInterface.email, idProduit);

                            Utility.insertNewVente(prixDepart, revocabilite, nbOffre, qteLot, sallVente, idProduit, venteMontante);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "afficher_ventes_pour_produit":
                    if (parties.length == 2) {
                        try {
                            int idProduit = Integer.parseInt(parties[1]);
                            Utility.checkAuteur(MyInterface.email, idProduit);
                            Vente.chercheTousVentesParProduit(idProduit);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                case "changer_email":
                    if (parties.length == 2) {
                        try {
                            MyInterface.email = parties[1];
                            System.out.printf("Email est change a %s. \n", MyInterface.email);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        }

                    }
                    break;

                case "afficher_offres_par_vente":
                    if (parties.length == 2) {

                        int idVente = Integer.parseInt(parties[1]);

                        System.out.printf("Afficher offres pour vente numero %d \n", idVente);

                        try {
                            Vente v = Vente.chercheVenteParId(idVente);
                            assert v != null;
                            // Utility.checkAuteur(MyInterface.email, v.idProduit);
                            v.chercheTousOffresParVente();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());

                        }

                    }
                    break;

                case "calculer_resultat_vente":
                    if (parties.length == 2) {

                        int idVente = Integer.parseInt(parties[1]);

                        System.out.printf("Calculer le resultat deu vente nb %d \n", idVente);

                        try {
                            Vente v = Vente.chercheVenteParId(idVente);
                            //Utility.checkAuteur(MyInterface.email, v.idProduit);
                            v.calculGagnant();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;


                // supprimer vente et reduire le stock du produit
                case "commit_vente":
                    if (parties.length == 2) {
                        try {
                            int idVente = Integer.parseInt(parties[1]);
                            Vente vente = Vente.chercheVenteParId(idVente);
                            Utility.checkAuteur(MyInterface.email, vente.idProduit);
                            vente.calculGagnant();
                            System.out.printf("Supprime vente No %d et reduire le stock du produit No %d par %d: \n", vente.idVente, vente.idProduit, vente.quteVendu);

                            //Utility.deleteOffresParVente(idVente);
                            //Utility.deleteVente(idVente, vente.produitStock - vente.quteVendu, vente.idProduit);


                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;

                // offre idVente prixOffre qteProduit
                case "offre":
                    if (parties.length == 4) {
                        try {
                            int idVente = Integer.parseInt(parties[1]);
                            int prixOffre = Integer.parseInt(parties[2]);
                            int qteAchat = Integer.parseInt(parties[3]);
                            System.out.println("Offre de " + prixOffre + " euros/unite pour la vente numéro " + idVente +" sur " + qteAchat + " unités du produit par " + MyInterface.email);

                            Vente v = Vente.chercheVenteParId(idVente);

                            v.insertNewOffre(MyInterface.email, prixOffre, qteAchat);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le prix et la quantité de produit.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    else {System.out.println("Paramètres invalides. Veuillez entrer dans l'ordre le numéro de la vente, le prix que vous proposez et la quantité de produit que vous voulez acheter.");}
                    break;

                // offre idVente prixOffre qteProduit
                case "revoquer_vente":
                    if (parties.length == 2) {
                        try {
                            int idVente = Integer.parseInt(parties[1]);

                            System.out.println("revoquer vente: supprimer toutes offres pour vente et recommencer du debut \n");

                            Utility.deleteOffresParVente(idVente);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le prix et la quantité de produit.");
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    else {System.out.println("Paramètres invalides. Veuillez entrer dans l'ordre le numéro de la vente, le prix que vous proposez et la quantité de produit que vous voulez acheter.");}
                    break;

                case "check_email":
                    if (parties.length == 1) {
                        System.out.printf("current email : %s \n", MyInterface.email);
                    }
                    else {System.out.println("Paramètres invalides. Veuillez entrer dans l'ordre le numéro de la vente, le prix que vous proposez et la quantité de produit que vous voulez acheter.");}
                    break;
            }

            scanner = new Scanner(System.in);
//            System.out.println(" Entrez une commande :");

            System.out.printf("\n");

            System.out.printf(">> ");

            input = scanner.nextLine();
            parties = input.trim().split("\\s+");
            MyInterface.commands = parties;
            first_command = parties[0].toLowerCase();

        }
    }
}
