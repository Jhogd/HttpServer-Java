package ogden.jake.http;

import java.net.Socket;

public class MockHandler implements Handler {
    public Socket socket;
    public boolean wasCalled;


  //  @Override
    public void handle(Socket socket) {
        this.socket = socket;
        this.wasCalled = true;

    }
}
