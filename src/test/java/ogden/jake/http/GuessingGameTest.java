package ogden.jake.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class GuessingGameTest {
    private GuessingGame game;
    @BeforeEach
    void setup() {
         game = new GuessingGame();
    }

    @Test
    void creation(){
       // System.out.println(game.randomNumber);
        assertTrue(((game.randomNumber < 100) & (game.randomNumber > 0)));
    }
    @Test
    void
    sendInitalResponse() throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        String expectedSub1 = "HTTP/1.1 200 OK\r\n";
        String expectedSub2 = "Content-Type: text/html\r\n\r\n<html><body><h1>Guessing Game</h1>";
        String expectedSub3 = "<p>Guess a number between 1 and 100:</p>" +"<form method=\"POST\">";
        String expectedSub4 = "<input type=\"number\" name=\"guess\">" + "<input type=\"submit\" value=\"Submit\">";
        String expectedSub5 = "</form>" + "</body></html>";
        game.initGameResponse(output);
        assertTrue(output.toString().contains(expectedSub1));
        assertTrue(output.toString().contains(expectedSub2));
        assertTrue(output.toString().contains(expectedSub3));
        assertTrue(output.toString().contains(expectedSub4));
        assertTrue(output.toString().contains(expectedSub5));
    }

    @Test
    void
    processInput(){

    }

}