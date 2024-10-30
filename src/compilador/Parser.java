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
        boolean enBloqueWhile = false;
        int indentacionBloqueWhile = -1;
        int contadorInstruccionesBloqueWhile = 0;

        boolean enBloqueDef = false;
        int indentacionBloqueDef = -1;
        int contadorInstruccionesBloqueDef = 0;

        int indentacionInstruccionActual = -1;

        System.out.println();

        for (List<Token> lineaDeCodigoEnTokens : listaDeTokens) {
            if (!lineaDeCodigoEnTokens.isEmpty()) {
                int numeroError = -1;

                //Valida si es la primera instruccion del bloque while para determinar cual es la indentacion del bloque
                if (enBloqueWhile) {

                    ++contadorInstruccionesBloqueWhile;
                    indentacionInstruccionActual = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                    System.out.println();
                    System.out.println("74 Indentacion en while token actual " + indentacionInstruccionActual + " contador " + contadorInstruccionesBloqueWhile);

                    if (contadorInstruccionesBloqueWhile == 1) {
                        indentacionBloqueWhile = indentacionInstruccionActual;
                        System.out.println();
                        System.out.println("67 Indentacion while " + indentacionBloqueWhile);
                        if (indentacionBloqueWhile == 0) { //No hay instrucciones en el bloque
                            numeroError = 629;
                            incluirErrorEncontrado(lineaDeCodigoEnTokens.getFirst().getNumeroLinea(), numeroError);
                            enBloqueWhile = false;
                            contadorInstruccionesBloqueWhile = 0;
                        }
                    } else if (contadorInstruccionesBloqueWhile > 1) {
                        if (validarIndentacionBloque(indentacionBloqueWhile, indentacionInstruccionActual)) {
                            if (indentacionInstruccionActual == 0) { //Salimos del bloque
                                enBloqueWhile = false;
                                contadorInstruccionesBloqueWhile = 0;
                            } else {
                                //Instruccion con indentacion diferente al definido para el bloque
                                System.out.println();
                                System.out.println("67 Indentacion while " + indentacionBloqueWhile + indentacionInstruccionActual);
                                numeroError = 630;
                                incluirErrorEncontrado(lineaDeCodigoEnTokens.getFirst().getNumeroLinea(), numeroError);
                            }
                        }
                    }
                }

                //Valida si es la primera instruccion del bloque def para determinar cual es la indentacion del bloque
                if (enBloqueDef) {

                    ++contadorInstruccionesBloqueDef;
                    indentacionInstruccionActual = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                    System.out.println();
                    System.out.println("107 Indentacion en def token actual " + indentacionInstruccionActual + " contador " + contadorInstruccionesBloqueDef);

                    if (contadorInstruccionesBloqueDef == 1) {
                        indentacionBloqueDef = indentacionInstruccionActual;
                        System.out.println();
                        System.out.println("112 Indentacion def " + indentacionBloqueDef);
                        if (indentacionBloqueDef == 0) { //No hay instrucciones en el bloque
                            numeroError = 664;
                            incluirErrorEncontrado(lineaDeCodigoEnTokens.getFirst().getNumeroLinea(), numeroError);
                            enBloqueWhile = false;
                            contadorInstruccionesBloqueWhile = 0;
                        }
                    } else if (contadorInstruccionesBloqueDef > 1) {
                        if (validarIndentacionBloque(indentacionBloqueDef, indentacionInstruccionActual)) {
                            if (indentacionInstruccionActual == 0) { //Salimos del bloque
                                enBloqueDef = false;
                                contadorInstruccionesBloqueDef = 0;
                            } else {
                                //Instruccion con indentacion diferente al definido para el bloque
                                System.out.println();
                                System.out.println("127 Indentacion def " + indentacionBloqueDef + indentacionInstruccionActual);
                                numeroError = 630;
                                incluirErrorEncontrado(lineaDeCodigoEnTokens.getFirst().getNumeroLinea(), numeroError);
                            }
                        }
                    }
                }

                for (int i = 0; i < lineaDeCodigoEnTokens.size(); i++) {
                    int numeroDeLineaTokenActual = 0;
                    Token tokenActual = lineaDeCodigoEnTokens.get(i);
                    indentacionInstruccionActual = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                    switch (tokenActual.getTipoDeToken()) {
                        case TipoDeToken.PALABRA_RESERVADA:

                            //OUTER:
                            switch (tokenActual.getLexema()) {
                                case "import":
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenImport = lineaDeCodigoEnTokens.indexOf(tokenActual);
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
                                case "while":
                                    enBloqueWhile = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenWhile = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    System.out.println();
                                    System.out.println("147 Encontramos una instruccion  while " + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenWhile);
                                    System.out.println();
                                    validarSintaxisDeLineaWhile(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenWhile);

                                    break;
                                case "input":
                                    //existeInput = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenInput = lineaDeCodigoEnTokens.indexOf(tokenActual);

                                    //Valida los tokens antes de input
                                    System.out.println();
                                    System.out.println("97 Indice de input " + lineaDeCodigoEnTokens.indexOf(tokenActual));
                                    System.out.println();
                                    validarOperadoresAntesDeInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenInput);

                                    System.out.println();
                                    System.out.println("101 Indice de input " + lineaDeCodigoEnTokens.indexOf(tokenActual));
                                    System.out.println();

                                    //Valida que los parentesis esten balanceados
                                    if (verificarExistenciaParentesis(lineaDeCodigoEnTokens)) {
                                        validarParentesisEnInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenInput);
                                        boolean parentesisBalanceadosEnInput = verificarParentesisBalanceados(lineaDeCodigoEnTokens, numeroDeLineaTokenActual);
                                        if (!parentesisBalanceadosEnInput) {
                                            System.out.println("109 Parentesis NO balanceados:" + parentesisBalanceadosEnInput);
                                            numeroError = 510;
                                            incluirErrorEncontrado(numeroDeLineaTokenActual, numeroError);
                                        }
                                        System.out.println();
                                        System.out.println("115 verificarExistenciaParentesis ");
                                        System.out.println();

                                    } else {
                                        validarParentesisEnInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenInput);
                                        System.out.println();
                                        System.out.println("121 No hay parentesis => validarParentesisEnInput ");
                                        System.out.println();
                                    }

                                    //Valida la existencia de ambas comillas
                                    System.out.println();
                                    System.out.println("128 No hay comillas => validarComillasEnInput ");
                                    System.out.println();
                                    validarComillasEnInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenInput);

                                    break;
                                case "print":
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenPrint = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    Token tokenSiguienteDePrint = new Token();

                                    if ((indiceTokenPrint + 1) < lineaDeCodigoEnTokens.size()) {
                                        tokenSiguienteDePrint = lineaDeCodigoEnTokens.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                    }
                                    if (tokenSiguienteDePrint.getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO")) {
                                        if ((indiceTokenPrint + 1) < lineaDeCodigoEnTokens.size()) {
                                            tokenSiguienteDePrint = lineaDeCodigoEnTokens.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                        }
                                    }
                                    if (tokenSiguienteDePrint.getTipoDeToken().toString().equals("TEXTO_ENTRE_COMILLAS")) {
                                        if ((indiceTokenPrint + 1) < lineaDeCodigoEnTokens.size()) {
                                            tokenSiguienteDePrint = lineaDeCodigoEnTokens.get((indiceTokenPrint + 1)); //Se mueve al token siguiente del siguiente de print
                                        }
                                    }

                                    if (verificarExistenciaParentesis(lineaDeCodigoEnTokens)) {
                                        validarParentesisEnInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenPrint);
                                        boolean parentesisBalanceadosEnPrint = verificarParentesisBalanceados(lineaDeCodigoEnTokens, numeroDeLineaTokenActual);
                                        if (!parentesisBalanceadosEnPrint) {
                                            System.out.println("109 Parentesis NO balanceados:" + parentesisBalanceadosEnPrint);
                                            numeroError = 510;
                                            incluirErrorEncontrado(numeroDeLineaTokenActual, numeroError);
                                        }
                                        System.out.println();
                                        System.out.println("115 verificarExistenciaParentesis ");
                                        System.out.println();
                                    } else {
                                        validarParentesisEnInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenPrint);
                                        System.out.println();
                                        System.out.println("141 No hay parentesis => validarParentesisEnInput ");
                                        System.out.println();
                                    }
                                    break;

                                case "def":
                                    enBloqueDef = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenDef = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    System.out.println();
                                    System.out.println("278 Encontramos una instruccion  def " + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenDef);
                                    System.out.println();
                                    validarSintaxisDeDef(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenDef);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        /*
                        case "all":
                            //Validar uso de la funcion all
                            break;
                         */
                        case TipoDeToken.MULTIPLICACION:
                        case TipoDeToken.DIVISION:
                        case TipoDeToken.SUMA:
                        case TipoDeToken.RESTA:
                        case TipoDeToken.POTENCIA:
                        case TipoDeToken.MODULO:
                        case TipoDeToken.DIVISION_ENTERA:
                            int indiceToken = lineaDeCodigoEnTokens.indexOf(tokenActual);
                            System.out.println();
                            System.out.println("248 Indice de token actual " + indiceToken);
                            System.out.println();
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                            validarOperadoresBinarios(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceToken);
                            break;
                        case TipoDeToken.ASIGNACION:
                            int indiceTokenAsignacion = lineaDeCodigoEnTokens.indexOf(tokenActual);
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                            validarOperadorAsignacion(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenAsignacion);
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

    private void validarSintaxisDeDef(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        int numeroError = 0;

        Token indentToken = lineaDeTokens.get(0);
        Token keywordToken = lineaDeTokens.get(1);

        Token ultimoToken = lineaDeTokens.get((lineaDeTokens.size() - 1));
        int posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);

        //Valida la indentacion de def
        if (indentToken.getTipoDeToken() != null) {
            if (Integer.parseInt(indentToken.getLiteral()) > 0) {
                //return "Error: indentación incorrecta debe ser cero";
                numeroError = 650;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
        //Valida que def este al comienzo de la linea de la instruccion def
        if (indiceToken > 1) { //La posicion correcta de def es 1
            numeroError = 655;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida forma (0,def)
        if (indiceToken == 1 && lineaDeTokens.size() == 2) {
            numeroError = 666;
            incluirErrorEncontrado(numeroDeLinea, numeroError);

        }
        //Valida si existe :
        if (lineaDeTokens.size() > 2 && (posicionTokenDosPuntos == -1)) { //No existen :
            numeroError = 657;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que no se cambie : por un operador aritmetico
        if (esOperadorAritmetico(ultimoToken.getTipoDeToken())) {
            numeroError = 658;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que no se cambie : por un operador relacional
        if (esOperadorRelacional(ultimoToken.getTipoDeToken())) {
            numeroError = 659;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        //Valida forma (0,def,nombre,:)
        if ((indiceToken + 1) < lineaDeTokens.size()) {

            Token tokenSucesor = lineaDeTokens.get((indiceToken + 1));
            System.out.println("411 " + (tokenSucesor.getTipoDeToken() == TipoDeToken.IDENTIFICADOR) + (ultimoToken.getTipoDeToken() == TipoDeToken.DOS_PUNTOS) + (lineaDeTokens.size() == 4) + listaDeTokens.size());
            if (tokenSucesor.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA && tokenSucesor.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
                numeroError = 662;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            if (tokenSucesor.getTipoDeToken() != TipoDeToken.IDENTIFICADOR && tokenSucesor.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
                numeroError = 663;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            if (tokenSucesor.getTipoDeToken() == TipoDeToken.IDENTIFICADOR && ultimoToken.getTipoDeToken() == TipoDeToken.DOS_PUNTOS && (lineaDeTokens.size() == 4)) {
                numeroError = 665;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                System.out.println("411 ");
            }
            if (tokenSucesor.getTipoDeToken() == TipoDeToken.DOS_PUNTOS) {
                numeroError = 660;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                numeroError = 657;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }

        }

        //No importa el tamano de la lista de linea de Tokens
        //Valida si existen parentesis, si estan balanceados luego valida la posicion de :
        boolean existeParentesisEnLineaDeTokens = verificarExistenciaParentesis(lineaDeTokens);
        System.out.println("428 " + existeParentesisEnLineaDeTokens);
        if (existeParentesisEnLineaDeTokens) {
            boolean existeParentesisBalanceados = verificarParentesisBalanceados(lineaDeTokens, numeroDeLinea);
             System.out.println("431 " + existeParentesisBalanceados);
            if (!existeParentesisBalanceados) {
                numeroError = 510;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            System.out.println("444 ");
        }
        //Valida que no se usen corchetes en lugar de parentesis redondos
        boolean existeCorchetesEnLineaDeTokens = verificarExistenciaCorchetes(lineaDeTokens);
        if (keywordToken.getLexema().equals("while") && existeCorchetesEnLineaDeTokens) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            boolean existeCorchetesBalanceados = verificarCorchetesBalanceados(lineaDeTokens, numeroDeLinea);
            if (existeCorchetesBalanceados) {
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            } else {
                numeroError = 522;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("441 ");
                }
            }
            System.out.println("444 ");
        }
        //Valida que se use llaves en lugar de parentesis redondos
        boolean existeLlavesEnLineaDeTokens = verificarExistenciaLlaves(lineaDeTokens);
        if (existeLlavesEnLineaDeTokens) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            boolean existeLlavesBalanceados = verificarLlavesBalanceados(lineaDeTokens, numeroDeLinea);
            if (existeLlavesBalanceados) {
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            } else {
                numeroError = 523;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("441 ");
                }
            }
            System.out.println("444 ");
        }
    }

    public void validarOperadoresBinarios(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        int numeroError = 0;
        Token tokenAntecesorAlOperador = lineaDeTokens.get((indiceToken - 1));
        Token tokenSucesorAlOperador = new Token();
        if ((indiceToken + 1) < lineaDeTokens.size()) {
            tokenSucesorAlOperador = lineaDeTokens.get((indiceToken + 1));
        }

        if (indiceToken == 1) {
            numeroError = 120;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (indiceToken == (lineaDeTokens.size() - 1)) {
            numeroError = 121;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (indiceToken == 2 && lineaDeTokens.size() == 4) {
            if (!esVariableValida_O_Numero(tokenAntecesorAlOperador.getTipoDeToken())) {
                numeroError = 122;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            if (!esVariableValida_O_Numero(tokenSucesorAlOperador.getTipoDeToken())) {
                numeroError = 123;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
        if (indiceToken == 2 && lineaDeTokens.size() > 4) {
            List<Token> listaLadoDerecho = lineaDeTokens.subList((indiceToken + 1), lineaDeTokens.size());
            if (listaLadoDerecho.size() > 2) {
                numeroError = 125;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
        if (indiceToken == 3 && lineaDeTokens.size() >= 4) {
            List<Token> listaLadoIzquierdo = lineaDeTokens.subList(1, indiceToken);
            List<Token> listaLadoDerecho = lineaDeTokens.subList((indiceToken + 1), lineaDeTokens.size());
            if (listaLadoIzquierdo.size() > 1) {
                numeroError = 124;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            if (listaLadoDerecho.size() > 1) {
                numeroError = 125;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }

    }

    //Valida la indentacion de la linea de codigo actual
    public boolean validarIndentacionBloque(int indentacionBloque, int indentacionInstruccionActual) {
        return indentacionInstruccionActual != indentacionBloque;
    }

    public void validarSintaxisDeLineaWhile(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenWhile) {
        int numeroError = 0;

        Token indentToken = lineaDeTokens.get(0);
        Token keywordToken = lineaDeTokens.get(1);
        Token ultimoToken = lineaDeTokens.get((lineaDeTokens.size() - 1));
        int posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);

        //Valida la indentacion de while
        if (indentToken.getTipoDeToken() != null) {
            if (Integer.parseInt(indentToken.getLiteral()) > 0) {
                //return "Error: indentación incorrecta debe ser cero";
                numeroError = 620;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
        //Valida que while este al comienzo de la linea de la instruccion while
        if (indiceTokenWhile > 1) {
            numeroError = 622;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (indiceTokenWhile == 1 && lineaDeTokens.size() == 2) {
            numeroError = 623;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (posicionTokenDosPuntos == -1) {
            numeroError = 624;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (esOperadorAritmetico(ultimoToken.getTipoDeToken())) {
            numeroError = 627;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (esOperadorRelacional(ultimoToken.getTipoDeToken())) {
            numeroError = 628;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (indiceTokenWhile == 1 && posicionTokenDosPuntos == 2) {
            numeroError = 623;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (posicionTokenDosPuntos != -1 && ultimoToken.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
            numeroError = 626;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (posicionTokenDosPuntos == indiceTokenWhile + 1) {
            numeroError = 623;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        //No importa el tamano de la lista de linea de Tokens
        //Valida si existen parentesis, si estan balanceados luego valida la posicion de :
        boolean existeParentesisEnLineaDeTokens = verificarExistenciaParentesis(lineaDeTokens);
        System.out.println("426 " + existeParentesisEnLineaDeTokens);
        //if (keywordToken.getLexema().equals("while") && existeParentesisEnLineaDeTokens) {
        if (existeParentesisEnLineaDeTokens) {
            boolean existeParentesisBalanceados = verificarParentesisBalanceados(lineaDeTokens, numeroDeLinea);
            
            if (existeParentesisBalanceados) {
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            } else {
                numeroError = 510;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("441 ");
                }
               
            }
          
           
            System.out.println("444 ");
        }
        //Valida que no se usen corchetes en lugar de parentesis redondos
        boolean existeCorchetesEnLineaDeTokens = verificarExistenciaCorchetes(lineaDeTokens);
        //if (keywordToken.getLexema().equals("while") && existeCorchetesEnLineaDeTokens) {
        if (keywordToken.getLexema().equals("while") && existeCorchetesEnLineaDeTokens) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            boolean existeCorchetesBalanceados = verificarCorchetesBalanceados(lineaDeTokens, numeroDeLinea);
            if (existeCorchetesBalanceados) {
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            } else {
                numeroError = 522;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("441 ");
                }
            }
            System.out.println("444 ");
        }
        //Valida que se use llaves en lugar de parentesis redondos
        boolean existeLlavesEnLineaDeTokens = verificarExistenciaLlaves(lineaDeTokens);
        // if (keywordToken.getLexema().equals("while") && existeLlavesEnLineaDeTokens) {
        if (existeLlavesEnLineaDeTokens) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            boolean existeLlavesBalanceados = verificarLlavesBalanceados(lineaDeTokens, numeroDeLinea);
            if (existeLlavesBalanceados) {
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            } else {
                numeroError = 523;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                posicionTokenDosPuntos = buscarTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("441 ");
                }
            }
            System.out.println("444 ");
        }

    }

    public boolean esVariableValida_O_Numero(TipoDeToken tipo) {
        return (tipo == TipoDeToken.NUMERO_ENTERO)
                || (tipo == TipoDeToken.NUMERO_DECIMAL)
                || (tipo == TipoDeToken.IDENTIFICADOR);
    }

    public boolean esNumero(TipoDeToken tipo) {
        return (tipo == TipoDeToken.NUMERO_ENTERO) || (tipo == TipoDeToken.NUMERO_DECIMAL);
    }

    public boolean esOperadorAritmetico(TipoDeToken tipo) {
        return (tipo == TipoDeToken.MULTIPLICACION)
                || (tipo == TipoDeToken.DIVISION)
                || (tipo == TipoDeToken.SUMA)
                || (tipo == TipoDeToken.RESTA)
                || (tipo == TipoDeToken.MODULO)
                || (tipo == TipoDeToken.POTENCIA)
                || (tipo == TipoDeToken.DIVISION_ENTERA);

    }

    public boolean esOperadorRelacional(TipoDeToken tipo) {
        return (tipo == TipoDeToken.MAYOR_QUE)
                || (tipo == TipoDeToken.MAYOR_O_IGUAL_QUE)
                || (tipo == TipoDeToken.MENOR_QUE)
                || (tipo == TipoDeToken.MENOR_O_IGUAL_QUE)
                || (tipo == TipoDeToken.IGUAL_QUE)
                || (tipo == TipoDeToken.DIFERENTE_QUE);

    }

    public int buscarTokenPorTipoDeToken(List<Token> lineaDeTokens, TipoDeToken tipoABuscar) {
        int posicion = -1;
        for (int i = 0; i < lineaDeTokens.size(); ++i) {
            if (lineaDeTokens.get(i).getTipoDeToken() == tipoABuscar) {
                posicion = i;
                break;
            }
        }
        return posicion;
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
/*
        String textoEntreComillas = "";
         Token nuevoToken = new Token();
        if (verificarExistenciaDeComillas(lineaDeTokens) != 0) {
            textoEntreComillas = extractStringBetweenQuotes(lineaDeTokens);
            nuevoToken = new Token(TipoDeToken.STRING, textoEntreComillas, null, numeroDeLinea);
        }
        */

        System.out.println();
        System.out.println("789 validarOperadorAsignacion " + " indice de token de asignacion  " + indiceTokenAsignacion);
        System.out.println();

        int numeroError = 0;
        Token tokenAntecesorAlOperador = new Token();
        Token tokenSucesorAlOperador = new Token();

        switch (indiceTokenAsignacion) {
            case 1: // Valida la form->  = 
                if (lineaDeTokens.size() == 2) {
                    numeroError = 600;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                } else {
                    if ((indiceTokenAsignacion + 1) < lineaDeTokens.size()) { //Verifica que haya mas token para evitar null
                        tokenSucesorAlOperador = lineaDeTokens.get((indiceTokenAsignacion + 1));
                        if (!esIdentificadorONumero(tokenSucesorAlOperador)) {
                            numeroError = 603;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                        }
                    }

                }
                break;
            case 2:
                // Operador en posicion correcta.Valida el lado izquierdo de la asignacion"
                // Valida la form->  x =  ó  x = y ó x = 10
                tokenAntecesorAlOperador = lineaDeTokens.get((indiceTokenAsignacion - 1));
                System.out.println("819 Token en posicion 2 " + tokenAntecesorAlOperador.getTipoDeToken() + " " + tokenAntecesorAlOperador.getLexema());
                switch (tokenAntecesorAlOperador.getTipoDeToken()) {
                    case TipoDeToken.PALABRA_RESERVADA:
                        // Forma> PALABRA_RESERVADA =
                        numeroError = 601;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        System.out.println("825 AQUI ESTA BIEN");
                        break;
                    case TipoDeToken.IDENTIFICADOR:
                        break;
                    default:
                        // Forma> 10 ó 10.25 =
                        numeroError = 602;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        break;
                }

                //Valida el lado derecho de la asignacion
                if ((indiceTokenAsignacion + 1) < lineaDeTokens.size()) {
                    tokenSucesorAlOperador = lineaDeTokens.get((indiceTokenAsignacion + 1));
                    switch (tokenSucesorAlOperador.getTipoDeToken()) {
                        case TipoDeToken.PALABRA_RESERVADA:
                            // Forma> IDENTIFICADOR = PALABRA_RESERVADA
                            numeroError = 601;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                        case TipoDeToken.IDENTIFICADOR:
                        case TipoDeToken.NUMERO_ENTERO:
                        case TipoDeToken.NUMERO_DECIMAL:
                        case TipoDeToken.COMILLAS:
                            break;
                        default:
                            numeroError = 603;
                            incluirErrorEncontrado(numeroDeLinea, numeroError);
                            break;
                    }
                } else { // No hay token despues de "="
                    numeroError = 600;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }

                break;
            default: //Forma id1, id2, ..., idN = variable_valida o numero
                if (tokenAntecesorAlOperador.getTipoDeToken() == TipoDeToken.IDENTIFICADOR && indiceTokenAsignacion == 2) {
                    //Esto es correcto
                } else if (indiceTokenAsignacion > 3) {
                    List<Token> sublistaDeTokens = lineaDeTokens.subList(1, indiceTokenAsignacion);
                    boolean listaValida[] = esListaDeVariablesValidas(sublistaDeTokens);
                    if (!listaValida[0]) { // false => hay al menos un identificador no valido
                        numeroError = 604;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                    }
                    if (!listaValida[1]) { //Comas despues de cada identificador excepto el ultimo
                        numeroError = 605;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                    }
                    if (listaValida[2]) {//Existe una coma despues del ultimo identificador
                        numeroError = 606;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                    }
                }

                break;
        }
    }

    private boolean[] esListaDeVariablesValidas(List<Token> sublistaDeTokens) {
        boolean[] listaValida = {false, false, false};
        for (int i = 0; i < sublistaDeTokens.size(); i++) {
            Token token = sublistaDeTokens.get(i);
            if (i % 2 == 0) { // Posiciones pares deben ser identificadores validos
                if (token.getTipoDeToken() == TipoDeToken.IDENTIFICADOR) {
                    listaValida[0] = true;
                }
            } else { // Posiciones impares deben ser comas
                if (token.getTipoDeToken() == TipoDeToken.COMA) {
                    listaValida[1] = true;
                }
            }
        }
        // Verificar que la lista no termine en una coma
        Token ultimoToken = sublistaDeTokens.getLast();
        if (ultimoToken.getTipoDeToken() == TipoDeToken.COMA) {
            listaValida[2] = true;
        }
        return listaValida;
    }

    //Valida si el token es del tipo identificador o un numero
    public boolean esIdentificadorONumero(Token token) {
        return token.getTipoDeToken() == TipoDeToken.NUMERO_DECIMAL
                || token.getTipoDeToken() == TipoDeToken.NUMERO_DECIMAL
                || token.getTipoDeToken() == TipoDeToken.IDENTIFICADOR;
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

    private int verificarExistenciaDeComillas(List<Token> lineaDeTokens) {
        int contadorComillas = 0;
        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken() == TipoDeToken.COMILLAS) {
                contadorComillas++;
            }
        }
        return contadorComillas;
    }
/*
    public String extractStringBetweenQuotes(List<Token> tokens) {
        StringBuilder extractedString = new StringBuilder();
        boolean insideQuotes = false;

        for (Token token : tokens) {
            String lexema = token.getLexema();
            if (lexema.equals("\"")) {
                if (insideQuotes && extractedString.length() > 0 && extractedString.charAt(extractedString.length() - 1) == '\\') {
                    // Remove the escape character and add the quote
                    extractedString.setCharAt(extractedString.length() - 1, '\"');
                } else {
                    insideQuotes = !insideQuotes;
                    continue;
                }
            }
            if (insideQuotes) {
                extractedString.append(lexema);
            }
        }

        return extractedString.toString();
    }
*/
    public boolean verificarExistenciaParentesis(List<Token> lineaDeTokens) {

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("PARENTESIS_IZQUIERDO") || token.getTipoDeToken().toString().equals("PARENTESIS_DERECHO")) {
                return true;
            }

        }
        return false;
    }
   /* 
    public boolean verificarParentesisBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            switch (token.getTipoDeToken()) {
                case TipoDeToken.PARENTESIS_IZQUIERDO:
                    pila.push("(");
                    break;

                case TipoDeToken.PARENTESIS_DERECHO:
                    if (pila.isEmpty()) {
                        return false;
                    }
                    String ultimoParentesis = pila.pop();
                    if (!"(".equals(ultimoParentesis)) {
                        return false;
                    }
                    break;

                default:
                    break;
            }
        }
        return pila.isEmpty();
    }
*/
    //Verifica que los parentesis este balanceados 
    public boolean verificarParentesisBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();
        boolean resultado = false;
        for (Token token : lineaDeTokens) {
            System.out.println();
            System.out.println(" 1121 Leyendo token " + token.getTipoDeToken());
            switch (token.getTipoDeToken()) {
                case TipoDeToken.PARENTESIS_IZQUIERDO:
                    pila.push("(");
                    System.out.println(" 1125 Se incluyo un nuevo  " + token.getTipoDeToken().toString());
                    System.out.println();
                    System.out.println(" 1127 Contenido de la pila: " + pila);
                    break;

               case TipoDeToken.PARENTESIS_DERECHO:
                    System.out.println(" 1131 Contenido de la pila: " + pila);
                    if (pila.isEmpty()) {
                        System.out.println("1133 Error: pila vacía al encontrar un paréntesis derecho.  " + pila.isEmpty());
                       resultado = false;
                       break;
                    }
                    System.out.println(" 1136 Contenido de la pila: " + pila);
                    System.out.println("1137  pila vacía al encontrar un paréntesis derecho. " + pila.isEmpty());
                    String ultimoParentesis = pila.pop();
                    System.out.println(" 1139 Se retiro un   " + ultimoParentesis);
                    if (!"(".equals(ultimoParentesis)) {
                        System.out.println(" 1141 Se retiro un   " + ultimoParentesis + " coinciden " + !"(".equals(ultimoParentesis));
                        resultado = false;
                    }
                    break;

                default:
                    System.out.println("1148 En default : " + token.getTipoDeToken());
                    break;
            }
            // Imprimir el contenido de la pila en cada iteración
            System.out.println(" 1152 Contenido de la pila: " + pila);
        }
        System.out.println(" 1154 Contenido de la pila.isEmpty(): " + pila.isEmpty());
        resultado = pila.isEmpty();
        System.out.println("1156 Resultado final: " + resultado);
        return resultado;
    }

    public boolean verificarExistenciaCorchetes(List<Token> lineaDeTokens) {

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("CORCHETE_IZQUIERDO") || token.getTipoDeToken().toString().equals("CORCHETE_DERECHO")) {
                return true;
            }
        }
        return false;
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
                    String ultimoCorchete = pila.pop();
                    if (!"[".equals(ultimoCorchete)) {
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

    public boolean verificarExistenciaLlaves(List<Token> lineaDeTokens) {

        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken().toString().equals("LLAVE_IZQUIERDA") || token.getTipoDeToken().toString().equals("LLAVE_DERECHA")) {
                return true;
            }
        }
        return false;
    }

    public boolean verificarLlavesBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();

        for (Token token : lineaDeTokens) {
            switch (token.getTipoDeToken().toString()) {

                case "LLAVE_IZQUIERDA":
                    pila.push("{");
                    break;

                case "LLAVE_DERECHA": {
                    if (pila.isEmpty()) {
                        return false;
                    }
                    String ultimoLlave = pila.pop();
                    if (!"{".equals(ultimoLlave)) {
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
