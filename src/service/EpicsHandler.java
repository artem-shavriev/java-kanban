package service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Endpoint;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

import static service.HttpTaskServer.getEndpoint;
import static service.HttpTaskServer.gson;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

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
                    ArrayList<Epic> epics = manager.getEpics();
                    String jsonEpics = gson.toJson(epics);
                    sendText(httpExchange, jsonEpics);
                    break;
                case GET_BY_ID:
                    int id = Integer.parseInt(splitStrings[2]);
                    if (manager.getEpicById(id) == null) {
                        sendNotFound(httpExchange);
                    } else {
                        Epic epic = manager.getEpicById(id);
                        String jsonEpic = gson.toJson(epic);
                        sendText(httpExchange, jsonEpic);
                    }
                    break;
                case POST:
                    String epicForPost = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Epic newEpic = gson.fromJson(epicForPost, Epic.class);

                    manager.addEpic(newEpic);
                    String response = "Эпик добавлен";
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case POST_BY_ID:
                    String epicForPostWithId = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    Epic updateEpicWithId = gson.fromJson(epicForPostWithId, Epic.class);
                    if (manager.getEpics().contains(updateEpicWithId)) {
                        manager.updateEpic(updateEpicWithId);
                        response = "Эпик обновлен";
                        httpExchange.sendResponseHeaders(201, 0);

                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());

                        }
                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case GET_EPIC_SUBTASKS:
                    id = Integer.parseInt(splitStrings[2]);
                    Epic epic = manager.getEpicById(id);
                    if (epic == null) {
                        sendNotFound(httpExchange);
                    } else {
                        List subtasksIds = epic.getSubtasksIds();
                        String jsonSubtasksIds = gson.toJson(subtasksIds);
                        sendText(httpExchange, jsonSubtasksIds);
                    }
                    break;
                case DELETE:
                    int idForDelete = Integer.parseInt(splitStrings[2]);
                    if (manager.getEpicById(idForDelete) == null) {
                        sendNotFound(httpExchange);
                    } else {
                        manager.removeEpicById(idForDelete);
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.close();
                    }
                    break;
                default: {
                    System.out.println("/epic получил: " + httpExchange.getRequestMethod());
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
