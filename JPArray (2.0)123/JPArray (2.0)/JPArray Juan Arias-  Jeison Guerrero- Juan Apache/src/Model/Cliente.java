package Model;

public class Cliente {
    private String id;
    private String tipoIdentificacion;
    private String email;
    private String direccion;
    private Registrador registrador;
    // NUEVO: Array para almacenar los consumos de este cliente por mes/día/hora
    private Consumo[][][] consumosCliente = new Consumo[12][31][24]; // Mes (0-11), Día (0-30), Hora (0-23)


    public Cliente(String id, String tipoIdentificacion, String email, String direccion, Registrador registrador) {
        this.id = id;
        this.tipoIdentificacion = tipoIdentificacion;
        this.email = email;
        this.direccion = direccion;
        this.registrador = registrador;
    }

    public String mObtenerId() { return id; }
    public String mObtenerTipoIdentificacion() { return tipoIdentificacion; }
    public String mObtenerEmail() { return email; }
    public String mObtenerDireccion() { return direccion; }
    public Registrador mObtenerRegistrador() { return registrador; }
    // NUEVO: Método para obtener los consumos de este cliente
    public Consumo[][][] mObtenerConsumosCliente() { return consumosCliente; }

    public void mActualizarEmail(String email) { this.email = email; }
    public void mActualizarDireccion(String direccion) { this.direccion = direccion; }
    public void mAsignarRegistrador(Registrador registrador) { this.registrador = registrador; }

    // NUEVO: Método para establecer un consumo específico para este cliente
    public void mEstablecerConsumo(int mes, int dia, int hora, Consumo consumo) {
        if (mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31 && hora >= 0 && hora < 24) {
            consumosCliente[mes - 1][dia - 1][hora] = consumo;
        } else {
            System.err.println("Error: Índice de consumo fuera de rango para cliente " + id);
        }
    }

    // NUEVO: Método para obtener un consumo específico de este cliente
    public Consumo mObtenerConsumoEspecifico(int mes, int dia, int hora) {
        if (mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31 && hora >= 0 && hora < 24) {
            return consumosCliente[mes - 1][dia - 1][hora];
        }
        return null;
    }

    @Override
    public String toString() {
        return "Cliente ID: " + id + ", Tipo: " + tipoIdentificacion + ", Email: " + email + ", Dirección: " + direccion + ", Registrador: " + (registrador != null ? registrador.mObtenerId() : "No asignado");
    }
}