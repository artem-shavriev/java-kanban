package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoint;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;

import static service.HttpTaskServer.getEndpoint;
import static service.HttpTaskServer.gson;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
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
                    ArrayList<Subtask> subtasks = manager.getSubtasks();
                    String jsonTasks = gson.toJson(subtasks);
                    sendText(httpExchange, jsonTasks);
                    break;
                case GET_BY_ID:
                    int id = Integer.parseInt(splitStrings[2]);
                    if (manager.getSubtaskById(id) == null) {
                        sendNotFound(httpExchange);
                    } else {
                        Subtask subtask = manager.getSubtaskById(id);
                        String jsonTask = gson.toJson(subtask);
                        sendText(httpExchange, jsonTask);
                        break;
                    }
                case POST:
                    String taskForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask newTask = gson.fromJson(taskForPost, Subtask.class);

                    if (manager.checkIntersectionTasks(newTask)) {
                        sendHasInteractions(httpExchange);
                    } else {
                        manager.addSubtask(newTask);

                        String response = "Подзадача добавлена";
                        httpExchange.sendResponseHeaders(201, 0);

                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;
                case POST_BY_ID:
                    String taskForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask updateTaskWithId = gson.fromJson(taskForPostWithId, Subtask.class);
                    if (manager.getSubtasks().contains(updateTaskWithId)) {
                        if (manager.checkIntersectionTasks(updateTaskWithId)) {
                            sendHasInteractions(httpExchange);
                        } else {
                            manager.updateSubtask(updateTaskWithId);
                            String response = "Подзадача обновлена";
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
                    if (manager.getSubtaskById(idForDelete) == null) {
                        sendNotFound(httpExchange);
                    } else {
                        manager.removeSubtaskById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                    }
                    break;
                default: {
                    System.out.println("/subtask получил: " + httpExchange.getRequestMethod());
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
