package service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTests {
    Managers managers = new Managers();

    @Test
    void shouldGetDefaultInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Некорректный экземпляр менеджера TaskManager");
    }

    @Test
    void shouldGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Некорректный эклемпляр HistoryManager");
    }
}
