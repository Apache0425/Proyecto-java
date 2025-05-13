package Model;

public class Cliente {
    private String id;
    private String tipoIdentificacion;
    private String email;
    private String direccion;
    private Registrador registrador;

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

    public void mActualizarEmail(String email) { this.email = email; }
    public void mActualizarDireccion(String direccion) { this.direccion = direccion; }
    public void mAsignarRegistrador(Registrador registrador) { this.registrador = registrador; }

    @Override
    public String toString() {
        return "Cliente ID: " + id + ", Tipo: " + tipoIdentificacion + ", Email: " + email + ", Dirección: " + direccion + ", Registrador: " + (registrador != null ? registrador.mObtenerId() : "No asignado");
    }
}