package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.List;

import static service.HttpTaskServer.gson;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager = HttpTaskServer.getTaskManager();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (!httpExchange.getRequestMethod().equals("GET")) {
                System.out.println("/history получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
                httpExchange.close();
            } else {
                List<Task> historyList = manager.getHistory();
                String jsonHistory = gson.toJson(historyList);
                sendText(httpExchange, jsonHistory);
            }
        } catch (NoSuchObjectException e) {
            sendNotFound(httpExchange);
        } catch (InternalError e) {
            sendInternalError(httpExchange);
        }
    }
}
