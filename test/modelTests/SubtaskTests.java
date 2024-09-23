package modelTests;

import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTests {
    @Test
    void subtasksShouldBeEqualsIfIdEquals() {
        Subtask subtask1 = new Subtask(1,"Купить шпатель", TaskStatus.NEW,
                "Выбрать в магазине шпатель и купить", 5);
        Subtask subtask2 = new Subtask(1, "Купить краску",  TaskStatus.DONE,
                "Выбрать краску и купить", 5);

        assertEquals(subtask1, subtask2, "Экземпляры не равны");
    }
}
