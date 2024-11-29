package org.example;

import org.example.exceptions.CustomInputException;

import java.sql.SQLException;
import java.util.Scanner;

public class Interface {

    private static String email;

    private static String[] commands;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(" Entrez votre email utilisateur : ");

        Interface.email = scanner.nextLine();
//        System.out.println(" entrez une commande : ");
//        input = scanner.nextLine();
//        String[] parties = input.trim().split("\\s+");
//        String command = parties[0].toLowerCase();

        String input = "";
        String first_command = "";
        String[] parties = new String[0];

        while (!first_command.equals("stop")) {

            switch (first_command) {

                case "afficher_toutes_ventes":
                    if (parties.length == 1) {
                        try {
                            Utility.afficheToutesVentes();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;

                case "afficher_mes_produits":
                    if (parties.length == 1) {
                        try {
                            Utility.getProduitsParUser(Interface.email);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;

                case "afficher_ventes_pour_produit":
                    if (parties.length == 2) {
                        try {
                            int idProduit = Integer.parseInt(parties[1]);
                            Vente.chercheTousVentesParProduit(idProduit);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    break;

                case "changer_email":
                    if (parties.length == 2) {
                        try {
                            Interface.email = parties[1];
                            System.out.printf("Email est change a %s. \n", Interface.email);

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
                            v.checkAuteur(Interface.email);
                            v.chercheTousOffresParVente();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
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
                            v.checkAuteur(Interface.email);
                            v.calculGagnant();
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException | CustomInputException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    break;

                // afficherVente idVente
                case "afficher_ventes":
                    if (parties.length > 1) {
                        try {
                            int idVente = Integer.parseInt(parties[1]);
                            System.out.println("afficher les caractéristiques de la vente numéro : " + idVente);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        }

                    }
                    break;

                case "commit_vente":
                    if (parties.length == 2) {
                        try {
                            int idVente = Integer.parseInt(parties[1]);
                            Vente vente = Vente.chercheVenteParId(idVente);
                            System.out.printf("Supprime vente No %d et reduire le stock du produit No %d par %d: \n", vente.idVente, vente.idProduit, vente.quteVendu);

                            Utility.deleteVente(idVente);
                            Utility.updateProduitStock(vente.quteVendu, vente.idProduit);

                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le numéro de la vente.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
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
                            System.out.println("Offre de " + prixOffre + " euros/unite pour la vente numéro " + idVente +" sur " + qteAchat + " unités du produit par " + Interface.email);
                            Offre.insertNewOffre(idVente, Interface.email, prixOffre, qteAchat);
                        } catch (NumberFormatException e) {
                            System.out.println("Paramètres invalides. Veuillez entrer le prix et la quantité de produit.");
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
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
            Interface.commands = parties;
            first_command = parties[0].toLowerCase();

        }
    }
}
