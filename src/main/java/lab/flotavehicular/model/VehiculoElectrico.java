package lab.flotavehicular.model;

import lab.flotavehicular.EstadoVehiculo;
import lab.flotavehicular.TipoVehiculo;

public class VehiculoElectrico extends Vehiculo{


    private double porcentajeBateria;

    // Cuántas veces se ha cargado la batería
    private int ciclosDeCarga;

    // Constructor completo: datos del padre (super) + batería + ciclos de carga
    public VehiculoElectrico(String placa, String marca, int kilometraje, double porcentajeBateria, int ciclosDeCarga) {
        // Envía placa, marca y km al padre (Vehiculo)
        super(placa, marca, kilometraje);

        this.porcentajeBateria = porcentajeBateria;
        this.ciclosDeCarga = ciclosDeCarga;
    }

    public double getPorcentajeBateria() {
        return porcentajeBateria;
    }
    @Override
    // Sobrescritura: cómo inicia ruta un vehículo eléctrico
    public void iniciarRuta() {

        // Regla 1: no puede iniciar una ruta si ya está en ruta
        if (estado == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] ya se encuentra en ruta."
            );
        }

        // Regla 2: si está en el taller, no puede salir
        if (this.estado == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] está en el taller. No puede salir."
            );
        }

        // Regla 3: no puede salir con menos de 15% de batería
        if (this.porcentajeBateria < 15.0) {
            throw new IllegalStateException(
                    "Batería insuficiente (" + porcentajeBateria + "%). Conecte al cargador."
            );
        }

        // Lógica propia: consume 15% de batería y avanza 50 km
        this.porcentajeBateria -= 15.0;
        this.kilometraje += 50;
        this.estado = EstadoVehiculo.EN_RUTA;
    }

    @Override
    // Sobrescritura: este vehículo es de tipo ELECTRICO
    public TipoVehiculo getTipo() {
        return TipoVehiculo.ELECTRICO;
    }

    @Override
    // Sobrescritura (interfaz Mantenible): evalúa el estado de la batería
    public String evaluarEstadoGeneral() {

        // Un eléctrico con muchos ciclos de carga tiene la batería degradada
        if (ciclosDeCarga > 1000) {
            return "Alerta: Batería muy degradada, requiere reemplazo";
        }

        // Si no, revisamos el porcentaje actual
        else if (porcentajeBateria < 20) {
            return "Requiere conexión a estación de carga";
        }

        // Si está por encima del 20%, todo bien
        else {
            return "Batería y sistemas óptimos";
        }
    }





}
