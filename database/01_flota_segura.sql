-- Ejecutar en MySQL Workbench con una cuenta administradora de laboratorio.

-- ============================================================
-- SECCION 1 · La base de datos y la tabla     --> Paso 3
-- ============================================================

-- ============================================================
-- SECCION 2 · Los usuarios                    --> Paso 4
-- ============================================================

-- ============================================================
-- SECCION 3 · Los procedimientos almacenados  --> Paso 5
-- ============================================================

-- ============================================================
-- SECCION 4 · Los permisos de la aplicación   --> Paso 6
-- ============================================================

CREATE DATABASE IF NOT EXISTS flota_vehicular
CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE flota_vehicular;

CREATE TABLE IF NOT EXISTS vehiculo (
                                        placa VARCHAR(12) PRIMARY KEY,
    marca VARCHAR(80) NOT NULL,
    kilometraje INT NOT NULL,
    estado ENUM('DISPONIBLE','EN_RUTA','TALLER') NOT NULL,
    tipo ENUM('COMBUSTION','ELECTRICO','PESADO') NOT NULL,
    nivel_energia DECIMAL(5,2) NOT NULL,
    ciclos_carga INT NOT NULL DEFAULT 0,
    tonelaje_maximo DECIMAL(8,2) NULL,
    carga_actual DECIMAL(8,2) NULL,
    tipo_carga ENUM('NORMAL','PELIGROSA','REFRIGERADA') NULL,
    CONSTRAINT chk_kilometraje CHECK (kilometraje >= 0),
    CONSTRAINT chk_energia CHECK (nivel_energia >= 0 AND nivel_energia <= 100),
    CONSTRAINT chk_ciclos CHECK (ciclos_carga >= 0),
    CONSTRAINT chk_pesado CHECK ((tipo = 'PESADO' AND tonelaje_maximo > 0 AND carga_actual >= 0 AND tipo_carga IS NOT NULL)
    OR (tipo <> 'PESADO' AND tonelaje_maximo IS NULL AND carga_actual IS NULL AND tipo_carga IS NULL))
    );

-- Cuenta definidora de privilegios mínimos. No usar root como DEFINER.
CREATE USER IF NOT EXISTS 'flota_definer'@'localhost' IDENTIFIED BY 'Llamadedragon01*';
GRANT SELECT, INSERT, UPDATE, DELETE ON flota_vehicular.vehiculo TO 'flota_definer'@'localhost';
CREATE USER IF NOT EXISTS 'flota_app'@'localhost' IDENTIFIED BY 'Llamadedragon05*';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'flota_app'@'localhost';


SHOW GRANTS FOR 'flota_definer'@'localhost';
SHOW GRANTS FOR 'flota_app'@'localhost';


DROP PROCEDURE IF EXISTS sp_listar_vehiculos;
DROP PROCEDURE IF EXISTS sp_insertar_vehiculo;
DROP PROCEDURE IF EXISTS sp_actualizar_vehiculo;
DROP PROCEDURE IF EXISTS sp_eliminar_vehiculo;

DELIMITER $$      # cambiamos el terminador a $$
   ... aquí van los CREATE PROCEDURE ...
DELIMITER ;       # lo devolvemos a ;