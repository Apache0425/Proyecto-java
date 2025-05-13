package Model;

public class Consumo {
    private int hora;
    private int consumoKW;
    private int precio;

    public Consumo(int hora, int consumoKW) {
        this.hora = hora;
        this.consumoKW = consumoKW;
        this.precio = mCalcularPrecio();
    }

    private int mCalcularPrecio() {
        return (hora >= 0 && hora < 6) ? 200 * consumoKW :
               (hora >= 7 && hora < 17) ? 300 * consumoKW :
               (hora >= 18 && hora < 23) ? 500 * consumoKW : 0;
    }

    public int mObtenerHora() { return hora; }
    public int mObtenerConsumoKW() { return consumoKW; }
    public int mObtenerPrecio() { return precio; }

    @Override
    public String toString() {
        return "Hora: " + hora + " Consumo: " + consumoKW + "KW - Precio: " + precio + "COP";
    }
}