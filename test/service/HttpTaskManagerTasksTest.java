package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.lang.reflect.Type;
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

public class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(1,"Тест1", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тест1", tasksFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    @Test
    public void shouldAddTaskById() throws IOException, InterruptedException {
        Task task = new Task(1,"Тест1", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task);

        Task taskForAddById = new Task(1,"Тесть 1 обновленная", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));

        String taskJson = gson.toJson(taskForAddById);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тесть 1 обновленная", tasksFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }

    /*@Test
    public void shouldGetTask() throws IOException, InterruptedException {
        Task task = new Task(1,"Тест1", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        class TaskListTypeToken extends TypeToken<ArrayList<Task>> {
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        //JsonElement jsonElement = JsonParser.parseString(response.body());
        //System.out.println("json element:" + jsonElement);
        ArrayList<Task> tasks = gson.fromJson(response.body(), (Type) new TaskListTypeToken().getClass());

        System.out.println("tasks:" + tasks);
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        System.out.println("tasksFromManager" + tasksFromManager);

       // assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Тест 1", tasksFromManager.get(0).getNameOfTask(), "Некорректное имя задачи");
    }*/

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(1,"Тест1", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());

        assertEquals(task, responseTask, "Задача не получена от сервера.");
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task(1,"Тест1", TaskStatus.NEW, "Тест1",
                LocalDateTime.of(2024, 10,3, 12, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task);

        assertNotNull(manager.getTaskById(1), "Задача не добавлена.");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());//нет заголовков ответа

        assertEquals(200, response.statusCode());

        assertNull(manager.getTaskById(1), "Задача не не удалена от сервера.");
    }
}
