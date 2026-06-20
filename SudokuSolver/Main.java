/* Main:
   Tests SudokuSolver with the example puzzle from the assignment.
*/
public class Main {

    public static void main(String[] args) {

        int[][] sudoku = {
            {0,0,0,0,2,0,0,0,1},
            {0,0,3,8,0,0,0,9,0},
            {7,0,4,0,9,5,8,0,0},
            {2,8,0,0,0,0,0,0,5},
            {0,0,0,0,0,0,0,0,0},
            {6,0,0,0,0,0,0,7,3},
            {0,0,2,7,5,0,6,0,9},
            {0,7,0,0,0,6,4,0,0},
            {5,0,0,0,0,9,0,0,0}
        };

        System.out.println("Sudoku before solving:");
        SudokuSolver.printSudoku(sudoku);

        SudokuSolver solver = new SudokuSolver();
        boolean solved = solver.solveSudoku(sudoku);

        System.out.println("\nSolved: " + solved);
        System.out.println("Sudoku after solving:");
        SudokuSolver.printSudoku(sudoku);
    }
}
