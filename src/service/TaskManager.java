package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generateNewId();

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    List<Task> getHistory();

    void setGenerateId(int id);
}
