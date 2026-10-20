import java.sql.SQLException;
import java.util.Scanner;

public class ConsolaConsultaProductos {

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        GestorProductos gestor = new GestorProductos();

        try {
            gestor.inicializarBaseDeDatos();
            gestor.cargarDesdeBaseDeDatos();

            int opcion = 0;
            do {
                System.out.println("\n=== MENÚ CONSULTAS EN MEMORIA (GESTOR PRODUCTOS) ===");
                System.out.println("1. Realizar consulta personalizada (a, b, c, d)");
                System.out.println("2. Recargar datos desde la base de datos");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");

                while (!scanner.hasNextInt()) {
                    System.out.println("Por favor, ingrese un número válido.");
                    scanner.next();
                }
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        ejecutarConsultaInteractiva(gestor, scanner);
                        break;
                    case 2:
                        gestor.cargarDesdeBaseDeDatos();
                        break;
                    case 3:
                        System.out.println("¡Saliendo del programa!");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            } while (opcion != 3);

        } catch (SQLException e) {
            System.out.println("Error con la base de datos SQLite: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private void ejecutarConsultaInteractiva(GestorProductos gestor, Scanner scanner) {
        System.out.println("\n--- PARÁMETROS DE LA CONSULTA ---");

        // a) Campos a mostrar
        System.out.println("a) Ingrese los campos a mostrar separados por coma (id, nombre, precio, stock) o * para todos:");
        System.out.print("Campos: ");
        String campos = scanner.nextLine().trim();
        if (campos.isEmpty()) {
            campos = "*";
        }

        // b) Condición
        System.out.println("b) ¿Desea aplicar condición de filtrado? (id, nombre, precio, stock o ENTER para omitir):");
        System.out.print("Campo condición: ");
        String campoCondicion = scanner.nextLine().trim();

        String operador = "=";
        String valorCondicion = "";
        if (!campoCondicion.isEmpty() && !"NINGUNO".equalsIgnoreCase(campoCondicion)) {
            System.out.print("Operador (=, !=, >, >=, <, <=, like): ");
            operador = scanner.nextLine().trim();
            System.out.print("Valor a comparar: ");
            valorCondicion = scanner.nextLine().trim();
        }

        // c) Ordenamiento
        System.out.println("c) Ingrese el campo para ordenar (id, nombre, precio, stock o ENTER para no ordenar):");
        System.out.print("Campo orden: ");
        String campoOrden = scanner.nextLine().trim();

        boolean ascendente = true;
        if (!campoOrden.isEmpty() && !"NINGUNO".equalsIgnoreCase(campoOrden)) {
            System.out.print("¿Orden ascendente? (s/n, por defecto s): ");
            String sentido = scanner.nextLine().trim().toLowerCase();
            if (sentido.startsWith("n")) {
                ascendente = false;
            }
        }

        // d) Límite
        System.out.println("d) Cantidad máxima de registros a mostrar (0 o ENTER para mostrar todos):");
        System.out.print("Límite: ");
        String limiteStr = scanner.nextLine().trim();
        int limite = 0;
        if (!limiteStr.isEmpty()) {
            try {
                limite = Integer.parseInt(limiteStr);
            } catch (NumberFormatException e) {
                System.out.println("Límite inválido, se mostrarán todos.");
                limite = 0;
            }
        }

        System.out.println("\n--- RESULTADO DE LA CONSULTA EN MEMORIA ---");
        gestor.consultar(campos, campoCondicion, operador, valorCondicion, campoOrden, ascendente, limite);
    }
}
