package com.textAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Text Analysis Program
 *
 * Reads a text file and generates:
 *  - Total word count (excluding stop words)
 *  - Top 5 most frequent words
 *  - First 50 unique words in alphabetical order
 */
public class TextAnalysisProgram {
    // words to exclude from analysis
    private static final Set<String> FILTER_WORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "in", "on", "at", "by", "for", "with", "about", "against", "between", "into",
            "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "out",
            "over", "under", "again", "further", "then", "once",
            "and", "but", "or", "because", "as", "until", "while",
            "of", "off", "so", "if",
            "he", "she", "it", "we", "they", "you", "i", "me", "him", "her", "us", "them",
            "is", "am", "are", "was", "were", "be", "been", "being",
            "do", "does", "did", "doing", "would", "should", "could",
            "has", "have", "had", "that", "this", "all", "his"
    ));

    // Regex patterns for cleanup
    private static final Pattern SINGULAR_POSSESSIVE  = Pattern.compile("\\b([A-Za-z]+)'s\\b");   // e.g., whale's -> whale
    private static final Pattern PLURAL_POSSESSIVE    = Pattern.compile("\\b([A-Za-z]+)s'\\b"); // e.g., kings' -> kings
    private static final Pattern NON_LETTERS = Pattern.compile("[^A-Za-z]+"); // keep only sequences of letters

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\lmailare\\Downloads\\Assignment\\TextAnalysis_Lax\\moby.txt";
        long start = System.nanoTime();
        Map<String, Integer> freqMap = analyzeFile(filePath);

        // Print results
        int totalWords = freqMap.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Total words (after filtering): " + totalWords);

        printTopWords(freqMap, 5);
        printUniqueWords(freqMap, 50);
        long end = System.nanoTime();

        double seconds = (end - start) / 1_000_000_000.0;
        System.out.printf("%nProcessing time: %.3fs%n", seconds);

    }
    /**
     * reads the file and builds a frequency map of words.
     */
    private static Map<String, Integer> analyzeFile(String filePath) throws IOException {
        Map<String, Integer> freqMap = new HashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines
                    // remove possessive endings first
                    .map(line -> SINGULAR_POSSESSIVE.matcher(line).replaceAll("$1"))
                    .map(line -> PLURAL_POSSESSIVE.matcher(line).replaceAll("$1s"))
                    // split into words
                    .flatMap(line -> Arrays.stream(NON_LETTERS.split(line)))
                    .map(String::toLowerCase) // normalize case
                    // filter unwanted words
                    .filter(w -> !w.isEmpty() && !w.equals("s") && !FILTER_WORDS.contains(w))
                    .forEach(word -> freqMap.put(word, freqMap.getOrDefault(word, 0) + 1));
        }
        return freqMap;
    }

    /**
     * Print the top N most frequent words.
     */
    private static void printTopWords(Map<String, Integer> freqMap, int limit) {
        System.out.println("\nTop " + limit + " most frequent words:");
        freqMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }

    /**
     * Print the first N unique words in alphabetical order.
     */
    private static void printUniqueWords(Map<String, Integer> freqMap, int limit) {
        System.out.println("\nTop " + limit + " unique words (alphabetical):");
        freqMap.keySet().stream()
                .sorted()
                .limit(limit)
                .forEach(System.out::println);
    }
}
