package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.List;

import static service.HttpTaskServer.gson;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (!httpExchange.getRequestMethod().equals("GET")) {
                System.out.println("/prioritized получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
                httpExchange.close();
            } else {
                List<Task> prioritizedTaskList = manager.getPrioritizedTask();
                String jsonPrioritized = gson.toJson(prioritizedTaskList);
                sendText(httpExchange, jsonPrioritized);
            }
        } catch (NoSuchObjectException e) {
            sendNotFound(httpExchange);
        } catch (InternalError e) {
            sendInternalError(httpExchange);
        } catch (Exception e) {
            sendException(httpExchange, e);
        }
    }
}
