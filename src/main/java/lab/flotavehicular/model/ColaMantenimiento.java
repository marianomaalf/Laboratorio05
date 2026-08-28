package lab.flotavehicular.model;

import lab.flotavehicular.EstadoVehiculo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;


public class ColaMantenimiento<T extends Vehiculo> {

    private Queue<T> filaDeEspera;

    public ColaMantenimiento() {
        // LinkedList implementa la interfaz Queue (Primero en entrar, primero en salir)
        this.filaDeEspera = new LinkedList<>();
    }


    // Agregar un vehículo a la cola (offer agrega al final)
    public void encolar(T vehiculo) {

        // Regla: no se puede encolar un vehículo que ya está en mantenimiento
        if (vehiculo.getEstado() == EstadoVehiculo.TALLER) {
            throw new IllegalStateException(
                    "El vehículo ya se encuentra en mantenimiento."
            );
        }

        // Regla: no se puede enviar a mantenimiento un vehículo que está en ruta
        if (vehiculo.getEstado() == EstadoVehiculo.EN_RUTA) {
            throw new IllegalStateException(
                    "No puede enviar a mantenimiento un vehículo que está en ruta."
            );
        }

        // Cambia el estado a TALLER y lo agrega al final de la fila
        vehiculo.setEstado(EstadoVehiculo.TALLER);
        filaDeEspera.offer(vehiculo);
    }

    // Atender al primer vehículo de la cola (poll lo quita del frente)
    public T atenderSiguiente() {

        // Regla: no hay nada que atender si la cola está vacía
        if (filaDeEspera.isEmpty()) {
            throw new IllegalStateException(
                    "No hay vehículos en la cola de mantenimiento."
            );
        }

        // poll() saca al primero (FIFO) y lo regresa como atendido
        T vehiculoAtendido = filaDeEspera.poll();

        // Al salir del taller queda DISPONIBLE para trabajar
        vehiculoAtendido.setEstado(EstadoVehiculo.DISPONIBLE);

        return vehiculoAtendido;
    }

    // ¿Está vacía la cola?
    public boolean estaVacia() {
        return filaDeEspera.isEmpty();
    }

    // ¿Cuántos vehículos hay pendientes?
    public int cantidadPendientes() {
        return filaDeEspera.size();
    }

    // Devuelve una copia de la lista de pendientes (para mostrarla en la vista)
    public List<T> obtenerListaPendientes() {
        return new ArrayList<>(filaDeEspera);
    }
}




