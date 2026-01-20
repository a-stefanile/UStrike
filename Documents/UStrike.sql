DROP DATABASE IF EXISTS ustrike_db;
CREATE DATABASE ustrike_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ustrike_db;

CREATE TABLE Cliente (
  IDCliente INT AUTO_INCREMENT PRIMARY KEY,
  NomeCliente VARCHAR(52) NOT NULL,
  CognomeCliente VARCHAR(52) NOT NULL,
  Email VARCHAR(255) NOT NULL,
  PasswordHash VARCHAR(255) NOT NULL,
  PuntiTicket INT NOT NULL DEFAULT 0,
  CONSTRAINT uq_cliente_email UNIQUE (Email)
) ENGINE=InnoDB;

CREATE TABLE Staff (
  IDStaff INT AUTO_INCREMENT PRIMARY KEY,
  NomeStaff VARCHAR(52) NOT NULL,
  CognomeStaff VARCHAR(52) NOT NULL,
  Email VARCHAR(255) NOT NULL,
  PasswordHash VARCHAR(255) NOT NULL,
  Ruolo ENUM('Bowling','GoKart') DEFAULT 'Bowling',
  CONSTRAINT uq_staff_email UNIQUE (Email)
) ENGINE=InnoDB;

CREATE TABLE Servizio (
  IDServizio INT AUTO_INCREMENT PRIMARY KEY,
  NomeServizio VARCHAR(52) NOT NULL,
  StatoServizio TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE Risorsa (
  IDRisorsa INT AUTO_INCREMENT PRIMARY KEY,
  Stato TINYINT(1) NOT NULL DEFAULT 1,
  Capacita INT NOT NULL,
  IDServizio INT NOT NULL,
  CONSTRAINT fk_risorsa_servizio FOREIGN KEY (IDServizio) REFERENCES Servizio(IDServizio)
) ENGINE=InnoDB;

CREATE TABLE Prenotazione (
  IDPrenotazione INT AUTO_INCREMENT PRIMARY KEY,
  Data TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  Orario TIMESTAMP NOT NULL,
  StatoPrenotazione ENUM('In attesa','Confermata','Rifiutata','Annullata') NOT NULL DEFAULT 'In attesa',
  Partecipanti VARCHAR(255) NOT NULL,
  IDServizio INT NOT NULL,
  IDRisorsa INT NOT NULL,
  IDCliente INT NOT NULL,
  IDStaff INT NULL,
  NoteStaff VARCHAR(255) DEFAULT NULL, -- Nuova colonna per il motivo
  CONSTRAINT fk_pren_servizio FOREIGN KEY (IDServizio) REFERENCES Servizio(IDServizio),
  CONSTRAINT fk_pren_risorsa  FOREIGN KEY (IDRisorsa)  REFERENCES Risorsa(IDRisorsa),
  CONSTRAINT fk_pren_cliente  FOREIGN KEY (IDCliente)  REFERENCES Cliente(IDCliente),
  CONSTRAINT fk_pren_staff    FOREIGN KEY (IDStaff)    REFERENCES Staff(IDStaff)
) ENGINE=InnoDB;

INSERT INTO Servizio (NomeServizio, StatoServizio)
VALUES 
('Bowling', 1),
('GoKart', 1);

INSERT INTO Staff (NomeStaff, CognomeStaff, Email, PasswordHash, Ruolo)
VALUES
('Mario', 'Rossi', 'admin.bowling@ustrike.staff.it', ('8f13f98497221ba4d9d8e8a6171da954:c1cf099caaf93d982fcea6aea3cd41795584440a712b02b18e09da10953a852c'), 'Bowling'),
('Luca', 'Bianchi', 'admin.gokart@ustrike.staff.it', ('46d932244931528f9846cbcbd15bd4f6:1e76d775ca8883cadc2779d4d88e0fe8e98591f57945d3147c8acc6b288eacda'), 'GoKart');

INSERT INTO Risorsa (Stato, Capacita, IDServizio)
VALUES
-- 7 piste da Bowling (capacità 6)
(1, 6, 1),
(1, 6, 1),
(1, 6, 1),
(1, 6, 1),
(1, 6, 1),
(1, 6, 1),
(1, 6, 1),
-- 1 pista Go-Kart (capacità 2)
(1, 15, 2);