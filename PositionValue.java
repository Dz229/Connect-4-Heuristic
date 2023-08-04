import sac.*;

public class PositionValue extends StateFunction {

  private static Connect.Symbol computerSymbol = Connect.Symbol.O;
  private static Connect.Symbol playerSymbol = Connect.Symbol.X;

  public static void setXasComputerSymbol() {
    computerSymbol = Connect.Symbol.X;
    playerSymbol = Connect.Symbol.O;
  }

  public static void setOasComputerSymbol() {
    computerSymbol = Connect.Symbol.O;
    playerSymbol = Connect.Symbol.X;
  }

  public double calculate(State state) {
    // Klasa Connect zawierająca pozycję do wyliczenia
    Connect conn = (Connect) state;
    // Zmienna na wartość całkowitą pozycji
    double totalValue = 0.0;
    // Zmienne m i n
    int m = conn.getM();
    int n = conn.getN();

    //////////////////////////////////////////////////
    //////// Wartości poszczególnych pozycji /////////
    //////////////////////////////////////////////////
    // double[] positiveValue = { 0.0, 1.0, 5.0, 20.0, Double.POSITIVE_INFINITY };
    // double[] negativeValue = { 0.0, -0.5, -4.5, -19.5, Double.NEGATIVE_INFINITY
    ////////////////////////////////////////////////// };
    double[] positiveValue = { 0.0, 1.0, 8.0, 27.0, Double.POSITIVE_INFINITY };
    double[] negativeValue = { 0.0, -0.8, -7.5, -26.5, Double.NEGATIVE_INFINITY };

    ///////////////////////////////////////////////////////
    // Przydzielanie bonusowej wartości za środkowe pola //
    ///////////////////////////////////////////////////////
    for (int i = 0; i < m; i++) {
      for (int j = 2; j < n - 3; j++) {
        if (conn.getBoard(i, j) == computerSymbol)
          totalValue += 0.25;
        if (conn.getBoard(i, j) == playerSymbol)
          totalValue -= 0.25;
      }
    }

    for (int i = 0; i < m; i++) {
      for (int j = 3; j < n - 4; j++) {
        if (conn.getBoard(i, j) == computerSymbol)
          totalValue += 0.35;
        if (conn.getBoard(i, j) == playerSymbol)
          totalValue -= 0.25;
      }
    }

    /////////////////////////////////////////////////////////
    // Liczenie wartości pozycji w pionie i reguła sufitu //
    /////////////////////////////////////////////////////////
    for (int i = 0; i < n; i++) {

      ///////////////////
      // Reguła sufitu //
      ///////////////////
      if (conn.getBoard(0, i) == computerSymbol) // Jeżeli symbol należy do komputera
        return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwróć
                                                                                                   // nieskończoność
                                                                                                   // dodatnią
      else if (conn.getBoard(0, i) == playerSymbol) // Jeżeli symbol należy do gracza
        return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwróć
                                                                                                   // nieskończoność
                                                                                                   // ujemną

      ////////////////////////////////////////
      // Liczenie wartości pozycji w pionie //
      ////////////////////////////////////////
      int perpendicularNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd pionowo
      int playerPerpendicularNumberOfSymbolsInARow = 0; // Ilość symboli gracza pod rząd pionowo

      for (int j = 0; j < m; j++) {
        if (conn.getBoard(j, i) == computerSymbol) { // Jeżeli symbol należy do komputera
          perpendicularNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
          if (perpendicularNumberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
            return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                       // nieskończoność
                                                                                                       // dodatnią
          // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
          totalValue += negativeValue[playerPerpendicularNumberOfSymbolsInARow];
          playerPerpendicularNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
        } else if (conn.getBoard(j, i) == playerSymbol) { // Jeżeli symbol należy do gracza
          playerPerpendicularNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
          if (playerPerpendicularNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
            return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                       // nieskończoność
                                                                                                       // ujemną
          // Dodajemy do totalValue wartość wcześniejszych symboli komputera
          totalValue += positiveValue[perpendicularNumberOfSymbolsInARow];
          perpendicularNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
        } else { // Jeżeli pole jest puste
          // Dodajemy do totalValue warości za wcześniejsze pola
          totalValue += positiveValue[perpendicularNumberOfSymbolsInARow];
          totalValue += negativeValue[playerPerpendicularNumberOfSymbolsInARow];
          // Zerujemy znalezione symbole gracza i komputera
          perpendicularNumberOfSymbolsInARow = 0;
          playerPerpendicularNumberOfSymbolsInARow = 0;
        }
      }
      // Dodajemy do totalValue pozostałości z liczenia granic planszy
      totalValue += positiveValue[perpendicularNumberOfSymbolsInARow];
      totalValue += negativeValue[playerPerpendicularNumberOfSymbolsInARow];
      // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
      if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
        return totalValue;
    }

    //////////////////////////////////////////
    // Liczenie wartości pozycji w poziomie //
    //////////////////////////////////////////
    for (int i = 0; i < m; i++) { // Pętla iterująca pionowo
      int horizontalNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd poziomo
      int playerHorizontalNumberOfSymbolsInARow = 0; // Ilość symboli gracza pod rząd poziomo
      for (int j = 0; j < n; j++) { // Pętla iterująza poziomo
        if (conn.getBoard(i, j) == computerSymbol) {
          horizontalNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
          if (horizontalNumberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
            return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                       // nieskończoność
                                                                                                       // dodatnią
          // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
          totalValue += negativeValue[playerHorizontalNumberOfSymbolsInARow];
          playerHorizontalNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
        } else if (conn.getBoard(i, j) == playerSymbol) { // Jeżeli symbol należy do gracza
          playerHorizontalNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
          if (playerHorizontalNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
            return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                       // nieskończoność
                                                                                                       // ujemną
          // Dodajemy do totalValue wartość wcześniejszych symboli komputera
          totalValue += positiveValue[horizontalNumberOfSymbolsInARow];
          horizontalNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
        } else { // Jeżeli pole jest puste
          // Dodajemy do totalValue warości za wcześniejsze pola
          totalValue += positiveValue[horizontalNumberOfSymbolsInARow];
          totalValue += negativeValue[playerHorizontalNumberOfSymbolsInARow];
          // Zerujemy znalezione symbole gracza i komputera
          horizontalNumberOfSymbolsInARow = 0;
          playerHorizontalNumberOfSymbolsInARow = 0;
        }
      }
      // Dodajemy do totalValue pozostałości z liczenia granic planszy
      totalValue += positiveValue[horizontalNumberOfSymbolsInARow];
      totalValue += negativeValue[playerHorizontalNumberOfSymbolsInARow];
      // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
      if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
        return totalValue;
    }

    ///////////////////////////////////////////////////////////////////////
    // Liczenie pozycji w skosie od lewej górnej strony do prawej dolnej //
    ///////////////////////////////////////////////////////////////////////

    // Pętla iterująca po polach zaczynających się przy suficie planszy
    for (int i = 0; i < n; i++) {
      boolean end = false; // Zmienna kończąca pętle
      int numberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd
      int playerNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli gracza pod rząd
      // Zmienne iteracyjne do chodzenia po planszy
      int k = 0;
      int l = i;
      // Pętla iterująca po planszy w skos
      while (!end) {
        if (k < m && l < n) { // Jeżeli zmienne nie wychodzą poza granicę planszy
          if (conn.getBoard(k, l) == computerSymbol) { // Jeżeli symbol należy do komputera
            numberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (numberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // dodatnią
            // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            playerNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
          } else if (conn.getBoard(k, l) == playerSymbol) { // Jeżeli symbol należy do gracza
            playerNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (playerNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // ujemną
            // Dodajemy do totalValue wartość wcześniejszych symboli komputera
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
          } else { // Jeżeli pole jest puste
            // Dodajemy do totalValue warości za wcześniejsze pola
            totalValue += positiveValue[numberOfSymbolsInARow];
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            // Zerujemy znalezione symbole gracza i komputera
            numberOfSymbolsInARow = 0;
            playerNumberOfSymbolsInARow = 0;
          }
          // Przejście o jeden w prawy dół
          k++;
          l++;
        } else { // Jeżeli plansza się skończyła
          end = true; // Zmiana zmiennej końcowej na true
          totalValue += positiveValue[numberOfSymbolsInARow];
          totalValue += negativeValue[playerNumberOfSymbolsInARow];
          // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
          if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
            return totalValue;
        }
      }
    }
    // Pętla iterująca po lewej ścianie planszy
    for (int i = 1; i < m; i++) {
      boolean end = false; // Zmienna kończąca pętle
      int numberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd
      int playerNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli gracza pod rząd
      // Zmienne iteracyjne do chodzenia po planszy
      int k = i;
      int l = 0;
      // Pętla iterująca po planszy w skos
      while (!end) {
        if (k < m && l < n) { // Jeżeli zmienne nie wychodzą poza granicę planszy
          if (conn.getBoard(k, l) == computerSymbol) { // Jeżeli symbol należy do komputera
            numberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (numberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // dodatnią
            // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            playerNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
          } else if (conn.getBoard(k, l) == playerSymbol) { // Jeżeli symbol należy do gracza
            playerNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (playerNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // ujemną
            // Dodajemy do totalValue wartość wcześniejszych symboli komputera
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
          } else { // Jeżeli pole jest puste
            // Dodajemy do totalValue warości za wcześniejsze pola
            totalValue += positiveValue[numberOfSymbolsInARow];
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            // Zerujemy znalezione symbole gracza i komputera
            numberOfSymbolsInARow = 0;
            playerNumberOfSymbolsInARow = 0;
          }
          // Przejście o jeden w prawy dół
          k++;
          l++;
        } else { // Jeżeli plansza się skończyła
          end = true; // Zmiana zmiennej końcowej na true
          totalValue += positiveValue[numberOfSymbolsInARow];
          totalValue += negativeValue[playerNumberOfSymbolsInARow];
          // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
          if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
            return totalValue;
        }
      }
    }

    ///////////////////////////////////////////////////////////////////////
    // Liczenie pozycji w skosie od prawej górnej strony do lewej dolnej //
    ///////////////////////////////////////////////////////////////////////
    // Pętla iterująca po polach zaczynających się przy suficie planszy
    for (int i = n - 1; i >= 0; i--) {
      boolean end = false; // Zmienna kończąca pętle
      int numberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd
      int playerNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli gracza pod rząd
      // Zmienne iteracyjne do chodzenia po planszy
      int k = 0;
      int l = i;
      // Pętla iterująca po planszy w skos
      while (!end) {
        if (k < m && l >= 0) { // Jeżeli zmienne nie wychodzą poza granicę planszy
          if (conn.getBoard(k, l) == computerSymbol) { // Jeżeli symbol należy do komputera
            numberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (numberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // dodatnią
            // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            playerNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
          } else if (conn.getBoard(k, l) == playerSymbol) { // Jeżeli symbol należy do gracza
            playerNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (playerNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // ujemną
            // Dodajemy do totalValue wartość wcześniejszych symboli komputera
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
          } else { // Jeżeli pole jest puste
            // Dodajemy do totalValue warości za wcześniejsze pola
            totalValue += positiveValue[numberOfSymbolsInARow];
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            // Zerujemy znalezione symbole gracza i komputera
            numberOfSymbolsInARow = 0;
            playerNumberOfSymbolsInARow = 0;
          }
          // Przejście o jeden w lewy dół
          k++;
          l--;
        } else { // Jeżeli plansza się skończyła
          end = true; // Zmiana zmiennej końcowej na true
          totalValue += positiveValue[numberOfSymbolsInARow];
          totalValue += negativeValue[playerNumberOfSymbolsInARow];
          // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
          if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
            return totalValue;
        }
      }
    }
    // Pętla iterująca po prawej ścianie planszy
    for (int i = 1; i < m; i++) {
      boolean end = false; // Zmienna kończąca pętle
      int numberOfSymbolsInARow = 0; // Ilość znalezionych symboli pod rząd
      int playerNumberOfSymbolsInARow = 0; // Ilość znalezionych symboli gracza pod rząd
      // Zmienne iteracyjne do chodzenia po planszy
      int k = i;
      int l = n - 1;
      // Pętla iterująca po planszy w skos
      while (!end) {
        if (k < m && l >= 0) { // Jeżeli zmienne nie wychodzą poza granicę planszy
          if (conn.getBoard(k, l) == computerSymbol) { // Jeżeli symbol należy do komputera
            numberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (numberOfSymbolsInARow == 4) // Jeżeli są 4 te samy symbole pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // dodatnią
            // Odejmujemy od totalValue wartość wcześniejszych sumboli gracza
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            playerNumberOfSymbolsInARow = 0; // Zerujemy liczbe symboli gracza
          } else if (conn.getBoard(k, l) == playerSymbol) { // Jeżeli symbol należy do gracza
            playerNumberOfSymbolsInARow++; // Zwiększamy liczbę znalezionych symboli
            if (playerNumberOfSymbolsInARow == 4) // Jeżeli są 4 pod rząd
              return (conn.isMaximizingTurnNow()) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; // Zwracamy
                                                                                                         // nieskończoność
                                                                                                         // ujemną
            // Dodajemy do totalValue wartość wcześniejszych symboli komputera
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0; // Zerujemy liczbe symboli komputera
          } else { // Jeżeli pole jest puste
            // Dodajemy do totalValue warości za wcześniejsze pola
            totalValue += positiveValue[numberOfSymbolsInARow];
            totalValue += negativeValue[playerNumberOfSymbolsInARow];
            // Zerujemy znalezione symbole gracza i komputera
            numberOfSymbolsInARow = 0;
            playerNumberOfSymbolsInARow = 0;
          }
          // Przejście o jeden w lewy dół
          k++;
          l--;
        } else { // Jeżeli plansza się skończyła
          end = true; // Zmiana zmiennej końcowej na true
          totalValue += positiveValue[numberOfSymbolsInARow];
          totalValue += negativeValue[playerNumberOfSymbolsInARow];
          // Jeżeli totalValue ma nieskończoność to przerywamy dalsze liczenie
          if (totalValue == Double.POSITIVE_INFINITY || totalValue == Double.NEGATIVE_INFINITY)
            return totalValue;
        }
      }
    }

    /////////////////////////////////
    // Zwrócenie wartości końcowej //
    /////////////////////////////////
    return -(totalValue);
  }

  public double calculateOld(State state) {
    // Klasa Connect zawierająca pozycję do wyliczenia
    Connect conn = (Connect) state;
    // Zmienna na wartość całkowitą pozycji
    double totalValue = 0.0;
    // Zmienne m i n
    int m = conn.getM();
    int n = conn.getN();

    //////////////////////////////////////////////////
    //////// Wartości poszczególnych pozycji /////////
    //////////////////////////////////////////////////
    double[] positiveValue = { 0.0, 1.0, 5.0, 20.0, Double.POSITIVE_INFINITY };
    double[] negativeValue = { 0.0, -0.5, -4.5, -19.5, Double.NEGATIVE_INFINITY };

    ///////////////////
    // Reguła sufitu //
    ///////////////////
    for (int i = 0; i < n; i++) {
      if (conn.getBoard(0, i) == computerSymbol)
        return (totalValue + positiveValue[4]);
    }

    //////////////////////////////////////////////////
    // Skos od lewej górnej strony do prawej dolnej //
    //////////////////////////////////////////////////
    for (int i = 0; i < n; i++) {
      boolean end = false;
      int numberOfSymbolsInARow = 0;
      int k = 0;
      int l = i;
      while (!end) {
        if (k < m && l < n) {
          if (conn.getBoard(k, l) == computerSymbol)
            numberOfSymbolsInARow++;
          else {
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0;
          }
          k++;
          l++;
        } else {
          end = true;
          totalValue += positiveValue[numberOfSymbolsInARow];
          if (totalValue == Double.POSITIVE_INFINITY)
            return totalValue;
        }
      }
    }
    for (int i = 1; i < m; i++) {
      boolean end = false;
      int numberOfSymbolsInARow = 0;
      int k = i;
      int l = 0;
      while (!end) {
        if (k < m && l < n) {
          if (conn.getBoard(k, l) == computerSymbol)
            numberOfSymbolsInARow++;
          else {
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0;
          }
          k++;
          l++;
        } else {
          end = true;
          totalValue += positiveValue[numberOfSymbolsInARow];
          if (totalValue == Double.POSITIVE_INFINITY)
            return totalValue;
        }
      }
    }

    //////////////////////////////////////////////////
    // Skos od prawej górnej strony do lewej dolnej //
    //////////////////////////////////////////////////
    for (int i = n - 1; i >= 0; i--) {
      boolean end = false;
      int numberOfSymbolsInARow = 0;
      int k = 0;
      int l = i;
      while (!end) {
        if (k < m && l >= 0) {
          if (conn.getBoard(k, l) == computerSymbol)
            numberOfSymbolsInARow++;
          else {
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0;
          }
          k++;
          l--;
        } else {
          end = true;
          totalValue += positiveValue[numberOfSymbolsInARow];
          if (totalValue == Double.POSITIVE_INFINITY)
            return totalValue;
        }
      }
    }
    for (int i = 1; i < m; i++) {
      boolean end = false;
      int numberOfSymbolsInARow = 0;
      int k = i;
      int l = n - 1;
      while (!end) {
        if (k < m && l >= 0) {
          if (conn.getBoard(k, l) == computerSymbol)
            numberOfSymbolsInARow++;
          else {
            totalValue += positiveValue[numberOfSymbolsInARow];
            numberOfSymbolsInARow = 0;
          }
          k++;
          l--;
        } else {
          end = true;
          totalValue += positiveValue[numberOfSymbolsInARow];
          if (totalValue == Double.POSITIVE_INFINITY)
            return totalValue;
        }
      }
    }

    /////////////
    // Pionowo //
    /////////////
    for (int i = 0; i < n; i++) {
      int numberOfSymbolsInARow = 0;
      for (int j = 0; j < m; j++) {
        if (conn.getBoard(j, i) == computerSymbol)
          numberOfSymbolsInARow++;
        if (numberOfSymbolsInARow == 4)
          return Double.POSITIVE_INFINITY;
        else {
          totalValue += positiveValue[numberOfSymbolsInARow];
          numberOfSymbolsInARow = 0;
        }
      }
      totalValue += positiveValue[numberOfSymbolsInARow];
      if (totalValue == Double.POSITIVE_INFINITY)
        return totalValue;
    }

    /////////////
    // Poziomo //
    /////////////
    for (int i = 0; i < m; i++) {
      int numberOfSymbolsInARow = 0;
      for (int j = 0; j < n; j++) {
        if (conn.getBoard(i, j) == computerSymbol)
          numberOfSymbolsInARow++;
        if (numberOfSymbolsInARow == 4)
          return Double.POSITIVE_INFINITY;
        else {
          totalValue += positiveValue[numberOfSymbolsInARow];
          numberOfSymbolsInARow = 0;
        }
      }
      totalValue += positiveValue[numberOfSymbolsInARow];
      if (totalValue == Double.POSITIVE_INFINITY)
        return totalValue;
    }

    return totalValue;
  }
}