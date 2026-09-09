package lab.flotavehicular.repository;

import lab.flotavehicular.model.EstadoVehiculo;
import lab.flotavehicular.model.TipoCarga;
import lab.flotavehicular.model.TipoVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.model.VehiculoCombustion;
import lab.flotavehicular.model.VehiculoElectrico;
import lab.flotavehicular.model.VehiculoPesado;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del Repository que guarda una línea por vehículo en un TXT.
 */
public class RepositorioVehiculoTxt implements RepositorioVehiculo {

    private static final String ENCABEZADO =
            "tipo;placa;marca;kilometraje;estado;energia;ciclos;tonelaje;carga;tipoCarga";

    private final Path archivo;

    public RepositorioVehiculoTxt(Path archivo) {
        this.archivo = archivo;
    }

    @Override
    public List<Vehiculo> cargarTodos() {
        if (Files.notExists(archivo)) {
            return new ArrayList<>();
        }

        try {
            List<Vehiculo> vehiculos = new ArrayList<>();

            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (linea.isBlank() || linea.equals(ENCABEZADO)) {
                    continue;
                }
                vehiculos.add(convertirDesdeLinea(linea));
            }

            return vehiculos;
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenciaException(
                    "No se pudieron cargar los vehículos desde " + archivo + ".", e
            );
        }
    }

    @Override
    public void guardarTodos(List<Vehiculo> vehiculos) {
        try {
            Path carpeta = archivo.getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }

            List<String> lineas = new ArrayList<>();
            lineas.add(ENCABEZADO);
            for (Vehiculo vehiculo : vehiculos) {
                lineas.add(convertirALinea(vehiculo));
            }

            Files.write(
                    archivo,
                    lineas,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new PersistenciaException(
                    "No se pudieron guardar los vehículos en " + archivo + ".", e
            );
        }
    }

    private String convertirALinea(Vehiculo vehiculo) {
        String energia = "";
        String ciclos = "";
        String tonelaje = "";
        String carga = "";
        String tipoCarga = "";

        if (vehiculo instanceof VehiculoPesado pesado) {
            energia = String.valueOf(pesado.getNivelCombustible());
            tonelaje = String.valueOf(pesado.getTonelajeMaximo());
            carga = String.valueOf(pesado.getCargaActual());
            tipoCarga = pesado.getTipoCarga().name();
        } else if (vehiculo instanceof VehiculoElectrico electrico) {
            energia = String.valueOf(electrico.getPorcentajeBateria());
            ciclos = String.valueOf(electrico.getCiclosDeCarga());
        } else if (vehiculo instanceof VehiculoCombustion combustion) {
            energia = String.valueOf(combustion.getNivelCombustible());
        }

        return String.join(";",
                vehiculo.getTipo().name(),
                vehiculo.getPlaca(),
                vehiculo.getMarca(),
                String.valueOf(vehiculo.getKilometraje()),
                vehiculo.getEstado().name(),
                energia,
                ciclos,
                tonelaje,
                carga,
                tipoCarga
        );
    }

    private Vehiculo convertirDesdeLinea(String linea) {
        String[] datos = linea.split(";", -1);
        if (datos.length != 10) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }

        TipoVehiculo tipo = TipoVehiculo.valueOf(datos[0]);
        String placa = datos[1];
        String marca = datos[2];
        int kilometraje = Integer.parseInt(datos[3]);
        EstadoVehiculo estado = EstadoVehiculo.valueOf(datos[4]);
        double energia = Double.parseDouble(datos[5]);

        Vehiculo vehiculo = switch (tipo) {
            case COMBUSTION -> new VehiculoCombustion(
                    placa, marca, kilometraje, energia
            );
            case ELECTRICO -> new VehiculoElectrico(
                    placa, marca, kilometraje, energia,
                    Integer.parseInt(datos[6])
            );
            case PESADO -> {
                VehiculoPesado pesado = new VehiculoPesado(
                        placa, marca, kilometraje, energia,
                        Double.parseDouble(datos[7]),
                        TipoCarga.valueOf(datos[9])
                );
                double cargaActual = Double.parseDouble(datos[8]);
                if (cargaActual > 0) {
                    pesado.cargarMercancia(cargaActual);
                }
                yield pesado;
            }
        };

        vehiculo.setEstado(estado);
        return vehiculo;
    }
}