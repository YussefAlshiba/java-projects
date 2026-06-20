/* SudokuSolver:
   Solves a 9x9 Sudoku puzzle using the backtracking algorithm.
   Empty cells are represented by 0.
*/
public class SudokuSolver {

    private static final int SIZE = 9;
    private static final int BOX_SIZE = 3;

    public boolean solveSudoku(int[][] sudoku) {
        return solve(sudoku);
    }

    private boolean solve(int[][] sudoku) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (sudoku[row][col] == 0) {
                    for (int num = 1; num <= SIZE; num++) {
                        if (isValid(sudoku, row, col, num)) {
                            sudoku[row][col] = num;
                            if (solve(sudoku)) {
                                return true;
                            }
                            sudoku[row][col] = 0; // backtrack
                        }
                    }
                    return false; // no number works here
                }
            }
        }
        return true; // all cells filled
    }

    private boolean isValid(int[][] sudoku, int row, int col, int num) {
        // check row
        for (int c = 0; c < SIZE; c++) {
            if (sudoku[row][c] == num) return false;
        }
        // check column
        for (int r = 0; r < SIZE; r++) {
            if (sudoku[r][col] == num) return false;
        }
        // check 3x3 box
        int boxRowStart = (row / BOX_SIZE) * BOX_SIZE;
        int boxColStart = (col / BOX_SIZE) * BOX_SIZE;
        for (int r = boxRowStart; r < boxRowStart + BOX_SIZE; r++) {
            for (int c = boxColStart; c < boxColStart + BOX_SIZE; c++) {
                if (sudoku[r][c] == num) return false;
            }
        }
        return true;
    }

    public static void printSudoku(int[][] sudoku) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(sudoku[row][col] + " ");
                if ((col + 1) % BOX_SIZE == 0 && col < SIZE - 1) System.out.print("| ");
            }
            System.out.println();
            if ((row + 1) % BOX_SIZE == 0 && row < SIZE - 1) {
                System.out.println("------+-------+------");
            }
        }
    }
}
