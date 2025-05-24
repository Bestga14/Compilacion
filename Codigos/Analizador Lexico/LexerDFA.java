package LenguajeShibaInu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LexerDFA {

    // Tipos de token
    enum TokenType {
        PALABRA_CLAVE, IDENTIFICADOR, NUMERO, OPERADOR, CADENA, ERROR
    }

    // Estados finales válidos
    static Map<Integer, TokenType> estadosFinales = Map.of(
            1, TokenType.IDENTIFICADOR,
            2, TokenType.NUMERO,
            3, TokenType.OPERADOR,
            4, TokenType.OPERADOR,
            5, TokenType.CADENA
    );

    // Palabras clave
    static Set<String> palabrasClave = Set.of("henka", "insatsu", "moshi");

    // Clasificar el tipo de caracter
    static int tipoCaracter(char c) {
        if (Character.isLetter(c)) return 0;
        if (Character.isDigit(c)) return 1;
        if (Character.isWhitespace(c)) return 2;
        if (c == '=') return 3;
        if (c == '!') return 4;
        if (c == '<') return 5;
        if (c == '>') return 6;
        if (c == '"') return 7;
        return 8;
    }

    // Matriz de transiciones: estadoActual x tipoCaracter → nuevoEstado
    static int[][] matriz = new int[][]{
            // L, D,  _,  =,  !,  <,  >,  ",  otro
            { 1, 2,  0,  3, 10,  4,  4,  6, -1 }, // Estado 0: inicio
            { 1, 1, -1, -1, -1, -1, -1, -1, -1 }, // Estado 1: identificador
            { -1, 2, -1, -1, -1, -1, -1, -1, -1 }, // Estado 2: número
            { -1, -1, -1, 4, -1, -1, -1, -1, -1 }, // Estado 3: '=' encontrado
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // Estado 4: operador relacional final
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // Estado 5: (libre)
            { 6, 6, 6, 6, 6, 6, 6, 7, 6 },         // Estado 6: dentro de cadena
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // Estado 7: fin de cadena
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 }, // libre
            { -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, 4, -1, -1, -1, -1, -1 }  // Estado 10: '!' encontrado
    };

    static void analizar(String codigo) {
        List<String> errores = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        Set<String> palabrasClave = Set.of("henka", "insatsu", "moshi");

        String[] lineas = codigo.split("\n");

        for (int linea = 0; linea < lineas.length; linea++) {
            String lineaTexto = lineas[linea].trim();
            if (lineaTexto.isEmpty()) continue;

            String[] partes = lineaTexto.split("\\s+");
            String primerToken = partes[0];

            if (!palabrasClave.contains(primerToken)) {
                errores.add("Error(id=E001, type=No es palabra clave, position=0, line=" + (linea + 1) +
                        ", desc=La palabra no es palabra clave : " + primerToken + ")");
                continue;
            }

            // ------------------- ANÁLISIS LÉXICO -------------------
            int pos = 0;
            while (pos < lineaTexto.length()) {
                int estado = 0;
                int inicio = pos;
                StringBuilder lexema = new StringBuilder();

                while (pos < lineaTexto.length()) {
                    char c = lineaTexto.charAt(pos);
                    int tipo = tipoCaracter(c);
                    int siguiente = (estado >= 0 && tipo < matriz[0].length) ? matriz[estado][tipo] : -1;
                    if (siguiente == -1) break;
                    estado = siguiente;
                    lexema.append(c);
                    pos++;
                    if (estado == 7) break; // cadena completa
                }

                if (lexema.length() == 0) {
                    pos++;
                    continue;
                }

                if (estado == 7) {
                    tokens.add("Token(type=CADENA, value=" + lexema + ", position=" + inicio + ")");
                } else if (estadosFinales.containsKey(estado)) {
                    TokenType tipo = estadosFinales.get(estado);
                    String valor = lexema.toString();
                    if (tipo == TokenType.IDENTIFICADOR && palabrasClave.contains(valor)) {
                        tipo = TokenType.PALABRA_CLAVE;
                    }
                    tokens.add("Token(type=" + tipo + ", value=" + valor + ", position=" + inicio + ")");
                } else {
                    errores.add("Error(id=E999, type=TOKEN_INVALIDO, position=" + inicio + ", line=" + (linea + 1) +
                            ", desc=Token no reconocido: " + lexema + ")");
                }

                while (pos < lineaTexto.length() && Character.isWhitespace(lineaTexto.charAt(pos))) pos++;
            }

            // ------------------- ANÁLISIS SINTÁCTICO -------------------
            if (primerToken.equals("henka")) {
                if (!lineaTexto.matches("henka\\s+\\w+\\s*=\\s*\\d+")) {
                    errores.add("Error(id=E004, type=SINTAXIS_INVALIDA, line=" + (linea + 1) +
                            ", desc=Formato incorrecto para definición de variable. Se esperaba: henka x = 12)");
                }
            } else if (primerToken.equals("insatsu")) {
                if (!lineaTexto.matches("insatsu\\s+\".*\"")) {
                    errores.add("Error(id=E005, type=SINTAXIS_INVALIDA, line=" + (linea + 1) +
                            ", desc=Formato incorrecto para impresión. Se esperaba: insatsu \"texto\")");
                }
            } else if (primerToken.equals("moshi")) {
                if (!lineaTexto.matches("moshi\\s+\\w+\\s+(==|!=|<|>)\\s+\\d+:")) {
                    errores.add("Error(id=E006, type=SINTAXIS_INVALIDA, line=" + (linea + 1) +
                            ", desc=Formato incorrecto para condición. Se esperaba: moshi x == 12:)");
                }
            }
        }

        System.out.println();
        System.out.println("------------Tabla de simbolos------------------");
        tokens.forEach(System.out::println);

        if (!errores.isEmpty()) {
            System.out.println();
            System.out.println("------------Tabla de Errores-------------------");
            errores.forEach(System.out::println);
        }
    }



    public static void main(String[] args) {
        String prueba = "henka x = 12\ninsatsu \"Hola mundo\"\nmoshi x == 12:";
        String[] lineas = prueba.split("\\n");
        for (String linea : lineas) {
            analizar(linea);
        }
    }
}

