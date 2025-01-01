package searchengine;

import java.io.IOException;
import java.util.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

public class TextProcessor {

    // Метод для обработки текста
    public static Map<String, Integer> processText(String text) {
        // Инициализация морфологического анализатора
        LuceneMorphology luceneMorph = null;
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            System.err.println("Ошибка при инициализации морфологического анализатора: " + e.getMessage());
            return Collections.emptyMap(); // Возвращаем пустую карту в случае ошибки
        }

        // Преобразуем текст в нижний регистр и удаляем все символы, кроме кириллических букв
        text = text.toLowerCase().replaceAll("[^а-яё]", " "); // Оставляем только буквы кириллицы и пробелы

        // Разделение текста на слова
        String[] words = text.split("\\s+");
        Map<String, Integer> lemmaCounts = new HashMap<>();

        // Определяем список исключаемых частей речи (междометия, союзы, предлоги и частицы)
        Set<String> excludedPartsOfSpeech = new HashSet<>(Arrays.asList(
                "PART", "CONJ", "PREP", "INTJ"
        ));

        // Обрабатываем каждое слово
        for (String word : words) {
            if (word.isEmpty()) continue; // Пропускаем пустые строки после замены

            // Получаем список лемм для каждого слова
            List<String> lemmas = luceneMorph.getMorphInfo(word);
            for (String lemma : lemmas) {
                // Получаем первую лемму из результатов анализа
                String lemmaBase = lemma.split("[|]")[0];
                String partOfSpeech = lemma.split("[|]")[1];

                // Если лемма не является исключаемой частью речи
                if (!excludedPartsOfSpeech.contains(partOfSpeech) && !lemmaBase.isEmpty()) {
                    // Увеличиваем счетчик упоминаний этой леммы
                    lemmaCounts.put(lemmaBase, lemmaCounts.getOrDefault(lemmaBase, 0) + 1);
                }
            }
        }

        return lemmaCounts;
    }

    public static void main(String[] args) {
        String text = "Повторное появление леопарда в Осетии позволяет предположить, что леопард постоянно обитает в некоторых районах Северного Кавказа.";

        // Получаем результат
        Map<String, Integer> result = processText(text);

        // Выводим результат
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            System.out.println(entry.getKey() + " — " + entry.getValue());
        }
    }
}
