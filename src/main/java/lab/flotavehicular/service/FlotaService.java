package lab.flotavehicular.service;

import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.repository.RepositorioVehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Capa de lógica de aplicación: aplica reglas y coordina la persistencia.
 */
public class FlotaService {

    private final List<Vehiculo> vehiculos;
    private final RepositorioVehiculo repositorio;

    public FlotaService(RepositorioVehiculo repositorio) {
        this.repositorio = repositorio;
        this.vehiculos = new ArrayList<>(repositorio.cargarTodos());
    }

    public void agregarVehiculo(Vehiculo vehiculo) {
        boolean existe = vehiculos.stream()
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(vehiculo.getPlaca()));

        if (existe) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        }

        vehiculos.add(vehiculo);
        guardarCambios();
    }

    public List<Vehiculo> obtenerVehiculos() {
        return new ArrayList<>(vehiculos);
    }

    public void actualizarVehiculo(Vehiculo vehiculoActualizado) {
        for (int i = 0; i < vehiculos.size(); i++) {
            if (vehiculos.get(i).getPlaca()
                    .equalsIgnoreCase(vehiculoActualizado.getPlaca())) {
                if (vehiculos.get(i).getEstado() != EstadoVehiculo.DISPONIBLE) {
                    throw new IllegalStateException(
                            "Solo se puede editar un vehículo disponible."
                    );
                }
                vehiculos.set(i, vehiculoActualizado);
                guardarCambios();
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró el vehículo.");
    }

    public void eliminarVehiculo(String placa) {
        Vehiculo encontrado = vehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el vehículo."
                ));

        if (encontrado.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException(
                    "Solo se puede eliminar un vehículo disponible."
            );
        }

        boolean eliminado = vehiculos.removeIf(
                vehiculo -> vehiculo.getPlaca().equalsIgnoreCase(placa)
        );

        if (!eliminado) {
            throw new IllegalArgumentException("No se encontró el vehículo.");
        }
        guardarCambios();
    }

    public void guardarCambios() {
        repositorio.guardarTodos(vehiculos);
    }
}