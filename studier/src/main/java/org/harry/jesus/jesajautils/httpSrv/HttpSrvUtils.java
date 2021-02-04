package org.harry.jesus.jesajautils.httpSrv;

import com.sun.net.httpserver.HttpExchange;
import org.harry.jesus.jesajautils.HTMLRendering;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpSrvUtils {


    /**
     * Be careful with changes here.. This method calculates the length in bytes
     * of a UTF-8 encoded String and sends the String as request answer
     * @param exchange the exchange context
     * @param textToSend the text
     * @throws IOException exception if the data cannot be transmitted
     */
    public static void getLengthAndSendText(HttpExchange exchange, String textToSend) throws IOException {
        int length = textToSend.getBytes(StandardCharsets.UTF_8).length;
        exchange.sendResponseHeaders(200, length);
        OutputStream os = exchange.getResponseBody();
        os.write(textToSend.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        exchange.close();
    }

    /**
     * Be careful with changes here.. This method calculates the length in bytes
     * of the written exception part
     * @param exchange the exchange context
     * @param exception the exception written
     * @throws IOException exception if the data cannot be transmitted
     */
    public static void getLengthAndSendException(HttpExchange exchange, Exception exception)  {
        try {
            String textToSend = HTMLRendering.renderException(exception);
            int length = textToSend.getBytes(StandardCharsets.UTF_8).length;
            exchange.sendResponseHeaders(500, length);
            OutputStream os = exchange.getResponseBody();
            os.write(textToSend.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();
            exchange.close();
        } catch (IOException ex) {
            System.err.println("Internal error" + ex.getMessage());
        }
    }
}
