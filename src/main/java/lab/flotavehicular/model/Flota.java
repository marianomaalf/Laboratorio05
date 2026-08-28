package lab.flotavehicular.model;

import java.util.ArrayList;
import java.util.List;

public class Flota {



    // La lista de vehículos. 'List' es la interfaz, 'ArrayList' la implementación
    private List<Vehiculo> vehiculos;

    public Flota() {
        // Se inicializa como un ArrayList vacío
        this.vehiculos = new ArrayList<>();

    }

    // CREATE: registra un vehículo nuevo en la flota
    public void agregarVehiculo(Vehiculo vehiculo) {

        // Stream + anyMatch + lambda: ¿ya existe algún vehículo con esa placa?
        boolean existe = vehiculos.stream()
                .anyMatch(v ->
                        v.getPlaca().equalsIgnoreCase(
                                vehiculo.getPlaca()
                        )
                );

        // Regla de negocio: las placas no pueden repetirse (sin importar mayúsculas)
        if (existe) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo con esa placa."
            );
        }

        // Si no existe, se agrega a la lista
        vehiculos.add(vehiculo);
    }

    // READ: devuelve una copia de la lista (para que nadie la modifique desde afuera)
    public List<Vehiculo> obtenerVehiculos() {
        return new ArrayList<>(vehiculos);
    }


    // UPDATE: reemplaza un vehículo existente por su versión actualizada
    public void actualizarVehiculo(Vehiculo vehiculoActualizado) {

        // Recorremos la lista con un índice
        for (int i = 0; i < vehiculos.size(); i++) {

            // Si encontramos la placa (ignorando mayúsculas) ...
            if (vehiculos.get(i).getPlaca()
                    .equalsIgnoreCase(vehiculoActualizado.getPlaca())) {

                // ... la reemplazamos por el objeto nuevo y terminamos
                vehiculos.set(i, vehiculoActualizado);
                return;
            }
        }

        // Si el ciclo termina sin encontrar la placa, es un error
        throw new IllegalArgumentException(
                "No se encontró el vehículo."
        );
    }

    // DELETE: elimina el vehículo con la placa indicada
    public void eliminarVehiculo(String placa) {

        // removeIf + lambda: elimina todos los que cumplan la condición
        boolean eliminado = vehiculos.removeIf(
                vehiculo ->
                        vehiculo.getPlaca().equalsIgnoreCase(placa)
        );

        // removeIf devuelve true si borró algo; si no, la placa no existía
        if (!eliminado) {
            throw new IllegalArgumentException(
                    "No se encontró el vehículo."
            );
        }
    }



}
