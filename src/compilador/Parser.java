/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador;

import auxiliares.LineaDeContenido;
import auxiliares.MiError;
import auxiliares.Simbolo;
import auxiliares.TablaDeSimbolos;
import auxiliares.TiposDeError;
import auxiliares.TipoDeToken;
import auxiliares.Token;
import auxiliares.Bloque;
import auxiliares.BloqueTryExcept;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TablaDeSimbolos tablaDeSimbolos;
    private int cantidadDeComentarios;

    private Map<Integer, List<MiError>> erroresEncontradosMap;

    public Parser(List<List<Token>> tokens, List<LineaDeContenido> programa, int cantidadDeComentarios, Map<Integer, List<MiError>> errores, TablaDeSimbolos tablaDeSimbolos) {
        this.listaDeTokens = tokens;
        this.listaContenidoFinal = programa;
        this.cantidadDeComentarios = cantidadDeComentarios;
        this.erroresEncontradosMap = errores;
        this.tablaDeSimbolos = tablaDeSimbolos;
    }

    public List<String> analisisSintactico() throws IOException {
        System.out.println();
        System.out.println(" 40 ESTAMOS EN EL ANALISIS SINTACTICO ");
        System.out.println();

        /*
        System.out.println();
        System.out.println("Lista de Tokens al ENTRAR AL PARSER tiene tamanio " + listaDeTokens.size());
        int count = 0;
        for (List tkn : listaDeTokens) {
            for (Object token : tkn) {
                System.out.println(token.toString());
                ++count;
            }

        }
         */
        boolean enBloqueDef = false;
        int indentacionDef = 0;
        int indentacionBloqueDef = -1;
        int cantidadDeInstruccionesBloqueDef = 0;
        int lineaTokenDef = 0;

        boolean enBloqueWhile = false;
        int indentacionWhile = 0;
        int indentacionBloqueWhile = -1;
        int cantidadDeInstruccionesBloqueWhile = 0;
        int lineaTokenWhile = 0;

        boolean enBloqueTry = false;
        int indentacionTry = 0;
        int indentacionBloqueTry = -1;
        int cantidadDeInstruccionesBloqueTry = 0;
        int lineaTokenTry = 0;

        boolean enBloqueExcept = false;
        int indentacionExcept = 0;
        int indentacionBloqueExcept = -1;
        int cantidadDeInstruccionesBloqueExcept = 0;
        int lineaTokenExcept = 0;

        boolean existeTokenAntesDeImport = false;

        boolean existeInput = false;

        boolean enBloqueFor = false;

        boolean enBloqueIf = false;
        boolean enBloqueElse = false;

        int indentacionFor = 0;

        boolean enInstruccionReturn = false;

        boolean enInstruccionPrint = false;

        int indentacionInstruccionActual = -1;
        int nivelDeIndentacion = 0;

        int contadorPrintEnExcept = 0;

        int contadorDeInstrucciones = 0;

        int indentacionReturn = 0;

        System.out.println();

        //Almacena la indentacion de los bloques
        Stack<Bloque> pilaIndentacion = new Stack<>();
        // Inicializar la pila con un valor de 0 para la indentación general de toda instrucción
        pilaIndentacion.push(new Bloque(0, "programa"));

        int nivelIndentacionActual = 0;

        int numeroInstruccionesEnBloqueExcept = 0;

        Token tokenActual = new Token();
        Token tokenSiguiente = new Token();

        /*
        boolean tryExceptBalanceados = verificarBloqueTryExceptBalanceado(listaDeTokens);
        if(!tryExceptBalanceados){
            System.out.println();
                System.out.println("132 TRY EXCEPT NO BALANCEADOS " + !tryExceptBalanceados );
        }
         */
        BloqueTryExcept tryExceptBalanceados = verificarBloqueTryExceptBalanceado(listaDeTokens);

        // Verifica si tryExceptBalanceados es null antes de intentar acceder a sus campos
        if (tryExceptBalanceados != null) {
            System.out.println("140 bloqueTryExceptBalanceado != null " + tryExceptBalanceados.tipo);

            if (tryExceptBalanceados.tipo != null && tryExceptBalanceados.tipo.equals("try")) {
                // Bloque try sin except
                incluirErrorEncontrado(tryExceptBalanceados.linea, 758);
            }

            if (tryExceptBalanceados.tipo != null && tryExceptBalanceados.tipo.equals("except")) {
                // Bloque except sin try 
                incluirErrorEncontrado(tryExceptBalanceados.linea, 759);
            }
        } else {
            System.out.println("152Los bloques try y except están balanceados.");
        }
        //for (List<Token> lineaDeCodigoEnTokens : listaDeTokens) {
        //if (!lineaDeCodigoEnTokens.isEmpty()) {
        for (int linea = 0; linea < listaDeTokens.size(); linea++) {
            List<Token> lineaDeCodigoEnTokens = listaDeTokens.get(linea);
            if (!lineaDeCodigoEnTokens.isEmpty()) {

                int numeroError = -1;

                System.out.println();
                System.out.println("133 LA LINEA ES " + (linea + 1));

                System.out.println("136 Esta es pila = ");
                // Recorrer e imprimir la pila sin eliminar elementos
                for (Bloque bloque : pilaIndentacion) {
                    System.out.println("Indentación: " + bloque.indentacion + ", Tipo: " + bloque.tipo);
                }
                System.out.println();
                System.out.println();

                System.out.println("143 la linea de tokens actual es:");
                for (int i = 0; i < lineaDeCodigoEnTokens.size(); i++) {
                    System.out.print(lineaDeCodigoEnTokens.get(i).getLexema() + " ->  ");
                }
                System.out.println();
                System.out.println();

                //Leemos el token de indentacion
                //Para conocer la indentacion de la linea de codigo actual
                tokenActual = lineaDeCodigoEnTokens.get(0);

                //Leemos el token siguiente al actual
                //Para observa cual es el primer token de la linea de codigo.
                tokenSiguiente = new Token();
                if (lineaDeCodigoEnTokens.size() > 2) {
                    tokenSiguiente = lineaDeCodigoEnTokens.get(1);
                    //System.out.println("151 token siguiente es " + tokenSiguiente.getLexema() + "  en linea " + tokenSiguiente.getNumeroLinea());
                }
                System.out.println("191 token siguiente es " + tokenSiguiente.getLexema() + "  en linea " + tokenSiguiente.getNumeroLinea());
                System.out.println();
                System.out.println("193 linea =  " + linea + " lineaDeCodigoEnTokens size =  " + lineaDeCodigoEnTokens.size());

                //Si la instruccion actual esta dentro del bloque un determinado bloque aumentamos su contador de instrucciones
                System.out.println("196 La instruccion es " + tokenActual.getLexema() + " enBloqueTry es " + enBloqueTry);
                System.out.println("197 La instruccion es " + tokenSiguiente.getLexema() + " enBloqueTry es " + enBloqueTry);

                if (enBloqueTry && !tokenSiguiente.getLexema().equals("except")) {
                    ++cantidadDeInstruccionesBloqueTry;
                }

                System.out.println();
                System.out.println("203 En bloque try " + enBloqueTry + " cantidad de instruccines " + cantidadDeInstruccionesBloqueTry);

                //Comprobamos la indentacion antes de clasificar los tokens y verificar la sintaxis.
                //El primer token siempre es el token de indentacion
                //if (linea == 0) {
                System.out.println("208 indentacionInstruccionActual antes de asignar la de la linea de tokens es " + indentacionInstruccionActual);
                //Almancena la indentacion de la instruccion actual no importa cual sea la instruccion
                //indentacionInstruccionActual = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                indentacionInstruccionActual = Integer.parseInt(tokenActual.getLiteral());

                System.out.println("213  en la linea " + linea + " la indentacionInstruccionActual = " + indentacionInstruccionActual);

                //Verifica si el peek de la pila de indentacion es 'if' cuando en la linea que leemos existe 'else'
                //porque 'if' se cierra al aparecer 'else'. Si existe 'if' (como debe ser) lo elimina de la pila
                if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().tipo.equals("if") && tokenSiguiente.getLexema().equals("else")) {
                    System.out.println("219  tokenSiguiente " + tokenSiguiente.getLexema() + " indentacionInstruccionActual  " + indentacionInstruccionActual);
                    pilaIndentacion.pop(); //Sacamos if porque encontramos else
                    enBloqueIf = false; //Cerramos el bloque try
                }

                //Verifica si la linea actual tiene una indentacion mayor que la indentacion de bloque 'programa', es decir, mayor que cero.
                if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().tipo.equals("programa") && indentacionInstruccionActual > 0) {
                    incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);// Código de error para indentación incorrecta 
                    System.out.println("226  el nivel es programa en linea " + linea + " la indentacionInstruccionActual = " + indentacionInstruccionActual + " y el tope de pila es " + pilaIndentacion.peek().indentacion);
                }
                /*
                if (!pilaIndentacion.isEmpty() && !pilaIndentacion.peek().tipo.equals("programa") && indentacionInstruccionActual == 0) {
                System.out.println("239 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " indentacion tope del bloque es  " + (pilaIndentacion.peek().indentacion + 4));    
                if (tokenSiguiente.getLexema().equals("except")) {
                        if (!pilaIndentacion.peek().tipo.equals("try")) {
                            incluirErrorEncontrado(tokenActual.getNumeroLinea(), 864);
                        }
                    }
                    pilaIndentacion.pop();
                 
                }
                 */
                //Asumimos que estamos en un bloque con !pilaIndentacion.peek().tipo.equals("programa") entonces veficamos que la instruccion tenga una indentacion de 
                //acuerdo con el bloque, por lo que deberia ser pilaIndentacion.peek().indentacion + 4 
                System.out.println("239 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " indentacion tope del bloque es  " + (pilaIndentacion.peek().indentacion + 4));
                if (!pilaIndentacion.isEmpty() && !pilaIndentacion.peek().tipo.equals("programa") && indentacionInstruccionActual != pilaIndentacion.peek().indentacion + 4) {

                    System.out.println("242 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " y indentacion tope de pila  " + pilaIndentacion.peek().indentacion);
                    //Verifica si la instruccion que leemos tiene una identacion mayor o menor que el bloque donde deberia estar
                    if (indentacionInstruccionActual < pilaIndentacion.peek().indentacion || indentacionInstruccionActual > pilaIndentacion.peek().indentacion) {
                        System.out.println("245 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " y indentacion tope de pila  " + pilaIndentacion.peek().indentacion);
                        //Verifica si el peek de la pila de indentacion es 'try' cuando en la linea que leemos existe 'except'
                        //porque 'try' se cierra al aparecer 'except'. Si existe try (como debe ser) lo elimina de la pila
                        //Un except puede cerrar otros except anidados
                        if (tokenSiguiente.getLexema().equals("except")) {
                            if (pilaIndentacion.peek().tipo.equals("except")) {
                                pilaIndentacion.pop(); //Sacamos try porque encontramos except
                                enBloqueExcept = false;
                                System.out.println("253 hay un except en la pila y la instruccion es otro except " + pilaIndentacion.peek().tipo + " y la instruccion es " + tokenSiguiente.getLexema());
                            }

                            if (pilaIndentacion.peek().tipo.equals("try") && indentacionInstruccionActual == pilaIndentacion.peek().indentacion) {
                                pilaIndentacion.pop(); //Sacamos try porque encontramos except
                                enBloqueTry = false; //Cerramos el bloque try
                                System.out.println("259 hay un try en la pila y la instruccion es un except " + pilaIndentacion.peek().tipo + " indentacion de la instruccion actual" + indentacionInstruccionActual + " tope de pila  " + pilaIndentacion.peek().indentacion);
                            } else if (pilaIndentacion.peek().tipo.equals("try") && indentacionInstruccionActual != pilaIndentacion.peek().indentacion) {
                                System.out.println("262 Encontramos un try en tope de pila y la instruccion es " + tokenSiguiente.getLexema() + " con IndentacionInstruccionActual" + indentacionInstruccionActual + " y tope de pila  " + pilaIndentacion.peek().indentacion);
                                pilaIndentacion.pop(); //Sacamos try porque encontramos except
                                enBloqueTry = false; //Cerramos el bloque try
                                System.out.println("266 Sacamos un try porque la instruccion es " + tokenSiguiente.getLexema() + " IndentacionInstruccionActual " + indentacionInstruccionActual + " tope de pila  " + pilaIndentacion.peek().indentacion);
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750); // Código de error para indentación incorrecta 
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);

                            }
                            /*
                            if (!pilaIndentacion.peek().tipo.equals("try")) {
                                pilaIndentacion.pop(); //Sacamos try porque encontramos except
                                enBloqueTry = false; //Cerramos el bloque try
                                System.out.println("275 Sacamos un try porque la instruccion es " + tokenSiguiente.getLexema() + " IndentacionInstruccionActual " + indentacionInstruccionActual + " tope de pila  " + pilaIndentacion.peek().indentacion);
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750); // Código de error para indentación incorrecta 
                                incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);
                                 incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);

                            }
                             */
                        } else {
                            if (!pilaIndentacion.isEmpty() && indentacionInstruccionActual < pilaIndentacion.peek().indentacion && pilaIndentacion.peek().tipo.equals("if")) {
                                pilaIndentacion.pop(); //Sacamos el if porque no es evaluado
                                enBloqueIf = false; //Cerramos el bloque if
                            }
                            if (!pilaIndentacion.isEmpty() && indentacionInstruccionActual == pilaIndentacion.peek().indentacion && pilaIndentacion.peek().tipo.equals("for")) {
                                pilaIndentacion.pop(); //Sacamos el For porque no es evaluado
                                enBloqueFor = false; //Cerramos el bloque For
                            }
                            /*
                            System.out.println("283 La indentacionInstruccionActual " + indentacionInstruccionActual + " es diferente del tope de pila  " + pilaIndentacion.peek().indentacion);
                            incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750); // Código de error para indentación incorrecta 
                            incluirErrorEncontrado(tokenActual.getNumeroLinea(), 750);
                             */
                        }

                    }//fin if indentacionInstruccionActual <> pilaIndentacion.peek().indentacion 

                    //Caso en la que la instruccion que leemos tiene una indentacion exactamente igual al bloque que esta en el tope de la final 
                    //Si el tope de pila es try y es except debe cerrar el bloque try 
                    //Si el tope de pila es try y no hay except debe cerrar el bloque try
                    //Si el tope de pila es def y no hay return debe cerrar el bloque def
                    //Si el tope de pila es while debe cerrar el bloque while
                    System.out.println("295 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " indentacion tope de pila  " + pilaIndentacion.peek().indentacion);

                    if (!pilaIndentacion.isEmpty() && !pilaIndentacion.peek().tipo.equals("programa") && (indentacionInstruccionActual == pilaIndentacion.peek().indentacion)) {
                        System.out.println("298 instruccion " + tokenSiguiente.getLexema() + " indentacionInstruccionActual " + indentacionInstruccionActual + " indentacion tope de pila  " + pilaIndentacion.peek().indentacion);
                        if (tokenSiguiente.getLexema().equals("except")) {
                            if (pilaIndentacion.peek().tipo.equals("except")) {
                                pilaIndentacion.pop(); //Sacamos except porque encontramos otro antes except
                                enBloqueExcept = false;
                            }

                            if (pilaIndentacion.peek().tipo.equals("try")) {
                                pilaIndentacion.pop(); //Sacamos try porque encontramos except
                                enBloqueTry = false; //Cerramos el bloque try
                                System.out.println("308 Sacamos un try porque la instruccion es " + tokenSiguiente.getLexema() + " IndentacionInstruccionActual" + indentacionInstruccionActual + " tope de pila  " + pilaIndentacion.peek().indentacion);
                            }
                        }
                        if (!pilaIndentacion.peek().tipo.equals("programa") && indentacionInstruccionActual == pilaIndentacion.peek().indentacion) {

                            pilaIndentacion.pop(); //Sacamos except porque encontramos otro antes except
                            enBloqueExcept = false;

                        }

                    } //fin if indentacionInstruccionActual == pilaIndentacion.peek().indentacion 

                }// fin if indentacionInstruccionActual != pilaIndentacion.peek().indentacion + 4

                //Como la comprobacion de la indentacion la hacemos con el token de indentacion 
                //podemos ahora verificar si en la linea actual el token en posicion 1 es un token de bloque
                //si es asi lo incluimos
                switch (tokenSiguiente.getLexema()) {
                    case "def" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "def"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "def"));
                        }
                        enBloqueDef = true;
                    }
                    case "while" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "while"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "while"));
                        }
                        enBloqueWhile = true;
                    }
                    case "try" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "try"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "try"));
                        }
                        enBloqueTry = true;
                    }
                    case "except" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "except"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "except"));
                        }
                        enBloqueTry = true;
                        enBloqueExcept = true;
                    }
                    case "for" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "for"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "for"));
                        }

                    }
                    case "if" -> {
                        if (!pilaIndentacion.isEmpty() && pilaIndentacion.peek().indentacion == 0 && indentacionInstruccionActual == 0) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "if"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "if"));
                        }

                    }
                    case "else" -> {
                        if (!pilaIndentacion.isEmpty()) {
                            pilaIndentacion.push(new Bloque(indentacionInstruccionActual, "else"));
                        } else {
                            nivelDeIndentacion = (!pilaIndentacion.isEmpty()) ? (pilaIndentacion.peek().indentacion + 4) : 0;
                            pilaIndentacion.push(new Bloque(nivelDeIndentacion, "else"));
                        }

                    }
                    default -> {
                    }
                } // }//Fin comprobacion de indentacion ( i == 0)

                //Iniciamos el recorrido de la los tokens de la linea de código, token por token
                //Y los clasificamos de acuerdo a su tipo para analizar su sintaxis.
                for (int i = 0; i < lineaDeCodigoEnTokens.size(); i++) {
                    int numeroDeLineaTokenActual = 0;
                    tokenActual = lineaDeCodigoEnTokens.get(i);

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
                                    System.out.println(" 75 IMPORT esta en numero de linea " + numeroDeLineaTokenActual);
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
                                    lineaTokenWhile = numeroDeLineaTokenActual;
                                    int indiceTokenWhile = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionWhile = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    //System.out.println("250 Indentacion actual: " + nivelIndentacionActual + " indentacion InstruccionActual " + indentacionInstruccionActual + "indentacionWhile " + indentacionWhile);
                                    System.out.println();
                                    System.out.println("379 Encontramos una instruccion  while " + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenWhile);
                                    validarSintaxisDeLineaWhile(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenWhile);
                                    if (numeroDeLineaTokenActual == lineaDeCodigoEnTokens.size()) {
                                        System.out.println("381 Instruccion del bloque while:  en linea " + numeroDeLineaTokenActual + " == " + lineaDeCodigoEnTokens.size());
                                        numeroError = 629;
                                        incluirErrorEncontrado(lineaTokenWhile, numeroError);
                                        enBloqueWhile = false;
                                    }

                                    break;
                                case "input":
                                    existeInput = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenInput = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    System.out.println();
                                    System.out.println("386 Encontramos una instruccion input " + " linea " + numeroDeLineaTokenActual + "  indice input " + indiceTokenInput);
                                    System.out.println();
                                    validarSintaxisInput(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenInput);

                                    break;
                                case "print":
                                    enInstruccionPrint = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenPrint = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    validarSintaxisPrint(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenPrint);
                                    break;

                                case "def":
                                    enBloqueDef = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    lineaTokenDef = numeroDeLineaTokenActual;

                                    indentacionDef = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                                    int indiceTokenDef = lineaDeCodigoEnTokens.indexOf(tokenActual);

                                    System.out.println();
                                    System.out.println("683 Encontramos una instruccion  def " + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenDef);
                                    System.out.println();
                                    boolean existenErrores = validarSintaxisDeDef(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenDef); //false no hay errores

                                    break;

                                case "return":
                                    enInstruccionReturn = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    indentacionReturn = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                                    int indiceTokenReturn = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    validarSintaxisDeReturn(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenReturn);
                                    enBloqueDef = false;
                                    System.out.println();
                                    System.out.println("305 return");
                                    System.out.println();
                                    break;

                                case "try":
                                    enBloqueTry = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    lineaTokenTry = numeroDeLineaTokenActual;

                                    indentacionBloqueTry = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());
                                    int indiceTokenTry = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionTry = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    System.out.println("710 Encontramos una instruccion  try " + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenTry);

                                    validarSintaxisDeLineaTry(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenTry);
                                    break;
                                case "except":
                                    enBloqueExcept = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    lineaTokenExcept = numeroDeLineaTokenActual;

                                    int indiceTokenExcept = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionExcept = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    System.out.println();
                                    System.out.println("723 Encontramos una instruccion  except " + " linea " + numeroDeLineaTokenActual + "  indice except " + indiceTokenExcept);
                                    System.out.println();
                                    validarSintaxisDeLineaExcept(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenExcept);
                                    break;
                                case "for":
                                    enBloqueFor = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenFor = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionFor = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    System.out.println("314 Encontramos un  for " + indiceTokenFor + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenFor);
                                    break;

                                case "if":
                                    enBloqueIf = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenIf = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionFor = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    System.out.println("314 Encontramos un  for " + indiceTokenIf + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenIf);
                                    break;
                                case "else":
                                    enBloqueElse = true;
                                    numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                                    int indiceTokenElse = lineaDeCodigoEnTokens.indexOf(tokenActual);
                                    indentacionFor = Integer.parseInt(lineaDeCodigoEnTokens.getFirst().getLiteral());

                                    System.out.println("314 Encontramos un  for " + indiceTokenElse + " linea " + numeroDeLineaTokenActual + "  indice while " + indiceTokenElse);
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

                        case TipoDeToken.UNARIO_RESTA:
                        case TipoDeToken.UNARIO_SUMA: {

                            int indiceToken = lineaDeCodigoEnTokens.indexOf(tokenActual);
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();

                            validarOperadoresUnarios(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceToken);

                            System.out.println();
                            System.out.println("248 Operardo unario " + tokenActual.getLexema() + " linea " + numeroDeLineaTokenActual + "  indice " + indiceToken);
                            System.out.println();

                        }
                        break;

                        case TipoDeToken.SUMA:
                        case TipoDeToken.RESTA:
                        case TipoDeToken.MULTIPLICACION:
                        case TipoDeToken.DIVISION:
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

                        case TipoDeToken.SUMA_Y_ASIGNACION:
                        case TipoDeToken.RESTA_Y_ASIGNACION:
                        case TipoDeToken.MULTIPLICACION_Y_ASIGNACION:
                        case TipoDeToken.DIVISION_Y_ASIGNACION:
                        case TipoDeToken.ASIGNACION:
                            int indiceTokenAsignacion = lineaDeCodigoEnTokens.indexOf(tokenActual);
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                            validarOperadorAsignacion(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenAsignacion);
                            System.out.println();
                            System.out.println("182 Token de asignacion ");
                            System.out.println();
                            break;

                        case TipoDeToken.IDENTIFICADOR:
                            System.out.println();
                            System.out.println("337 En llamada de funcion " + enBloqueDef + " " + tokenActual.getLexema() + " " + i);
                            System.out.println();
                            int indiceTokenActual = lineaDeCodigoEnTokens.indexOf(tokenActual);
                            numeroDeLineaTokenActual = tokenActual.getNumeroLinea();
                            Token sucesorTokenActual = new Token();
                            if ((indiceTokenActual) + 1 < lineaDeCodigoEnTokens.size()) {
                                sucesorTokenActual = lineaDeCodigoEnTokens.get(indiceTokenActual + 1);
                            }
                            if (sucesorTokenActual.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA) {
                                break;
                            }
                            if (enBloqueDef) {
                                //Cambiamos el nombre de la funcion de variable a funcion en la tabla de simbolos
                                modificarTipoSimboloEnTablaSimbolos(tokenActual.getLexema(), "funcion");
                                System.out.println();
                                System.out.println("337 En llamada de funcion " + enBloqueDef + " " + tokenActual.getLexema() + " " + i);
                                System.out.println();
                            }
                            if (!enBloqueDef && !enInstruccionReturn) {
                                if (esFuncion(tokenActual.getLexema())) {
                                    System.out.println();
                                    System.out.println("347 En llamada de funcion " + tokenActual.getLexema() + " " + numeroDeLineaTokenActual + " " + i);
                                    System.out.println();
                                    validarSintaxisDeLlamadaDeFuncion(lineaDeCodigoEnTokens, numeroDeLineaTokenActual, indiceTokenActual);
                                }
                            }

                            if (esFuncion(tokenActual.getLexema())) {
                                boolean llamadaDeFuncion = esLlamadaAFuncion(lineaDeCodigoEnTokens, indiceTokenActual);
                                if (llamadaDeFuncion) {
                                    System.out.println();
                                    System.out.println("502 El identificador es una llamda de funcion: " + tokenActual.getLexema() + " en linea " + numeroDeLineaTokenActual);
                                } else {
                                    System.out.println();
                                    System.out.println("505 El identificador no es una llamda de funcion: " + tokenActual.getLexema() + " en linea " + numeroDeLineaTokenActual);
                                }
                            } else {
                                System.out.println();
                                System.out.println("509 El identificador no es una funcion: " + tokenActual.getLexema() + " en linea " + numeroDeLineaTokenActual);
                            }

                            break;
                        default:
                            break;

                    }
                    if (lineasDeCodigoNoEvaluadas(lineaDeCodigoEnTokens)) {
                        i = lineaDeCodigoEnTokens.size() - 1;
                    }

                }//fin for de lectura de tokens de una lineas de codigo

            }
        }//fin for lectura de lineas de codigo

        System.out.println("136 Esta es pila = ");
        // Recorrer e imprimir la pila sin eliminar elementos
        for (Bloque bloque : pilaIndentacion) {
            System.out.println("Indentación: " + bloque.indentacion + ", Tipo: " + bloque.tipo);
        }
        System.out.println();

       
        System.out.println();
        System.out.println("LISTA CONTENIDO FINAL PARSE:");
        //imprimirListas(listaContenidoFinal);
        imprimirListasDeContenidoFinal(listaContenidoFinal);
        System.out.println();

        System.out.println();
        System.out.println("PROGRAMA EN PYTHON REVISADO:");
        //imprimirListas(listaContenidoFinal);
        List<String> programaRevisado = generarProgramaEnPythonRevisado(listaContenidoFinal);

        /*
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
        
         */
        System.out.println();
        System.out.println("370 PARSER TABLA DE SIMBOLOS ");
        imprimirTablaDeSimbolos();

        System.out.println();
        System.out.println("616 Esta es antes de salir del sintactico: ");
        // Recorrer e imprimir la pila sin eliminar elementos
        //pilaIndentacion.pop();
        for (Bloque bloque : pilaIndentacion) {
            System.out.println("Indentación: " + bloque.indentacion + ", Tipo: " + bloque.tipo);
        }
        System.out.println();

        return programaRevisado;
    } // fin metodo analisisSintactico

    //INICIO METODOS AUXILIARES
    public boolean lineasDeCodigoNoEvaluadas(List<Token> lineaDeTokens) {
        boolean lineaNoEvaluada = false;
        int indice = 0;

        Token token1 = new Token();
        Token token2 = new Token();
        Token token3 = new Token();
        Token token4 = new Token();
        Token token5 = new Token();
        /*
    if (lineaDeCodigoActual.contains("print(f\"")
                    || lineaDeCodigoActual.contains("print(\"|\",")
                    || lineaDeCodigoActual.contains("if")
                    || lineaDeCodigoActual.contains("all(")
                    || lineaDeCodigoActual.contains(" fila, columna")
                    || lineaDeCodigoActual.contains("resultado = f\"\\n")
                    || lineaDeCodigoActual.contains("if jugador_actual ==")
                    || lineaDeCodigoActual.contains("tablero[fila][columna]")
                    || lineaDeCodigoActual.contains("tablero = [[")
                   )
         */
        if ((indice + 1) < lineaDeTokens.size()) {
            token1 = lineaDeTokens.get((indice + 1));
        }
        if ((indice + 2) < lineaDeTokens.size()) {
            token2 = lineaDeTokens.get((indice + 2));
        }
        if ((indice + 3) < lineaDeTokens.size()) {
            token3 = lineaDeTokens.get((indice + 3));
        }
        if ((indice + 4) < lineaDeTokens.size()) {
            token4 = lineaDeTokens.get((indice + 4));
        }
        if ((indice + 5) < lineaDeTokens.size()) {
            token5 = lineaDeTokens.get((indice + 5));
        }
        if (token1.getLexema().equals("print") && lineaDeTokens.size() == 2) {
            lineaNoEvaluada = false;
            System.out.println("561 print");
        } else if (lineaDeTokens.size() >= 4 && token1.getLexema().equals("print") && token2.getLexema().equals("(") && token3.getLexema().equals("f") && token4.getLexema().equals("\"")) {
            lineaNoEvaluada = true;
            System.out.println("561 print(f\"");
        } else if (lineaDeTokens.size() >= 5 && token1.getLexema().equals("print") && token2.getLexema().equals("(") && token3.getLexema().equals("\"") && token4.getLexema().equals("|") && token5.getLexema().equals("\"")) {
            lineaNoEvaluada = true;
            System.out.println("565 print(\"|\"");
        }
        if (token1.getLexema().equals("if")) {
            lineaNoEvaluada = true;
            System.out.println("569 if");
        }
        if (token1.getLexema().equals("else")) {
            lineaNoEvaluada = true;
            System.out.println("569 else");
        }
        if (token1.getLexema().equals("all")) {
            lineaNoEvaluada = true;
            System.out.println("573 all");
        }
        if (token1.getLexema().equals("fila") && token2.getLexema().equals(",") && token3.getLexema().equals("columna")) {
            lineaNoEvaluada = true;
            System.out.println("577 fila, columna");
        }
        if (token1.getLexema().equals("resultado") && token2.getLexema().equals("=") && token3.getLexema().equals("f") && token4.getLexema().equals("\"") && token5.getLexema().equals("\\n")) {
            lineaNoEvaluada = true;
            System.out.println("581 resultado = f\"\n");
        }
        /*
        if (token1.getLexema().equals("if") && token2.getLexema().equals("jugador_actual") && token3.getLexema().equals("==")) {
            lineaNoEvaluada = true;
             System.out.println("561 print(f\"");
        }
         */
        if (lineaDeTokens.size() >= 4 && token1.getLexema().equals("tablero") && token2.getLexema().equals("[") && token3.getLexema().equals("fila") && token4.getLexema().equals("]")) {
            lineaNoEvaluada = true;
            System.out.println("591 tablero [fila]");
        }
        /*
        //tablero = [[" " for _ in range(3)] for _ in range(3)]
        if (lineaDeTokens.size() >= 4 && token1.getLexema().equals("tablero") && token2.getLexema().equals("=") && token3.getLexema().equals("[") && token4.getLexema().equals("[")) {
            lineaNoEvaluada = true;
            System.out.println("595 tablero = [[");
        }
         */

        //  movimientos_posibles = [(i, j) for i in range(3) for j in range(3) if tablero[i][j] == " "]
        if (lineaDeTokens.size() >= 4 && token1.getLexema().equals("movimientos_posibles") && token2.getLexema().equals("=") && token3.getLexema().equals("[") && token4.getLexema().equals("(")) {
            lineaNoEvaluada = true;
            System.out.println("595 movimientos_posibles = [(");
        }
        return lineaNoEvaluada;

    }

    public BloqueTryExcept verificarBloqueTryExceptBalanceado(List<List<Token>> listaDeTokens) {
        Stack<BloqueTryExcept> pila = new Stack<>();
        BloqueTryExcept resultado = null;

        for (List<Token> lineaDeTokens : listaDeTokens) {
            int indiceTry = buscarPosicionDeTokenPorLexema(lineaDeTokens, "try");
            int indiceExcept = buscarPosicionDeTokenPorLexema(lineaDeTokens, "except");

            Token token = null;
            if (indiceTry != -1) {
                token = lineaDeTokens.get(indiceTry);
            } else if (indiceExcept != -1) {
                token = lineaDeTokens.get(indiceExcept);
            }

            if (token != null) {
                switch (token.getLexema()) {
                    case "try":
                        pila.push(new BloqueTryExcept(token.getNumeroLinea(), "try"));
                        break;

                    case "except":
                        if (pila.isEmpty()) {
                            return new BloqueTryExcept(token.getNumeroLinea(), "try");
                        }
                        BloqueTryExcept ultimoBloque = pila.pop();
                        if (!"try".equals(ultimoBloque.tipo)) {
                            return new BloqueTryExcept(token.getNumeroLinea(), "try");
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        if (!pila.isEmpty()) {
            resultado = pila.peek();
        }

        return resultado;
    }

    public void verificarIndentacion(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        Stack<Integer> pilaIndentacion = new Stack<>();
        int nivelIndentacionActual = 0;
        boolean existeWhile = false;
        boolean existeTry = false;
        boolean existeExcept = false;

        //int nivelIndentacion = contarEspacios(linea);
    }

    public void validarSintaxisInput(List<Token> lineaDeTokens, int numeroDeLinea, int indiceInput) {
        int numeroError = 0;
        Token tokenAnteriorInput = new Token();
        if ((indiceInput - 1) <= (lineaDeTokens.size() - 1)) {
            tokenAnteriorInput = lineaDeTokens.get((indiceInput - 1));
        }
        Token tokenSiguienteInput = new Token();
        if ((indiceInput + 1) < lineaDeTokens.size()) {
            tokenSiguienteInput = lineaDeTokens.get((indiceInput + 1));
        }
        Token tokenSiguienteSiguienteInput = new Token();
        if ((indiceInput + 2) < lineaDeTokens.size()) {
            tokenSiguienteSiguienteInput = lineaDeTokens.get((indiceInput + 2));
        }
        Token ultimoToken = lineaDeTokens.getLast();
        Token penultimoToken = lineaDeTokens.get(lineaDeTokens.size() - 2);

        int indiceParentesisIzquierdo = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.PARENTESIS_IZQUIERDO);
        int indiceParentesisDerecho = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.PARENTESIS_DERECHO);

        //No existe el parentesis izquierdo despues de input
        if (tokenSiguienteInput.getTipoDeToken() != TipoDeToken.PARENTESIS_IZQUIERDO) {
            numeroError = 401;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            System.out.println("579 Encontramos un error  500 " + indiceParentesisIzquierdo);
        }
        //Existe un token diferente de ) al final de la linea
        if (ultimoToken.getTipoDeToken() != TipoDeToken.PARENTESIS_DERECHO) {
            numeroError = 501;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            System.out.println("584 Encontramos un error  501 " + indiceParentesisDerecho);
        }

        //Valida que los parentesis esten balanceados
        if (indiceParentesisIzquierdo != -1 || indiceParentesisDerecho != -1) {
            boolean parentesisBalanceados = verificarParentesisBalanceados(lineaDeTokens, numeroDeLinea);
            if (!parentesisBalanceados) {
                numeroError = 510;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                System.out.println("592 Encontramos un error  510 " + parentesisBalanceados);
            }
        }

        //Validamos si el arguemento de input es una string, en cuyo caso debe existir  comillas antes y despues
        int posicionTextoEntreComillas = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.TEXTO_ENTRE_COMILLAS);
        if (posicionTextoEntreComillas != -1) {
            //Existe un texto entre comillas como argumento de input
            Token tokenAnterior = lineaDeTokens.get((posicionTextoEntreComillas - 1)); //Validamos si haya comillas
            Token tokenSiguiente = lineaDeTokens.get((posicionTextoEntreComillas + 1)); //Validamos si haya comillas
            if (tokenAnterior.getTipoDeToken() != TipoDeToken.COMILLAS) {
                numeroError = 518;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                System.out.println("640 Encontramos un error  518 " + indiceParentesisIzquierdo);
            }
            if (tokenSiguiente.getTipoDeToken() != TipoDeToken.COMILLAS) {
                numeroError = 519;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                System.out.println("645 Encontramos un error  519 " + indiceParentesisIzquierdo);
            }

        }
        //Valida si las comillas estan en pares sino falta alguna
        int existenComillas = verificarExistenciaDeComillas(lineaDeTokens);
        if ((existenComillas % 2 != 0)) {
            numeroError = 524;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            System.out.println("654 Encontramos un error  524" + indiceParentesisIzquierdo);
        }

        if (tokenSiguienteInput.getTipoDeToken() == TipoDeToken.PARENTESIS_IZQUIERDO) {
            switch (tokenSiguienteSiguienteInput.getTipoDeToken()) {
                case TipoDeToken.PALABRA_RESERVADA:
                    numeroError = 403;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    break;

                case TipoDeToken.COMILLAS:
                    break;
                case TipoDeToken.IDENTIFICADOR:
                    if (!verificarVariableEnTablaDeSimbolos(tokenSiguienteSiguienteInput.getLexema())) {
                        numeroError = 405;
                        incluirErrorEncontrado(numeroDeLinea, numeroError);
                        System.out.println("638 Encontramos un error  405 " + tokenSiguienteSiguienteInput.getLexema());

                    }
                    break;

                default:
                    numeroError = 404;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("670 Encontramos un error  404 " + indiceParentesisIzquierdo);
                    break;
            }
        }

    }

    public void validarSintaxisDeLineaExcept(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenExcept) {
        int numeroError = 0;
        int posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
        Token tokenIndentacion = lineaDeTokens.getFirst();
        Token tokenAntecesorDeExcept = lineaDeTokens.get((indiceTokenExcept - 1));
        Token tokenSucesorDeExcept = new Token();
        Token tokenSucesorDelSucesorDeExcept = new Token();
        Token ultimoToken = lineaDeTokens.getLast();

        if ((indiceTokenExcept + 1) < lineaDeTokens.size()) {
            tokenSucesorDeExcept = lineaDeTokens.get((indiceTokenExcept + 1));
        }

        //Valida que except este al comienzo de la linea de la instruccion while
        if (indiceTokenExcept > 1) {
            numeroError = 852;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que existan los dos puntos
        if (posicionTokenDosPuntos == -1) {
            numeroError = 854;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que cuando no existe los dos puntos no se puede usar otro token en su lugar 
        if ((indiceTokenExcept + 2) < lineaDeTokens.size()) {
            tokenSucesorDelSucesorDeExcept = lineaDeTokens.get((indiceTokenExcept + 2));
            if (tokenSucesorDelSucesorDeExcept.getLexema().equals(ultimoToken.getLexema()) && posicionTokenDosPuntos == -1) {
                numeroError = 853;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }

        //Valida que existe algun otro token despues de :
        if (posicionTokenDosPuntos == 3 && ultimoToken.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
            numeroError = 855;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida si aparecen parentesis
        if (verificarExistenciaParentesis(lineaDeTokens)) {
            numeroError = 851;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que el siguiente token despues de except sea una token de EXCEPTION
        if (tokenSucesorDeExcept.getTipoDeToken() != TipoDeToken.EXCEPCION) {
            numeroError = 851;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que el siguiente token no sea una palabra reservada
        if (tokenSucesorDeExcept.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA) {
            numeroError = 858;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que el siguiente token no sea un identificador
        if (tokenSucesorDeExcept.getTipoDeToken() == TipoDeToken.IDENTIFICADOR) {
            numeroError = 859;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

    }

    public void validarSintaxisDeLineaTry(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenTry) {

        int numeroError = 0;
        int posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
        Token tokenIndentacion = lineaDeTokens.getFirst();
        Token tokenAntecesorATry = lineaDeTokens.get((indiceTokenTry - 1));
        Token tokenSucesorATry = new Token();
        Token ultimoToken = lineaDeTokens.getLast();
        if ((indiceTokenTry + 1) < lineaDeTokens.size()) {
            tokenSucesorATry = lineaDeTokens.get((indiceTokenTry + 1));
        }

        //Valida que try este al comienzo de la linea de la instruccion while
        if (indiceTokenTry > 1) {
            numeroError = 752;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que existan los dos puntos
        if (posicionTokenDosPuntos == -1) {
            numeroError = 754;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Valida que cuando no existe los dos puntos no se puede usar otro token en su lugar 
        if (tokenSucesorATry.getLexema().equals(ultimoToken.getLexema()) && posicionTokenDosPuntos == -1) {
            numeroError = 753;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (posicionTokenDosPuntos == 2 && ultimoToken.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
            numeroError = 755;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (verificarExistenciaParentesis(lineaDeTokens)) {
            numeroError = 751;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

    }

    public void validarSintaxisPrint(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenPrint) {
        int numeroError = 0;
        Token tokenAntecesorAPrint = lineaDeTokens.get((indiceTokenPrint - 1));
        Token tokenSucesorAPrint = new Token();
        Token ultimoToken = lineaDeTokens.getLast();
        if ((indiceTokenPrint + 1) < lineaDeTokens.size()) {
            tokenSucesorAPrint = lineaDeTokens.get((indiceTokenPrint + 1));
        }

        if (indiceTokenPrint != 1) {
            numeroError = 700; // algo antes de print
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (ultimoToken.getTipoDeToken() != TipoDeToken.PARENTESIS_DERECHO) {
            numeroError = 701; // algo despues de )
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (tokenSucesorAPrint.getTipoDeToken() != TipoDeToken.PARENTESIS_IZQUIERDO) {
            numeroError = 702;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        if (tokenSucesorAPrint.getTipoDeToken() == TipoDeToken.CORCHETE_IZQUIERDO || ultimoToken.getTipoDeToken() == TipoDeToken.CORCHETE_DERECHO) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (tokenSucesorAPrint.getTipoDeToken() == TipoDeToken.LLAVE_IZQUIERDA || ultimoToken.getTipoDeToken() == TipoDeToken.LLAVE_DERECHA) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }

        System.out.println();
        System.out.println("452 verificarExistenciaParentesis " + verificarExistenciaParentesis(lineaDeTokens));
        System.out.println();
        if (verificarExistenciaParentesis(lineaDeTokens)) {
            boolean parentesisBalanceadosEnPrint = verificarParentesisBalanceados(lineaDeTokens, numeroDeLinea);
            if (!parentesisBalanceadosEnPrint) {
                System.out.println("457 Parentesis NO balanceados:" + parentesisBalanceadosEnPrint);
                numeroError = 510;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
            System.out.println();
            System.out.println("115 verificarExistenciaParentesis ");
            System.out.println();
        }

    }

    public boolean validarSintaxisDeReturn(List<Token> lineaDeCodigoEnTokens, int numeroDeLinea, int indiceReturn) {
        int numeroError = 0;
        Token antecesor = lineaDeCodigoEnTokens.get(indiceReturn - 1);
        Token sucesor = new Token();
        Token ultimoToken = lineaDeCodigoEnTokens.getLast();
        boolean resultado = true; //No hay errores sintaxis correcta

        if (indiceReturn + 1 < lineaDeCodigoEnTokens.size()) {
            sucesor = lineaDeCodigoEnTokens.get(indiceReturn + 1);
            if (sucesor.getTipoDeToken() == TipoDeToken.PALABRA_RESERVADA && !sucesor.getLexema().equals("True") && !sucesor.getLexema().equals("False")) {
                numeroError = 671;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                resultado = false;
            }
            if (esOperadorRelacional(ultimoToken.getTipoDeToken())) {
                numeroError = 672;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                resultado = false;
            }
            if (esOperadorAritmetico(ultimoToken.getTipoDeToken())) {
                numeroError = 673;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                resultado = false;
            }
        }

        return resultado;

    }

    public boolean esLlamadaAFuncion(List<Token> lineaDeCodigoEnTokens, int indiceTokenActual) {

        Token sucesor = new Token();
        Token ultimoToken = lineaDeCodigoEnTokens.getLast();
        boolean resultado = true; //Cumple sintaxis de llamada de funcion

        if (indiceTokenActual + 1 < lineaDeCodigoEnTokens.size()) {
            sucesor = lineaDeCodigoEnTokens.get(indiceTokenActual + 1);
        }

        if (verificarExistenciaParentesis(lineaDeCodigoEnTokens)) {
            int indiceParentesisIzquierdo = buscarPosicionDeTokenPorTipoDeToken(lineaDeCodigoEnTokens, TipoDeToken.PARENTESIS_IZQUIERDO);
            int indiceParentesisDerecho = buscarPosicionDeTokenPorTipoDeToken(lineaDeCodigoEnTokens, TipoDeToken.PARENTESIS_DERECHO);
            if (indiceParentesisIzquierdo != -1) {
                if (lineaDeCodigoEnTokens.get(indiceParentesisIzquierdo).getTipoDeToken() == sucesor.getTipoDeToken()) {
                    resultado = true;
                }
            }
            if (indiceParentesisDerecho != -1) {
                if (lineaDeCodigoEnTokens.get(indiceParentesisDerecho).getTipoDeToken() == ultimoToken.getTipoDeToken()) {
                    resultado = true;
                }
            }
        } else {
            resultado = false;
        }

        return resultado;
    }

    public void validarSintaxisDeLlamadaDeFuncion(List<Token> lineaDeCodigoEnTokens, int numeroDeLinea, int indiceIdentificador) {
        int numeroError = 0;
        Token antecesor = lineaDeCodigoEnTokens.get(indiceIdentificador - 1);
        Token sucesor = new Token();
        Token ultimoToken = lineaDeCodigoEnTokens.getLast();

        if (indiceIdentificador + 1 < lineaDeCodigoEnTokens.size()) {
            sucesor = lineaDeCodigoEnTokens.get(indiceIdentificador + 1);
        } else {
            numeroError = 667;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        if (antecesor.getTipoDeToken() != TipoDeToken.INDENTACION) {
            numeroError = 668;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        /*
        if (sucesor.getTipoDeToken() != TipoDeToken.PARENTESIS_IZQUIERDO) {
            numeroError = 500;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
         */
        boolean p = verificarExistenciaParentesis(lineaDeCodigoEnTokens);
        System.out.println();
        System.out.println("498 validarSintaxisDeLlamadaDeFuncion-> existen parentesis " + p);
        boolean b = verificarParentesisBalanceados(lineaDeCodigoEnTokens, numeroDeLinea);
        System.out.println("498 validarSintaxisDeLlamadaDeFuncion-> parentesis balanceados " + b);
        if (verificarExistenciaParentesis(lineaDeCodigoEnTokens)) {
            System.out.println("498 validarSintaxisDeLlamadaDeFuncion-> parentesis balanceados " + b);
            if (!verificarParentesisBalanceados(lineaDeCodigoEnTokens, numeroDeLinea)) {
                numeroError = 510;
                incluirErrorEncontrado(numeroDeLinea, numeroError);

            }
        }

    }

    private boolean validarSintaxisDeDef(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        boolean existenErrores = false;
        int numeroError = 0;

        Token indentToken = lineaDeTokens.get(0);
        Token keywordToken = lineaDeTokens.get(1);

        Token ultimoToken = lineaDeTokens.get((lineaDeTokens.size() - 1));
        int posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);

        //Valida que def este al comienzo de la linea de la instruccion def
        if (indiceToken > 1) { //La posicion correcta de def es 1
            numeroError = 655;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            existenErrores = true;
        }
        //Valida forma (0,def)
        if (indiceToken == 1 && lineaDeTokens.size() == 2) {
            numeroError = 666;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            existenErrores = true;
        }
        //Valida si existe :
        if (lineaDeTokens.size() > 2 && (posicionTokenDosPuntos == -1)) { //No existen :
            numeroError = 657;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            existenErrores = true;
        }
        //Valida que no se cambie : por un operador aritmetico
        if (esOperadorAritmetico(ultimoToken.getTipoDeToken())) {
            numeroError = 658;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            existenErrores = true;
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
                existenErrores = true;
            }
            if (tokenSucesor.getTipoDeToken() != TipoDeToken.IDENTIFICADOR && tokenSucesor.getTipoDeToken() != TipoDeToken.DOS_PUNTOS) {
                numeroError = 663;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                existenErrores = true;
            }
            if (tokenSucesor.getTipoDeToken() == TipoDeToken.IDENTIFICADOR && ultimoToken.getTipoDeToken() == TipoDeToken.DOS_PUNTOS && (lineaDeTokens.size() == 4)) {
                numeroError = 665;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                existenErrores = true;
                System.out.println("411 ");
            }
            if (tokenSucesor.getTipoDeToken() == TipoDeToken.DOS_PUNTOS) {
                numeroError = 660;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                existenErrores = true;
            }
            if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                numeroError = 657;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                existenErrores = true;
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
                existenErrores = true;
            }
            System.out.println("444 ");
        }

        //Valida que no se usen corchetes en lugar de parentesis redondos
        boolean existeCorchetesEnLineaDeTokens = verificarExistenciaCorchetes(lineaDeTokens);
        if (keywordToken.getLexema().equals("while") && existeCorchetesEnLineaDeTokens) {
            numeroError = 521;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
            existenErrores = true;
            boolean existeCorchetesBalanceados = verificarCorchetesBalanceados(lineaDeTokens, numeroDeLinea);
            if (existeCorchetesBalanceados) {
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                }
            } else {
                numeroError = 522;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
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
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                }
            } else {
                numeroError = 523;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
                existenErrores = true;
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
                if (posicionTokenDosPuntos == -1) {
                    System.out.println("435 ");
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                } else if (posicionTokenDosPuntos == (lineaDeTokens.size() - 2)) {
                    numeroError = 624;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    existenErrores = true;
                    System.out.println("441 ");
                }
            }
            System.out.println("444 ");
        }
        return existenErrores;
    }

    public void validarOperadoresBinarios(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        int numeroError = 0;
        Token tokenAntecesorAlOperador = lineaDeTokens.get((indiceToken - 1));
        Token tokenSucesorAlOperador = new Token();
        //Token tokenReturn = new Token();
        //Token tokenPrint = new Token();

        //int posicionTokenReturn = buscarPosicionDeTokenPorLexema(lineaDeTokens, "return");
        //System.out.println("1100 existe return en posicion: " + posicionTokenReturn);
        //int posicionTokenPrint = buscarPosicionDeTokenPorLexema(lineaDeTokens, "print");
        //System.out.println("1102 existe print en posicion: " + posicionTokenPrint);
        //Observa el siguiente token al operador
        if ((indiceToken + 1) < lineaDeTokens.size()) {
            tokenSucesorAlOperador = lineaDeTokens.get((indiceToken + 1));
        }

        /*    if (posicionTokenReturn != -1) {
           tokenReturn =  lineaDeTokens.get(posicionTokenReturn);
            System.out.println("1110 existe return en posicion: " + tokenAntecesorAlOperador.getLexema());
            System.out.println("1111 existe return en posicion: " + tokenSucesorAlOperador.getLexema());
            if (posicionTokenReturn == 1) {
                if (!esVariableValida_O_Numero(tokenAntecesorAlOperador.getTipoDeToken())) {
                    numeroError = 122;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("1116 existe return en posicion: " + tokenAntecesorAlOperador.getLexema());
                }
                if (!esVariableValida_O_Numero(tokenSucesorAlOperador.getTipoDeToken())) {
                    numeroError = 123;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                    System.out.println("1121 existe return en posicion: " + tokenSucesorAlOperador.getLexema());
                }
            }
        } else if (posicionTokenPrint != -1) {
            tokenPrint = lineaDeTokens.get(posicionTokenPrint);
            System.out.println("1126  print  token antecesorr a operador: " + tokenAntecesorAlOperador.getLexema());
            System.out.println("1127 print token sucesor a operador" + tokenSucesorAlOperador.getLexema());
            if (posicionTokenPrint == 1) {
                if (!esVariableValida_O_Numero(tokenAntecesorAlOperador.getTipoDeToken())) {
                    numeroError = 122;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
                if (!esVariableValida_O_Numero(tokenSucesorAlOperador.getTipoDeToken())) {
                    numeroError = 123;
                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                }
            }
        } else {
         */
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
        //}

    }

    public void validarOperadoresUnarios(List<Token> lineaDeTokens, int numeroDeLinea, int indiceToken) {
        int numeroError = 0;
        Token tokenAnteriorAlOperador = lineaDeTokens.get((indiceToken - 1));
        Token tokenSiguienteAlOperador = new Token();
        System.out.println("1334 El indice del operador unario es " + indiceToken + " el token anterior es " + tokenAnteriorAlOperador.getLexema());

        //Observa el siguiente token al operador
        if ((indiceToken + 1) < lineaDeTokens.size()) {
            tokenSiguienteAlOperador = lineaDeTokens.get((indiceToken + 1));
        }
        //Caso variable++
        if (tokenAnteriorAlOperador.getTipoDeToken() != TipoDeToken.IDENTIFICADOR && tokenSiguienteAlOperador == null) {
            numeroError = 122;
            incluirErrorEncontrado(numeroDeLinea, numeroError);
        }
        //Caso ++variable
        if (tokenSiguienteAlOperador != null) {
            if ((tokenSiguienteAlOperador.getTipoDeToken() != TipoDeToken.IDENTIFICADOR) && tokenAnteriorAlOperador == null) {
                numeroError = 123;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
        //Caso variable++variable
        if (tokenSiguienteAlOperador != null) {
            if (tokenAnteriorAlOperador.getTipoDeToken() == TipoDeToken.IDENTIFICADOR && tokenSiguienteAlOperador.getTipoDeToken() == TipoDeToken.IDENTIFICADOR) {
                numeroError = 127;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
    }

    //Valida la indentacion de la linea de codigo actual
    public boolean validarIndentacionBloque(int indentacionBloque, int indentacionInstruccion) {
        return indentacionInstruccion != indentacionBloque; //sin son diferentes devuelve false
    }

    public void validarSintaxisDeLineaWhile(List<Token> lineaDeTokens, int numeroDeLinea, int indiceTokenWhile) {
        int numeroError = 0;

        Token indentToken = lineaDeTokens.get(0);
        Token keywordToken = lineaDeTokens.get(1);
        Token ultimoToken = lineaDeTokens.get((lineaDeTokens.size() - 1));
        int posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);

        //Valida la indentacion de while
        /*if (indentToken.getTipoDeToken() != null) {
            if (Integer.parseInt(indentToken.getLiteral()) > 0) {
                //return "Error: indentación incorrecta debe ser cero";
                numeroError = 620;
                incluirErrorEncontrado(numeroDeLinea, numeroError);
            }
        }
         */
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
        //System.out.println("426 " + existeParentesisEnLineaDeTokens);
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
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
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
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
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
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
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
                posicionTokenDosPuntos = buscarPosicionDeTokenPorTipoDeToken(lineaDeTokens, TipoDeToken.DOS_PUNTOS);
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

    //Encuentra el indice del token usando el tipo de token a buscar
    //devuelve -1 si no lo encuentra
    public int buscarPosicionDeTokenPorTipoDeToken(List<Token> lineaDeTokens, TipoDeToken tipoABuscar) {
        int posicion = -1;
        for (int i = 0; i < lineaDeTokens.size(); ++i) {
            if (lineaDeTokens.get(i) != null) {
                if (lineaDeTokens.get(i).getTipoDeToken() != null) {
                    if (lineaDeTokens.get(i).getTipoDeToken() == tipoABuscar) {
                        posicion = i;
                        break;
                    }
                }

            }
        }
        return posicion;
    }

    public int buscarPosicionDeTokenPorLexema(List<Token> lineaDeTokens, String lexemaABuscar) {
        int posicion = -1;
        for (int i = 0; i < lineaDeTokens.size(); ++i) {
            if (lineaDeTokens.get(i) != null) {
                if (lineaDeTokens.get(i).getLexema() != null) {
                    if (lineaDeTokens.get(i).getLexema().equals(lexemaABuscar)) {
                        posicion = i;
                        break;
                    }
                }

            }

        }
        return posicion;
    }

    public int buscarTokenPorLexema(List<Token> lineaDeTokens, String lexemaABuscar) {
        int posicion = -1;
        for (int i = 0; i < lineaDeTokens.size(); ++i) {
            if (lineaDeTokens.get(i) != null) {
                if (lineaDeTokens.get(i).getLexema() != null) {
                    if (lineaDeTokens.get(i).getLexema().equals(lexemaABuscar)) {
                        posicion = i;
                        break;
                    }
                }

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
        // SINTEXIS CORRECTA:  VARIABLE_VALIDA = VARIABLE_VALIDA | NUMERO | LLAMADA_FUNCION

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
                            if (!tokenSucesorAlOperador.getLexema().equals("input") && !tokenSucesorAlOperador.getLexema().equals("None")) {
                                // Forma> IDENTIFICADOR = PALABRA_RESERVADA (diferente de input
                                numeroError = 601;
                                incluirErrorEncontrado(numeroDeLinea, numeroError);
                                System.out.println("1428 El termino derecho del operador de asignacion es: " + tokenSucesorAlOperador.getLexema());

                            }

                            break;
                        case TipoDeToken.IDENTIFICADOR:
                            if (esLlamadaAFuncion(lineaDeTokens, indiceTokenAsignacion)) {
                                if (!esFuncion(tokenSucesorAlOperador.getLexema()) && !tokenSucesorAlOperador.getLexema().equals("int")) {
                                    numeroError = 675;
                                    incluirErrorEncontrado(numeroDeLinea, numeroError);
                                    System.out.println();
                                    System.out.println("509 El identificador no es una funcion: " + tokenSucesorAlOperador.getLexema() + " en linea " + numeroDeLinea);

                                }
                            } else {
                                System.out.println();
                                System.out.println("505 El identificador no es una llamda de funcion: " + tokenSucesorAlOperador.getLexema() + " en linea " + numeroDeLinea);
                            }

                            break;
                        case TipoDeToken.CORCHETE_IZQUIERDO:
                            //Caso de linea no evaluada pero si verificar que el identificador este correcto
                            //tablero = [[" " for _ in range(3)] for _ in range(3)]

                            Token token1 = new Token();
                            Token token2 = new Token();
                            Token token3 = new Token();

                            if ((indiceTokenAsignacion + 1) < lineaDeTokens.size()) {
                                token1 = lineaDeTokens.get((indiceTokenAsignacion + 1));
                            }
                            if ((indiceTokenAsignacion + 2) < lineaDeTokens.size()) {
                                token2 = lineaDeTokens.get((indiceTokenAsignacion + 2));
                            }
                            if ((indiceTokenAsignacion + 3) < lineaDeTokens.size()) {
                                token3 = lineaDeTokens.get((indiceTokenAsignacion + 3));
                            }

                            if (lineaDeTokens.size() >= 4 && token1.getLexema().equals("[") && token2.getLexema().equals("\"")) {
                                //No hacemos nada
                            }
                            break;
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
                } else if (indiceTokenAsignacion >= 3) {
                    List<Token> sublistaDeTokens = lineaDeTokens.subList(1, indiceTokenAsignacion);
                    System.out.println();
                    System.out.println("1455 Existe una sublista de variables: " + sublistaDeTokens.toString());
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

    private int verificarExistenciaDeComillas(List<Token> lineaDeTokens) {
        int contadorComillas = 0;
        for (Token token : lineaDeTokens) {
            if (token.getTipoDeToken() == TipoDeToken.COMILLAS) {
                contadorComillas++;
            }
        }
        return contadorComillas;
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
    //Devuelve true si los parentesis estan balanceados, false en caso contrario
    public boolean verificarParentesisBalanceados(List<Token> lineaDeTokens, int numeroDeLinea) {
        Stack<String> pila = new Stack<>();
        boolean resultado = false;
        for (Token token : lineaDeTokens) {
            // System.out.println();
            //System.out.println(" 1121 Leyendo token " + token.getTipoDeToken() + " " + token.getLexema());
            switch (token.getTipoDeToken()) {
                case TipoDeToken.PARENTESIS_IZQUIERDO:
                    pila.push("(");
                    //System.out.println(" 1125 Se incluyo un nuevo  " + token.getTipoDeToken().toString());
                    //System.out.println();
                    //System.out.println(" 1127 Contenido de la pila: " + pila);
                    break;

                case TipoDeToken.PARENTESIS_DERECHO:
                    //System.out.println(" 1131 Contenido de la pila: " + pila);
                    if (pila.isEmpty()) {
                        //System.out.println("1133 Error: pila vacía al encontrar un paréntesis derecho.  " + pila.isEmpty());
                        return false;
                    }
                    //System.out.println(" 1136 Contenido de la pila: " + pila);
                    //System.out.println("1137  pila vacía al encontrar un paréntesis derecho. " + pila.isEmpty());
                    String ultimoParentesis = pila.pop();
                    //System.out.println(" 1139 Se retiro un   " + ultimoParentesis);
                    if (!"(".equals(ultimoParentesis)) {
                        //System.out.println(" 1141 Se retiro un   " + ultimoParentesis + " coinciden " + !"(".equals(ultimoParentesis));
                        resultado = true;
                    }
                    break;

                default:
                    //System.out.println("1148 En default : " + token.getTipoDeToken());
                    break;
            }
            // Imprimir el contenido de la pila en cada iteración
            //System.out.println(" 1152 Contenido de la pila: " + pila);
        }
        //System.out.println(" 1921 Contenido de la pila.isEmpty(): " + pila.isEmpty());
        resultado = pila.isEmpty();
        //System.out.println("1156 Resultado final: " + resultado);
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

    public boolean verificarVariableEnTablaDeSimbolos(String nombreDeVariable) {
        return this.tablaDeSimbolos.contieneSimbolo(nombreDeVariable);
    }

    public void modificarTipoSimboloEnTablaSimbolos(String nombre, String nuevoTipo) {
        System.out.println();
        System.out.println("1482 Token sucesor def " + nombre);
        System.out.println();
        Simbolo simbolo = tablaDeSimbolos.obtenerSimbolo(nombre);
        System.out.println();
        System.out.println("1482 Token sucesor def " + simbolo.getTipo());
        System.out.println();
        if (simbolo != null) {
            simbolo.setTipo(nuevoTipo);
        }
    }

    public boolean esFuncion(String nombre) {
        Simbolo simbolo = tablaDeSimbolos.obtenerSimbolo(nombre);
        if (simbolo != null) {
            if (simbolo.getTipo().equals("funcion")) {
                return true;
            }
            System.out.println();
            System.out.println("1666 Es funcion " + simbolo.getTipo() + " " + simbolo.getTipo().equals("funcion"));

        }

        return false;
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

    public void imprimirTablaDeSimbolos() {
        System.out.println(tablaDeSimbolos.toString());
    }

    //Recorre una lista de string y la imprime BORRAR
    public void imprimirListas(List<String> contenidoArchivo) {
        for (String linea : contenidoArchivo) {
            System.out.println(linea);
        }
    }

    public void imprimirListasDeContenidoFinal(List<LineaDeContenido> contenido) {
        for (LineaDeContenido linea : contenido) {
            System.out.println(linea);
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
                    programaEnPythonRevisado.add(formatoError + numeroDeError + ": " + descripcion);
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

}
