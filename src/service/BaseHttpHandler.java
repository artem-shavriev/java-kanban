package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String text = "Объект не был найден.";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String text = "При создании или обновлении задача пересекается с уже существующими.";
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
