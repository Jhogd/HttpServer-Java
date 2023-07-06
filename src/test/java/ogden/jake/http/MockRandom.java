package ogden.jake.http;

import java.util.concurrent.ConcurrentHashMap;

public class MockRandom implements randomMap
{
    public static ConcurrentHashMap<Integer, Integer> randomMap = new ConcurrentHashMap<>();
    public static int randomNumber = 22;


    public static int initGameResponse(){
        int gameId = randomMap.size();
        randomMap.put(gameId, randomNumber);
        return gameId;
    }
}
