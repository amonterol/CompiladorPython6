/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.LineaDeContenido;
import auxiliares.MiError;
import auxiliares.TiposDeError;
import auxiliares.TipoDeToken;
import auxiliares.Token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author A Montero
 */
public class Parser {

    private final List<List<Token>> listaDeTokens; //Almacena los tokens que se identifican
    //private List<String> programaEnPythonRevisado; //Almacena los tokens que se identifican

    private List<LineaDeContenido> listaContenidoFinal;
    private LineaDeContenido nuevoContenido;
    private List<MiError> erroresEncontrados;
    private List<String> archivoFinal;
    private TiposDeError tipos = new TiposDeError();
    private int cantidadDeComentarios;

    private Map<Integer, List<MiError>> erroresEncontradosMap;

    public Parser(List<List<Token>> tokens, List<LineaDeContenido> programa, int cantidadDeComentarios, Map<Integer, List<MiError>> errores) {
        this.listaDeTokens = tokens;
        this.listaContenidoFinal = programa;
        this.cantidadDeComentarios = cantidadDeComentarios;
        this.erroresEncontradosMap = errores;
    }

    public List<String> analisisSintactico() throws IOException {
        System.out.println();
        System.out.println(" 40 ESTAMOS EN EL ANALISIS SINTACTICO ");
        System.out.println();

        boolean existeTokenAntesDeImport = false;

        System.out.println();

        for (List<Token> tokensEnLaLinea : listaDeTokens) {
            if (!tokensEnLaLinea.isEmpty()) {

                for (int i = 0; i < tokensEnLaLinea.size(); i++) {
                    int numeroDeLineaTokenActual = 0;
                    int numeroError = -1;
                    Token tokenActual = tokensEnLaLinea.get(i);
                    switch (tokenActual.getTipoDeToken().toString()) {
                        case "PALABRA_RESERVADA":

                            //OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenImport = tokensEnLaLinea.indexOf(tokenActual);
                                    //Valida los tokens antes de input
                                    System.out.println();
                                    System.out.println("72 Indice de import " + indiceTokenImport);
                                    System.out.println();
                                    System.out.println();
                                    System.out.println(" 75 IMPORT esta en numero de linea " + numeroDeLineaTokenActual);
                                    System.out.println();
                                    System.out.println();
                                    System.out.println(" 80 Numero de linea primera instruccion diferente de IMPORT " + obtenerLineaPrimerTokenDiferenteDeImport());
                                    System.out.println();
                                    
                                    boolean existeInstruccionesAntesDeImport = numeroDeLineaTokenActual >= obtenerLineaPrimerTokenDiferenteDeImport();

                                                              
                                    if (existeInstruccionesAntesDeImport) {
                                       numeroError = 300;
                                        incluirErrorEncontrado(numeroDeLineaTokenActual, numeroError);

                                        //USANDO HASHMAP PARA ERRORES ENCONTRADOS
                                        /*
                                        MiError e = new MiError(numeroDeLineaTokenActual, numeroError, tipos.obtenerDescripcionDelError(numeroError));
                                        if (erroresEncontradosMap.containsKey((numeroDeLineaTokenActual + 1))) {
                                            if (erroresEncontradosMap.get((numeroDeLineaTokenActual + 1)) != null) {
                                                List<MiError> errores3 = erroresEncontradosMap.get((numeroDeLineaTokenActual + 1));
                                                errores3.add(e);
                                            }
                                        } else {
                                            List<MiError> errores3 = new ArrayList<>();
                                            errores3.add(e);
                                            erroresEncontradosMap.put((numeroDeLineaTokenActual + 1), errores3);
                                        }
                                        */
                                    }
                                    
                                    break;
                                case "input":
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenInput = tokensEnLaLinea.indexOf(tokenActual);

                                    //Valida los tokens antes de input
                                    System.out.println();
                                    System.out.println("97 Indice de input " + tokensEnLaLinea.indexOf(tokenActual));
                                    System.out.println();
                                    validarOperadoresAntesDeInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenInput);

                                    System.out.println();
                                    System.out.println("101 Indice de input " + tokensEnLaLinea.indexOf(tokenActual));
                                    System.out.println();

                                    //Valida que los parentesis esten balanceados
                                    if (verificarExistenciaParentesis(tokensEnLaLinea)) {
                                        validarParentesisEnInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenInput);
                                        boolean parentesisBalanceadosEnInput = verificarParentesisBalanceados(tokensEnLaLinea, numeroDeLineaTokenActual);
                                        if (!parentesisBalanceadosEnInput) {
                                            System.out.println("109 Parentesis NO balanceados:" + parentesisBalanceadosEnInput);
                                            numeroError = 510;
                                            incluirErrorEncontrado(numeroDeLineaTokenActual, numeroError);
                                        }
                                        System.out.println();
                                        System.out.println("115 verificarExistenciaParentesis ");
                                        System.out.println();

                                    } else {
                                        validarParentesisEnInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenInput);
                                        System.out.println();
                                        System.out.println("121 No hay parentesis => validarParentesisEnInput ");
                                        System.out.println();
                                    }

                                    //Valida la existencia de ambas comillas
                                    System.out.println();
                                    System.out.println("128 No hay comillas => validarComillasEnInput ");
                                    System.out.println();
                                    validarComillasEnInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenInput);

                                    break;
                                case "print":
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenPrint = tokensEnLaLinea.indexOf(tokenActual);
                                    Token tokenSiguienteDePrint = new Token();

                                    if ((indiceTokenPrint + 1) < tokensEnLaLinea.size()) {
                                        tokenSiguienteDePrint = tokensEnLaLinea.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                    }
                                    if (tokenSiguienteDePrint.getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO")) {
                                        if ((indiceTokenPrint + 1) < tokensEnLaLinea.size()) {
                                            tokenSiguienteDePrint = tokensEnLaLinea.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                        }
                                    }
                                    if (tokenSiguienteDePrint.getTipoDeToken().toString().equals("TEXTO_ENTRE_COMILLAS")) {
                                        if ((indiceTokenPrint + 1) < tokensEnLaLinea.size()) {
                                            tokenSiguienteDePrint = tokensEnLaLinea.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                        }
                                    }

                                    if (verificarExistenciaParentesis(tokensEnLaLinea)) {
                                        validarParentesisEnInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenPrint);
                                        boolean parentesisBalanceadosEnPrint = verificarParentesisBalanceados(tokensEnLaLinea, numeroDeLineaTokenActual);
                                        if (!parentesisBalanceadosEnPrint) {
                                            System.out.println("109 Parentesis NO balanceados:" + parentesisBalanceadosEnPrint);
                                            numeroError = 510;
                                            incluirErrorEncontrado(numeroDeLineaTokenActual, numeroError);
                                        }
                                        System.out.println();
                                        System.out.println("115 verificarExistenciaParentesis ");
                                        System.out.println();
                                    } else {
                                        validarParentesisEnInput(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenPrint);
                                        System.out.println();
                                        System.out.println("141 No hay parentesis => validarParentesisEnInput ");
                                        System.out.println();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        case "all":
                            //Validar uso de la funcion all
                            break;

                        case "ASIGNACION":
                            int indiceTokenAsignacion = tokensEnLaLinea.indexOf(tokenActual);
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                            validarOperadorAsignacion(tokensEnLaLinea, numeroDeLineaTokenActual, indiceTokenAsignacion);
                            System.out.println();
                            System.out.println("182 Token de asignacion ");
                            System.out.println();
                            break;
                        default:
                            break;

                    }

                }

            }
        }
        System.out.println();
        System.out.println("LISTA CONTENIDO FINAL PARSE:");
        //imprimirListas(listaContenidoFinal);
        imprimirListasDeContenidoFinal(listaContenidoFinal);
        System.out.println();

        System.out.println();
        System.out.println("PROGRAMA EN PYTHON REVISADO:");
        //imprimirListas(listaContenidoFinal);
        List<String> programaRevisado = generarProgramaEnPythonRevisado(listaContenidoFinal);
        imprimirListas(programaRevisado);
        System.out.println();

        System.out.println();
        System.out.println("140");
        System.out.println("PARSER: Contenido del mapa de errores encontrados " + erroresEncontradosMap.size());
        for (Map.Entry<Integer, List<MiError>> entry : erroresEncontradosMap.entrySet()) {
            Integer key = entry.getKey();
            List<MiError> errorList = entry.getValue();
            System.out.println("Linea: " + key);
            for (MiError error : errorList) {
                System.out.println(error.getKey() + "  " + error.getDescripcion());
            }
        }
        System.out.println();

        return programaRevisado;
    } // fin metodo analisisSintactico

    public boolean verificarOtrosTokenAntesDeImport(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenImport) {
        boolean encontradoOtroToken = false;

        if (indiceTokenImport != 1) {
            encontradoOtroToken = true;
        }
        return encontradoOtroToken;
    }

    public int obtenerLineaPrimerTokenDiferenteDeImport() {

        int lineaPrimerTokenDiferenteDeImport = 0;

        List<Integer> lineasConTokensValidos = new ArrayList<>();

        for (List<Token> linea : listaDeTokens) {
            for (Token linea1 : linea) {
                Token token = new Token();
                if (linea.getFirst() != null) {
                    token = linea.get(1);
                }

                if (!token.getLexema().equals("import")) {
                    System.out.println("182 El token " + token.getTipoDeToken() + " lexema  " + token.getLexema() + " linea " + token.getNumeroLinea());
                    return lineaPrimerTokenDiferenteDeImport = token.getNumeroLinea();
                }
            }

        }

        System.out.println();
        System.out.println("189 Numero linea primer token diferente a import-> " + lineaPrimerTokenDiferenteDeImport);
        System.out.println();
        return lineaPrimerTokenDiferenteDeImport;
    }
    
    
    public void validarOperadorAsignacion(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenAsignacion) {
        // SINTEXIS CORRECTA:  VARIABLE VALIDA = VARIABLE VALIDA o NUMERO

        System.out.println();
        System.out.println("251 validarOperadorAsignacion " + " indice de token de asignacion  " + indiceTokenAsignacion);
        System.out.println();

        int numeroError = 0;
        String token_en_posicion_0 = "";
        String token_en_posicion_1 = "";
        String token_en_posicion_2 = "";

        switch (indiceTokenAsignacion) {
            case 0: // Forma>  = 
                if (lineaDeTokens.size() == 1) {
                    numeroError = 600;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else {
                    if ((indiceTokenAsignacion + 1) < lineaDeTokens.size()) {
                        Token tkn_en_posicion_1 = lineaDeTokens.get(1);
                        if (!(tkn_en_posicion_1.getTipoDeToken().toString().equals(("NUMERO_ENTERO"))
                                || tkn_en_posicion_1.getTipoDeToken().toString().equals(("NUMERO_ENTERO"))
                                || tkn_en_posicion_1.getTipoDeToken().toString().equals(("IDENTIFICADOR")))) {
                            numeroError = 603;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                        }
                    }

                }
                break;
            case 1:
                // Operador en posicion correcta.Revisa el token anterior a el "="
                token_en_posicion_0 = lineaDeTokens.get(0).getTipoDeToken().toString();
                System.out.println("278 Token en posicion 0 " + token_en_posicion_0);
                switch (token_en_posicion_0) {
                    case "PALABRA_RESERVADA":
                        // Forma> PALABRA_RESERVADA =
                        numeroError = 601;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        System.out.println("238 AQUI ESTA BIEN");
                        break;
                    case "DESCONOCIDO":
                        // Forma> DESCONOCIDO =
                        numeroError = 602;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        break;
                    case "NUMERO_ENTERO":
                    case "NUMERO_DECIMAL":
                        // Forma> 10 ó 10.25 =
                        numeroError = 602;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        break;
                    default:
                        break;
                }

                //Verifica si hay un token despues de "="
                if ((indiceTokenAsignacion + 1) < lineaDeTokens.size()) {
                    //Operador en posicion correcta. Revisa el token siguiente a =
                    token_en_posicion_2 = lineaDeTokens.get(2).getTipoDeToken().toString();
                    switch (token_en_posicion_2) {
                        case "PALABRA_RESERVADA":
                            // Forma> IDENTIFICADOR = PALABRA_RESERVADA
                            numeroError = 601;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "DESCONOCIDO":
                            numeroError = 602;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "IDENTIFICADOR":
                        case "NUMERO_ENTERO":
                        case "NUMERO_DECIMAL":
                            break;
                        default:
                            numeroError = 602;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                } else { // No hay token despues de "="
                    numeroError = 600;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }

                break;

            default:
                List<Token> sublistaDeTokens = lineaDeTokens.subList(0, indiceTokenAsignacion);
                if(!esListaDeVariablesValidas(sublistaDeTokens)){
                    numeroError = 604;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
              
                break;
        }
    }

    private static boolean esListaDeVariablesValidas(List<Token> sublistaDeTokens) {
        for (int i = 0; i <sublistaDeTokens.size(); i++) {
            Token token = sublistaDeTokens.get(i);
            if (i % 2 == 0) { // Posiciones pares deben ser identificadores
                if (token.getTipoDeToken() != TipoDeToken.IDENTIFICADOR) {
                    return false;
                }
            } else { // Posiciones impares deben ser comas
                if (token.getTipoDeToken() != TipoDeToken.COMA ) {
                    return false;
                }
            }
        }
        // Verificar que la lista no termine en una coma
        if (sublistaDeTokens.size() % 2 == 0) {
            Token ultimoToken = sublistaDeTokens.get(sublistaDeTokens.size() - 1);
            if (ultimoToken.getTipoDeToken() == TipoDeToken.COMA ) {
                return false;
            }
        }
        return true;
    }
          
    public void incluirErrorEncontrado(int numeroDeLinea, int numeroError) {
        MiError e = new MiError(numeroDeLinea, numeroError, tipos.obtenerDescripcionDelError(numeroError));
        for (LineaDeContenido linea : listaContenidoFinal) {
            if (linea.getNumeroDeLinea() == numeroDeLinea) {
                if (linea.getErroresEncontrados() == null) {
                    List<MiError> errores = new ArrayList<>();
                    errores.add(e);
                    linea.setErroresEncontrados(errores);
                } else {
                    linea.getErroresEncontrados().add(e);
                }
            }
        }
    }

    //Valida-> identificador, =, input, (, "string", )
    public void validarOperadoresAntesDeInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenInput) {
        int numeroError = 0;
        String token_en_posicion_0 = "";
        switch (indiceTokenInput) {
            case 0:
                numeroError = 400;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                numeroError = 401;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                break;
            case 1:
                token_en_posicion_0 = lineaDeTokens.get(0).getTipoDeToken().toString();
                if (token_en_posicion_0.equals("IDENTIFICADOR")) { //Falta =
                    numeroError = 401;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (token_en_posicion_0.equals("ASIGNACION")) {//Falta ID
                    numeroError = 400;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (!(token_en_posicion_0.equals("IDENTIFICADOR") && token_en_posicion_0.equals("ASIGNACION"))) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                }
                break;
            case 2:
                token_en_posicion_0 = lineaDeTokens.get(0).getTipoDeToken().toString();
                String token_en_posicion_1 = lineaDeTokens.get(1).getTipoDeToken().toString();
                if (!(token_en_posicion_0.equals("IDENTIFICADOR") && token_en_posicion_1.equals("ASIGNACION"))) {
                    numeroError = 406;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (!(token_en_posicion_0.equals("IDENTIFICADOR")) && token_en_posicion_1.equals("ASIGNACION")) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                } else if (token_en_posicion_0.equals("IDENTIFICADOR") && !(token_en_posicion_1.equals("ASIGNACION"))) {
                    switch (token_en_posicion_0) {
                        case "DESCONOCIDO":
                            numeroError = 402;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "PALABRA_RESERVADA":
                            numeroError = 404;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case "NUMERO_DECIMAL":
                        case "NUMERO_ENTERO":
                            numeroError = 405;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        default:
                            numeroError = 400;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            numeroError = 401;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                }
                break;
            default:
                numeroError = 406;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                break;
        }

    }

    public boolean verificarExistenciaParentesis(List<Token> lineaDeTokens) {

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO") || token.getTipoDeToken().toString().equals("PARENTESIS_DERECHO")) {
                return true;
            }

        }
        return false;
    }

    //Verifica que los parentesis este balanceados 
    public boolean verificarParentesisBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            System.out.println();
            System.out.println(" 392 Leyendo token " + token.getTipoDeToken());
            switch (token.getTipoDeToken().toString()) {
                case "PARENTESIS_IZQUIERDO":
                    pila.push("(");
                    System.out.println(" 395 Se incluyo un nuevo  " + token.getTipoDeToken().toString());
                    System.out.println();
                    System.out.println(" 399 Contenido de la pila: " + pila);
                    break;

                case "PARENTESIS_DERECHO":
                    System.out.println(" 403 Contenido de la pila: " + pila);
                    if (pila.isEmpty()) {
                        System.out.println("403 Error: pila vacía al encontrar un paréntesis derecho.  " + pila.isEmpty());
                        return false;
                    }
                    System.out.println(" 403 Contenido de la pila: " + pila);
                    System.out.println("406Error: pila vacía al encontrar un paréntesis derecho." + pila.isEmpty());
                    String ultimoParentesis = pila.pop();
                    System.out.println(" 406 Se retiro un   " + ultimoParentesis);
                    if (!"(".equals(ultimoParentesis)) {
                        System.out.println(" 406 Se retiro un   " + ultimoParentesis + " coinciden " + !"(".equals(ultimoParentesis));
                        return false;
                    }

                    break;

                default:
                    System.out.println("419 En default : " + token.getTipoDeToken());
                    break;
            }
            // Imprimir el contenido de la pila en cada iteración
            System.out.println(" 413 Contenido de la pila: " + pila);
        }
        System.out.println(" 420 Contenido de la pila.isEmpty(): " + pila.isEmpty());
        boolean resultado = pila.isEmpty();
        System.out.println("Resultado final: " + resultado);
        return resultado;
    }

    public boolean verificarCorchetesBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            switch (token.getTipoDeToken().toString()) {

                case "CORCHETE_IZQUIERDO":
                    pila.push("[");
                    break;

                case "CORCHETE_DERECHO": {
                    if (pila.isEmpty()) {
                        return false;
                    }
                    String ultimoParentesis = pila.pop();
                    if (!existeParentesisEmparejados(ultimoParentesis, "]")) {
                        return false;
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return pila.isEmpty();
    }

    private boolean existeParentesisEmparejados(String ultimoParentesis, String actual) {
        System.out.println("451 existeParentesisEmparejados => llegan " + ultimoParentesis + "  " + actual);
        return "(".equals(ultimoParentesis) && ")".equals(actual);
    }

    private void validarParentesisEnInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceDeInput) {
        System.out.println();
        System.out.println("469 No hay parentesis => validarParentesisEnInput " + " el indice de input es " + indiceDeInput);
        System.out.println();

        int numeroError;

        String tokenSiguienteDeInput = "";
        String tokenSiguienteSiguienteDeInput = "";
        String ultimoTokenDeLineaDeInput = "";
        String penultimoTokenDeLineaDeInput = "";

        //Valida si el token input es el ultimo de la linea de tokens
        if (!lineaDeTokens.isEmpty()) {
            if (indiceDeInput + 1 == lineaDeTokens.size()) {
                tokenSiguienteDeInput = lineaDeTokens.get((indiceDeInput)).getTipoDeToken().toString();

                numeroError = 511;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                System.out.println("484 tokenSiguienteSiguienteDeInput.equals " + tokenSiguienteSiguienteDeInput);

            }

        }

        if (!lineaDeTokens.isEmpty()) {
            if (indiceDeInput + 1 < lineaDeTokens.size()) {
                tokenSiguienteDeInput = lineaDeTokens.get((indiceDeInput + 1)).getTipoDeToken().toString();

                if (!(tokenSiguienteDeInput.equals("PARENTESIS_IZQUIERDO") || tokenSiguienteDeInput.equals("CORCHETE_IZQUIERDO")
                        || tokenSiguienteDeInput.equals("LLAVE_IZQUIERDA"))) {
                    System.out.println();
                    System.out.println("480 tokenSiguienteDeInput.equals " + tokenSiguienteDeInput);

                    numeroError = 511;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("484 tokenSiguienteSiguienteDeInput.equals " + tokenSiguienteSiguienteDeInput);

                }

            }
        }
        //Valida si el ultimo token de la linea es "
        if (!lineaDeTokens.isEmpty()) {
            if (lineaDeTokens.size() > indiceDeInput - 2) {
                ultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 1)).getTipoDeToken().toString();
                if (!(ultimoTokenDeLineaDeInput.equals("PARENTESIS_DERECHO") || ultimoTokenDeLineaDeInput.equals("CORCHETE_DERECHO")
                        || ultimoTokenDeLineaDeInput.equals("LLAVE_DERECHA"))) {
                    System.out.println();
                    System.out.println("511 no hay parentesis ni comillas finales " + ultimoTokenDeLineaDeInput);

                    numeroError = 512;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            }
        }

        //Valida [ o {
        if (!lineaDeTokens.isEmpty()) {
            if (indiceDeInput + 1 < lineaDeTokens.size()) {
                tokenSiguienteDeInput = lineaDeTokens.get((indiceDeInput + 1)).getTipoDeToken().toString();

                if (tokenSiguienteDeInput.equals("CORCHETE_IZQUIERDO")) {
                    System.out.println();
                    System.out.println("480 tokenSiguienteDeInput.equals " + tokenSiguienteDeInput);

                    numeroError = 513;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("484 tokenSiguienteSiguienteDeInput.equals " + tokenSiguienteSiguienteDeInput);

                }

                if (tokenSiguienteDeInput.equals("LLAVE_IZQUIERDA")) {
                    System.out.println();
                    System.out.println("480 tokenSiguienteDeInput.equals " + tokenSiguienteDeInput);

                    numeroError = 514;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("484 tokenSiguienteSiguienteDeInput.equals " + tokenSiguienteSiguienteDeInput);
                }

            }
        }
        //Valida ] o }
        if (!lineaDeTokens.isEmpty()) {
            if (lineaDeTokens.size() > indiceDeInput - 2) {
                ultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 1)).getTipoDeToken().toString();
                if (ultimoTokenDeLineaDeInput.equals("CORCHETE_DERECHO")) {
                    System.out.println();
                    System.out.println("511 no hay parentesis ni comillas finales " + ultimoTokenDeLineaDeInput);

                    numeroError = 515;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (ultimoTokenDeLineaDeInput.equals("LLAVE_DERECHA")) {
                    System.out.println();
                    System.out.println("511 no hay parentesis ni comillas finales " + ultimoTokenDeLineaDeInput);

                    numeroError = 516;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            }
        }

    }

    private void validarComillasEnInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceDeInput) {
        System.out.println();
        System.out.println("511 No hay comillas => validarComillasEnInput ");
        System.out.println();

        int numeroError;

        System.out.println();
        System.out.println("466 No hay comillas => validarComillasEnInput indice de input es "
                + indiceDeInput + " indice siguiente a input " + (indiceDeInput + 1)
                + " indice de token siguiente al siguiente  " + (indiceDeInput + 2));
        System.out.println();

        String tokenSiguienteDeInput = "";
        String tokenSiguienteSiguienteDeInput = "";
        String ultimoTokenDeLineaDeInput = "";
        String penultimoTokenDeLineaDeInput = "";

        //Valida (TextoEntreComillas,  [TextoEntreComillas y {TextoEntreComillas
        if (!lineaDeTokens.isEmpty()) {
            if (indiceDeInput + 2 < lineaDeTokens.size()) {
                tokenSiguienteDeInput = lineaDeTokens.get((indiceDeInput + 1)).getTipoDeToken().toString();
                tokenSiguienteSiguienteDeInput = lineaDeTokens.get((indiceDeInput + 2)).getTipoDeToken().toString();
                if (tokenSiguienteDeInput.equals("PARENTESIS_IZQUIERDO") || tokenSiguienteDeInput.equals("CORCHETE_IZQUIERDO")
                        || tokenSiguienteDeInput.equals("LLAVE_IZQUIERDA")) {
                    System.out.println();
                    System.out.println("480 tokenSiguienteDeInput.equals " + tokenSiguienteDeInput);
                    if (!tokenSiguienteSiguienteDeInput.equals("COMILLAS")) {
                        numeroError = 518;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        System.out.println("484 tokenSiguienteSiguienteDeInput.equals " + tokenSiguienteSiguienteDeInput);
                    }
                }

            }
        }
        //Valida TextoEntreComillas),  TextoEntreComillas] y TextoEntreComillas}
        if (!lineaDeTokens.isEmpty()) {
            if (lineaDeTokens.size() > indiceDeInput - 2) {
                ultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 1)).getTipoDeToken().toString();
                if (ultimoTokenDeLineaDeInput.equals("PARENTESIS_DERECHO") || ultimoTokenDeLineaDeInput.equals("CORCHETE_DERECHO")
                        || ultimoTokenDeLineaDeInput.equals("LLAVE_DERECHA")) {
                    System.out.println();
                    System.out.println("494 ultimoTokenDeLineaDeInput.equals " + ultimoTokenDeLineaDeInput);
                    penultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 2)).getTipoDeToken().toString();
                    if (!penultimoTokenDeLineaDeInput.equals("COMILLAS")) {
                        numeroError = 519;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        System.out.println("498 penultimoTokenDeLineaDeInput.equals " + penultimoTokenDeLineaDeInput + " no es igual a " + "COMILLAS");
                    }
                }
            }
        }
        //Valida TextoEntreComillas no hay comillas al inicio del texto
        if (!lineaDeTokens.isEmpty()) {
            if (indiceDeInput + 1 < lineaDeTokens.size()) {
                tokenSiguienteDeInput = lineaDeTokens.get((indiceDeInput + 1)).getTipoDeToken().toString();

                if (!(tokenSiguienteDeInput.equals("PARENTESIS_IZQUIERDO") || tokenSiguienteDeInput.equals("CORCHETE_IZQUIERDO")
                        || tokenSiguienteDeInput.equals("LLAVE_IZQUIERDA") || tokenSiguienteDeInput.equals("COMILLAS"))) {
                    System.out.println();
                    System.out.println("566 no hay parentesis ni comillas iniciales " + tokenSiguienteDeInput);

                    numeroError = 518;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);

                } else {
                    System.out.println();
                    System.out.println("566 no hay parentesis ni comillas iniciales " + tokenSiguienteDeInput);
                }

            }
        }
        //Valida TextoEntreComillas no hay comillas al final del texto
        if (!lineaDeTokens.isEmpty()) {
            if (lineaDeTokens.size() > indiceDeInput - 2) {
                ultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 1)).getTipoDeToken().toString();
                if (!(ultimoTokenDeLineaDeInput.equals("PARENTESIS_DERECHO") || ultimoTokenDeLineaDeInput.equals("CORCHETE_DERECHO")
                        || ultimoTokenDeLineaDeInput.equals("LLAVE_DERECHA") || ultimoTokenDeLineaDeInput.equals("COMILLAS"))
                        && !(lineaDeTokens.get((lineaDeTokens.size() - 1)).getLexema().equals("input"))) {
                    System.out.println();
                    System.out.println("582 no hay parentesis ni comillas finales " + ultimoTokenDeLineaDeInput);

                    numeroError = 519;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            }
        }
        //Valida TextoEntreComillas no hay comillas al final del texto
        if (!lineaDeTokens.isEmpty()) {
            if (lineaDeTokens.size() > indiceDeInput - 1) {
                ultimoTokenDeLineaDeInput = lineaDeTokens.get((lineaDeTokens.size() - 1)).getLexema();
                if (ultimoTokenDeLineaDeInput.equals("input")) {
                    System.out.println();
                    System.out.println("582 no hay parentesis ni comillas finales " + ultimoTokenDeLineaDeInput);

                    numeroError = 518;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    numeroError = 519;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    numeroError = 520;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);

                }
            }
        }

    }

    public List<String> generarProgramaEnPythonRevisado(List<LineaDeContenido> listaContenidoFinal) {
        List<String> programaEnPythonRevisado = new ArrayList<>();

        for (int i = 0; i < listaContenidoFinal.size(); ++i) {
            int numeroDeLinea = listaContenidoFinal.get(i).getNumeroDeLinea() + 1;
            String instruccion = listaContenidoFinal.get(i).getInstruccion();

            String formatoLinea = String.format("%05d", numeroDeLinea);
            String formatoError = String.format("%14s", "Error ");

            if (listaContenidoFinal.get(i).getErroresEncontrados() == null) {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
            } else {
                programaEnPythonRevisado.add(formatoLinea + " " + instruccion);
                for (int k = 0; k < listaContenidoFinal.get(i).getErroresEncontrados().size(); ++k) {
                    int numeroDeError = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getKey();
                    String descripcion = listaContenidoFinal.get(i).getErroresEncontrados().get(k).getDescripcion();
                    programaEnPythonRevisado.add(formatoError + numeroDeError + ". " + descripcion);
                }
            }
        }

        HashMap<String, Integer> histograma = contarLaCantidadOperadoresComparacion();

        ArrayList<String> operadores = new ArrayList<String>(histograma.keySet());
        programaEnPythonRevisado.add("=================================================");
        for (String operador : operadores) {
            programaEnPythonRevisado.add(histograma.get(operador) + " Token " + operador);
        }
        programaEnPythonRevisado.add(this.cantidadDeComentarios + " lineas de comentario");

        return programaEnPythonRevisado;
    }

    //Recorre una lista de string y la imprime BORRAR
    public static void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }

    public static void imprimirListasDeContenidoFinal(List<LineaDeContenido> contenido) {
        for (LineaDeContenido linea : contenido) {
            System.out.println(linea);
        }
    }

    public HashMap<String, Integer> inicializarHistogramaOperadoresComparacion() {
        HashMap<String, Integer> histograma = new HashMap<String, Integer>(); //Almacena la cuenta de los operadores de comparación
        histograma.put(">", 0);
        histograma.put(">=", 0);
        histograma.put("<", 0);
        histograma.put("<=", 0);
        histograma.put("==", 0);
        histograma.put("!=", 0);

        return histograma;

    }

    public HashMap<String, Integer> contarLaCantidadOperadoresComparacion() {

        HashMap<String, Integer> histograma = inicializarHistogramaOperadoresComparacion();

        for (List lista : listaDeTokens) {
            for (Object token : lista) {
                Token tkn = (Token) token;
                switch (tkn.getTipoDeToken().toString().trim()) {
                    case "MAYOR_QUE":
                        histograma.put(">", histograma.get(">") + 1);
                        break;
                    case "MAYOR_O_IGUAL_QUE":
                        histograma.put(">=", histograma.get(">=") + 1);
                        break;
                    case "MENOR_QUE":
                        histograma.put("<", histograma.get("<") + 1);
                        break;
                    case "MENOR_O_IGUAL_QUE":
                        histograma.put("<=", histograma.get("<=") + 1);
                        break;
                    case "IGUAL_QUE":
                        histograma.put("==", histograma.get("==") + 1);
                        break;
                    case "DIFERENTE_QUE":
                        histograma.put("!=", histograma.get("!=") + 1);
                        break;
                    default:
                        break;

                }

            }
        }
        return histograma;

    }

    public Map<Integer, List<Error>> inicializarHashMapErroresEncontrados() {

        Map<Integer, List<Error>> erroresEncontradosMap = new HashMap<>();

        return erroresEncontradosMap;

    }

}
