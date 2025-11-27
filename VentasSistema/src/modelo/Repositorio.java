package modelo;

import java.util.List;

public interface Repositorio<T> {
    List<T> cargar();
    void guardar(List<T> lista);
}
