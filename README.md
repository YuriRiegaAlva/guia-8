# Guía 08: Bases de Datos SQLite y JDBC en Java

Laboratorio de Lenguajes de Programación 3 (UCSM).

## Estructura del Proyecto

El repositorio está organizado en dos módulos independientes:

```
guia-8/
├── actividades/          # Prácticas guiadas de la guía oficial
│   ├── Act_1/           # CRUD básico con Statement
│   ├── Act_2/           # CRUD parametrizado con PreparedStatement
│   └── Act_3/           # Transacciones explícitas (Commit / Rollback)
│
├── ejercicios/           # Ejercicios propuestos resueltos
│   ├── ejercicio1/      # CRUD transaccional con confirmación por clave
│   └── ejercicio2/      # Gestor de registros en memoria con consultas SQL en Java
│
└── .gitignore
```

---

## Actividades Desarrolladas

### Actividad 1: CRUD Básico con `Statement`
Ubicación: `actividades/Act_1`

- **Objetivo**: Conexión directa a base de datos SQLite embebida (`data/actividad1.db`) mediante el driver JDBC (`org.sqlite.JDBC`).
- **Implementación**: Creación de la tabla `emp (id INTEGER PRIMARY KEY, name TEXT, age INTEGER)` y ejecución de sentencias SQL directas con `java.sql.Statement`.
- **Operaciones**: Inserción de registros, consulta filtrada por edad, actualización de datos y borrado con salida formateada en consola.

### Actividad 2: CRUD Parametrizado con `PreparedStatement`
Ubicación: `actividades/Act_2`

- **Objetivo**: Implementación de buenas prácticas y seguridad frente a inyecciones SQL en consultas y manipulaciones relacionales.
- **Implementación**: Reemplazo de sentencias concatenadas por `java.sql.PreparedStatement` con comodines posicionales (`?`) asignados mediante `setInt()` y `setString()`.
- **Evolución**: Métodos modulares que propagan `SQLException` para delegar el control de errores al bloque principal.

### Actividad 3: Control Transaccional Explícito (`Commit` y `Rollback`)
Ubicación: `actividades/Act_3`

- **Objetivo**: Asegurar la atomicidad y la consistencia de los datos ante posibles fallos o inconsistencias durante la ejecución.
- **Implementación**: Desactivación del modo automático con `con.setAutoCommit(false)`.
- **Control**: Confirmación definitiva mediante `con.commit()` tras operaciones exitosas y reversión preventiva mediante `con.rollback()` dentro del bloque de captura de excepciones (`catch`), restaurando el estado con `con.setAutoCommit(true)` en el bloque `finally`.

---

## Ejercicios Propuestos

### Ejercicio 1: Transacciones JDBC con Clave de Seguridad
Ubicación: `ejercicios/ejercicio1`

- Base de datos SQLite (`data/ejercicio1.db`) con tabla de 4 campos (`id`, `nombre`, `precio`, `stock`).
- Menú interactivo por consola y operaciones CRUD completas mediante `PreparedStatement`.
- Al realizar cualquier modificación (insertar, actualizar o borrar), el sistema solicita una clave de confirmación (`1234`).
  - Clave correcta: `con.commit()` consolidando la transacción.
  - Clave incorrecta / error: `con.rollback()` revirtiendo cualquier modificación.

Ejecución:
```bash
cd ejercicios/ejercicio1
make run
```

---

### Ejercicio 2: Gestor en Memoria y Consultas Dinámicas
Ubicación: `ejercicios/ejercicio2`

- Extrae registros de SQLite (`data/ejercicio2.db`) hacia un arreglo de objetos `Producto[]` en memoria.
- Emula un motor de consultas SQL íntegramente desde Java con parámetros definidos por el usuario en tiempo de ejecución:
  - **a) Proyección**: Selección de campos a mostrar (`id, nombre, precio, stock` o `*`).
  - **b) Filtrado**: Condición WHERE con operadores (`=`, `!=`, `>`, `<`, `>=`, `<=`, `like`).
  - **c) Ordenamiento**: ORDER BY ascendente o descendente según cualquier atributo.
  - **d) Límite**: Cláusula LIMIT para restringir el número máximo de filas.

Ejecución:
```bash
cd ejercicios/ejercicio2
make run
```
