package ogden.jake.http;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GuessingGame implements randomMap {
    public static ConcurrentHashMap<Integer, Integer> randomMap = new ConcurrentHashMap<>();


    public static int initGameResponse() throws IOException {
        int randomNumber  = (int) ((Math.random() * 100) +1);
        int gameId = randomMap.size();
        randomMap.put(gameId, randomNumber);
        return gameId;
    }


    public static List<Object> processGuess(int numGuesses, int guess, int gameId) throws IOException {
        int maxGuesses = 7;
        List<Object> guessResponse = new ArrayList<>();
        int answer = randomMap.get(gameId);
       if  (numGuesses < maxGuesses) {
           if (guess == randomMap.get(gameId)) {
               guessResponse.add(numGuesses);
               guessResponse.add("correct");
           } else if (guess < answer) {
               guessResponse.add(numGuesses + 1);
               guessResponse.add("low");

           } else {
               guessResponse.add(numGuesses + 1);
               guessResponse.add("high");
           }
       } else {
           if (guess == randomMap.get(gameId)) {
               guessResponse.add(numGuesses);
               guessResponse.add("correct");
           }
           guessResponse.add(numGuesses);
           guessResponse.add("maxAttempts");
       }
       return guessResponse;
   }
}
