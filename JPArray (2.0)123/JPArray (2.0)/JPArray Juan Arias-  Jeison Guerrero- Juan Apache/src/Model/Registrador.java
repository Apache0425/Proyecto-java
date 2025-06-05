package Model;

public class Registrador {
    private String id;
    private String direccion;
    private String ciudad;

    public Registrador(String id, String direccion, String ciudad) {
        this.id = id;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public String mObtenerId() { return id; }
    public String mObtenerDireccion() { return direccion; }
    public String mObtenerCiudad() { return ciudad; }

    public void mActualizarDireccion(String direccion) { this.direccion = direccion; }
    public void mActualizarCiudad(String ciudad) { this.ciudad = ciudad; }
}