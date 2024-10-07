package org.example;
import java.io.*;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    // Русский алфавит (заглавные и строчные буквы)
    private static final String ALPHABET_UPPER = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String ALPHABET_LOWER = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
    private static final int ALPHABET_LENGTH = 33;

    // Шифрование текста
    public static String encrypt(String text, int key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (ALPHABET_UPPER.indexOf(currentChar) != -1) {
                int originalPos = ALPHABET_UPPER.indexOf(currentChar);
                int newPos = (originalPos + key) % ALPHABET_LENGTH;
                result.append(ALPHABET_UPPER.charAt(newPos));
            } else if (ALPHABET_LOWER.indexOf(currentChar) != -1) {
                int originalPos = ALPHABET_LOWER.indexOf(currentChar);
                int newPos = (originalPos + key) % ALPHABET_LENGTH;
                result.append(ALPHABET_LOWER.charAt(newPos));
            } else {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    // Расшифровка текста с известным ключом
    public static String decrypt(String text, int key) {
        return encrypt(text, ALPHABET_LENGTH - key); // Обратный сдвиг для расшифровки
    }

    // Метод Brute Force
    public static void bruteForceDecrypt(String text) {
        for (int key = 1; key < ALPHABET_LENGTH; key++) {
            System.out.println("Key " + key + ": " + decrypt(text, key));
        }
    }

    // Чтение текста из файла
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // Запись текста в файл
    public static void writeFile(String directory, String fileNamePrefix, String originalFileName, String text) throws IOException {
        // Получаем имя файла без пути
        File originalFile = new File(originalFileName);
        String newFileName = fileNamePrefix + originalFile.getName();

        // Создаем новый путь для файла с префиксом
        File newFile = new File(directory, newFileName);

        // Записываем данные в новый файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(newFile))) {
            bw.write(text);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Программа шифра Цезаря для русского языка");

        int choice = 0;
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("Выберите опцию:");
            System.out.println("1. Зашифровать файл");
            System.out.println("2. Расшифровать файл с известным ключом");
            System.out.println("3. Взломать файл методом brute force");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 3) {
                    validChoice = true;
                } else {
                    System.out.println("Ошибка: выберите опцию от 1 до 3.");
                }
            } else {
                System.out.println("Ошибка: введите число.");
                scanner.next(); // Очищаем некорректный ввод
            }
        }

        scanner.nextLine(); // Считываем конец строки после выбора опции

        System.out.print("Введите путь к файлу: ");
        String filePath = scanner.nextLine();

        try {
            String text = readFile(filePath);
            String directory = Paths.get(filePath).getParent().toString(); // Получаем директорию файла

            switch (choice) {
                case 1: // Шифрование
                    System.out.print("Введите ключ для шифрования: ");
                    int key = getValidKey(scanner);
                    String encryptedText = encrypt(text, key);
                    writeFile(directory, "encrypted_", filePath, encryptedText);
                    System.out.println("Файл успешно зашифрован!");
                    break;

                case 2: // Расшифровка
                    System.out.print("Введите ключ для расшифровки: ");
                    int decryptKey = getValidKey(scanner);
                    String decryptedText = decrypt(text, decryptKey);
                    writeFile(directory, "decrypted_", filePath, decryptedText);
                    System.out.println("Файл успешно расшифрован!");
                    break;

                case 3: // Brute force
                    System.out.println("Попытка взлома методом brute force...");
                    bruteForceDecrypt(text);
                    break;

                default:
                    System.out.println("Неверный выбор!");
            }

        } catch (IOException e) {
            System.out.println("Ошибка обработки файла: " + e.getMessage());
        }

        scanner.close();
    }

    // Метод для валидации ввода ключа (целое число)
    private static int getValidKey(Scanner scanner) {
        int key = 0;
        boolean validKey = false;
        while (!validKey) {
            if (scanner.hasNextInt()) {
                key = scanner.nextInt();
                validKey = true;
            } else {
                System.out.println("Ошибка: введите корректное целое число для ключа.");
                scanner.next(); // Очищаем некорректный ввод
            }
        }
        return key % ALPHABET_LENGTH; // Возвращаем с учётом длины алфавита
    }
}
