package lab.flotavehicular.dto;

import lab.flotavehicular.model.TipoCarga;
import lab.flotavehicular.model.TipoVehiculo;

public record DatosVehiculo(
    String placa,
    String marca,
    int kilometraje,
    TipoVehiculo tipo,
    double nivelEnergia,
    double tonelajeMaximo,
    TipoCarga tipoCarga

) {
}
