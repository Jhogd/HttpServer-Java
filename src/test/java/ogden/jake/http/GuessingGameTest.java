package ogden.jake.http;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuessingGameTest {

    @Test
    void
    randomNumber() {
    int gameIdTest = MockRandom.initGameResponse();
    int randomTest = MockRandom.randomMap.get(0);
    assertEquals(22, randomTest);
    assertEquals(0, gameIdTest);
    }

    @Test void
    processingGuessLow() throws IOException {
        GuessingGame.randomMap.put(0, 46);
        List<Object> result = new ArrayList<>();
        result.add(1);
        result.add("low");
        System.out.println(GuessingGame.processGuess(0, 1, 0));
        assertEquals(result, GuessingGame.processGuess(0, 1, 0));
    }

    @Test void
    processingGuessHigh() throws IOException {
        GuessingGame.randomMap.put(0, 46);
        List<Object> result = new ArrayList<>();
        result.add(1);
        result.add("high");
        System.out.println(GuessingGame.processGuess(0, 80, 0));
        assertEquals(result, GuessingGame.processGuess(0, 80, 0));
    }


    @Test void
    processingGuessWin() throws IOException {
        GuessingGame.randomMap.put(0, 46);
        List<Object> result = new ArrayList<>();
        result.add(0);
        result.add("correct");
        System.out.println(GuessingGame.processGuess(0, 46, 0));
        assertEquals(result, GuessingGame.processGuess(0, 46, 0));
    }

    @Test void
    processingGuessMax() throws IOException {
        GuessingGame.randomMap.put(0, 46);
        List<Object> result = new ArrayList<>();
        result.add(7);
        result.add("maxAttempts");
        System.out.println(GuessingGame.processGuess(7, 46, 0));
        assertEquals(result, GuessingGame.processGuess(7, 46, 0));
    }

}