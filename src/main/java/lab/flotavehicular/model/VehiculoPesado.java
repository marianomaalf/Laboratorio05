package lab.flotavehicular.model;

import lab.flotavehicular.TipoCarga;
import lab.flotavehicular.TipoVehiculo;

public class VehiculoPesado extends VehiculoCombustion {

    // Cuántas toneladas puede transportar como máximo
    private double tonelajeMaximo;

    // Toneladas cargadas actualmente (arranca en 0.0)
    private double cargaActual;

    // Tipo de carga que transporta (enum)
    private TipoCarga tipoCarga;

    // Constructor: recibe los datos del abuelo (super de Combustion) + los suyos
    public VehiculoPesado(
            String placa,
            String marca,
            int kilometraje,
            double nivelCombustible,
            double tonelajeMaximo,
            TipoCarga tipoCarga) {

        // Llama al constructor de VehiculoCombustion (que a su vez llama al de Vehiculo)
        super(placa, marca, kilometraje, nivelCombustible);

        this.tonelajeMaximo = tonelajeMaximo;
        this.cargaActual = 0.0;
        this.tipoCarga = tipoCarga;


    }







    // Getter: cuántas toneladas soporta el camión como máximo
    public double getTonelajeMaximo() {
        return tonelajeMaximo;
    }

    // Getter: cuántas toneladas lleva cargadas ahora mismo
    public double getCargaActual() {
        return cargaActual;
    }

    // Getter: qué tipo de carga transporta (NORMAL, PELIGROSA o REFRIGERADA)
    public TipoCarga getTipoCarga() {
        return tipoCarga;
    }

    // Regla de negocio: cargar mercancía en el camión
    public void cargarMercancia(double pesoEnToneladas) {

        // No se puede cargar un peso negativo o cero
        if (pesoEnToneladas <= 0) {
            throw new IllegalArgumentException(
                    "El peso de la mercancía debe ser mayor que cero."
            );
        }

        // No se puede exceder el tonelaje máximo (sobrecarga)
        if (pesoEnToneladas > tonelajeMaximo) {
            throw new IllegalArgumentException(
                    "Sobrecarga: el camión solo soporta " + tonelajeMaximo + " toneladas."
            );
        }

        // Si pasó las validaciones, se registra la carga
        this.cargaActual = pesoEnToneladas;
    }

    @Override
    // Sobrescritura: un camión NO puede salir vacío a ruta
    public void iniciarRuta() {

        // Regla propia del pesado: debe llevar carga
        if (cargaActual == 0.0) {
            throw new IllegalStateException(
                    "El camión [" + placa + "] no puede salir a ruta vacío."
            );
        }

        // Reutiliza TODA la validación y lógica del padre (combustible, estado...)
        super.iniciarRuta();

        // Y agrega su comportamiento particular: un camión pesado recorre más (150 km)
        this.kilometraje += 150;
    }

    @Override
    // Sobrescritura: al finalizar la ruta se descarga el camión
    public void finalizarRuta() {

        // Reutiliza la lógica del padre (valida estado y vuelve a DISPONIBLE)
        super.finalizarRuta();

        // Y agrega su comportamiento particular: la carga se vacía
        this.cargaActual = 0.0;


    }

    @Override
    // Sobrescritura: este vehículo es de tipo PESADO
    public TipoVehiculo getTipo() {
        return TipoVehiculo.PESADO;
    }

    @Override
    // Sobrescritura (interfaz Mantenible): estado del camión, con alerta de ejes
    public String evaluarEstadoGeneral() {

        // Guarda primero el texto que devuelve el padre (nivel de combustible)
        String estadoPadre = super.evaluarEstadoGeneral();

        // Un camión con mucho kilometraje necesita revisión de ejes y frenos de aire
        if (kilometraje > 150000) {
            return estadoPadre
                    + " | Alerta: Requiere revisión de ejes y frenos de aire.";
        }

        // Si el kilometraje es normal, devolvemos solo lo que dijo el padre
        return estadoPadre;
    }


}
