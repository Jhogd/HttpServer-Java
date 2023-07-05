package ogden.jake.http;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class GuessingGame {
    public static ConcurrentHashMap<Integer, Integer> randomMap = new ConcurrentHashMap<>();


    public static void initGameResponse(OutputStream out) throws IOException {
        int randomNumber  = (int) ((Math.random() * 100) +1);
        int gameId = randomMap.size();
        randomMap.put(gameId, randomNumber);
        String response = formatGameHtml("Guessing Game", "Guess a number between 1 and 100", 1, gameId );
        out.write(response.getBytes());
    }

    public static String formatGameHtml(String header, String body, int numGuesses, int gameId){
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n\r\n<html><body><h1>" + header + "</h1>" +
                "<p>" + body + ":</p>" + "<form action=\"game\">" +
                "<input type=\"hidden\" id=\"numGuesses\" name=\"numGuesses\" value=\"" + numGuesses + "\">" +
                "<input type=\"hidden\" id=\"gameId\" name=\"gameId\" value=\"" + gameId + "\">" +
                "<input type=\"number\" id=\"guess\" name=\"guess\">" + "<input type=\"submit\" value=\"Submit\">" +
                "</form>" + "</body></html>";
    return  response;
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

    public static void processGuess(String resource, OutputStream out) throws IOException {
        HashMap<String, Integer> gameValues = parseInput(resource);
        int maxGuesses = 7;
        String response;
        int numGuesses = gameValues.get("numGuesses");
        int guess = gameValues.get("guess");
        int gameId = gameValues.get("gameId");
        int answer = randomMap.get(gameId);
    //    System.out.println(randomMap);
       if  (numGuesses < maxGuesses) {
           if (guess == randomMap.get(gameId)) {
               response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n" +
                       "<html><body><h1>Congratulations!</h1>" +
                       "<p>Total guesses: " + numGuesses + "</p>" +
                       "</body></html>";
           } else if (guess < answer) {
               numGuesses++;
               response = formatGameHtml("Wrong Guess!", "Your guess is too low", numGuesses, gameId);
           } else {
               numGuesses++;
               response = formatGameHtml("Wrong Guess!", "Your guess is too high", numGuesses, gameId);
           }
       }
       else {
           response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n" +
                   "<html><body><h1>Game Over</h1>" +
                   "<p>Sorry you have ran out of attempts to guess. </p>" +
                   "<p> The number was: " + answer + "<p>" + "</body></html>";
       }
       out.write(response.getBytes());
   }
}
