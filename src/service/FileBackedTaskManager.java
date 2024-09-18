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
import java.io.Writer;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File backedFile;

    public FileBackedTaskManager() {
    }

    public FileBackedTaskManager(File backedFile) {
        this.backedFile = backedFile;
    }

    void loadFromFile(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String taskString = bufferedReader.readLine();
                Task task = taskFromString(taskString);
                if (task.getTaskType().equals(TaskType.TASK)) {
                    super.addTask(task);
                } else if (task.getTaskType().equals(TaskType.EPIC)) {
                    super.addEpic((Epic) task);
                } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                    super.addSubtask((Subtask) task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backedFile))) {
            bufferedWriter.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTasks()) {
                bufferedWriter.write(toString((task)) + "\n");
            }

            for (Epic epic : super.getEpics()) {
                bufferedWriter.write(toString(epic) + "\n");
            }

            for (Subtask subtask : super.getSubtasks()) {
                bufferedWriter.write(toString(subtask) + "\n");
            }
            throw new ManagerSaveException("Проблема с сохранением");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    String toString(Task task) {
        String taskToString;
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskToString = task.getId() + "," + task.getTaskType() + ","
                    + task.getNameOfTask() + "," + task.getTaskStatus() + ","
                    + task.getDescription() + "," + subtask.getEpicId();
        } else {
            taskToString = task.getId() + "," + task.getTaskType() + ","
                    + task.getNameOfTask() + "," + task.getTaskStatus() + ","
                    + task.getDescription();
        }

        return taskToString;
    }

    Task taskFromString(String value) {
        String[] array = value.split(",");
        int id = Integer.valueOf(array[0]);
        TaskType type = TaskType.valueOf(array[1]);
        String name = array[2];
        TaskStatus status = TaskStatus.valueOf(array[3]);
        String description = array[4];

        if (type.equals(TaskType.SUBTASK)) {
            int epicId = Integer.valueOf(array[5]);
            return new Subtask(id, type, name, status, description, epicId);
        } else if (type.equals(TaskType.TASK)) {
            return new Task(id, type, name, status, description);
        } else {
            return new Epic(id, type, name, status, description);
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
    public Task removeTaskById(int id) {
        super.removeTaskById(id);
        save();
        return super.getTaskById(id);
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
        return super.getSubtaskById(id);
    }

    @Override
    public Epic removeEpicById(int id) {
        super.removeEpicById(id);
        save();
        return super.getEpicById(id);
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
