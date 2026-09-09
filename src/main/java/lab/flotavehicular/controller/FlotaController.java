package lab.flotavehicular.controller;

import lab.flotavehicular.dto.DatosVehiculo;
import lab.flotavehicular.factory.VehiculoFactory;
import lab.flotavehicular.model.*;
import lab.flotavehicular.repository.PersistenciaException;
import lab.flotavehicular.repository.RepositorioVehiculo;
import lab.flotavehicular.repository.RepositorioVehiculoTxt;
import lab.flotavehicular.service.FlotaService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.nio.file.Path;

public class FlotaController {

    private final FlotaService flota;
    private final ColaMantenimiento<Vehiculo> colaMantenimiento;

    @FXML
    private TextField txtPlaca;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtKilometraje;

    @FXML
    private TextField txtCombustible;

    @FXML
    private TextField txtBateria;

    @FXML
    private TextField txtTonelaje;

    @FXML
    private TextField txtCargaActual;

    @FXML
    private ComboBox<TipoVehiculo> cbxTipoVehiculo;

    @FXML
    private ComboBox<TipoCarga> cbxTipoCarga;

    @FXML
    private Label lblCombustible;

    @FXML
    private Label lblBateria;

    @FXML
    private Label lblTonelaje;

    @FXML
    private Label lblTipoCarga;

    @FXML
    private Label lblPendientesMantenimiento;

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    @FXML
    private TableColumn<Vehiculo, String> colPlaca;

    @FXML
    private TableColumn<Vehiculo, String> colMarca;

    @FXML
    private TableColumn<Vehiculo, String> colTipo;

    @FXML
    private TableColumn<Vehiculo, Integer> colKilometraje;

    @FXML
    private TableColumn<Vehiculo, EstadoVehiculo> colEstado;


    public FlotaController() {
        RepositorioVehiculo repositorio = new RepositorioVehiculoTxt(
                Path.of("datos", "vehiculos.txt")
        );
        this.flota = new FlotaService(repositorio);
        this.colaMantenimiento = new ColaMantenimiento<>();

        flota.obtenerVehiculos().stream()
                .filter(v -> v.getEstado() == EstadoVehiculo.TALLER)
                .forEach(colaMantenimiento::restaurarPendiente);
    }


    @FXML
    public void initialize() {
        cbxTipoVehiculo.getItems().addAll(
                TipoVehiculo.values()
        );

        cbxTipoCarga.getItems().addAll(
                TipoCarga.values()
        );

        cbxTipoVehiculo.setValue(TipoVehiculo.COMBUSTION);
        cbxTipoCarga.setValue(TipoCarga.NORMAL);

        colPlaca.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getPlaca()
                )
        );

        colMarca.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getMarca()
                )
        );

        colTipo.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getTipo()
                ).asString()
        );

        colKilometraje.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getKilometraje()
                )
        );

        colEstado.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getEstado()
                )
        );

        tablaVehiculos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {

                    if (seleccionado != null) {
                        cargarVehiculoEnFormulario(seleccionado);
                    }
                });

        cambiarTipoVehiculo();
        actualizarTabla();
        actualizarPendientesMantenimiento();
    }


    @FXML
    private void cambiarTipoVehiculo() {

        TipoVehiculo tipo = cbxTipoVehiculo.getValue();

        if (tipo == null) {
            return;
        }

        switch (tipo) {

            case COMBUSTION -> {
                mostrarCampo(lblCombustible, txtCombustible, true);
                mostrarCampo(lblBateria, txtBateria, false);
                mostrarCampo(lblTonelaje, txtTonelaje, false);
                mostrarCampo(lblTipoCarga, cbxTipoCarga, false);
            }

            case ELECTRICO -> {
                mostrarCampo(lblCombustible, txtCombustible, false);
                mostrarCampo(lblBateria, txtBateria, true);
                mostrarCampo(lblTonelaje, txtTonelaje, false);
                mostrarCampo(lblTipoCarga, cbxTipoCarga, false);
            }

            case PESADO -> {
                mostrarCampo(lblCombustible, txtCombustible, true);
                mostrarCampo(lblBateria, txtBateria, false);
                mostrarCampo(lblTonelaje, txtTonelaje, true);
                mostrarCampo(lblTipoCarga, cbxTipoCarga, true);
            }
        }
    }

    private void mostrarCampo(Label label, Control control, boolean mostrar) {

        label.setVisible(mostrar);
        label.setManaged(mostrar);

        control.setVisible(mostrar);
        control.setManaged(mostrar);
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("No se pudo realizar la operación");
        alert.setContentText(mensaje);

        alert.showAndWait();
    }

    private void actualizarTabla() {
        tablaVehiculos.getItems().setAll(
                flota.obtenerVehiculos()
        );
    }


    private Vehiculo construirVehiculoDesdeFormulario() {

        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        TipoVehiculo tipo = cbxTipoVehiculo.getValue();

        int kilometraje = leerEntero(txtKilometraje, "Debe ingresar el kilometraje.");
        double energia;
        double tonelaje = 0;
        TipoCarga tipoCarga = null;

        if (tipo == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de vehículo.");
        }

        switch (tipo) {
            case COMBUSTION -> energia = leerDecimal(
                    txtCombustible, "Debe ingresar el nivel de combustible."
            );
            case ELECTRICO -> energia = leerDecimal(
                    txtBateria, "Debe ingresar el porcentaje de batería."
            );
            case PESADO -> {
                energia = leerDecimal(
                        txtCombustible, "Debe ingresar el nivel de combustible."
                );
                tonelaje = leerDecimal(
                        txtTonelaje, "Debe ingresar el tonelaje máximo."
                );
                tipoCarga = cbxTipoCarga.getValue();
            }
            default -> throw new IllegalArgumentException("Tipo de vehículo no válido.");
        }

        DatosVehiculo datos = new DatosVehiculo(
                placa, marca, kilometraje, tipo, energia, tonelaje, tipoCarga
        );

        return VehiculoFactory.crear(datos);
    }

    private int leerEntero(TextField campo, String mensajeVacio) {
        if (campo.getText().isBlank()) {
            throw new IllegalArgumentException(mensajeVacio);
        }
        return Integer.parseInt(campo.getText().trim());
    }

    private double leerDecimal(TextField campo, String mensajeVacio) {
        if (campo.getText().isBlank()) {
            throw new IllegalArgumentException(mensajeVacio);
        }
        return Double.parseDouble(campo.getText().trim());
    }

    private void cargarVehiculoEnFormulario(Vehiculo vehiculo) {

        txtPlaca.setText(vehiculo.getPlaca());
        txtPlaca.setDisable(true);

        txtMarca.setText(vehiculo.getMarca());
        txtKilometraje.setText(
                String.valueOf(vehiculo.getKilometraje())
        );

        cbxTipoVehiculo.setValue(vehiculo.getTipo());

        cambiarTipoVehiculo();

        if (vehiculo instanceof VehiculoPesado pesado) {

            txtCombustible.setText(
                    String.valueOf(pesado.getNivelCombustible())
            );

            txtTonelaje.setText(
                    String.valueOf(pesado.getTonelajeMaximo())
            );

            cbxTipoCarga.setValue(
                    pesado.getTipoCarga()
            );

        } else if (vehiculo instanceof VehiculoElectrico electrico) {

            txtBateria.setText(
                    String.valueOf(electrico.getPorcentajeBateria())
            );

        } else if (vehiculo instanceof VehiculoCombustion combustion) {

            txtCombustible.setText(
                    String.valueOf(combustion.getNivelCombustible())
            );
        }
    }

    @FXML
    private void registrarVehiculo() {

        try {

            Vehiculo vehiculo = construirVehiculoDesdeFormulario();

            flota.agregarVehiculo(vehiculo);

            actualizarTabla();

            limpiarFormulario();

            mostrarMensaje(
                    "Vehículo registrado",
                    "El vehículo fue registrado correctamente."
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Kilometraje, combustible, batería y tonelaje deben contener valores numéricos."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            mostrarError(e.getMessage());

        } catch (PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void editarVehiculo() {

        try {

            Vehiculo seleccionado =
                    tablaVehiculos.getSelectionModel()
                            .getSelectedItem();

            if (seleccionado == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un vehículo de la tabla."
                );
            }

            Vehiculo vehiculoActualizado =
                    construirVehiculoDesdeFormulario();

            flota.actualizarVehiculo(
                    vehiculoActualizado
            );

            actualizarTabla();

            tablaVehiculos.getSelectionModel().clearSelection();

            limpiarFormulario();

            mostrarMensaje(
                    "Vehículo actualizado",
                    "Los datos del vehículo se actualizaron correctamente."
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Los valores numéricos ingresados no son válidos."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            mostrarError(e.getMessage());

        } catch (PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarVehiculo() {

        Vehiculo seleccionado =
                tablaVehiculos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Debe seleccionar un vehículo de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar vehículo");
        confirmacion.setContentText(
                "¿Desea eliminar el vehículo con placa "
                        + seleccionado.getPlaca() + "?"
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {

            if (respuesta == ButtonType.OK) {
                try {
                    flota.eliminarVehiculo(seleccionado.getPlaca());
                    actualizarTabla();
                    limpiarFormulario();
                    mostrarMensaje(
                            "Vehículo eliminado",
                            "El vehículo fue eliminado correctamente."
                    );
                } catch (IllegalStateException | PersistenciaException e) {
                    mostrarError(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        txtPlaca.setDisable(false);
        txtPlaca.clear();
        txtMarca.clear();
        txtKilometraje.clear();

        txtCombustible.clear();
        txtBateria.clear();
        txtTonelaje.clear();

        cbxTipoVehiculo.setValue(TipoVehiculo.COMBUSTION);
        cbxTipoCarga.setValue(TipoCarga.NORMAL);

        cambiarTipoVehiculo();

        txtPlaca.requestFocus();
    }


    // LÓGICA DE LA FLOTILLA

    @FXML
    private void iniciarRuta() {

        Vehiculo seleccionado =
                tablaVehiculos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Debe seleccionar un vehículo de la tabla.");
            return;
        }

        try {

            seleccionado.iniciarRuta();
            flota.guardarCambios();

            actualizarTabla();

            mostrarMensaje(
                    "Ruta iniciada",
                    "El vehículo "
                            + seleccionado.getPlaca()
                            + " inició la ruta correctamente."
            );

        } catch (IllegalStateException | PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void finalizarRuta() {

        Vehiculo seleccionado =
                tablaVehiculos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Debe seleccionar un vehículo de la tabla.");
            return;
        }

        try {

            seleccionado.finalizarRuta();
            flota.guardarCambios();

            actualizarTabla();

            mostrarMensaje(
                    "Ruta finalizada",
                    "El vehículo "
                            + seleccionado.getPlaca()
                            + " finalizó su ruta correctamente."
            );

        } catch (IllegalStateException | PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    private void actualizarPendientesMantenimiento() {

        lblPendientesMantenimiento.setText(
                "Pendientes en mantenimiento: "
                        + colaMantenimiento.cantidadPendientes()
        );
    }

    @FXML
    private void enviarMantenimiento() {

        Vehiculo seleccionado =
                tablaVehiculos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Debe seleccionar un vehículo de la tabla.");
            return;
        }

        try {

            colaMantenimiento.encolar(seleccionado);
            flota.guardarCambios();

            actualizarTabla();
            actualizarPendientesMantenimiento();

            mostrarMensaje(
                    "Vehículo enviado a mantenimiento",
                    "El vehículo " + seleccionado.getPlaca()
                            + " fue agregado a la cola de mantenimiento."
            );

        } catch (IllegalStateException | PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void atenderMantenimiento() {

        try {

            Vehiculo vehiculoAtendido =
                    colaMantenimiento.atenderSiguiente();

            flota.guardarCambios();

            actualizarTabla();
            actualizarPendientesMantenimiento();

            mostrarMensaje(
                    "Mantenimiento finalizado",
                    "Vehículo atendido: "
                            + vehiculoAtendido.getPlaca()
                            + "\nEstado general: "
                            + vehiculoAtendido.evaluarEstadoGeneral()
            );

        } catch (IllegalStateException | PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cargarMercancia() {

        Vehiculo seleccionado =
                tablaVehiculos.getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {
            mostrarError("Debe seleccionar un vehículo de la tabla.");
            return;
        }

        if (!(seleccionado instanceof VehiculoPesado pesado)) {
            mostrarError(
                    "La carga de mercancía solo aplica a vehículos pesados."
            );
            return;
        }

        try {

            if (txtCargaActual.getText().isBlank()) {
                throw new IllegalArgumentException(
                        "Debe ingresar el peso de la mercancía."
                );
            }

            double peso = Double.parseDouble(
                    txtCargaActual.getText()
            );

            pesado.cargarMercancia(peso);
            flota.guardarCambios();

            txtCargaActual.clear();

            mostrarMensaje(
                    "Mercancía cargada",
                    "Se asignaron "
                            + peso
                            + " toneladas al vehículo "
                            + pesado.getPlaca()
                            + "."
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "El peso de la mercancía debe ser un número."
            );

        } catch (IllegalArgumentException e) {

            mostrarError(e.getMessage());

        } catch (PersistenciaException e) {

            mostrarError(e.getMessage());
        }
    }

}