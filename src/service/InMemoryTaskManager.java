package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int generateNewId() {
        taskId++;
        return taskId;
    }

    @Override
    public Task addTask(Task task) {
        int newId = generateNewId();
        tasks.put(newId, task);
        task.setId(newId);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        int newId = generateNewId();
        epics.put(newId, epic);
        epic.setId(newId);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (subtask != null) {
            int newId = generateNewId();
            subtask.setEpicId(epic.getId());
            subtasks.put(newId, subtask);
            subtask.setId(newId);
            epic.setSubtaskId(newId);
            updateEpicStatus(epics.get(subtask.getEpicId()));
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (task != null) {
            int id = task.getId();
            if (tasks.containsKey(id)) {
                tasks.put(id, task);
                return task;
            } else {
                System.out.println("Задачи с данным id не существует");
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic != null) {
            int id = epic.getId();
            if (epics.containsKey(id)) {
                ArrayList<Integer> subtasksIds = epics.get(id).getSubtasksIds();
                epics.put(id, epic);
                epic.setSubtasksIds(subtasksIds);
                updateEpicStatus(epic);
                return epic;
            } else {
                System.out.println("Эпика с данным id не существует");
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = subtask.getId();
            if (subtasks.containsKey(id)) {
                if (subtasks.get(id).getEpicId() == subtask.getEpicId()) {
                    subtasks.put(subtask.getId(), subtask);
                    updateEpicStatus(epics.get(subtask.getEpicId()));
                    return subtask;
                } else {
                    System.out.println("У подзадачи не корректный эпик.");
                    return null;
                }
            } else {
                System.out.println("Подзадачи с данным id не существует");
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Задачи с данным id не существует");
        }
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Эпика с данным id не существует");
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Подзадачи с данным id не существует");
        }
        return null;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            Epic currentEpic = epics.get(epicId);
            ArrayList<Integer> subtasksIds = currentEpic.getSubtasksIds();
            for (int subtaskId : subtasksIds) {
                epicSubtasks.add(subtasks.get(subtaskId));
            }
            return epicSubtasks;
        } else {
            System.out.println("Эпика с данным id не существует");
        }
        return null;
    }

    @Override
    public Task removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            historyManager.remove(task);
            return task;
        } else {
            System.out.println("Задачи с данным id не существует");
            return null;
        }
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epicOfSubtask = epics.get(epicId);

            subtasks.remove(id);
            historyManager.remove(subtask);
            epicOfSubtask.removeSubtaskIdById(id);
            updateEpicStatus(epicOfSubtask);
            return subtask;
        } else {
            System.out.println("Подзадачи с данным id не существует");
            return null;
        }
    }

    @Override
    public Epic removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> idsForRemove = epic.getSubtasksIds();

            for (int i : idsForRemove) {
                subtasks.remove(i);
                historyManager.remove(subtasks.get(i));
            }
            epics.remove(id);
            historyManager.remove(epic);
            return epic;
        } else {
            System.out.println("Эпика с данным id не существует");
            return null;
        }
    }

    @Override
    public void removeTasks() {
        for(Task task : tasks.values()) {
            historyManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setTaskStatus(TaskStatus.NEW);
            epic.removeAllSubtasksIds();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic != null) {
            ArrayList<TaskStatus> taskStatuses = new ArrayList<>();
            ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());

            if (subtasks.isEmpty()) {
                epic.setTaskStatus(TaskStatus.NEW);
            } else {
                for (Subtask subtask : subtasksOfEpic) {
                    taskStatuses.add(subtask.getTaskStatus());
                }
                if (epic.getSubtasksIds().isEmpty()) {
                    epic.setTaskStatus(TaskStatus.NEW);
                } else if (taskStatuses.contains(TaskStatus.DONE)
                        && !taskStatuses.contains(TaskStatus.IN_PROGRESS)
                        && !taskStatuses.contains(TaskStatus.NEW)) {
                    epic.setTaskStatus(TaskStatus.DONE);
                } else if (taskStatuses.contains(TaskStatus.NEW)
                        && !taskStatuses.contains(TaskStatus.IN_PROGRESS)
                        && !taskStatuses.contains(TaskStatus.DONE)) {
                    epic.setTaskStatus(TaskStatus.NEW);
                } else {
                    epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }
}
