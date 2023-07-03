package ogden.jake.http;

import java.io.IOException;
import java.io.OutputStream;

public class GuessingGame {

    public static final int maxNumber = 100;
    public final int randomNumber;
    int numGuesses =  0;
    boolean gameOver = false;
    int MAxGuesses = 7;

    public GuessingGame(){
        this.randomNumber = (int) (Math.random() * maxNumber);
    }

    public void initGameResponse(OutputStream out) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
        "Content-Type: text/html\r\n\r\n<html><body><h1>Guessing Game</h1>" +
        "<p>Guess a number between 1 and 100:</p>" +"<form method=\"POST\">" +
        "<input type=\"number\" name=\"guess\">" + "<input type=\"submit\" value=\"Submit\">" +
        "</form>" + "</body></html>";
        out.write(response.getBytes());

    }



}
