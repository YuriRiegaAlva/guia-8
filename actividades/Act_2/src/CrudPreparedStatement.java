import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class CrudPreparedStatement {

    private static final String URL_BD = "jdbc:sqlite:data/actividad2.db";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            crearTabla();

            int opcion = 0;
            do {
                System.out.println("\n=== MENÚ CRUD ===");
                System.out.println("1. Insertar nuevo registro");
                System.out.println("2. Listar registros por edad mínima");
                System.out.println("3. Actualizar edad de un registro");
                System.out.println("4. Eliminar un registro");
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
                        System.out.print("Ingrese Edad: ");
                        int edadIns = scanner.nextInt();
                        scanner.nextLine();

                        insertarRegistro(idIns, nombreIns, edadIns);
                        break;

                    case 2:
                        System.out.print("Ingrese Edad Mínima: ");
                        int minEdad = scanner.nextInt();
                        scanner.nextLine();
                        listarRegistrosEdad(minEdad);
                        break;

                    case 3:
                        System.out.print("Ingrese el ID del registro a actualizar: ");
                        int idAct = scanner.nextInt();
                        System.out.print("Ingrese la NUEVA Edad: ");
                        int nuevaEdad = scanner.nextInt();
                        scanner.nextLine();

                        actualizarEdad(idAct, nuevaEdad);
                        break;

                    case 4:
                        System.out.print("Ingrese el ID del registro a eliminar: ");
                        int idEli = scanner.nextInt();
                        scanner.nextLine();

                        eliminarRegistro(idEli);
                        break;

                    case 5:
                        System.out.println("¡Saliendo de la aplicación!");
                        break;

                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 5);

        } catch (SQLException e) {
            System.out.println("Error crítico en la base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static void crearTabla() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS emp ("
                   + " id INTEGER PRIMARY KEY,"
                   + " name TEXT NOT NULL,"
                   + " age INTEGER NOT NULL"
                   + ");";

        try (Connection con = DriverManager.getConnection(URL_BD);
             Statement stmt = con.createStatement()) {
            stmt.execute(sql);
            System.out.println("Verificación de la tabla 'emp' exitosa.");
        }
    }

    public static void insertarRegistro(int id, String nombre, int edad) throws SQLException {
        String sql = "INSERT OR IGNORE INTO emp (id, name, age) VALUES (?, ?, ?);";

        try (Connection con = DriverManager.getConnection(URL_BD);
             PreparedStatement pStmt = con.prepareStatement(sql)) {
            pStmt.setInt(1, id);
            pStmt.setString(2, nombre);
            pStmt.setInt(3, edad);

            int filasAfectadas = pStmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Registro con ID=" + id + " agregado exitosamente.");
            } else {
                System.out.println("No se insertó: el ID=" + id + " ya existía.");
            }
        }
    }

    public static void listarRegistrosEdad(int minEdad) throws SQLException {
        String sql = "SELECT id, name, age FROM emp WHERE age >= ? ORDER BY age ASC;";

        try (Connection con = DriverManager.getConnection(URL_BD);
             PreparedStatement pStmt = con.prepareStatement(sql)) {
            pStmt.setInt(1, minEdad);

            try (ResultSet rs = pStmt.executeQuery()) {
                System.out.println(String.format("%-6s | %-25s | %-6s", "ID", "NOMBRE", "EDAD"));
                System.out.println("----------------------------------------------");

                boolean hayDatos = false;
                while (rs.next()) {
                    hayDatos = true;
                    int id = rs.getInt("id");
                    String nombre = rs.getString("name");
                    int edad = rs.getInt("age");
                    System.out.println(String.format("%-6s | %-25s | %-6s", id, nombre, edad));
                }

                if (!hayDatos) {
                    System.out.println("No hay registros que cumplan con la condición.");
                }
            }
        }
    }

    public static void actualizarEdad(int id, int nEdad) throws SQLException {
        String sql = "UPDATE emp SET age = ? WHERE id = ?;";

        try (Connection con = DriverManager.getConnection(URL_BD);
             PreparedStatement pStmt = con.prepareStatement(sql)) {
            pStmt.setInt(1, nEdad);
            pStmt.setInt(2, id);

            int filasAfectadas = pStmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("ID " + id + " actualizado a nueva edad: " + nEdad);
            } else {
                System.out.println("No se encontró el registro con ID=" + id + " para actualizar.");
            }
        }
    }

    public static void eliminarRegistro(int id) throws SQLException {
        String sql = "DELETE FROM emp WHERE id = ?;";

        try (Connection con = DriverManager.getConnection(URL_BD);
             PreparedStatement pStmt = con.prepareStatement(sql)) {
            pStmt.setInt(1, id);

            int filasAfectadas = pStmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Registro con ID=" + id + " eliminado con éxito.");
            } else {
                System.out.println("No se encontró el registro con ID=" + id + " para eliminar.");
            }
        }
    }
}