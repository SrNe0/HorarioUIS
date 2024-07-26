-- Eliminar la base de datos preexistente
DROP DATABASE IF EXISTS horario_uis;

-- Crear la base de datos 'horario_uis'
CREATE DATABASE IF NOT EXISTS horario_uis;
USE horario_uis;

-- MySQL dump 10.13  Distrib 8.0.37, for Linux (x86_64)
--
-- Host: localhost    Database: horario_uis
-- ------------------------------------------------------
-- Server version	8.0.37-0ubuntu0.20.04.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accion`
--

DROP TABLE IF EXISTS `accion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accion` (
  `id_accion` bigint NOT NULL AUTO_INCREMENT,
  `nombre_accion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_accion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asignatura`
--

DROP TABLE IF EXISTS `asignatura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignatura` (
  `id_asignatura` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) DEFAULT NULL,
  `horas_practica` int DEFAULT NULL,
  `horas_teoria` int DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `necesita_computadores` BOOLEAN DEFAULT NULL,
  PRIMARY KEY (`id_asignatura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `aula`
--

DROP TABLE IF EXISTS `aula`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aula` (
  `id_aula` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `capacidad` int DEFAULT NULL,
  `tiene_computadores` BOOLEAN DEFAULT NULL,
  `id_edificio` bigint DEFAULT NULL,
  PRIMARY KEY (`id_aula`),
  KEY `id_edificio` (`id_edificio`),
  CONSTRAINT `aula_ibfk_1` FOREIGN KEY (`id_edificio`) REFERENCES `edificio` (`id_edificio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dictado`
--

DROP TABLE IF EXISTS `dictado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dictado` (
  `id_dictado` bigint NOT NULL AUTO_INCREMENT,
  `id_asignatura` bigint DEFAULT NULL,
  `id_profesor` bigint DEFAULT NULL,
  PRIMARY KEY (`id_dictado`),
  KEY `FKh2j3v6crvvr2g04l1jt7re7b5` (`id_asignatura`),
  KEY `FKa6000vkfcwj44in2nbwpked2t` (`id_profesor`),
  CONSTRAINT `FKa6000vkfcwj44in2nbwpked2t` FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`),
  CONSTRAINT `FKh2j3v6crvvr2g04l1jt7re7b5` FOREIGN KEY (`id_asignatura`) REFERENCES `asignatura` (`id_asignatura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `disponibilidad_horaria`
--

DROP TABLE IF EXISTS `disponibilidad_horaria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disponibilidad_horaria` (
  `id_disponibilidad` bigint NOT NULL AUTO_INCREMENT,
  `dia` varchar(255) DEFAULT NULL,
  `hora_fin` time DEFAULT NULL,
  `hora_inicio` time DEFAULT NULL,
  PRIMARY KEY (`id_disponibilidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `edificio`
--

DROP TABLE IF EXISTS `edificio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `edificio` (
  `idedificio` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idedificio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grupo`
--

DROP TABLE IF EXISTS `grupo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grupo` (
  `idgrupo` bigint NOT NULL AUTO_INCREMENT,
  `cupo` int DEFAULT NULL,
  `nombregrupo` varchar(255) DEFAULT NULL,
  `idasignatura` bigint DEFAULT NULL,
  PRIMARY KEY (`idgrupo`),
  KEY `FKexsnelyd41sfqwjxwiq5c8go5` (`idasignatura`),
  CONSTRAINT `FKexsnelyd41sfqwjxwiq5c8go5` FOREIGN KEY (`idasignatura`) REFERENCES `asignatura` (`id_asignatura`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `horario`
--

DROP TABLE IF EXISTS `horario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario` (
  `id_horario` bigint NOT NULL AUTO_INCREMENT,
  `dia` varchar(255) DEFAULT NULL,
  `hora_fin` time DEFAULT NULL,
  `hora_inicio` time DEFAULT NULL,
  `id_aula` bigint DEFAULT NULL,
  `id_grupo` bigint DEFAULT NULL,
  `id_profesor` bigint DEFAULT NULL,
  PRIMARY KEY (`id_horario`),
  KEY `FK74io69d6km297uuf1s9mt9bej` (`id_aula`),
  KEY `FKdodah75vq84c8cdcjhs4fgo0p` (`id_grupo`),
  KEY `FK1alm3vwaj7x1qx9avlgdeaqgv` (`id_profesor`),
  CONSTRAINT `FK1alm3vwaj7x1qx9avlgdeaqgv` FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`),
  CONSTRAINT `FK74io69d6km297uuf1s9mt9bej` FOREIGN KEY (`id_aula`) REFERENCES `aula` (`id_aula`),
  CONSTRAINT `FKdodah75vq84c8cdcjhs4fgo0p` FOREIGN KEY (`id_grupo`) REFERENCES `grupo` (`idgrupo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `horario_profesor`
--

DROP TABLE IF EXISTS `horario_profesor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario_profesor` (
  `id_horario_profesor` bigint NOT NULL AUTO_INCREMENT,
  `id_disponibilidad` bigint DEFAULT NULL,
  `id_profesor` bigint DEFAULT NULL,
  PRIMARY KEY (`id_horario_profesor`),
  KEY `FKgpe5h9iq1km8h8eyxmrj1tdnl` (`id_disponibilidad`),
  KEY `FKh4yt7fegch93tj94tg8sk9yrn` (`id_profesor`),
  CONSTRAINT `FKgpe5h9iq1km8h8eyxmrj1tdnl` FOREIGN KEY (`id_disponibilidad`) REFERENCES `disponibilidad_horaria` (`id_disponibilidad`),
  CONSTRAINT `FKh4yt7fegch93tj94tg8sk9yrn` FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profesor`
--

DROP TABLE IF EXISTS `profesor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesor` (
  `id_profesor` bigint NOT NULL AUTO_INCREMENT,
  `apellido1` varchar(255) DEFAULT NULL,
  `apellido2` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `documento_identidad` varchar(255) DEFAULT NULL,
  `nombre1` varchar(255) DEFAULT NULL,
  `nombre2` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `id_usuario` bigint DEFAULT NULL,
  PRIMARY KEY (`id_profesor`),
  UNIQUE KEY `UK_axsy20hq2lqbpfcsifi54k6it` (`id_usuario`),
  CONSTRAINT `FKbyhq45hsgy32alfydfpypqctp` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `nombre_rol` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `UK_l0qdsam7tunbtmxcmeeyfcifk` (`nombre_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rol_accion`
--

DROP TABLE IF EXISTS `rol_accion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_accion` (
  `id_rol_accion` bigint NOT NULL AUTO_INCREMENT,
  `id_accion` bigint DEFAULT NULL,
  `id_rol` bigint DEFAULT NULL,
  PRIMARY KEY (`id_rol_accion`),
  KEY `FK26nro84udg065l6ity0y2cdyx` (`id_accion`),
  KEY `FKg8gqouveklytih3mbqrpu0mol` (`id_rol`),
  CONSTRAINT `FK26nro84udg065l6ity0y2cdyx` FOREIGN KEY (`id_accion`) REFERENCES `accion` (`id_accion`),
  CONSTRAINT `FKg8gqouveklytih3mbqrpu0mol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `contrasena` varchar(255) NOT NULL,
  `nombre_usuario` varchar(255) NOT NULL,
  `id_rol` bigint DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  KEY `FKmyv3138vvci6kaq3y5kt4cntu` (`id_rol`),
  CONSTRAINT `FKmyv3138vvci6kaq3y5kt4cntu` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-18 18:20:32






