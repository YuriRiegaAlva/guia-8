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
├── Guía08_LP3_2024_rub.pdf
└── .gitignore
```

---

## Ejercicios Propuestos

### Ejercicio 1: Transacciones JDBC con Clave de Seguridad
Ubicación: `ejercicios/ejercicio1`

- Base de datos SQLite (`data/ejercicio1.db`) con tabla de 4 campos (`id`, `nombre`, `precio`, `stock`).
- CRUD completo por consola usando `PreparedStatement`.
- Al realizar cualquier modificación (insertar, actualizar o borrar), el sistema solicita una clave de confirmación (`1234`).
  - Clave correcta: `con.commit()`
  - Clave incorrecta / error: `con.rollback()`

Ejecución:
```bash
cd ejercicios/ejercicio1
make run
```

---

### Ejercicio 2: Gestor en Memoria y Consultas Dinámicas
Ubicación: `ejercicios/ejercicio2`

- Extrae registros de SQLite (`data/ejercicio2.db`) hacia un arreglo de objetos `Producto[]`.
- Emula motor de consultas SQL íntegramente desde Java con parámetros en tiempo de ejecución:
  - **a) Proyección**: Selección de campos a mostrar (`id, nombre, precio, stock` o `*`).
  - **b) Filtrado**: Condición WHERE con operadores (`=`, `!=`, `>`, `<`, `>=`, `<=`, `like`).
  - **c) Ordenamiento**: ORDER BY ascendente o descendente según cualquier atributo.
  - **d) Límite**: Cláusula LIMIT para restringir el número de filas.

Ejecución:
```bash
cd ejercicios/ejercicio2
make run
```
