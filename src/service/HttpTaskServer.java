package service;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Endpoint;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager taskManager;
    //Не понимаю как сделать taskManager не статическим. Когда он не статический
    //не получается получить именно его в классах хендлерах.
    private static HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public static Gson getGson() {
        return gson;
    }

    public static void stop() {
        httpServer.stop(0);
    }

    public static void start() throws IOException {
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/epics",  new EpicsHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/history",new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
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
        } else if (requestMethod.equals("GET") && splitStrings[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}

