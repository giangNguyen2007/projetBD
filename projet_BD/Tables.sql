CREATE TABLE Categorie (
                           NomCategorie varchar (30) NOT NULL ,
                           Description varchar(100) NOT NULL ,
                           PRIMARY KEY (NomCategorie )
);

INSERT INTO Categorie VALUES('Électronique', 'Produits électroniques et gadgets');
INSERT INTO Categorie VALUES('abcd', 'Produits électroniques et gadgets');
INSERT INTO Categorie VALUES('Mobilier', 'tables chaises chambres ');


CREATE TABLE SalleDeVente (
                              idSalleDeVente integer NOT NULL,
                              NomCategorie varchar (30) NOT NULL ,
                              PRIMARY KEY (idSalleDeVente),
                              FOREIGN KEY (NomCategorie) REFERENCES Categorie (NomCategorie)
);



INSERT INTO SalleDeVente (idSalleDeVente,NomCategorie) VALUES (1, 'Électronique');

CREATE TABLE Utilisateur (
                             Email varchar(50) NOT NULL ,
                             Nom varchar(30) NOT NULL ,
                             Prenom varchar(30) NOT NULL ,
                             Adresse varchar(50) NOT NULL ,
                             PRIMARY KEY (Email)
);

INSERT INTO Utilisateur (Email, Nom, Prenom, Adresse)
VALUES('johndoe@gmail.com', 'Doe', 'John', '123 Rue des Lilas, Paris');
INSERT INTO Utilisateur (Email, Nom, Prenom, Adresse)
VALUES('janedoe@yahoo.com', 'Doe', 'Jane', '456 Avenue des Champs, Lyon');
INSERT INTO Utilisateur (Email, Nom, Prenom, Adresse)
VALUES('paulmartin@hotmail.com', 'Martin', 'Paul', '789 Boulevard Haussmann, Marseille');

CREATE TABLE Produit (
                         idProduit number GENERATED BY DEFAULT AS IDENTITY,
                         NomProduit varchar (30) NOT NULL ,
                         PrixRevient integer NOT NULL CHECK ( PrixRevient >= 0),
                         Stock integer NOT NULL CHECK ( Stock >= 0) ,
                         Email varchar(50) NOT NULL ,
                         NomCategorie varchar (30) NOT NULL ,
                         PRIMARY KEY (idProduit ),
                         FOREIGN KEY (Email) REFERENCES Utilisateur (Email),
                         FOREIGN KEY (NomCategorie) REFERENCES Categorie (NomCategorie)
);
INSERT INTO Produit (NomProduit, PrixRevient, Stock, Email, NomCategorie)
VALUES('Smartphone XYZ', 200, 50, 'johndoe@gmail.com', 'Électronique');
INSERT INTO Produit (NomProduit, PrixRevient, Stock, Email, NomCategorie)
VALUES('Table basse', 75, 20, 'janedoe@yahoo.com', 'Mobilier');

CREATE TABLE DateHeure (
                           DateHeure TIMESTAMP(0) NOT NULL ,
                           PRIMARY KEY (DateHeure)
);


INSERT INTO DateHeure
VALUES (TO_TIMESTAMP('2023-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO DateHeure
VALUES (TO_TIMESTAMP('2023-11-18 15:30:45', 'YYYY-MM-DD HH24:MI:SS'));


INSERT INTO DateHeure
VALUES (TO_TIMESTAMP('2022-08-01 16:00:00', 'YYYY-MM-DD HH24:MI:SS'));

CREATE TABLE Vente (
                       idVente integer GENERATED BY DEFAULT AS IDENTITY,
                       PrixDepart FLOAT NOT NULL CHECK (PrixDepart > 0),
                       Revocabilite CHAR(1) CHECK (Revocabilite IN ('Y','N')),
                       NbreOffre CHAR(1) CHECK (NbreOffre IN ('M','U')),
                       QuantiteLot integer NOT NULL CHECK ( QuantiteLot >= 0),
                       idSalleDeVente integer NOT NULL,
                       idProduit integer NOT NULL ,
                       DateHeure TIMESTAMP(0) NOT NULL ,
                       PRIMARY KEY (idVente),
                       FOREIGN KEY (idSalleDeVente) REFERENCES SalleDeVente (idSalleDeVente),
                       FOREIGN KEY (idProduit) REFERENCES Produit (idProduit),
                       FOREIGN KEY (DateHeure) REFERENCES DateHeure (DateHeure)
);

INSERT INTO Vente ( PrixDepart, Revocabilite, NbreOffre, QuantiteLot, idSalleDeVente, idProduit, DateHeure)
VALUES
(210, 'Y', 'M', 10, 1, 1, TO_TIMESTAMP('2023-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'));

INSERT INTO Vente (PrixDepart, Revocabilite, NbreOffre, QuantiteLot, idSalleDeVente, idProduit, DateHeure)
VALUES
    (1000, 'N', 'M', 5, 1, 2, TO_TIMESTAMP('2023-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'));


-------------------- SOU TYPAGE VENTE -----------------------------------------------------

CREATE TABLE VenteLibre (
                            idVente integer NOT NULL ,
                            delai integer NOT NULL CHECK ( delai >= 0) ,
                            PRIMARY KEY (idVente),
                            FOREIGN KEY (idVente) REFERENCES Vente (idVente)
);
INSERT INTO VenteLibre (idVente,delai) VALUES(1,10);


CREATE TABLE VenteLimitee (
                              idVente integer NOT NULL ,
                              DateHeure TIMESTAMP(0) NOT NULL ,
                              PRIMARY KEY (idVente),
                              FOREIGN KEY (DateHeure) REFERENCES DateHeure (DateHeure),
                              FOREIGN KEY (idVente) REFERENCES Vente (idVente)
);
INSERT INTO VenteLimitee (idVente,DateHeure) VALUES(2,TO_TIMESTAMP('2023-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'));


CREATE TABLE VenteMontante (
                               idVente integer NOT NULL ,
                               PRIMARY KEY (idVente),
                               FOREIGN KEY (idVente) REFERENCES Vente (idVente)
);
INSERT INTO VenteMontante (idVente) VALUES(1);

CREATE TABLE VenteDescendante (
                                  idVente integer NOT NULL ,
                                  PrixReduction  integer NOT NULL CHECK ( PrixReduction >= 0) ,
                                  PRIMARY KEY (idVente),
                                  FOREIGN KEY (idVente) REFERENCES Vente (idVente)
);
INSERT INTO VenteDescendante (idVente,PrixReduction) VALUES(2,100);

--------------- OFFRE -------------------------------------------

CREATE TABLE Offre (
                       idVente integer NOT NULL,
                       DateHeure TIMESTAMP(0) NOT NULL ,
                       Email varchar(50) NOT NULL ,
                       PrixOffre  integer NOT NULL CHECK ( PrixOffre >= 0) ,
                       QuantiteAchat integer NOT NULL CHECK ( QuantiteAchat >= 0),
                       PRIMARY KEY(idVente,DateHeure,Email),
                       FOREIGN KEY (idVente) REFERENCES Vente (idVente),
                       FOREIGN KEY (DateHeure) REFERENCES DateHeure (DateHeure),
                       FOREIGN KEY (Email) REFERENCES Utilisateur (Email)
);
INSERT INTO Offre (idVente, DateHeure, Email, PrixOffre, QuantiteAchat) VALUES
(1, TO_TIMESTAMP('2023-11-19 15:30:45', 'YYYY-MM-DD HH24:MI:SS'), 'johndoe@gmail.com', 220, 1);
INSERT INTO Offre (idVente, DateHeure, Email, PrixOffre, QuantiteAchat) VALUES
(1, TO_TIMESTAMP('2023-11-18 15:30:45', 'YYYY-MM-DD HH24:MI:SS'), 'paulmartin@hotmail.com', 230, 1);
INSERT INTO Offre (idVente, DateHeure, Email, PrixOffre, QuantiteAchat) VALUES
(2, TO_TIMESTAMP('2022-08-01 16:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'janedoe@yahoo.com', 75, 2);

CREATE TABLE Caracteristiques(
                                 idProduit integer NOT NULL ,
                                 NomCar varchar (20) NOT NULL ,
                                 ValCar varchar (20) NOT NULL ,
                                 PRIMARY KEY(idProduit,NomCar),
                                 FOREIGN KEY (idProduit) REFERENCES Produit (idProduit)
);
INSERT INTO Caracteristiques (idProduit, NomCar, ValCar) VALUES
(1, 'Couleur', 'Rouge');
INSERT INTO Caracteristiques (idProduit, NomCar, ValCar) VALUES
(1, 'Capac_stockage', '128 Go');
INSERT INTO Caracteristiques (idProduit, NomCar, ValCar) VALUES
(2, 'Matériau', 'Bois');
INSERT INTO Caracteristiques (idProduit, NomCar, ValCar) VALUES
(2, 'Forme', 'Rectangulaire');
