package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class VentasSistemaViewController implements Initializable {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;

    @FXML private ListView<String> listaCarrito;

    @FXML private TextField txtCantidad;
    @FXML private Label lblEstado;
    
    private ObservableList<Producto> productos;
    private final RepositorioProductos repo = new RepositorioProductos();
    private final Map<Producto, Integer> carrito = new HashMap<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getNombre()));
        colPrecio.setCellValueFactory(e -> new javafx.beans.property.SimpleDoubleProperty(e.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(e -> new javafx.beans.property.SimpleObjectProperty<>(e.getValue().getStock()));

        productos = FXCollections.observableArrayList(repo.cargar());
        tablaProductos.setItems(productos);
    }    
    
        public void agregarAlCarrito() {
        Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p == null) {
            lblEstado.setText("Seleccione un producto.");
            return;
        }

        int cant;
        try {
            cant = Integer.parseInt(txtCantidad.getText());
        } catch (Exception e) {
            lblEstado.setText("Cantidad inválida.");
            return;
        }

        if (cant <= 0) {
            lblEstado.setText("La cantidad debe ser mayor a 0.");
            return;
        }

        if (cant > p.getStock()) {
            lblEstado.setText("Stock insuficiente.");
            return;
        }

        carrito.put(p, carrito.getOrDefault(p, 0) + cant);
        actualizarCarritoVisual();
        lblEstado.setText("Producto agregado.");
    }

    private void actualizarCarritoVisual() {
        listaCarrito.getItems().clear();
        for (var e : carrito.entrySet()) {
            listaCarrito.getItems().add(e.getKey().getNombre() + " x" + e.getValue());
        }
    }

    @FXML
    public void finalizarCompra() {
        if (carrito.isEmpty()) {
            lblEstado.setText("El carrito está vacío.");
            return;
        }

        int total = 0;
        for (var e : carrito.entrySet()) {
            total += e.getKey().getPrecio() * e.getValue();
        }

        generarTicket(total);
        actualizarStock();

        carrito.clear();
        actualizarCarritoVisual();
        tablaProductos.refresh();

        lblEstado.setText("Compra realizada.");
    }

    private void generarTicket(int total) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("ticket.txt"))) {
            pw.println("********** TICKET **********");
            for (var e : carrito.entrySet()) {
                pw.println(e.getKey().getNombre() + " x" + e.getValue()
                        + " = $" + (e.getKey().getPrecio() * e.getValue()));
            }
            pw.println("---------------------------");
            pw.println("TOTAL: $" + total);
            pw.println("****************************");
        } catch (Exception e) {
            lblEstado.setText("Error generando ticket.");
        }
    }

    private void actualizarStock() {
        for (var e : carrito.entrySet()) {
            Producto p = e.getKey();
            p.setStock(p.getStock() - e.getValue());
        }
        repo.guardar(new ArrayList<>(productos));
    }
}