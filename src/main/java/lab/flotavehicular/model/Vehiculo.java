package lab.flotavehicular.model;

import lab.flotavehicular.EstadoVehiculo;
import lab.flotavehicular.TipoVehiculo;

public abstract class Vehiculo implements Mantenible {

    protected String placa;

    // Marca del vehículo (ej. Toyota, Volvo)
    protected String marca;

    // Kilometraje acumulado
    protected int kilometraje;

    // Estado actual del vehículo (enum)
    protected EstadoVehiculo estado;

    public Vehiculo(String placa, String marca, int kilometraje) {
        this.placa = placa;
        this.marca = marca;
        this.kilometraje = kilometraje;
        // Todo vehículo nace DISPONIBLE (no está en ruta ni en taller)
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    public abstract void iniciarRuta();

    // Método concreto: finalizar la ruta es igual para todos los vehículos
    public void finalizarRuta() {

        // Regla de negocio: solo se puede finalizar si está EN_RUTA
        if (estado != EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "El vehículo [" + placa + "] no se encuentra en ruta."
            );
        }

        // Regresa a DISPONIBLE
        estado = EstadoVehiculo.DISPONIBLE;
    }

    // Método abstracto: cada subclase devuelve su propio tipo
    public abstract TipoVehiculo getTipo();


    public String getPlaca()        { return placa; }

    // Getter público: permite leer la marca
    public String getMarca()        { return marca; }

    // Getter público: permite leer el kilometraje
    public int getKilometraje()   { return kilometraje; }

    // Getter público: permite leer el estado actual
    public EstadoVehiculo getEstado() { return estado; }

    // Setter público: permite cambiar el estado (lo usará la cola de mantenimiento)
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }

    // Sobrescritura: así se mostrará el vehículo en la futura lista (ListView)
    @Override
    public String toString() {
        return marca + " [" + placa + "]";
    }

}
