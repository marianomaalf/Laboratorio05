module lab.flotavehicular {

    // Módulos de JavaFX incluidos en el proyecto
    requires javafx.controls;
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