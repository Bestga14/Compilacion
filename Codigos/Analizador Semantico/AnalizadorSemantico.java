package LenguajeShibaInu;
import java.util.*;
public class AnalizadorSemantico {
    private Set<String> variablesDefinidas = new HashSet<>();
    private List<String> errores = new ArrayList<>();

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
                                    errores.add("Error Semántico: Variable '" + nombreVar + "' ya fue declarada.");
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
                                    errores.add("Error Semántico: Variable '" + varCond + "' usada sin declarar.");
                                }
                            }
                            break;

                        // Otros casos de palabras clave si es necesario
                    }
                    break;

                case IDENTIFICADOR:
                    // Si no es parte de una declaración ni condición, se podría verificar aquí
                    // si deseas validar usos arbitrarios
                    break;
            }
        }
    }

    public void mostrarErrores() {
        if (errores.isEmpty()) {
            System.out.println();
            System.out.println("✔ No se encontraron errores semánticos.");
        } else {
            System.out.println();
            System.out.println("------------Errores Semánticos------------------");
            errores.forEach(System.out::println);
        }
    }

    public void reset() {
        variablesDefinidas.clear();
        errores.clear();
    }
}
