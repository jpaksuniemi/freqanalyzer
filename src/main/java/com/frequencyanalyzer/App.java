package com.frequencyanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;


public class App  {

    private static final String FILE_PATH = ("./text-to-decrypt.txt");
    private static final Path PATH = Paths.get(FILE_PATH);

    public static void main( String[] args ) {
        Scanner sc = new Scanner(System.in, "Cp850");
        String originalText;
        String decryptableText;
        int[] characterCount = new int[28];
        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'å', 'ä', 'ö',};
        double[] percentages = new double[29];
        int input = 0;

        originalText = getTextFromFile(PATH);
        decryptableText = originalText.toLowerCase();
        characterCount = countCharacters(originalText);
        percentages = calculatePercentages(characterCount);
        sortArrays(characterCount, percentages, alphabet);

        while (input != -1) {
            printUI();
            try {       
                input = sc.nextInt();
                switch (input) {
                    case 1:
                        System.out.println("\n" + decryptableText);
                        break;
                    case 2:
                        System.out.println("\n" + originalText);
                        break;
                    case 3:
                        printStatistics(characterCount, percentages, alphabet);
                        break;
                    case 4:
                        decryptableText = changeCharacter(sc, decryptableText);
                        break;
                    case 5:
                        printInstructions();
                        break;
                    case -1:
                        System.out.println("\nQuitting program...");
                        break;
                    default:
                        System.out.println("\nInvalid choice");
                        break;
                }
            } catch (InputMismatchException e) {
                System.err.println("\n" + e + ": Please enter an Integer!");
                sc.nextLine();
            }
        }
    }

    public static void printUI() {
        System.out.println("\n1. Print decryptable text");
        System.out.println("2. Print original text");
        System.out.println("3. Print statistics");
        System.out.println("4. Change a character in the text");
        System.out.println("5. Print instructions");
        System.out.println("-1. Quit program");
        System.out.print("> ");
    }

    public static void printInstructions() {
        System.out.println("\nTo change a character, choose functionality 4. in the UI.");
        System.out.println("Changed characters are represented as uppercase letters in the decryptable text.");
        System.out.println("Original text is never edited and is used as a comparison to work out the key to the encryption.");
        System.out.println("If you think you made a mistake, you can fix it by inputting an uppercase letter as the character to be changed. (X=y)");
    }

    public static String changeCharacter(Scanner sc, String decryptableText) {
        char[] charsToSwap = new char[2];
        sc.nextLine();

        System.out.print("\nEnter characters in the format \"x=y\" where x is the character to replace and y is the character used to replace. \n > ");

        String input = sc.nextLine();


        if (input.length() == 3 && input.charAt(1) == '=') {
            charsToSwap = parseStringInput(input);
            decryptableText = decryptableText.replace(charsToSwap[0], charsToSwap[1]);
        } else {
            System.out.println("Invalid input. No changes have been made. \n");
        }
        return decryptableText;
    }

    private static char[] parseStringInput(String input) {
        char[] parsedChars = new char[2];
        parsedChars[0] = input.charAt(0);
        input = input.toUpperCase();
        parsedChars[1] = input.charAt(2);
        return parsedChars;
    }

    private static void sortArrays(int[] charCount, double[] percentages, char[] alphabet) {
        boolean isDifferent = false;
        int iBuffer;
        double dBuffer;
        char cBuffer;
        int indexBuffer = 0;

        for (int i = 0; i < charCount.length; i++) {
            isDifferent = false;
            iBuffer = charCount[i];
            dBuffer = percentages[i];
            cBuffer = alphabet[i];
            for (int j = i + 1; j < charCount.length ; j++) {
                if (iBuffer < charCount[j]) {
                    iBuffer = charCount[j];
                    dBuffer = percentages[j];
                    cBuffer = alphabet[j];
                    indexBuffer = j;
                    isDifferent = true;
                }
            }

            if (isDifferent) {
                charCount[indexBuffer] = charCount[i];
                charCount[i] = iBuffer;

                percentages[indexBuffer] = percentages[i];
                percentages[i] = dBuffer;

                alphabet[indexBuffer] = alphabet[i];
                alphabet[i] = cBuffer;
            }

        }
    }

    public static void printStatistics(int[] charCount, double[] percentages, char[] alphabet) {
        System.out.println();
        for (int i = 0; i < charCount.length; i++) {
            if (charCount[i] != 0) {
                System.out.printf("%c = %d (%.2f", alphabet[i], charCount[i], percentages[i]);
                System.out.print("%)\n");
            }
        }
    }

    private static double[] calculatePercentages(int[] charCount) {
        double[] percentages = new double[29];

        double total = 0.0;
        for (int i = 0; i < charCount.length; i++) {
            total += charCount[i];
        }
        for (int i = 0; i < charCount.length; i++) {
            percentages[i] = charCount[i] / total * 100.0;
        }
        return percentages;
    }

    private static String getTextFromFile(Path path) {
        String text = "";
        int ch;
        try (BufferedReader bReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            while ( (ch = bReader.read()) != -1 ) {
                text += (char) ch;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return text;
    }

    private static int[] countCharacters(String text) {
        int ch;
        String textLowerCase = text.toLowerCase();
        int[] characterCount = new int[29];

        for (int i = 0; i < textLowerCase.length(); i++) {
            ch = textLowerCase.charAt(i);
            if (ch == 32 || ch == 46 || ch == 44 || ch == 45) {
                continue;
            }

            ch = ch - 97;

            if (ch == 132) { // å
                characterCount[26] += 1;
                continue;
            }
            if (ch == 149) { // ö
                characterCount[28] += 1;
                continue;
            }
            if (ch == 131) { // ä
                characterCount[27] += 1;
                continue;
            }

            characterCount[ch] += 1;
        }

        return characterCount;
    }

}
