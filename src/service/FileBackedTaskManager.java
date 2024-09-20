package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    static File backedFile;

    public FileBackedTaskManager(File backedFile) {
        this.backedFile = backedFile;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String taskString = bufferedReader.readLine();
                Task task = TaskConvertor.convertTaskFromString(taskString);
                if (task.getTaskType().equals(TaskType.TASK)) {
                    fileBackedTaskManager.addTask(task);
                } else if (task.getTaskType().equals(TaskType.EPIC)) {
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                    fileBackedTaskManager.addSubtask((Subtask) task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileBackedTaskManager;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backedFile))) {
            for (Task task : super.getTasks()) {
                bufferedWriter.write(TaskConvertor.convertTaskToString(task) + "\n");
            }

            for (Epic epic : super.getEpics()) {
                bufferedWriter.write(TaskConvertor.convertTaskToString(epic) + "\n");
            }

            for (Subtask subtask : super.getSubtasks()) {
                bufferedWriter.write(TaskConvertor.convertTaskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }
}
