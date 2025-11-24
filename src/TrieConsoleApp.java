import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TrieConsoleApp {

    public static void main(String[] args) {
        Trie trie = new Trie();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            System.out.print("Введите номер команды: ");
            String line = scanner.nextLine();
            int command;

            try {
                command = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: требуется целое число.");
                continue;
            }

            if (command == 0) {
                System.out.println("Программа завершена.");
                break;
            }

            switch (command) {
                case 1:
                    System.out.print("Введите слово для вставки: ");
                    String wordToInsert = scanner.nextLine();
                    try {
                        boolean already = trie.contains(wordToInsert);
                        trie.insert(wordToInsert);
                        if (already) {
                            System.out.println("Слово уже содержится в дереве.");
                        } else {
                            System.out.println("Слово добавлено в дерево.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.print("Введите слово для проверки: ");
                    String wordToCheck = scanner.nextLine();
                    try {
                        boolean contains = trie.contains(wordToCheck);
                        if (contains) {
                            System.out.println("Слово присутствует в дереве.");
                        } else {
                            System.out.println("Слово отсутствует в дереве.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Введите префикс: ");
                    String prefixForStart = scanner.nextLine();
                    try {
                        boolean starts = trie.startsWith(prefixForStart);
                        if (starts) {
                            System.out.println("Слова с таким префиксом существуют.");
                        } else {
                            System.out.println("Слов с таким префиксом не найдено.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Введите префикс для поиска слов: ");
                    String prefixForList = scanner.nextLine();
                    try {
                        String[] words = trie.getByPrefix(prefixForList);
                        if (words.length == 0) {
                            System.out.println("Слова с таким префиксом не найдены.");
                        } else {
                            System.out.println("Слова с таким префиксом найдены:");
                            for (int i = 0; i < words.length; i++) {
                                System.out.println(words[i]);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 5:
                    System.out.print("Введите префикс для подсчёта: ");
                    String prefixForCount = scanner.nextLine();
                    try {
                        int count = trie.countByPrefix(prefixForCount);
                        System.out.println("Количество слов с таким префиксом: " + count);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Общее количество слов в дереве: " + trie.size());
                    break;
                case 7:
                    System.out.print("Введите слово для удаления: ");
                    String wordToRemove = scanner.nextLine();
                    try {
                        boolean removed = trie.remove(wordToRemove);
                        if (removed) {
                            System.out.println("Слово удалено из дерева.");
                        } else {
                            System.out.println("Такого слова в дереве не было.");
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                case 8:
                    if (trie.size() == 0) {
                        System.out.println("Дерево не содержит слов.");
                    } else {
                        System.out.println("Список всех слов в дереве:");
                        System.out.print(trie.toString());
                    }
                    break;
                case 9:
                    System.out.print("Введите имя файла для загрузки слов: ");
                    String loadFile = scanner.nextLine();
                    loadFromFile(trie, loadFile);
                    break;
                case 10:
                    System.out.print("Введите имя файла для сохранения слов: ");
                    String saveFile = scanner.nextLine();
                    saveToFile(trie, saveFile);
                    break;
                case 11:
                    System.out.print("Введите имя файла для записи DOT-представления: ");
                    String dotFile = scanner.nextLine();
                    saveDotToFile(trie, dotFile);
                    break;

                default:
                    System.out.println("Неизвестная команда. Повторите ввод.");
                    break;
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Меню:");
        System.out.println("1  - Добавить слово");
        System.out.println("2  - Проверить, есть ли слово");
        System.out.println("3  - Проверить, есть ли слова с префиксом");
        System.out.println("4  - Вывести слова по префиксу");
        System.out.println("5  - Подсчитать количество слов по префиксу");
        System.out.println("6  - Показать размер (количество слов)");
        System.out.println("7  - Удалить слово");
        System.out.println("8  - Показать все слова в дереве");
        System.out.println("9  - Загрузить слова из файла");
        System.out.println("10 - Сохранить все слова в файл");
        System.out.println("11 - Сохранить DOT-представление в файл");
        System.out.println("0  - Выход");
    }

    private static void loadFromFile(Trie trie, String fileName) {
        int loaded = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    try {
                        trie.insert(word);
                        loaded++;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Строка \"" + word + "\" пропущена: " + e.getMessage());
                    }
                }
            }
            System.out.println("Загружено слов: " + loaded);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private static void saveToFile(Trie trie, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            String allWords = (trie.size() == 0) ? "" : trie.toString();
            writer.write(allWords);
            System.out.println("Слова записаны в файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла: " + e.getMessage());
        }
    }

    private static void saveDotToFile(Trie trie, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            String dot = trie.toDot();
            writer.write(dot);
            System.out.println("DOT-представление записано в файл.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла: " + e.getMessage());
        }
    }
}
