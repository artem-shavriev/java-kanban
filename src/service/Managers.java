package service;

import java.io.File;

public class Managers {

    public static TaskManager getDefault(File backedFile) {
        TaskManager manager = new FileBackedTaskManager(backedFile);
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
