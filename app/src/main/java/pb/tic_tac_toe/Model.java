package pb.tic_tac_toe;

/**
 * Created by per on 09/04/15.
 */


public class Model{

    private final int GRID_WIDTH = 3;
    private final int GRID_HEIGHT = 3;

    private Piece[][] board = new Piece[GRID_WIDTH][GRID_HEIGHT];
    private Piece currentPlayer = Piece.X;

    public Model() {
        reset();
    }

    public void reset() {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                board[i][j] = Piece._;
                System.out.println("YOYO");
            }
        }
    }

    public void setValue(int row, int col, Piece p) {
        board[row][col] = p;
        togglePlayer();
    }

    private void togglePlayer() {
        if (currentPlayer == Piece.X) {
            currentPlayer = Piece.O;
        } else {
            currentPlayer = Piece.X;
        }
    }

    public Piece getCurrentPlayer() {
        return currentPlayer;
    }

    public Piece getValue(int row, int col) {
        return board[row][col];
    }

    public Piece checkWinner() {
        Piece winner = Piece._;

        // check for rows that win
        for (int i = 0; i < GRID_WIDTH; i++) {
            if (winner == Piece._ && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                winner = board[i][0];
            }
        }

        // check for cols that win
        for (int i = 0; i < GRID_HEIGHT; i++) {
            if (winner == Piece._ && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                winner = board[0][i];
            }
        }

        // check the two diagonals
        if (winner == Piece._ && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            winner = board[0][0];
        }
        if (winner == Piece._ && board[2][0] == board[1][1] && board[1][1] == board[0][2]) {
            winner = board[2][0];
        }

        return winner;
    }

    public Boolean checkIfGameIsOver() {
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (board[i][j] == Piece._) {
                    return false;
                }
            }
        }
        return true;
    }

}
