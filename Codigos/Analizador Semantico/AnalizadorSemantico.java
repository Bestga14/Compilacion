package LenguajeShibaInu;
import java.util.*;
public class AnalizadorSemantico {
    private Set<String> variablesDefinidas = new HashSet<>();
    private TablaErrores tablaErrores;

    public AnalizadorSemantico(TablaErrores tablaErrores) {
        this.tablaErrores = tablaErrores;
    }

    public void analizar(List<LexerShibaInu.Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            LexerShibaInu.Token token = tokens.get(i);

            switch (token.type) {
                case PALABRA_CLAVE:
                    switch (token.value) {
                        case "henka":
                            if (i + 1 < tokens.size() && tokens.get(i + 1).type == LexerShibaInu.TokenType.IDENTIFICADOR) {
                                String nombreVar = tokens.get(i + 1).value;
                                if (variablesDefinidas.contains(nombreVar)) {
                                    tablaErrores.add(new ErrorCompilador("E010", "Semántico", "VARIABLE_DECLARADA"
                                            , i,1, "Variable '" + nombreVar + "' ya fue declarada."
                                    ));
                                } else {
                                    variablesDefinidas.add(nombreVar);
                                }
                            }
                            break;

                        case "moshi":
                            // Verificamos si se usa una variable declarada en la condición
                            if (i + 1 < tokens.size() && tokens.get(i + 1).type == LexerShibaInu.TokenType.IDENTIFICADOR) {
                                String varCond = tokens.get(i + 1).value;
                                if (!variablesDefinidas.contains(varCond)) {
                                    tablaErrores.add(new ErrorCompilador("E020", "Semántico", "VARIABLE_SIN_DECLARAR"
                                            , i,1, "Variable '" + varCond + "' usada sin declarar."
                                    ));
                                }
                            }
                            break;

                        // Otros casos de palabras clave si es necesario
                    }
                    break;

                case IDENTIFICADOR:
                    // Si no es parte de una declaración ni condición, se podría verificar aquí
                    break;
            }
        }
    }

    // Se usa para probar errores de manera local
//    public void mostrarErrores() {
//        if (errores.isEmpty()) {
//            System.out.println();
//            System.out.println("✔ No se encontraron errores semánticos.");
//        } else {
//            System.out.println();
//            System.out.println("------------Errores Semánticos------------------");
//            errores.forEach(System.out::println);
//        }
//    }

//    public void reset() {
//        variablesDefinidas.clear();
//        errores.clear();
//    }
}
