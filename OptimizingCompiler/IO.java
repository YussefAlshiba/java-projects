import java.util.Scanner;

public class IO {
    private static Scanner scanner = new Scanner(System.in);

    public static void print(String s) {
        System.out.print(s);
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void println() {
        System.out.println();
    }

    public static String readln() {
        return scanner.nextLine();
    }

    public static int readInt() {
        return scanner.nextInt();
    }
}
