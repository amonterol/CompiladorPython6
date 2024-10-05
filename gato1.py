def verificar_ganador(tablero, jugador):
    """ Función para verificar si hay un ganador """
    # Verificar filas, columnas y diagonales
    for i in range(3):
        if (all(tablero[i][j] == jugador for j in range(3)) or       # Filas
            all(tablero[j][i] == jugador for j in range(3)) or       # Columnas
            all(tablero[i][i] == jugador for i in range(3)) or       # Diagonal principal
            all(tablero[i][2 - i] == jugador for i in range(3))):   # Diagonal secundaria
            return True
    return False
