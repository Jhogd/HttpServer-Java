package ogden.jake.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class GuessingGameHtml {

    public static void initGameHtml(OutputStream out) throws IOException {
        int gameId = GuessingGame.initGameResponse();
        String response = formatGameHtml("Guessing Game", "Guess a number between 1 and 100", 1, gameId);
        out.write(response.getBytes());
    }

    public static HashMap<String, Integer> parseInput(String resource){
        HashMap<String, Integer> valueMap = new HashMap<>();
        String parameters = resource.substring(resource.indexOf("?") + 1);
        String[] parameterList = parameters.split("&");
        for (String strings: parameterList){
            String[] values = strings.split("=");
            valueMap.put(values[0], Integer.valueOf(values[1]));
        }
        return valueMap;
    }

    public static String formatGameHtml(String header, String body, int numGuesses, int gameId){
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n\r\n<html><body><h1>" + header + "</h1>" +
                "<p>" + body + ":</p>" + "<form action=\"game\">" +
                "<input type=\"hidden\" id=\"numGuesses\" name=\"numGuesses\" value=\"" + numGuesses + "\">" +
                "<input type=\"hidden\" id=\"gameId\" name=\"gameId\" value=\"" + gameId + "\">" +
                "<input type=\"number\" id=\"guess\" name=\"guess\">" + "<input type=\"submit\" value=\"Submit\">" +
                "</form>" + "</body></html>";
    }

   public static void processGameHtml(String resource, OutputStream out) throws IOException {
       HashMap<String, Integer> gameValues = GuessingGameHtml.parseInput(resource);
       String response = null;
       int numGuesses = gameValues.get("numGuesses");
       int guess = gameValues.get("guess");
       int gameId = gameValues.get("gameId");
       List<Object> gameResult = GuessingGame.processGuess(numGuesses, guess, gameId);
       String guessResult = (String) gameResult.get(1);
       numGuesses = (int) gameResult.get(0);
       if (guessResult.equals("correct")) {
           response = HttpHandler.htmlHeader("text/html") + "\r\n" +
                   "<html><body><h1>Congratulations!</h1>" +
                   "<p>Total guesses: " + numGuesses + "</p>" +
                   "</body></html>";
       }
       if (guessResult.equals("low")){
           response = formatGameHtml("Wrong Guess!", "Your guess is too low", numGuesses, gameId);
       }
       if (guessResult.equals("high")){
           response = formatGameHtml("Wrong Guess!", "Your guess is too high", numGuesses, gameId);
       }

       if (guessResult.equals("maxAttempts")){
           response = HttpHandler.htmlHeader("text/html") + "\r\n" +
                   "<html><body><h1>Game Over</h1>" +
                   "<p>Sorry you have ran out of attempts to guess. </p>" +
                   "<p> The number was: " + GuessingGame.randomMap.get(gameId) + "<p>" + "</body></html>";
       }
    out.write(response.getBytes());
   }
}
