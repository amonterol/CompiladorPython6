import random

def imprimir_tablero(tablero):
    """ Función para imprimir el tablero actual """
    for i in range(3):
        for j in range(3):
            print(f" {tablero[i][j]} ", end="")
            if j < 2:
                print("|", end="")
        print()  # Salto de línea después de cada fila
        if i < 2:
            print("-----------")  # Línea horizontal entre filas