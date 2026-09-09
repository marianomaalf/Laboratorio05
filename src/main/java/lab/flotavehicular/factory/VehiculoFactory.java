package lab.flotavehicular.factory;

import lab.flotavehicular.dto.DatosVehiculo;
import lab.flotavehicular.model.Vehiculo;
import lab.flotavehicular.model.VehiculoCombustion;
import lab.flotavehicular.model.VehiculoElectrico;
import lab.flotavehicular.model.VehiculoPesado;

/**
 * Simple Factory: concentra la decisión de cuál subtipo de Vehiculo construir.
 */
public final class VehiculoFactory {

    private VehiculoFactory() {
    }

    public static Vehiculo crear(DatosVehiculo datos) {
        validarDatosComunes(datos);

        return switch (datos.tipo()) {
            case COMBUSTION -> {
                validarPorcentaje(datos.nivelEnergia(), "combustible");
                yield new VehiculoCombustion(
                        datos.placa(), datos.marca(), datos.kilometraje(),
                        datos.nivelEnergia()
                );
            }
            case ELECTRICO -> {
                validarPorcentaje(datos.nivelEnergia(), "batería");
                yield new VehiculoElectrico(
                        datos.placa(), datos.marca(), datos.kilometraje(),
                        datos.nivelEnergia(), 0
                );
            }
            case PESADO -> {
                validarPorcentaje(datos.nivelEnergia(), "combustible");
                if (datos.tonelajeMaximo() <= 0) {
                    throw new IllegalArgumentException(
                            "El tonelaje debe ser mayor que cero."
                    );
                }
                if (datos.tipoCarga() == null) {
                    throw new IllegalArgumentException(
                            "Debe seleccionar el tipo de carga."
                    );
                }
                yield new VehiculoPesado(
                        datos.placa(), datos.marca(), datos.kilometraje(),
                        datos.nivelEnergia(), datos.tonelajeMaximo(),
                        datos.tipoCarga()
                );
            }
        };
    }

    private static void validarDatosComunes(DatosVehiculo datos) {
        if (datos == null || datos.tipo() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de vehículo.");
        }
        if (datos.placa() == null || datos.placa().isBlank()
                || datos.marca() == null || datos.marca().isBlank()) {
            throw new IllegalArgumentException("La placa y la marca son obligatorias.");
        }
        if (datos.placa().contains(";") || datos.marca().contains(";")) {
            throw new IllegalArgumentException(
                    "La placa y la marca no pueden contener punto y coma."
            );
        }
        if (datos.kilometraje() < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo.");
        }
    }

    private static void validarPorcentaje(double valor, String nombre) {
        if (valor < 0 || valor > 100) {
            throw new IllegalArgumentException(
                    "El nivel de " + nombre + " debe estar entre 0 y 100."
            );
        }
    }
}