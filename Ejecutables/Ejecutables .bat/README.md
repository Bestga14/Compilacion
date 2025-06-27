# Uso de comandos: `shibainu` y `hoeru`

Este documento describe cómo utilizar los comandos principales del proyecto y el propósito de los archivos `.bat` incluidos.

---

## Archivos `.bat`

Los archivos ubicados en la carpeta `Ejecutables\Ejecutables .bat` aseguran el funcionamiento correcto del código. 

---

## Comando `shibainu`

Este comando realiza los siguientes procesos:

- Análisis Léxico
- Análisis Sintáctico
- Análisis Semántico
- Compilación del código fuente para su posterior ejecución

### Ejemplo de uso

1. Abrir una terminal.
2. Navegar a la ubicación exacta del archivo fuente.
3. Ejecutar el siguiente comando en bash:

shibainu nombre_de_archivo.shiba

Esto mostrará en consola los resultados de los análisis respectivos.

---

## Comando `hoeru`

Este comando se encarga de:

- Generación de código
- Ejecución del mismo

### Ejemplo de uso

Una vez ejecutado shibainu, usar en bash:

hoeru nombre_de_archivo.shiba

Esto ejecutará el código y mostrará su salida en la consola.

Importante: Es necesario ejecutar shibainu antes de hoeru para evitar errores.

---

## Notas adicionales

- Es necesario editar las rutas dentro de los ejecutables .bat, cambiando la ruta al archivo .jar correspondiente.

- Puede que debas agregar la carpeta de ejecutables .bat al PATH de variables de entorno del sistema.