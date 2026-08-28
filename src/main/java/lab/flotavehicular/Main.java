package lab.flotavehicular;

// Importaciones de JavaFX necesarias para lanzar la app
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Main hereda de Application: la clase base de toda app JavaFX
public class Main extends Application {

    @Override
    // start() es donde JavaFX "muestra" la ventana principal (el Stage)
    public void start(Stage stage) throws IOException {

        // FXMLLoader lee el archivo FXML (la vista). Aún NO existe, lo crearemos en clase
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/lab/flotavehicular/view/flotaView.fxml")
        );

        // La escena se construye cargando el FXML
        Scene scene = new Scene(loader.load());

        // Configura el título de la ventana
        stage.setTitle("Gestión de Flota Vehicular");

        // Asigna la escena a la ventana y la muestra
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // launch() arranca el ciclo de vida de JavaFX
        launch(args);
    }
}