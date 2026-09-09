package lab.flotavehicular.repository;

import lab.flotavehicular.model.Vehiculo;

import java.util.List;

public interface RepositorioVehiculo{

    List<Vehiculo> cargarTodos();

    void guardarTodos(List<Vehiculo> vehiculos);
    
}
