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
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static TaskManager taskManager = new InMemoryTaskManager();

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static Gson getGson() {
        return gson;
    }

    public static HttpServer httpServer;

    public void stop() {
        httpServer.stop(0);
    }

    public static void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        //httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/history", new HistoryHandler());
        // httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        }
    }

    enum Endpoint {GET, GET_BY_ID, POST, POST_BY_ID, GET_PRIORITIZED, GET_HISTORY, DELETE, UNKNOWN}

    public static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] splitStrings = requestPath.split("/");
        if (requestMethod.equals("GET") && splitStrings.length < 3) {
            return Endpoint.GET;
        } else if (requestMethod.equals("GET") && splitStrings.length == 3) {
            return Endpoint.GET_BY_ID;
        } else if (requestMethod.equals("POST") && splitStrings.length < 3) {
            return Endpoint.POST;
        } else if (requestMethod.equals("POST") && splitStrings.length == 3) {
            return Endpoint.POST_BY_ID;
        } else if (requestMethod.equals("GET") && splitStrings[1].equals("history")) {
            return Endpoint.GET_HISTORY;
        } else if (requestMethod.equals("GET") && splitStrings[1].equals("prioritized")) {
            return Endpoint.GET_PRIORITIZED;
        } else if (requestMethod.equals("DELETE") && splitStrings.length == 3) {
            return Endpoint.DELETE;
        } else {
            return Endpoint.UNKNOWN;
        }
    }

    static class TasksHandler extends BaseHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                InputStream requestBody = httpExchange.getRequestBody();
                String requestMethod = httpExchange.getRequestMethod();
                String requestPath = httpExchange.getRequestURI().getPath();
                String[] splitStrings = requestPath.split("/");
                Endpoint endpoint = getEndpoint(requestPath, requestMethod);

                switch (endpoint) {
                    case GET:
                        ArrayList<Task> tasks = taskManager.getTasks();
                        String jsonTasks = gson.toJson(tasks);
                        sendText(httpExchange, jsonTasks);
                        break;
                    case GET_BY_ID:
                        int id = Integer.parseInt(splitStrings[2]);
                        Task task = taskManager.getTaskById(id);
                        String jsonTask = gson.toJson(task);
                        sendText(httpExchange, jsonTask);
                        break;
                    case POST:
                        String taskForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Task newTask = gson.fromJson(taskForPost, Task.class);

                        if (taskManager.checkIntersectionTasks(newTask)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.addTask(newTask);

                            String response = "Задача добавлена";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case POST_BY_ID:
                        String taskForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Task newTaskWithId = gson.fromJson(taskForPostWithId, Task.class);
                        if (taskManager.checkIntersectionTasks(newTaskWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.updateTask(newTaskWithId);
                            String response = "Задача добавлена";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case DELETE:
                        int idForDelete = Integer.parseInt(splitStrings[2]);
                        taskManager.removeTaskById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                }
            } catch (NoSuchObjectException e) {
                sendNotFound(httpExchange);
            } catch (InternalError e) {
                sendInternalError(httpExchange);
            }
        }
    }

    /*static class EpicsHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                InputStream requestBody = httpExchange.getRequestBody();
                String requestMethod = httpExchange.getRequestMethod();
                String requestPath = httpExchange.getRequestURI().getPath();
                String[] splitStrings = requestPath.split("/");
                Endpoint endpoint = getEndpoint(requestPath, requestMethod);
                Epic epic1 = new Epic(1,"Эпик 1", "Организовать путешествие");
                taskManager.addEpic(epic1);
                switch (endpoint) {
                    case GET:
                        ArrayList<Epic> epics = taskManager.getEpics();
                        String jsonEpics = gson.toJson(epics);
                        sendText(httpExchange, jsonEpics);
                        break;
                    case GET_BY_ID:
                        int id = Integer.parseInt(splitStrings[2]);
                        Epic epic = taskManager.getEpicById(id);
                        String jsonEpic = gson.toJson(epic);
                        sendText(httpExchange, jsonEpic);
                        break;
                    case POST:
                        String epicForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Epic newEpic = gson.fromJson(epicForPost, Epic.class);

                        if (taskManager.checkIntersectionTasks(newEpic)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.addEpic(newEpic);
                            String response = "Эпик добавлен";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case POST_BY_ID:
                        String epicForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Epic newEpicWithId = gson.fromJson(epicForPostWithId, Epic.class);
                        if (taskManager.checkIntersectionTasks(newEpicWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.updateTask(newEpicWithId);
                            String response = "Эпик добавлен";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case DELETE:
                        int idForDelete = Integer.parseInt(splitStrings[2]);
                        taskManager.removeEpicById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                }
            } catch (NoSuchObjectException e) {
                sendNotFound(httpExchange);
            } catch (InternalError e) {
                sendInternalError(httpExchange);
            }
        }
    }*/

    static class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                InputStream requestBody = httpExchange.getRequestBody();
                String requestMethod = httpExchange.getRequestMethod();
                String requestPath = httpExchange.getRequestURI().getPath();
                String[] splitStrings = requestPath.split("/");
                Endpoint endpoint = getEndpoint(requestPath, requestMethod);

                switch (endpoint) {
                    case GET:
                        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
                        String jsonTasks = gson.toJson(subtasks);
                        sendText(httpExchange, jsonTasks);
                        break;
                    case GET_BY_ID:
                        int id = Integer.parseInt(splitStrings[2]);
                        Subtask subtask = taskManager.getSubtaskById(id);
                        String jsonTask = gson.toJson(subtask);
                        sendText(httpExchange, jsonTask);
                        break;
                    case POST:
                        String taskForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask newTask = gson.fromJson(taskForPost, Subtask.class);

                        if (taskManager.checkIntersectionTasks(newTask)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.addSubtask(newTask);

                            String response = "Подзадача добавлена";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case POST_BY_ID:
                        String taskForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                        Subtask newTaskWithId = gson.fromJson(taskForPostWithId, Subtask.class);
                        if (taskManager.checkIntersectionTasks(newTaskWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            taskManager.updateSubtask(newTaskWithId);
                            String response = "Подзадача обновлена";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                        break;
                    case DELETE:
                        int idForDelete = Integer.parseInt(splitStrings[2]);
                        taskManager.removeSubtaskById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                        break;
                }
            } catch (NoSuchObjectException e) {
                sendNotFound(httpExchange);
            } catch (InternalError e) {
                sendInternalError(httpExchange);
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String requestPath = httpExchange.getRequestURI().getPath();
                List<Task> historyList = taskManager.getHistory();
                String jsonHistory = gson.toJson(historyList);
                sendText(httpExchange, jsonHistory);

            } catch (NoSuchObjectException e) {
                sendNotFound(httpExchange);
            } catch (InternalError e) {
                sendInternalError(httpExchange);
            }
        }
    }
}

