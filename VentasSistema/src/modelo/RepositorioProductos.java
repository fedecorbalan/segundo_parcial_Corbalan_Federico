package modelo;

import java.io.*;
import java.util.*;

public class RepositorioProductos implements Repositorio<Producto> {
    
    private final String archivo = "productos.dat";
    
    @Override
    public List<Producto> cargar() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Producto>) in.readObject();
        } catch (Exception e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public void guardar(List<Producto> lista) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(lista);
        } catch (Exception e) {
            System.out.println("Error al guardar archivo: " + e.getMessage());
        }
    }
}
