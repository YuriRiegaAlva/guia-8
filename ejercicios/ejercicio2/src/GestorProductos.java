import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GestorProductos {

    private static final String URL_BD = "jdbc:sqlite:data/ejercicio2.db";
    private Producto[] productos;

    public GestorProductos() {
        this.productos = new Producto[0];
    }

    public void inicializarBaseDeDatos() throws SQLException {
        String crearTablaSql = "CREATE TABLE IF NOT EXISTS productos ("
                             + " id INTEGER PRIMARY KEY,"
                             + " nombre TEXT NOT NULL,"
                             + " precio REAL NOT NULL,"
                             + " stock INTEGER NOT NULL"
                             + ");";

        try (Connection con = DriverManager.getConnection(URL_BD);
             Statement stmt = con.createStatement()) {
            stmt.execute(crearTablaSql);
            
        }
    }

    public void cargarDesdeBaseDeDatos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, stock FROM productos;";

        try (Connection con = DriverManager.getConnection(URL_BD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );
                lista.add(p);
            }
        }

        this.productos = lista.toArray(new Producto[0]);
        System.out.println("Se cargaron " + this.productos.length + " productos en el arreglo de memoria.");
    }

    public int getCantidadRegistros() {
        return productos.length;
    }

    public void consultar(
        String camposProyeccion,
        String campoCondicion,
        String operador,
        String valorCondicion,
        String campoOrden,
        boolean ascendente,
        int limite
    ) {
        if (productos.length == 0) {
            System.out.println("No hay productos cargados en memoria.");
            return;
        }

        // 1. Filtrado (WHERE)
        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : productos) {
            if (cumpleCondicion(p, campoCondicion, operador, valorCondicion)) {
                filtrados.add(p);
            }
        }

        if (filtrados.isEmpty()) {
            System.out.println("Ningún registro cumple con la condición especificada.");
            return;
        }

        Producto[] resultado = filtrados.toArray(new Producto[0]);

        // 2. Ordenamiento (ORDER BY)
        Comparator<Producto> comparador = obtenerComparador(campoOrden);
        if (comparador != null) {
            if (!ascendente) {
                comparador = comparador.reversed();
            }
            Arrays.sort(resultado, comparador);
        }

        // 3. Límite (LIMIT)
        int totalAMostrar = resultado.length;
        if (limite > 0 && limite < totalAMostrar) {
            totalAMostrar = limite;
        }

        // 4. Proyección de campos (SELECT campos)
        imprimirProyeccion(resultado, totalAMostrar, camposProyeccion);
    }

    private boolean cumpleCondicion(Producto p, String campo, String operador, String valor) {
        if (campo == null || campo.trim().isEmpty() || "NINGUNO".equalsIgnoreCase(campo)) {
            return true;
        }

        campo = campo.trim().toLowerCase();
        operador = operador != null ? operador.trim() : "=";
        valor = valor != null ? valor.trim() : "";

        try {
            switch (campo) {
                case "id": {
                    int val = Integer.parseInt(valor);
                    return compararNumeros(p.getId(), val, operador);
                }
                case "precio": {
                    double val = Double.parseDouble(valor);
                    return compararNumeros(p.getPrecio(), val, operador);
                }
                case "stock": {
                    int val = Integer.parseInt(valor);
                    return compararNumeros(p.getStock(), val, operador);
                }
                case "nombre": {
                    String pNombre = p.getNombre().toLowerCase();
                    String buscado = valor.toLowerCase();
                    if ("=".equals(operador) || "==".equals(operador)) {
                        return pNombre.equals(buscado);
                    } else if ("!=".equals(operador)) {
                        return !pNombre.equals(buscado);
                    } else if ("like".equalsIgnoreCase(operador) || "contiene".equalsIgnoreCase(operador)) {
                        return pNombre.contains(buscado);
                    }
                    return false;
                }
                default:
                    return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Valor de comparación numérico no válido: " + valor);
            return false;
        }
    }

    private boolean compararNumeros(double a, double b, String operador) {
        switch (operador) {
            case "=":
            case "==":
                return Double.compare(a, b) == 0;
            case "!=":
                return Double.compare(a, b) != 0;
            case ">":
                return a > b;
            case ">=":
                return a >= b;
            case "<":
                return a < b;
            case "<=":
                return a <= b;
            default:
                return true;
        }
    }

    private Comparator<Producto> obtenerComparador(String campoOrden) {
        if (campoOrden == null || campoOrden.trim().isEmpty() || "NINGUNO".equalsIgnoreCase(campoOrden)) {
            return null;
        }
        switch (campoOrden.trim().toLowerCase()) {
            case "id":
                return Comparator.comparingInt(Producto::getId);
            case "nombre":
                return Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER);
            case "precio":
                return Comparator.comparingDouble(Producto::getPrecio);
            case "stock":
                return Comparator.comparingInt(Producto::getStock);
            default:
                return null;
        }
    }

    private void imprimirProyeccion(Producto[] lista, int cantidad, String camposStr) {
        boolean mostrarTodos = camposStr == null || camposStr.trim().isEmpty() || "*".equals(camposStr.trim());
        List<String> columnas = new ArrayList<>();

        if (mostrarTodos) {
            columnas.add("id");
            columnas.add("nombre");
            columnas.add("precio");
            columnas.add("stock");
        } else {
            String[] tokens = camposStr.split(",");
            for (String t : tokens) {
                String c = t.trim().toLowerCase();
                if (c.equals("id") || c.equals("nombre") || c.equals("precio") || c.equals("stock")) {
                    columnas.add(c);
                }
            }
            if (columnas.isEmpty()) {
                columnas.add("id");
                columnas.add("nombre");
                columnas.add("precio");
                columnas.add("stock");
            }
        }

        StringBuilder cabecera = new StringBuilder();
        StringBuilder divisor = new StringBuilder();

        for (String c : columnas) {
            switch (c) {
                case "id":
                    cabecera.append(String.format("%-6s | ", "ID"));
                    divisor.append("-------+");
                    break;
                case "nombre":
                    cabecera.append(String.format("%-25s | ", "NOMBRE"));
                    divisor.append("---------------------------+");
                    break;
                case "precio":
                    cabecera.append(String.format("%-10s | ", "PRECIO"));
                    divisor.append("-----------+");
                    break;
                case "stock":
                    cabecera.append(String.format("%-6s | ", "STOCK"));
                    divisor.append("-------+");
                    break;
            }
        }

        System.out.println(cabecera.toString());
        System.out.println(divisor.toString());

        for (int i = 0; i < cantidad; i++) {
            Producto p = lista[i];
            StringBuilder fila = new StringBuilder();
            for (String c : columnas) {
                switch (c) {
                    case "id":
                        fila.append(String.format("%-6d | ", p.getId()));
                        break;
                    case "nombre":
                        fila.append(String.format("%-25s | ", p.getNombre()));
                        break;
                    case "precio":
                        fila.append(String.format("%-10.2f | ", p.getPrecio()));
                        break;
                    case "stock":
                        fila.append(String.format("%-6d | ", p.getStock()));
                        break;
                }
            }
            System.out.println(fila.toString());
        }
        System.out.println("Total registros mostrados: " + cantidad);
    }
}
