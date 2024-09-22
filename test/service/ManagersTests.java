package service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTests {
    Managers managers = new Managers();

    @Test
    void shouldGetDefaultInMemoryTaskManager() {
        File backedFile;
        {
            try {
                backedFile = File.createTempFile("backedFile", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        TaskManager taskManager = Managers.getDefault(backedFile);

        assertNotNull(taskManager, "Некорректный экземпляр менеджера TaskManager");
    }

    @Test
    void shouldGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Некорректный эклемпляр HistoryManager");
    }
}
