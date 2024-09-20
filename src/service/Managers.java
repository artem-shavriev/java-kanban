package service;

import java.io.File;
import java.io.IOException;

public class Managers {
    public static TaskManager getDefault() {
        File backedFile;
        {
            try {
                backedFile = File.createTempFile("backedFile", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        TaskManager manager = new FileBackedTaskManager(backedFile);
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
