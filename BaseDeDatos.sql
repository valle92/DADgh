CREATE DATABASE  IF NOT EXISTS `dad` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `dad`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dad
-- ------------------------------------------------------
-- Server version	5.5.5-10.1.30-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `boton`
--

DROP TABLE IF EXISTS `boton`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `boton` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_boton` int(11) NOT NULL,
  `marca_temporal` bigint(20) DEFAULT NULL,
  `valor` tinyint(4) DEFAULT NULL,
  `sesion` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sesion_idx` (`sesion`),
  CONSTRAINT `sesion2` FOREIGN KEY (`sesion`) REFERENCES `sesion` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boton`
--

LOCK TABLES `boton` WRITE;
/*!40000 ALTER TABLE `boton` DISABLE KEYS */;
INSERT INTO `boton` VALUES (13,2,101,1,4),(14,3,111,1,4),(15,1,137,1,4),(16,1,141,1,4);
/*!40000 ALTER TABLE `boton` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maquina`
--

DROP TABLE IF EXISTS `maquina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maquina` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alias` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maquina`
--

LOCK TABLES `maquina` WRITE;
/*!40000 ALTER TABLE `maquina` DISABLE KEYS */;
INSERT INTO `maquina` VALUES (1,'NodeMCU');
/*!40000 ALTER TABLE `maquina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `potenciometro`
--

DROP TABLE IF EXISTS `potenciometro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `potenciometro` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_potenciometro` int(11) NOT NULL,
  `marca_temporal` bigint(20) DEFAULT NULL,
  `valor` int(11) DEFAULT NULL,
  `sesion` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sesion_idx` (`sesion`),
  CONSTRAINT `sesion` FOREIGN KEY (`sesion`) REFERENCES `sesion` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `potenciometro`
--

LOCK TABLES `potenciometro` WRITE;
/*!40000 ALTER TABLE `potenciometro` DISABLE KEYS */;
INSERT INTO `potenciometro` VALUES (13,1,100,156,4),(14,1,110,553,4),(15,1,137,684,4);
/*!40000 ALTER TABLE `potenciometro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesion`
--

DROP TABLE IF EXISTS `sesion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sesion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tiempo_inicio` bigint(20) DEFAULT NULL,
  `tiempo_fin` bigint(20) DEFAULT NULL,
  `maquina` int(11) DEFAULT NULL,
  `usuario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `maquina_idx` (`maquina`),
  KEY `usuario_idx` (`usuario`),
  CONSTRAINT `maquina` FOREIGN KEY (`maquina`) REFERENCES `maquina` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `usuario` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesion`
--

LOCK TABLES `sesion` WRITE;
/*!40000 ALTER TABLE `sesion` DISABLE KEYS */;
INSERT INTO `sesion` VALUES (4,90,163,1,1);
/*!40000 ALTER TABLE `sesion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `apellidos` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Paco','Martinez');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-04 23:25:31
