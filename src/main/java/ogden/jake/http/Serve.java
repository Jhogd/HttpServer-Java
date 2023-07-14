package ogden.jake.http;

import java.io.IOException;
import java.io.OutputStream;

public interface Serve {
   void sendResponse(OutputStream output, String resource, String data) throws InterruptedException, IOException;

}
