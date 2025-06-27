package LenguajeShibaInu;

import java.util.ArrayList;
import java.util.List;

public class TablaErrores {
    private List<ErrorCompilador> errores;

    public TablaErrores() {
        this.errores = new ArrayList<>();
    }

    public void add(ErrorCompilador error) {
        errores.add(error);
    }

    public void addAll(List<ErrorCompilador> listaErrores) {
        errores.addAll(listaErrores);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public void mostrarErrores() {
        System.out.println("-------------Tabla de Errores----------------");
        for (ErrorCompilador e : errores) {
            System.out.printf("Error(id=%s, tipo=%s, tipoEspecifico=%s, posicion=%d, linea=%d, desc=%s)",
                    e.getId(), e.getTipoGeneral(), e.getTipoEspecifico() ,e.getLinea(), e.getPosicion(), e.getDescripcion());
        }
    }

    public List<ErrorCompilador> getErrores() {
        return errores;
    }
}
