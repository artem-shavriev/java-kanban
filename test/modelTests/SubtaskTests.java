package modelTests;

import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTests {
    @Test
    void subtasksShouldBeEqualsIfIdEquals() {
        Subtask subtask1 = new Subtask(1, 5,"Купить шпатель",
                "Выбрать в магазине шпатель и купить", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(1, 5,"Купить краску",
                "Выбрать краску и купить", TaskStatus.DONE);

        assertEquals(subtask1, subtask2, "Экземпляры не равны");
    }
}
