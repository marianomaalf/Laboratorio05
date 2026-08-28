package lab.flotavehicular.model;

import lab.flotavehicular.EstadoVehiculo;
import lab.flotavehicular.TipoVehiculo;

public class VehiculoCombustion extends Vehiculo {


    private double nivelCombustible;

    // Constructor: recibe los datos del padre (super) + el nivel de combustible
    public VehiculoCombustion(
            String placa,
            String marca,
            int kilometraje,
            double nivelCombustible) {

        // super(...) llama al constructor de la clase padre (Vehiculo)
        super(placa, marca, kilometraje);

        // El atributo propio se inicializa aquí
        this.nivelCombustible = nivelCombustible;


    }

    public double getNivelCombustible() { return nivelCombustible; }


    @Override
    // Sobrescritura: cómo inicia ruta un vehículo de combustión
    public void iniciarRuta() {

        // Regla 1: si está en el taller, no puede salir
        if (estado == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] está en el taller. No puede salir."
            );
        }

        // Regla 2: si ya está en ruta, no puede iniciar otra
        if (estado == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] ya se encuentra en ruta."
            );
        }

        // Regla 3: no puede salir con menos de 10% de combustible
        if (nivelCombustible < 10) {
            throw new IllegalStateException(
                    "Combustible insuficiente. Debe ir a la gasolinera."
            );
        }

        // Lógica propia: consume 10% y avanza 50 km
        nivelCombustible -= 10;
        kilometraje += 50;
        estado = EstadoVehiculo.EN_RUTA;
    }

    @Override
    // Sobrescritura: este vehículo es de tipo COMBUSTION
    public TipoVehiculo getTipo() {
        return TipoVehiculo.COMBUSTION;
    }

    @Override
    // Sobrescritura (interfaz Mantenible): evalúa el estado según el combustible
    public String evaluarEstadoGeneral() {
        // Operador ternario: si hay menos de 15% avisa; si no, todo bien
        return nivelCombustible < 15
                ? "Requiere ir a la gasolinera"
                : "Niveles óptimos";
    }



}