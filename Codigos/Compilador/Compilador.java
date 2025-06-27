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

        LexerShibaInu lexer = new LexerShibaInu();
        lexer.imprimirCodigo(codigo);
        lexer.analizar(codigo);

        System.out.println("------------Analizador Léxico--------------------");
        lexer.imprimirTokens();
        lexer.imprimirErrores();

        List<String> simbolos = lexer.obtenerSimbolosSimplificados();

        System.out.println("------------Analizador Sintáctico---------------");
        TablaParser.imprimirTodo(simbolos);
        boolean resultado = TablaParser.analizar(simbolos);

        if (resultado) {
            System.out.println("Código válido sintácticamente.");
            System.out.println();

            // ANALIZADOR SEMÁNTICO
            AnalizadorSemantico semantico = new AnalizadorSemantico();
            List<LexerShibaInu.Token> tokens = lexer.obtenerTokens();
            semantico.analizar(tokens);
            semantico.mostrarErrores();
        } else {
            System.out.println("Error sintáctico.");
        }
    }
}
