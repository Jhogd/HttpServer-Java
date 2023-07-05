package ogden.jake.http;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class GuessingGameTest {

   @Test
    void
    setup() throws IOException {
       OutputStream out = new ByteArrayOutputStream();
       GuessingGame.initGameResponse(out);
       int rand = GuessingGame.randomMap.get(1);
       assertTrue(rand < 101);
       assertTrue(rand > 0);
       assertEquals(GuessingGame.formatGameHtml("Guessing Game", "Guess a number between 1 and 100", 1, 1), out.toString());

    }

    @Test
    void
    formatGameHtml() throws IOException {
        String expectedSub1 = "HTTP/1.1 200 OK\r\n";
        String expectedSub2 = "Content-Type: text/html\r\n\r\n<html><body><h1>Guessing Game</h1>";
        String expectedSub3 = "<p>Guess a number between 1 and 100:</p><form action=\"game\">";
        String expectedSub4 = "<input type=\"hidden\" id=\"numGuesses\" name=\"numGuesses\" value=\"" + 0 + "\">";
        String expectedSub5 = "<input type=\"hidden\" id=\"gameId\" name=\"gameId\" value=\"" + 1 + "\">";
        String expectedSub6 = "<input type=\"number\" id=\"guess\" name=\"guess\">" + "<input type=\"submit\" value=\"Submit\">";
        String expectedSub7 = "</form>" + "</body></html>";
        String response = GuessingGame.formatGameHtml("Guessing Game", "Guess a number between 1 and 100", 0 ,1);
        assertTrue(response.contains(expectedSub1));
        assertTrue(response.contains(expectedSub2));
        assertTrue(response.contains(expectedSub3));
        assertTrue(response.contains(expectedSub4));
        assertTrue(response.contains(expectedSub5));
        assertTrue(response.contains(expectedSub6));
        assertTrue(response.contains(expectedSub7));
    }

    @Test
    void
    processGuessLow() throws IOException {
    OutputStream out = new ByteArrayOutputStream();
    GuessingGame.randomMap.put(2, 87);
    GuessingGame.processGuess("/game?numGuesses=0&gameId=2&guess=39", out);
    assertTrue(out.toString().equals(GuessingGame.formatGameHtml("Wrong Guess!", "Your guess is too low", 1, 2)));

    }

    @Test
    void
    processGuessHigh() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        GuessingGame.randomMap.put(2, 87);
        GuessingGame.processGuess("/game?numGuesses=0&gameId=2&guess=99", out);
        assertTrue(out.toString().equals(GuessingGame.formatGameHtml("Wrong Guess!", "Your guess is too high", 1, 2)));
    }

    @Test
    void
    processGuessMaxGuess() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        GuessingGame.randomMap.put(2, 87);
        GuessingGame.processGuess("/game?numGuesses=7&gameId=2&guess=99", out);
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>Game Over</h1>" +
                "<p>Sorry you have ran out of attempts to guess. </p>" +
                "<p> The number was: " + 87 + "<p>" + "</body></html>";
        assertEquals(out.toString(), response);
    }
    @Test
    void
    processGuessWin() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        GuessingGame.randomMap.put(2, 87);
        GuessingGame.processGuess("/game?numGuesses=4&gameId=2&guess=87", out);
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n" +
                "<html><body><h1>Congratulations!</h1>" +
                "<p>Total guesses: " + 4 + "</p>" +
                "</body></html>";
        assertTrue(out.toString().equals(response));
    }


    @Test
    void
    parseString(){
       HashMap values = GuessingGame.parseInput("/game?numGuesses=0&gameId=1&guess=39");
       assertEquals(39, values.get("guess"));
       assertEquals(0, values.get("numGuesses"));
       assertEquals(1, values.get("gameId"));
    }

}