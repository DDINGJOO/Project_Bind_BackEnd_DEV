package util.nicknamefilter;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class BadWordLoader {
    private final Set<String> badWords = new HashSet<>();

    @PostConstruct
    public void load() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/badwords.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            throw new RuntimeException("금지어 로딩 실패", e);
        }
    }

    public Set<String> getBadWords() {
        return badWords;
    }
}
