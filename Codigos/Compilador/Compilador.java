package LenguajeShibaInu;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Compilador {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Compilador <archivo.shiba>");
            return;
        }

        String archivo = args[0];
        String[] codigo;

        try {
            List<String> lineas = Files.readAllLines(Paths.get(archivo));
            codigo = lineas.toArray(new String[0]);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        // 1. Crear tabla de errores
        TablaErrores tablaErrores = new TablaErrores();

        // 2. Lexer
        LexerShibaInu lexer = new LexerShibaInu(tablaErrores);
        lexer.imprimirCodigo(codigo);
        lexer.analizar(codigo); // Guarda errores en tablaErrores

        System.out.println();
        System.out.println("------------Analizador Léxico--------------------");
        lexer.imprimirTokens();

        // 3. Sintáctico
        System.out.println();
        System.out.println("------------Analizador Sintáctico---------------");
        List<String> simbolos = lexer.obtenerSimbolosSimplificados();
        TablaParser.analizar(simbolos, tablaErrores); // Pasar tabla de errores también
        TablaParser.imprimirTodo(simbolos);

        // 4. Semántico (solo si no hay errores sintácticos)
        if (!tablaErrores.tieneErrores()) {
            AnalizadorSemantico semantico = new AnalizadorSemantico(tablaErrores);
            semantico.analizar(lexer.obtenerTokens()); // Guarda errores semánticos
        }

        // 5. Mostrar errores (si hay)
        if (tablaErrores.tieneErrores()) {
            tablaErrores.mostrarErrores();
        } else {
            System.out.println();
            System.out.println("Compilación exitosa.");
        }
    }
}
