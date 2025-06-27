package LenguajeShibaInu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EjecutorArchivo {
    private static final Map<String, Integer> variables = new HashMap<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java EjecutorArchivo archivo.shiba");
            return;
        }

        String archivo = args[0];

        try {
            List<String> lineas = Files.readAllLines(Paths.get(archivo));
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.isEmpty()) continue;
                ejecutarLinea(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void ejecutarLinea(String linea) {
        if (linea.startsWith("insatsu")) {
            // insatsu "hola mundo"
            int idx = linea.indexOf("\"");
            if (idx != -1) {
                String mensaje = linea.substring(idx + 1, linea.lastIndexOf("\""));
                System.out.println(mensaje);
            } else {
                System.out.println("Error de sintaxis en insatsu.");
            }

        } else if (linea.startsWith("henka")) {
            // henka x = 10
            String[] partes = linea.split("\\s+");
            if (partes.length == 4 && partes[2].equals("=")) {
                String nombreVar = partes[1];
                try {
                    int valor = Integer.parseInt(partes[3]);
                    variables.put(nombreVar, valor);
                } catch (NumberFormatException e) {
                    System.out.println("Error: valor no numérico en línea: " + linea);
                }
            } else {
                System.out.println("Error de sintaxis en henka.");
            }

        } else if (linea.startsWith("moshi")) {
            // moshi x != 2 : insatsu "correcto"
            String[] partes = linea.split(":");
            if (partes.length != 2) {
                System.out.println("Error de sintaxis en moshi.");
                return;
            }

            String condicion = partes[0].substring(6).trim(); // quitar 'moshi '
            String instruccion = partes[1].trim();

            String[] comp = condicion.split("\\s+");
            if (comp.length != 3) {
                System.out.println("Condición inválida en moshi.");
                return;
            }

            String var = comp[0];
            String operador = comp[1];
            String valorStr = comp[2];

            Integer varVal = variables.get(var);
            if (varVal == null) {
                System.out.println("Variable no definida: " + var);
                return;
            }

            try {
                int valor = Integer.parseInt(valorStr);
                boolean cumple = switch (operador) {
                    case "==" -> varVal == valor;
                    case "!=" -> varVal != valor;
                    case ">" -> varVal > valor;
                    case "<" -> varVal < valor;
                    default -> false;
                };

                if (cumple) {
                    ejecutarLinea(instruccion); // ejecuta la instrucción si se cumple
                }

            } catch (NumberFormatException e) {
                System.out.println("Valor no numérico en condición.");
            }
        } else {
            System.out.println("Instrucción no reconocida: " + linea);
        }
    }
}
