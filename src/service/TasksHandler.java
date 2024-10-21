package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoint;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;

import static service.HttpTaskServer.getEndpoint;
import static service.HttpTaskServer.gson;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager manager = HttpTaskServer.getTaskManager();

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
                    ArrayList<Task> tasks = manager.getTasks();
                    String jsonTasks = gson.toJson(tasks);
                    sendText(httpExchange, jsonTasks);
                    break;
                case GET_BY_ID:
                    int id = Integer.parseInt(splitStrings[2]);
                    Task task = manager.getTaskById(id);
                    if (task == null) {
                        sendNotFound(httpExchange);
                    } else {
                        String jsonTask = gson.toJson(task);
                        sendText(httpExchange, jsonTask);
                    }
                    break;
                case POST:
                    String taskForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Task newTask = gson.fromJson(taskForPost, Task.class);

                    if (manager.checkIntersectionTasks(newTask)) {
                        sendHasInteractions(httpExchange);
                    } else {
                        manager.addTask(newTask);

                        String response = "Задача добавлена";
                        httpExchange.sendResponseHeaders(201, 0);

                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;
                case POST_BY_ID:
                    String taskForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Task taskForUpdateWithId = gson.fromJson(taskForPostWithId, Task.class);
                    if (manager.getTasks().contains(taskForUpdateWithId)) {
                        if (manager.checkIntersectionTasks(taskForUpdateWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            manager.updateTask(taskForUpdateWithId);
                            String response = "Задача добавлена";
                            httpExchange.sendResponseHeaders(201, 0);

                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        }
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case DELETE:
                    int idForDelete = Integer.parseInt(splitStrings[2]);
                    if (manager.getTaskById(idForDelete) == null) {
                        sendNotFound(httpExchange);
                    } else {
                        manager.removeTaskById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                    }
                    break;
                default: {
                    System.out.println("/task получил: " + httpExchange.getRequestMethod());
                    httpExchange.sendResponseHeaders(405, 0);
                    httpExchange.close();
                    break;
                }
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