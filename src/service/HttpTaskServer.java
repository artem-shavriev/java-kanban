package service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Task;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static File backedFile = new File("data.csv");
    public static TaskManager taskManager = Managers.getDefault(backedFile);

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/subTasks", new SubtasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
    }

    class TasksArrayTypeToken extends TypeToken<List<Task>> {
    }

    enum Endpoint {GET_TASKS, GET_TASK_BY_ID, POST_TASK, POST_TASK_BY_ID, GET_PRIORITIZED, GET_HISTORY, UNKNOWN}

    public static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] splitStrings = requestPath.split("/");
        if (requestMethod.equals("GET") && splitStrings.length < 3) {
            return Endpoint.GET_TASKS;
        } else if (requestMethod.equals("GET") && splitStrings.length == 3) {
            return Endpoint.GET_TASK_BY_ID;
        } else if (requestMethod.equals("POST") && splitStrings.length < 3) {
            return Endpoint.POST_TASK;
        } else if (requestMethod.equals("POST") && splitStrings.length == 3) {
            return Endpoint.POST_TASK_BY_ID;
        } else if (requestMethod.equals("GET") && splitStrings[1].equals("history")) {
            return Endpoint.GET_HISTORY;
        } else if (requestMethod.equals("GET") && splitStrings[1].equals("prioritized")) {
            return Endpoint.GET_PRIORITIZED;
        } else {
            return Endpoint.UNKNOWN;
        }
    }


    static class TasksHandler extends BaseHttpHandler implements HttpHandler {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                String requestMethod = httpExchange.getRequestMethod();
                String requestPath = httpExchange.getRequestURI().getPath();
                String[] splitStrings = requestPath.split("/");
                Endpoint endpoint = getEndpoint(requestPath, requestMethod);

                switch (endpoint) {
                    case GET_TASKS:
                        ArrayList<Task> tasks = taskManager.getTasks();
                        String jsonTasks = gson.toJson(tasks);
                        sendText(httpExchange, jsonTasks);
                        break;
                    case GET_TASK_BY_ID:
                        int id = Integer.parseInt(splitStrings[3]);
                        Task task = taskManager.getTaskById(id);
                        String jsonTask = gson.toJson(task);
                        sendText(httpExchange, jsonTask);
                    case POST_TASK:
                        String taskForPost = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task newTask = gson.fromJson(taskForPost, Task.class);
                        taskManager.addTask(newTask);
                        httpExchange.sendResponseHeaders(201, 0);
                        httpExchange.close();
                    case POST_TASK_BY_ID:
                        String taskForPostWithId = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task newTaskWithId = gson.fromJson(taskForPostWithId, Task.class);
                        if (taskManager.checkIntersectionTasks(newTaskWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.updateTask(newTaskWithId);
                            httpExchange.sendResponseHeaders(201, 0);
                            httpExchange.close();
                        }
                }
            } catch (NoSuchObjectException e) {
                sendNotFound(httpExchange);
            }
        }
    }

    static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

           /* Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/plain; charset=utf-8");*/
        }
    }

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

           /* Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/plain; charset=utf-8");*/
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

           /* Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/plain; charset=utf-8");*/

        }
    }

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

           /* Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/plain; charset=utf-8");*/
        }
    }
}
