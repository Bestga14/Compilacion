package LenguajeShibaInu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TablaParser {
    // Tabla del parser LL(1)
    static Map<String, Map<String, List<String>>> tabla = new HashMap<>();

    private TablaErrores tablaErrores;

    public TablaParser(TablaErrores tablaErrores) {
        this.tablaErrores = tablaErrores;
    }

    // Inicializar tabla LL(1)
    static {
        tabla.put("S", Map.of(
                "henka", List.of("Stmt", "S"),
                "insatsu", List.of("Stmt", "S"),
                "moshi", List.of("Stmt", "S"),
                "$", List.of("ε")
        ));
        tabla.put("Stmt", Map.of(
                "henka", List.of("Asig"),
                "insatsu", List.of("Imp"),
                "moshi", List.of("Cond")
        ));
        tabla.put("Asig", Map.of(
                "henka", List.of("henka", "ID", "=", "NUM")
        ));
        tabla.put("Imp", Map.of(
                "insatsu", List.of("insatsu", "CADENA")
        ));
        tabla.put("Cond", Map.of(
                "moshi", List.of("moshi", "Comp", ":", "Stmt")
        ));
        tabla.put("Comp", Map.of(
                "ID", List.of("ID", "op", "NUM")
        ));
        tabla.put("op", Map.of(
                "==", List.of("=="),
                "!=", List.of("!="),
                "<", List.of("<"),
                ">", List.of(">")
        ));
    }

    // Analizador sintáctico LL(1)
    public static boolean analizar(List<String> tokens, TablaErrores tablaErrores) {
        Stack<String> pila = new Stack<>();
        pila.push("$");    // símbolo de fin
        pila.push("S");    // símbolo inicial

        int i = 0;

        while (!pila.isEmpty()) {
            String cima = pila.pop();
            String actual = tokens.get(i);

            if (cima.equals("ε")) continue;

            if (esTerminal(cima)) {
                if (cima.equals(actual)) {
                    i++; // avanzar en la entrada
                } else {
                    tablaErrores.add(new ErrorCompilador("E100", "Sintáctico", "TOKEN_ESPERADO"
                            , i,1, "Se esperaba '" + cima + "', pero se encontró '" + actual + "'"
                    ));
                    return false;
                }
            } else {
                Map<String, List<String>> fila = tabla.get(cima);
                if (fila != null && fila.containsKey(actual)) {
                    List<String> produccion = fila.get(actual);
                    for (int j = produccion.size() - 1; j >= 0; j--) {
                        pila.push(produccion.get(j));
                    }
                } else {
                    tablaErrores.add(new ErrorCompilador("E200", "Sintáctico", "REGLA_DESCONOCIDA"
                            , i,1, "No hay regla para [" + cima + ", " + actual + "]"
                    ));
                    return false;
                }
            }
        }

        if (i == tokens.size()) {
            System.out.println("✔ Análisis sintáctico exitoso.");
            return true;
        } else {
            System.out.println("Error: Tokens restantes sin procesar.");
            return false;
        }
    }

    // Verifica si un símbolo es terminal
    private static boolean esTerminal(String simbolo) {
        return !tabla.containsKey(simbolo);
    }

    public static void imprimirTodo(List<String> tokens){
        String input = "";
        for (String token: tokens) {
            input = input + token + " ";
        }
        System.out.println("Input: " + input);
        System.out.println();
    }

    // Ejemplo de uso
    //public static void main(String[] args) {
    //    // Caso válido
    //    List<String> tokens = List.of("henka", "ID", "=", "NUM", "insatsu", "CADENA", "$");
    //    imprimirTodo(tokens);
    //    analizar(tokens);
    //    System.out.println();
//
    //    // Caso con error
    //    List<String> tokensConError = List.of("henka", "ID", "NUM", "$"); // falta '='
    //    imprimirTodo(tokensConError);
    //    analizar(tokens);
    //    analizar(tokensConError);
    //}
}
