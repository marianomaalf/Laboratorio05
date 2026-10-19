module lab.flotavehicular {

    // Módulos de JavaFX incluidos en el proyecto
    requires javafx.controls;
    requires java.sql;            // AGREGAR: habilita JDBC (Connection, CallableStatement...)
    // OJO: el nombre del MÓDULO no es com.mysql.cj (ese es el nombre del paquete).
    // mysql-connector-j-9.4.0.jar no trae module-info ni Automatic-Module-Name,
    // así que JPMS deriva el nombre automático del nombre del jar: mysql.connector.j
    requires mysql.connector.j;   // AGREGAR: habilita el driver de MySQL
    requires javafx.fxml;
    requires javafx.web;


    // Librerías adicionales seleccionadas al crear el proyecto (Paso 3.3)

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    // Abre el paquete del controller para que JavaFX (FXML) acceda por reflexión
    opens lab.flotavehicular.controller to javafx.fxml;

    // Paquetes que exponemos al exterior
    exports lab.flotavehicular;
    exports lab.flotavehicular.model;
}