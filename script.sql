CREATE DATABASE  IF NOT EXISTS `clojure` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `clojure`;
-- MySQL dump 10.13  Distrib 8.0.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: clojure
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `name` varchar(45) NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (21,'Jos jedna'),(23,'Kategorija'),(1,'Laptops'),(2,'Mobile phones'),(22,'Nova kategorija'),(19,'Tablets');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderlines`
--

DROP TABLE IF EXISTS `orderlines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderlines` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `orderId` int(11) NOT NULL,
                              `productName` varchar(100) NOT NULL,
                              `quantity` int(11) NOT NULL,
                              `price` decimal(10,0) NOT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FK_ORDERLINE_ORDER_idx` (`orderId`),
                              CONSTRAINT `FK_ORDERLINE_ORDER` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderlines`
--

LOCK TABLES `orderlines` WRITE;
/*!40000 ALTER TABLE `orderlines` DISABLE KEYS */;
INSERT INTO `orderlines` VALUES (24,21,'OMEN X by HP Dual Screen',4,639),(25,21,'HP Notebook',2,335);
/*!40000 ALTER TABLE `orderlines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `dateCreated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `userId` int(11) NOT NULL,
                          `address` varchar(255) NOT NULL,
                          `firstName` varchar(45) NOT NULL,
                          `lastName` varchar(45) NOT NULL,
                          `email` varchar(45) NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK_ORDER_USERS_idx` (`userId`),
                          CONSTRAINT `FK_ORDER_USERS` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (21,'2019-09-15 22:44:48',19,'Stanka Paunovica 3','Milica','Krstic','milica.cica7@gmail.com');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `name` varchar(45) NOT NULL,
                            `price` decimal(10,0) NOT NULL,
                            `description` varchar(255) DEFAULT NULL,
                            `image` varchar(255) NOT NULL,
                            `categoryId` int(11) DEFAULT NULL,
                            `isNew` tinyint(4) NOT NULL DEFAULT '0',
                            `isBestSeller` tinyint(4) NOT NULL DEFAULT '0',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `name_UNIQUE` (`name`),
                            KEY `fk_product_category_idx` (`categoryId`),
                            CONSTRAINT `fk_product_category` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (22,'COOLPAD N3D (Crni)',100,' Dijagonala ekrana: 5.45\"\r\nRAM memorija: 2 GB\r\nZadnja kamera: 8.0 Mpix + 2.0 Mpix\r\nInterna memorija: 16 GB\r\nKapacitet baterije: 2500 mAh','20190524140229_12488.jpg',2,0,1),(32,'HP Notebook',335,' Power through your day with a stylish laptop created to keep you connected, and on top of everyday tasks. With reliableperformance and long lasting battery life—you can easily surf, stream and stay in touch with what matters most. ','4US43EA_190526205228192.png',1,1,1),(33,'OMEN X by HP Dual Screen',639,'Reap the benefits of a dual-monitor setup from anywhere with an industry-first 6” 1080p touchscreen mounted above the keyboard. Allows you to seamlessly display and interact with other applications without interrupting gameplay.','dims.jpg',1,1,1),(34,'Apple iPad 9.7\" WiFi+Cellular 32GB (Gold) ',540,'The new iPad combines the power and capability of a computer with the ease of use and versatility you’d never expect from one.','UHF5OsYPAnO.jpg',19,1,0),(35,'HONOR View 20 ',400,' Dijagonala ekrana: 6.4\"\r\nRAM memorija: 8 GB\r\nZadnja kamera: 48.0 Mpix\r\nInterna memorija: 256 GB\r\nKapacitet baterije: 4000 mAh  ','a08df32d4128a3a3b9372bdda9d3.jpg',2,0,1),(36,'NOKIA 216 Dual SIM',49,'  Dijagonala ekrana: 2.4\"\r\nZadnja kamera: 0.3 Mpix\r\nKapacitet baterije: 1020 mAh ','t_16853.png',2,0,0),(37,'CAT B25 Dual SIM',80,' Dijagonala ekrana: 2.0\"\r\nZadnja kamera: 2.0 Mpix\r\nKapacitet baterije: 1300 mAh','0001798_caterpillar-cat-b25-dual-sim-black_610.jpeg',2,0,0),(38,'APPLE iPad Air 10.5',749,' Dijagonala ekrana: 10.5\"\r\nBroj jezgara: Šest jezgara\r\nRAM memorija: 3GB\r\nInternet konekcija: WiFi','l_10191663_002.jpg',2,0,0),(39,'NOKIA 2 ',110,' Dijagonala ekrana: 5.0\"\r\nRAM memorija: 1 GB\r\nZadnja kamera: 8.0 Mpix\r\nInterna memorija: 8 GB\r\nKapacitet baterije: 4100 mAh','Nokia-2-Black-new.jpg',2,1,0),(40,'HUAWEI Mate 20 Lite',299,'  \r\nDijagonala ekrana: 6.3\"\r\nRAM memorija: 4 GB\r\nZadnja kamera: 20.0 Mpix + 2.0 Mpix\r\nInterna memorija: 64 GB\r\nKapacitet baterije: 3750 mAh ','huawei-mate-20-lite-zlatni-mobilni-6-3-quot-octa-core-4x2-2ghz-4x1-7ghz-4gb-64gb-20mpx-2mpx-dual-sim_MNnyG_3.jpg',2,0,0),(41,'SAMSUNG GALAXY S9+ G965F',840,' Dijagonala ekrana: 6.2\"\r\nRAM memorija: 6 GB\r\nZadnja kamera: 12.0 Mpix + 12.0 Mpix\r\nInterna memorija: 64 GB\r\nKapacitet baterije: 3500 mAh','f59ffd94b457c1e502cd7aea81dfa9d8.jpg_340x340q80.jpg_.webp',2,0,0),(42,'APPLE iPhone XS Max',1900,' Dijagonala ekrana: 6.5\"\r\nRAM memorija: 4 GB\r\nZadnja kamera: 12.0 Mpix + 12.0 Mpix\r\nInterna memorija: 256 GB\r\nKapacitet baterije: 3180 mAh','индекс.jpg',2,1,0),(43,'HUAWEI Mediapad T3 8',149,' Dijagonala ekrana: 8\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 2GB\r\nInternet konekcija: WiFi','8542546_R_Z001A.jpg',19,0,1),(44,'HUAWEI MediaPad T3 7 Kids 7',120,' Dijagonala ekrana: 7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 1GB\r\nInternet konekcija: WiFi','huawei_mediapad_t3_7_kids_8gb_wifi_only_tablet_-_grey_lowest_price_in_kuwait.jpg',19,0,0),(45,'MLS Alu Plus 4G ',159,' Dijagonala ekrana: 10.1\"\r\nBroj jezgara: Osam jezgara\r\nRAM memorija: 2GB\r\nInternet konekcija: 4G/WiFi','8619477.jpg',19,0,0),(46,'APPLE iPad 6 9.7',539,' Dijagonala ekrana: 9.7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 2GB\r\nInternet konekcija: WiFi','ipad-6th-gen-early-2018.jpg',19,0,0),(47,'VIVAX TPC-101 3G 10.1',148,' \r\nDijagonala ekrana: 10.1\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 2GB\r\nInternet konekcija: 3G/WiFi','vivax-tpc-101-3g-slika-3_1496_843.jpeg',19,1,0),(49,'APPLE iPad 6',479,'Dijagonala ekrana: 9.7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 2GB\r\nInternet konekcija: WiFi','61Crw4y3q7L._SX466_.jpg',19,0,0),(51,'VIVAX TPC-101',150,' Dijagonala ekrana: 10.1\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 2GB\r\nInternet konekcija: 3G/WiFi','b9b8ba14c3226102bc8fb5910985196c.jpg',19,0,0),(52,'eSTAR Themed Tablet HERO AVENGERS 7',79,' Dijagonala ekrana: 7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 1GB\r\nInternet konekcija: WiFi','avengers.png',19,0,0),(53,'INCESS 7',79,' Dijagonala ekrana: 7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 1GB\r\nInternet konekcija: WiFi','image5b6177ed58d30.png',19,1,1),(54,'eSTAR Themed Tablet HERO FROZEN',79,' \r\nDijagonala ekrana: 7\"\r\nBroj jezgara: Četiri jezgra\r\nRAM memorija: 1GB\r\nInternet konekcija: WiFi','image5b617813f0e61.png',19,0,0),(55,'HP EliteBook x360 1030 G3',1089,' Model procesora: Intel® Core™ i5 8350U...\r\nDijagonala ekrana: 13.3\"\r\nHDD: 256GB SSD\r\nRAM memorija: 8GB','c06060103_1750x1285.jpg',1,0,0),(56,'HP Spectre x360 ',1300,' Model procesora: Intel® Core™ i5 8250U...\r\nDijagonala ekrana: 13.3\"\r\nHDD: 256GB SSD\r\nRAM memorija: 8GB','hero-img.jpg',1,1,0),(57,'HP 15-ra038nm',329,' Model procesora: Intel® Celeron® N3060...\r\nDijagonala ekrana: 15.6\"\r\nHDD: 500GB HDD\r\nRAM memorija: 4GB','hp-15-ra038nm-3qt59ea-laptop-15-6-quot-hd-intel-celeron-n3060-4gb-500gb-intel-hd-400-dvd-rw-win10-crni-4-cell_m2Qka_3.jpg',1,0,0),(58,'LENOVO IdeaPad 520',640,' Model procesora: Intel® Core™ i5 8250U...\r\nDijagonala ekrana: 15.6\"\r\nHDD: 256GB SSD\r\nRAM memorija: 8GB','71BQPwAHa-L._SL1500_.jpg',1,0,1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `firstName` varchar(45) NOT NULL,
                         `lastName` varchar(45) NOT NULL,
                         `email` varchar(45) NOT NULL,
                         `address` varchar(255) DEFAULT NULL,
                         `password` varchar(100) NOT NULL,
                         `username` varchar(15) NOT NULL,
                         `isAdmin` bit(1) NOT NULL DEFAULT b'0',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `username_UNIQUE` (`username`),
                         UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (3,'Admin','Admin','admin@admin.com','Admina Adminovica 532','sifra1','admin',_binary ''),(13,'Milica','Krstic','cokka88@gmail.com','Stanka Paunovica Veljka 41','sifra1','cokka',_binary '\0'),(19,'Milica','Krstic','milica.cica7@gmail.com','Stanka Paunovica 32','sifra1','milica',_binary '\0');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-16 21:27:57