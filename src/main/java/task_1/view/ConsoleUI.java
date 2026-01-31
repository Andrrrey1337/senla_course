package task_1.view;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    public void print(String message) {
        System.out.println(message);
    }

    public void printError(String message) {
        System.err.println("ОШИБКА: " + message);
    }

    public String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                printError("Введите корректное целое число");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                printError("Введите корректное число (например, 1500.0)");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                printError("Неверный формат даты. Используйте формат ГГГГ-ММ-ДД");
            }
        }
    }
}