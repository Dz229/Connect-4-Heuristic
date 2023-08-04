import sac.game.*;
import sac.*;
import java.util.*;

public class Connect extends GameStateImpl {

  ///////////////////////////////////////////
  // Enum dla możliwych symboli na planszy //
  ///////////////////////////////////////////
  enum Symbol {
    X, O
  }

  ///////////////////////
  // Zmienne Statyczne //
  ///////////////////////
  private Symbol[][] board;
  private static int m;
  private static int n;

  ////////////////////////
  // Funkcje pomocnicze //
  ////////////////////////
  public Symbol getBoard(int i, int j) {
    return board[i][j];
  }

  public Symbol getCurrentPlayerSymbol() {
    if (isMaximizingTurnNow())
      return Symbol.X;
    else
      return Symbol.O;
  }

  public int getM() {
    return m;
  }

  public int getN() {
    return n;
  }

  //////////////////
  // Konstruktory //
  //////////////////
  Connect(int m, int n) {
    this.m = m;
    this.n = n;
    board = new Symbol[m][n];
    setMaximizingTurnNow(true);
  }

  Connect(Connect parent) {
    board = new Symbol[m][n];
    this.m = parent.m;
    this.n = parent.n;
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        board[i][j] = parent.board[i][j];
    setMaximizingTurnNow(parent.isMaximizingTurnNow());
  }

  ///////////////////////
  // Metoda ToString() //
  ///////////////////////
  public String toString() {
    // String builder
    StringBuilder s = new StringBuilder();
    // Budowanie numerowania kolumn
    for (int j = 0; j < 2 * n + 1; j++) {
      if (j % 2 != 0)
        s.append(j / 2);
      else
        s.append(" ");
    }
    s.append("\n");
    // Budowanie "Dachu"
    for (int j = 0; j < 2 * n + 1; j++)
      s.append("-");
    s.append("\n");
    // Budowanie planszy
    for (int i = 0; i < m; i++) {
      // Tworzenie lewej ściany
      s.append("|");
      // Dodawanie symboli i odstępów pomiędzy nimi
      for (int j = 0; j < n; j++) {
        if (board[i][j] == Symbol.X)
          s.append("X");
        else if (board[i][j] == Symbol.O)
          s.append("O");
        else
          s.append(".");
        if (j != n - 1)
          s.append(" ");
      }
      // Tworzenie prawej ściany
      s.append("|");
      s.append(i);
      // Następna linia planszy
      s.append("\n");
    }
    // Budowanie "Podłogi"
    for (int j = 0; j < 2 * n + 1; j++)
      s.append("-");
    s.append("\n");
    // Budowanie numerowania kolumn
    for (int j = 0; j < 2 * n + 1; j++) {
      if (j % 2 != 0)
        s.append(j / 2);
      else
        s.append(" ");
    }
    s.append("\n");
    // Zwracanie stringa
    return s.toString();
  }

  //////////////
  // HashCode //
  //////////////
  public int hashCode() {
    Symbol[] connectFlat = new Symbol[m * n];
    int k = 0;
    for (int i = 0; i < m; i++)
      for (int j = 0; j < n; j++)
        connectFlat[k++] = board[i][j];
    return Arrays.hashCode(connectFlat);
  }

  /////////////////////////////
  // Metoda weykonująca ruch //
  /////////////////////////////
  public boolean makeMove(int column) {
    // Sprawdzanie czy kolumna istnieje
    if (column < n && column >= 0) {
      // Pętla sprawdzająca na którym wierszu możliwe jest wykonanie ruchu
      for (int i = m - 1; i >= 0; i--) {
        // Jeżeli miejsce na możliwy ruch zostało znalezione, to symbol jest dodawany
        if (board[i][column] == null) {
          if (isMaximizingTurnNow())
            board[i][column] = Symbol.X;
          else
            board[i][column] = Symbol.O;
          // Zmiana tury na drugiego gracza
          setMaximizingTurnNow(!isMaximizingTurnNow());
          // Zwrócenie true jeżeli ruch został wykonany
          return true;
        }
      }
    }
    // Zwrócenie false jeżeli ruch został wykonany
    return false;
  }

  //////////////////////////////////////////////////
  // Metoda zwracająca listę potencjalnych dzieci //
  //////////////////////////////////////////////////
  public List<GameState> generateChildren() {
    List<GameState> children = new LinkedList<GameState>();
    for (int i = 0; i < n; i++) {
      Connect child = new Connect(this);
      child.setMoveName(Integer.toString(i));
      if (child.makeMove(i))
        children.add(child);
    }
    return children;
  }

  static {
    setHFunction(new PositionValue());
  }
}