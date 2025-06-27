package LenguajeShibaInu;

public class ErrorCompilador {
    private String id;
    private String tipoGeneral; // "Léxico", "Sintáctico", "Semántico"
    private String tipoEspecifico;
    private int linea;
    private int posicion;
    private String descripcion;

    public ErrorCompilador(String id, String tipoGeneral, String tipoEspecifico, int posicion, int linea, String descripcion) {
        this.id = id;
        this.tipoGeneral = tipoGeneral;
        this.tipoEspecifico = tipoEspecifico;
        this.posicion = posicion;
        this.linea = linea;
        this.descripcion = descripcion;
    }

    // Getters
    public String getId() { return id; }
    public String getTipoGeneral() { return tipoGeneral; }
    public String getTipoEspecifico() { return tipoEspecifico; }
    public int getPosicion() { return posicion; }
    public int getLinea() { return linea; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return String.format("Error(id=%s, tipoGeneral=%s, tipoEspecifico=%s, posicion=%d, linea=%d, desc=%s)",
                id, tipoGeneral, tipoEspecifico, posicion, linea, descripcion);
    }
}
