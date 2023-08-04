import java.util.Scanner;
import sac.game.*;
import sac.*;
import java.util.*;

public class GameLoop {
  private static String whoStarts;
  Connect conn;

  GameLoop(int m, int n) {
    whoStarts = "player";
    conn = new Connect(m, n);
    PositionValue ps = new PositionValue();
    // ps.setXasComputerSymbol();
  }

  GameLoop(int m, int n, String s) {
    whoStarts = "computer";
    conn = new Connect(m, n);
  }

  public void startGame() {
    boolean end = false;

    while (!end) {

      if (whoStarts.equals("player")) {
        // Ruch gracza
        System.out.println(conn);
        System.out.println("Enter number of column: ");

        try {
          Scanner input = new Scanner(System.in);
          int inputInt = input.nextInt();
          conn.makeMove(inputInt);
        } catch (Exception e) {
          System.out.println("Please enter a number!");
        }
        end = checkForWinningMove();
        if (end) {
          System.out.println("X WON!");
          break;
        }

        // MinMax

        // Ruch komputera
        // Algorithm algo = new Algorithm(conn);
        // if (!end)
        // conn.makeMove(algo.findBestMove());
        // end = checkForWinningMove();
        // if (end) {
        // System.out.println("O WON!");
        // break;
        // }

        GameSearchAlgorithm algo = new AlphaBetaPruning(conn);
        algo.execute();
        Map<String, Double> movesScores = algo.getMovesScores();
        System.out.println(movesScores);
        if (!end)
          conn.makeMove(Integer.parseInt(algo.getFirstBestMove()));
        end = checkForWinningMove();
        if (end) {
          System.out.println("O WON!");
          break;
        }

        ///////////
      }

      if (whoStarts.equals("computer")) {

        // Ruch komputera
        GameSearchAlgorithm algo = new AlphaBetaPruning(conn);
        algo.execute();
        Map<String, Double> movesScores = algo.getMovesScores();
        System.out.println(movesScores);
        if (!end)
          conn.makeMove(Integer.parseInt(algo.getFirstBestMove()));
        end = checkForWinningMove();
        if (end) {
          System.out.println("X WON!");
          break;
        }

        // Ruch gracza
        //System.out.println(conn.hashCode());
        System.out.println(conn);
        System.out.println("Enter number of column: ");

        try {
          Scanner input = new Scanner(System.in);
          int inputInt = input.nextInt();
          conn.makeMove(inputInt);
        } catch (Exception e) {
          System.out.println("Please enter a number!");
        }
        end = checkForWinningMove();
        if (end) {
          System.out.println("O WON!");
          break;
        }
      }
    }
    System.out.println(conn);
    //System.out.println(conn.hashCode());

  }

  private boolean checkForWinningMove() {
    PositionValue val = new PositionValue();
    double res = val.calculate(conn);
    // System.out.println("X value: " + res);
    if (res == Double.POSITIVE_INFINITY || res == Double.NEGATIVE_INFINITY)
      return true;
    val.setXasComputerSymbol();
    res = val.calculate(conn);
    // System.out.println("O value: " + res);
    if (res == Double.POSITIVE_INFINITY || res == Double.NEGATIVE_INFINITY)
      return true;
    val.setOasComputerSymbol();
    return false;
  }
}