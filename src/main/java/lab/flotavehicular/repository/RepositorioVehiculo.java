package lab.flotavehicular.repository;

import lab.flotavehicular.model.Vehiculo;

import java.util.List;

public interface RepositorioVehiculo{
    List<Vehiculo> cargarTodos();
    void insertar(Vehiculo vehiculo);
    void actualizar(Vehiculo vehiculo);
    void eliminar(String placa);
}
