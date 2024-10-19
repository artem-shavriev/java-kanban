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

public class HttpTaskManagerEpicTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(1,"Эпик 1", "Организовать путешествие");
        manager.addEpic(epic);

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Эпик 1", epicsFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(1,"Эпик 1", "Организовать путешествие");
        manager.addEpic(epic);
        Epic epicForUpdate = new Epic(1,"Эпик обновленный", "Организовать путешествие");
        epicForUpdate.setEndTime(LocalDateTime.now());
        epicForUpdate.setDuration(Duration.ofMinutes(0));
        epicForUpdate.setStartTime(LocalDateTime.now());

        String taskJson = gson.toJson(epicForUpdate);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals("Эпик обновленный", epicsFromManager.get(0).getNameOfTask(),
                "Некорректное имя задачи");
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(1,"Эпик 1", "Организовать путешествие");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        class EpicListTypeToken extends TypeToken<ArrayList<Epic>> {
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ArrayList<Epic> epics = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertEquals(200, response.statusCode());

        assertEquals("Эпик 1", epics.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(1,"Эпик 1", "Организовать путешествие");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());

        assertEquals(epic, responseEpic, "Эпик не получен от сервера.");
    }

    @Test
    public void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic(1,"Эпик 1", "Организовать путешествие");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(200, response.statusCode());

        assertNull(manager.getEpicById(1), "Задача не не удалена от сервера.");
    }

    @Test
    public  void shouldGetSubtasksIds() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1,"Поехать в отпуск", "Организовать путешествие");
        Subtask subtask1 = new Subtask(6, "Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить",
                LocalDateTime.of(2024, 10,1, 12, 30,00),
                Duration.ofMinutes(45),1);
        Subtask subtask2 = new Subtask(7, "Купить краску", TaskStatus.DONE,
                "Выбрать краску и купить",
                LocalDateTime.of(2024, 10,1, 13, 30,00),
                Duration.ofMinutes(45),1);

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        class IdsListTypeToken extends TypeToken<ArrayList<Integer>> {
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        ArrayList<Integer> responseSubtasksIds = gson.fromJson(response.body(), new IdsListTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertNotNull(responseSubtasksIds, "Id не получены от сервера.");
        assertEquals(7, responseSubtasksIds.get(1), "Id не совпадает с ожидаемым.");
    }
}
