package service;

public class Managers {
    public static TaskManager getDefault() {
        TaskManager manager = new InMemoryTaskManager();
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
