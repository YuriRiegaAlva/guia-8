import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AdministradorProductosTransacciones {

    private static final String URL_BD = "jdbc:sqlite:data/ejercicio1.db";
    private static final String CLAVE_SEGURIDAD = "1234";

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        try {
            crearTabla();

            int opcion = 0;
            do {
                System.out.println("\n=== MENÚ DE PRODUCTOS (TRANSACCIONES CON CLAVE) ===");
                System.out.println("1. Ingresar nuevo producto");
                System.out.println("2. Mostrar todos los productos");
                System.out.println("3. Actualizar producto");
                System.out.println("4. Borrar producto");
                System.out.println("5. Salir");
                System.out.print("Seleccione una opción: ");

                while (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.next();
                }
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese ID: ");
                        int idIns = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese Nombre: ");
                        String nombreIns = scanner.nextLine();
                        System.out.print("Ingrese Precio: ");
                        double precioIns = scanner.nextDouble();
                        System.out.print("Ingrese Stock: ");
                        int stockIns = scanner.nextInt();
                        scanner.nextLine();

                        insertarProducto(idIns, nombreIns, precioIns, stockIns, scanner);
                        break;

                    case 2:
                        mostrarProductos();
                        break;

                    case 3:
                        System.out.print("Ingrese el ID del producto a actualizar: ");
                        int idAct = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Ingrese el NUEVO Nombre: ");
                        String nuevoNombre = scanner.nextLine();
                        System.out.print("Ingrese el NUEVO Precio: ");
                        double nuevoPrecio = scanner.nextDouble();
                        System.out.print("Ingrese el NUEVO Stock: ");
                        int nuevoStock = scanner.nextInt();
                        scanner.nextLine();

                        actualizarProducto(idAct, nuevoNombre, nuevoPrecio, nuevoStock, scanner);
                        break;

                    case 4:
                        System.out.print("Ingrese el ID del producto a borrar: ");
                        int idEli = scanner.nextInt();
                        scanner.nextLine();

                        borrarProducto(idEli, scanner);
                        break;

                    case 5:
                        System.out.println("¡Saliendo de la aplicación!");
                        break;

                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 5);

        } catch (SQLException e) {
            System.out.println("Error general en la base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS productos ("
                   + " id INTEGER PRIMARY KEY,"
                   + " nombre TEXT NOT NULL,"
                   + " precio REAL NOT NULL,"
                   + " stock INTEGER NOT NULL"
                   + ");";

        try (Connection con = DriverManager.getConnection(URL_BD);
             Statement stmt = con.createStatement()) {
            stmt.execute(sql);
            System.out.println("Verificación de la tabla 'productos' exitosa.");
        }
    }

    private boolean confirmarTransaccion(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Ingrese la clave para confirmar la operación: ");
        String clave = scanner.nextLine();

        if (CLAVE_SEGURIDAD.equals(clave)) {
            con.commit();
            System.out.println("Clave correcta. Transacción confirmada (COMMIT).");
            return true;
        } else {
            con.rollback();
            System.out.println("Clave incorrecta. Transacción revertida (ROLLBACK).");
            return false;
        }
    }

    private void insertarProducto(int id, String nombre, double precio, int stock, Scanner scanner) throws SQLException {
        String sql = "INSERT OR IGNORE INTO productos (id, nombre, precio, stock) VALUES (?, ?, ?, ?);";

        try (Connection con = DriverManager.getConnection(URL_BD)) {
            con.setAutoCommit(false);

            try (PreparedStatement pStmt = con.prepareStatement(sql)) {
                pStmt.setInt(1, id);
                pStmt.setString(2, nombre);
                pStmt.setDouble(3, precio);
                pStmt.setInt(4, stock);

                int filasAfectadas = pStmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Operación preparada: Producto con ID=" + id + " listo para guardar.");
                    confirmarTransaccion(con, scanner);
                } else {
                    System.out.println("No se insertó porque el ID=" + id + " ya existe en la base de datos.");
                    con.rollback();
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error en la inserción. Transacción revertida (ROLLBACK): " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private void mostrarProductos() throws SQLException {
        String sql = "SELECT id, nombre, precio, stock FROM productos ORDER BY id ASC;";

        try (Connection con = DriverManager.getConnection(URL_BD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println(String.format("%-6s | %-25s | %-10s | %-6s", "ID", "NOMBRE", "PRECIO", "STOCK"));
            System.out.println("---------------------------------------------------------");

            boolean hayDatos = false;
            while (rs.next()) {
                hayDatos = true;
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                System.out.println(String.format("%-6s | %-25s | %-10.2f | %-6s", id, nombre, precio, stock));
            }

            if (!hayDatos) {
                System.out.println("No hay registros en la tabla.");
            }
        }
    }

    private void actualizarProducto(int id, String nuevoNombre, double nuevoPrecio, int nuevoStock, Scanner scanner) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?;";

        try (Connection con = DriverManager.getConnection(URL_BD)) {
            con.setAutoCommit(false);

            try (PreparedStatement pStmt = con.prepareStatement(sql)) {
                pStmt.setString(1, nuevoNombre);
                pStmt.setDouble(2, nuevoPrecio);
                pStmt.setInt(3, nuevoStock);
                pStmt.setInt(4, id);

                int filasAfectadas = pStmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Operación preparada: Producto con ID=" + id + " listo para actualizar.");
                    confirmarTransaccion(con, scanner);
                } else {
                    System.out.println("No se encontró ningún producto con ID=" + id + ".");
                    con.rollback();
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error en la actualización. Transacción revertida (ROLLBACK): " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private void borrarProducto(int id, Scanner scanner) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?;";

        try (Connection con = DriverManager.getConnection(URL_BD)) {
            con.setAutoCommit(false);

            try (PreparedStatement pStmt = con.prepareStatement(sql)) {
                pStmt.setInt(1, id);

                int filasAfectadas = pStmt.executeUpdate();

                if (filasAfectadas > 0) {
                    System.out.println("Operación preparada: Producto con ID=" + id + " listo para eliminar.");
                    confirmarTransaccion(con, scanner);
                } else {
                    System.out.println("No se encontró ningún producto con ID=" + id + ".");
                    con.rollback();
                }
            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error en la eliminación. Transacción revertida (ROLLBACK): " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
