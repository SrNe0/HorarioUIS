-- Eliminar la base de datos preexistente
DROP DATABASE IF EXISTS horario_uis;

-- Crear la base de datos 'horario_uis'
CREATE DATABASE IF NOT EXISTS horario_uis;
USE horario_uis;

-- Tabla `rol`
DROP TABLE IF EXISTS `rol`;
CREATE TABLE `rol` (
  `id_rol` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre_rol` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE (`nombre_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `usuario`
DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id_usuario` BIGINT NOT NULL AUTO_INCREMENT,
  `contrasena` VARCHAR(255) NOT NULL,
  `nombre_usuario` VARCHAR(255) NOT NULL,
  `id_rol` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `edificio`
DROP TABLE IF EXISTS `edificio`;
CREATE TABLE `edificio` (
  `id_edificio` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id_edificio`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `accion`
DROP TABLE IF EXISTS `accion`;
CREATE TABLE `accion` (
  `id_accion` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre_accion` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id_accion`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `asignatura`
DROP TABLE IF EXISTS `asignatura`;
CREATE TABLE `asignatura` (
  `id_asignatura` BIGINT NOT NULL AUTO_INCREMENT,
  `codigo` VARCHAR(255) DEFAULT NULL,
  `horas_practica` INT DEFAULT NULL,
  `horas_teoria` INT DEFAULT NULL,
  `necesita_computadores` BIT(1) DEFAULT NULL,
  `nombre` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id_asignatura`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `aula`
DROP TABLE IF EXISTS `aula`;
CREATE TABLE `aula` (
  `id_aula` BIGINT NOT NULL AUTO_INCREMENT,
  `capacidad` INT DEFAULT NULL,
  `codigo` VARCHAR(255) DEFAULT NULL,
  `descripcion` VARCHAR(255) DEFAULT NULL,
  `tiene_computadores` BIT(1) DEFAULT NULL,
  `id_edificio` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_aula`),
  FOREIGN KEY (`id_edificio`) REFERENCES `edificio` (`id_edificio`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `profesor`
DROP TABLE IF EXISTS `profesor`;
CREATE TABLE `profesor` (
  `id_profesor` BIGINT NOT NULL AUTO_INCREMENT,
  `apellido1` VARCHAR(255) DEFAULT NULL,
  `apellido2` VARCHAR(255) DEFAULT NULL,
  `correo` VARCHAR(255) DEFAULT NULL,
  `documento_identidad` VARCHAR(255) DEFAULT NULL,
  `nombre1` VARCHAR(255) DEFAULT NULL,
  `nombre2` VARCHAR(255) DEFAULT NULL,
  `telefono` VARCHAR(255) DEFAULT NULL,
  `id_usuario` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_profesor`),
  FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `dictado`
DROP TABLE IF EXISTS `dictado`;
CREATE TABLE `dictado` (
  `id_dictado` BIGINT NOT NULL AUTO_INCREMENT,
  `id_asignatura` BIGINT DEFAULT NULL,
  `id_profesor` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_dictado`),
  FOREIGN KEY (`id_asignatura`) REFERENCES `asignatura` (`id_asignatura`),
  FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `disponibilidad_horaria`
DROP TABLE IF EXISTS `disponibilidad_horaria`;
CREATE TABLE `disponibilidad_horaria` (
  `id_disponibilidad` BIGINT NOT NULL AUTO_INCREMENT,
  `dia` VARCHAR(255) DEFAULT NULL,
  `hora_fin` TIME DEFAULT NULL,
  `hora_inicio` TIME DEFAULT NULL,
  PRIMARY KEY (`id_disponibilidad`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `grupo`
DROP TABLE IF EXISTS `grupo`;
CREATE TABLE `grupo` (
  `idgrupo` BIGINT NOT NULL AUTO_INCREMENT,
  `cupo` INT DEFAULT NULL,
  `nombregrupo` VARCHAR(255) DEFAULT NULL,
  `idasignatura` BIGINT DEFAULT NULL,
  PRIMARY KEY (`idgrupo`),
  FOREIGN KEY (`idasignatura`) REFERENCES `asignatura` (`id_asignatura`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `horario`
DROP TABLE IF EXISTS `horario`;
CREATE TABLE `horario` (
  `id_horario` BIGINT NOT NULL AUTO_INCREMENT,
  `dia` VARCHAR(255) DEFAULT NULL,
  `hora_fin` TIME DEFAULT NULL,
  `hora_inicio` TIME DEFAULT NULL,
  `id_aula` BIGINT DEFAULT NULL,
  `id_grupo` BIGINT DEFAULT NULL,
  `id_profesor` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_horario`),
  FOREIGN KEY (`id_aula`) REFERENCES `aula` (`id_aula`),
  FOREIGN KEY (`id_grupo`) REFERENCES `grupo` (`idgrupo`),
  FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `horario_profesor`
DROP TABLE IF EXISTS `horario_profesor`;
CREATE TABLE `horario_profesor` (
  `id_horario_profesor` BIGINT NOT NULL AUTO_INCREMENT,
  `id_disponibilidad` BIGINT DEFAULT NULL,
  `id_profesor` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_horario_profesor`),
  FOREIGN KEY (`id_disponibilidad`) REFERENCES `disponibilidad_horaria` (`id_disponibilidad`),
  FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id_profesor`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Tabla `rol_accion`
DROP TABLE IF EXISTS `rol_accion`;
CREATE TABLE `rol_accion` (
  `id_rol_accion` BIGINT NOT NULL AUTO_INCREMENT,
  `id_accion` BIGINT DEFAULT NULL,
  `id_rol` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id_rol_accion`),
  FOREIGN KEY (`id_accion`) REFERENCES `accion` (`id_accion`),
  FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

