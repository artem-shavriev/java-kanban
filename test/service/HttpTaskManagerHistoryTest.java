package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Task;
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

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void shouldGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(1,"задача 1", TaskStatus.NEW, "Собрать и вынести мусор",
                LocalDateTime.of(2024, 10,2, 12, 30,00),
                Duration.ofMinutes(45));
        Task task2 = new Task(2,"задача 2",  TaskStatus.NEW, "Приготовить еду",
                LocalDateTime.of(2024, 10,2, 13, 30,00),
                Duration.ofMinutes(45));
        Task task3 = new Task(3,"задача 3", TaskStatus.NEW, "Постирать вещи",
                LocalDateTime.of(2024, 10,2, 14, 30,00),
                Duration.ofMinutes(45));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        class HistoryListTypeToken extends TypeToken<List<Task>> {
        }

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> responseHistory = gson.fromJson(response.body(), new HistoryListTypeToken().getType());

        assertEquals(200, response.statusCode());

       // assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("задача 1", responseHistory.get(0).getNameOfTask(), "Некорректное имя задачи");
        assertEquals("задача 2", responseHistory.get(1).getNameOfTask(), "Некорректное имя задачи");
        assertEquals("задача 3", responseHistory.get(2).getNameOfTask(), "Некорректное имя задачи");
    }
}
