package demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Demo {
    static final String NOTE =
            "open-java-format reflows a string literal that runs past the column limit, the way its other integrations"
                    + " do it as well";

    public static Map<String, List<String>> groupByFirstLetter(List<String> words) {
        return words.stream()
                .filter(word -> !word.isBlank())
                .map(String::trim)
                .collect(Collectors.groupingBy(
                        word -> word.substring(0, 1).toUpperCase(),
                        Collectors.mapping(word -> word.toLowerCase(), Collectors.toList())));
    }
}
