package lab.flotavehicular.service;

import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.repository.RepositorioVehiculo;
import java.util.ArrayList;
import java.util.List;

/** Reglas de aplicación y coordinación. No conoce SQL, JDBC ni credenciales. */
public class FlotaService {
    private final List<Vehiculo> vehiculos;
    private final RepositorioVehiculo repositorio;

    public FlotaService(RepositorioVehiculo repositorio) {
        this.repositorio = repositorio;
        this.vehiculos = new ArrayList<>(repositorio.cargarTodos());
    }

    public void agregarVehiculo(Vehiculo v) {
        if (vehiculos.stream().anyMatch(x -> x.getPlaca().equalsIgnoreCase(v.getPlaca())))
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        repositorio.insertar(v); // Primero persistimos; si falla, no alteramos la lista local.
        vehiculos.add(v);
    }

    public List<Vehiculo> obtenerVehiculos() { return new ArrayList<>(vehiculos); }

    public void actualizarVehiculo(Vehiculo nuevo) {
        for (int i = 0; i < vehiculos.size(); i++) {
            Vehiculo actual = vehiculos.get(i);
            if (actual.getPlaca().equalsIgnoreCase(nuevo.getPlaca())) {
                if (actual.getEstado() != EstadoVehiculo.DISPONIBLE)
                    throw new IllegalStateException("Solo se puede editar un vehículo disponible.");
                repositorio.actualizar(nuevo);
                vehiculos.set(i, nuevo);
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró el vehículo.");
    }

    public void eliminarVehiculo(String placa) {
        Vehiculo encontrado = vehiculos.stream().filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No se encontró el vehículo."));
        if (encontrado.getEstado() != EstadoVehiculo.DISPONIBLE)
            throw new IllegalStateException("Solo se puede eliminar un vehículo disponible.");
        repositorio.eliminar(placa);
        vehiculos.remove(encontrado);
    }

    /** Persiste los cambios de estado/energía realizados por las operaciones de la flota. */
    public void guardarCambios(Vehiculo v) { repositorio.actualizar(v); }
}