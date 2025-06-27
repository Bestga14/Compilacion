package LenguajeShibaInu;

import java.util.*;

public class LexerShibaInu {
    enum TokenType {
        PALABRA_CLAVE, IDENTIFICADOR, OPERADOR, NUMERO, CADENA, DELIMITADOR
    }

    enum ErrorType {
        NO_PALABRA_CLAVE, IDENTIFICADOR_INVALIDO, CADENA_NO_TERMINADA, OPERADOR_INVALIDO
    }

    static class Token {
        TokenType type;
        String value;
        int position;

        Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }

        public TokenType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }



        public String toString() {
            return String.format("Token(type=%s, value=%s, position=%d)", type, value, position);
        }
    }

    static class Error {
        String id;
        ErrorType type;
        int position;
        int line;
        String desc;

        Error(String id, ErrorType type, int position, int line, String desc) {
            this.id = id;
            this.type = type;
            this.position = position;
            this.line = line;
            this.desc = desc;
        }

        public String toString() {
            return String.format("Error(id=%s, type=%s, position=%d, line=%d, desc=%s)", id, type, position, line, desc);
        }
    }

    // Palabras clave válidas
    private static final Set<String> keywords = new HashSet<>(Arrays.asList("henka", "insatsu", "moshi"));
    // Operadores válidos
    private static final Set<String> operadores = new HashSet<>(Arrays.asList("=", "==", "!=", "<", ">"));

    public static List<Token> tokens = new ArrayList<>();
    private TablaErrores tablaErrores;

    public LexerShibaInu(TablaErrores tablaErrores) {
        this.tablaErrores = tablaErrores;
    }

    public void analizar(String[] lineas) {
        for (int ln = 0; ln < lineas.length; ln++) {
            String linea = lineas[ln];
            String[] parte = linea.trim().split("\\s+");
            List<String> partes = tokenizeLinea(linea);
            if (partes.size() == 0) continue;

            int pos = 0;
            if (!keywords.contains(partes.get(0))) {
                tablaErrores.add(new ErrorCompilador("E001", "Lexico",
                        ErrorType.NO_PALABRA_CLAVE.toString(), 0, ln + 1,
                        "La palabra no es palabra clave: " + partes.get(0)));
                continue;
            }

            tokens.add(new Token(TokenType.PALABRA_CLAVE, partes.get(0), linea.indexOf(partes.get(0))));

            for (int i = 1; i < partes.size(); i++) {
                String p = partes.get(i);
                pos = linea.indexOf(p, pos);

                if (keywords.contains(p)) {
                    tokens.add(new Token(TokenType.PALABRA_CLAVE, p, pos));
                } else if (p.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                    tokens.add(new Token(TokenType.IDENTIFICADOR, p, pos));
                } else if (p.matches("==|!=|=|<|>")) {
                    tokens.add(new Token(TokenType.OPERADOR, p, pos));
                } else if (p.matches("[0-9]+")) {
                    tokens.add(new Token(TokenType.NUMERO, p, pos));
                } else if (p.matches("\".*\"")) {
                    tokens.add(new Token(TokenType.CADENA, p, pos));
                } else if (p.equals(":") && partes.get(0).equals("moshi")) {
                    tokens.add(new Token(TokenType.DELIMITADOR, ":", pos));
                } else if (p.startsWith("\"") && !p.endsWith("\"")) {
                    tablaErrores.add(new ErrorCompilador("E003", "Lexico",
                            ErrorType.CADENA_NO_TERMINADA.toString(), pos, ln + 1,
                            "Cadena no terminada: " + p));
                } else {
                    tablaErrores.add(new ErrorCompilador("E999", "Lexico",
                            ErrorType.IDENTIFICADOR_INVALIDO.toString(), pos, ln + 1,
                            "Token no reconocido: " + p));
                }
                pos += p.length();
            }
        }
    }


    private List<String> tokenizeLinea(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean enCadena = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '\"') {
                token.append(c);
                if (enCadena) {
                    // Fin de cadena
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                enCadena = !enCadena;
            } else if (enCadena) {
                token.append(c); // dentro de una cadena
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else if (c == '=' || c == '!' || c == '<' || c == '>') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }

                // Verificamos si es un operador doble como == o !=
                if ((c == '=' || c == '!') && i + 1 < linea.length() && linea.charAt(i + 1) == '=') {
                    tokens.add("" + c + "=");
                    i++;
                } else {
                    tokens.add("" + c);
                }
            } else if (c == ':') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(":");
            } else {
                token.append(c);
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    public List<String> obtenerSimbolosSimplificados() {
        List<String> tipos = new ArrayList<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case PALABRA_CLAVE:
                    tipos.add(token.getValue()); // Ej: henka, moshi, insatsu
                    break;
                case IDENTIFICADOR:
                    tipos.add("ID");
                    break;
                case NUMERO:
                    tipos.add("NUM");
                    break;
                case CADENA:
                    tipos.add("CADENA");
                    break;
                case OPERADOR:
                    tipos.add(token.getValue()); // Ej: ==, !=, <, >
                    break;
                case DELIMITADOR:
                    tipos.add(":"); // importante
                    break;
                default:
                    tipos.add(token.getType().name());
            }
        }
        tipos.add("$"); // Fin de cadena
        return tipos;
    }


    public void imprimirTokens() {
        //System.out.println();
        System.out.println("------------Tabla de Simbolos--------------------");
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
// Metodo para observar errores de manera local
//    public void imprimirErrores() {
//        if (errores.isEmpty()) {
//            System.out.println();
//        } else {
//            System.out.println();
//            System.out.println("------------Tabla de Errores-------------------");
//            for (Error e : errores) {
//                System.out.println(e);
//            }
//        }
//    }

    public void imprimirCodigo(String[] codigo) {
        System.out.println("------------Código Fuente----------------------");
        for (int i = 0; i < codigo.length; i++) {
            System.out.printf("Línea %d: %s\n", i + 1, codigo[i]);
        }
        System.out.println();
    }

    public List<Token> obtenerTokens() {
        return this.tokens; // suponiendo que tokens es una lista que guardas internamente
    }


    //public static void main(String[] args) {
    //    LexerShibaInu lexer = new LexerShibaInu();
    //    String[] codigo = {
    //            "henka x = 12",
    //            //"insatsu \"Hola mundo\"",
    //            //"moshi x == 5 : insatsu \"ok\"",
    //            //"print x", // error: no es palabra clave
    //            //"henka 1x = 2", // error: identificador inválido
    //            //"insatsu \"sin fin" // error: cadena no cerrada
    //    };
//
    //    lexer.imprimirCodigo(codigo);
    //    lexer.analizar(codigo);
    //    lexer.imprimirTokens();
    //    lexer.imprimirErrores();
    //}
}
