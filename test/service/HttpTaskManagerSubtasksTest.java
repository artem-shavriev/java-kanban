package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeTasks();
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить шпатель", subtasksFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);
        manager.addSubtask(subtask);

        Subtask subtaskForAddById = new Subtask(1, "Обновленная подзадача", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);

        String subtaskJson = gson.toJson(subtaskForAddById);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Обновленная подзадача", subtasksFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "подзадача 1", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);
        manager.addSubtask(subtask);

        Subtask subtask2 = new Subtask(2, "подзадача 2", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 13, 30,00),
                Duration.ofMinutes(45),5);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        class SubtaskListTypeToken extends TypeToken<ArrayList<Subtask>> {
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ArrayList<Subtask> subtasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals("подзадача 1", subtasks.get(0).getNameOfTask(), "Некорректное имя задачи");
        assertEquals("подзадача 2", subtasks.get(1).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "подзадача 1", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());

        assertEquals(subtask, responseSubtask, "Подзадача не получена от сервера.");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(5,"Сделать ремонт", "Покрасить стены на балконе");
        manager.addEpic(epic);
        Subtask subtask = new Subtask(1, "подзадача 1", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),5);
        manager.addSubtask(subtask);


        assertNotNull(manager.getSubtaskById(1), "Подзадача не добавлена.");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(200, response.statusCode());

        assertNull(manager.getTaskById(1), "Подзадача не удалена.");
    }
}
