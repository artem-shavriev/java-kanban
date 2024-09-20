package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.List;
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
        int  id;
        if (task.getId() == null) {
            id = generateNewId();
        } else {
            id = task.getId();
        }
        tasks.put(id, task);
        task.setId(id);
        return task;
    }

    public Epic addEpic(Epic epic) {
        int id;
        if (epic.getId() == null) {
            id = generateNewId();
        } else {
            id = epic.getId();
        }
        epics.put(id, epic);
        epic.setId(id);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        int id;
        Epic epic = epics.get(subtask.getEpicId());

        if (subtask != null && subtask.getId() != epic.getId()) {
            if (subtask.getId() == null) {
                id = generateNewId();
            } else {
                id = subtask.getId();
            }
            subtask.setEpicId(epic.getId());
            subtasks.put(id, subtask);
            subtask.setId(id);
            epic.setSubtaskId(id);
            updateEpicStatus(epics.get(subtask.getEpicId()));
            return subtask;
        } else {
            System.out.println("id подзадачи не может совпадать с id ее эпика, подзадача не добавлена.");
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
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задачи с данным id не существует");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            int epicId = subtask.getEpicId();
            Epic epicOfSubtask = epics.get(epicId);

            subtasks.remove(id);
            historyManager.remove(id);
            epicOfSubtask.removeSubtaskIdById(id);
            updateEpicStatus(epicOfSubtask);
        } else {
            System.out.println("Подзадачи с данным id не существует");
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> idsForRemove = epic.getSubtasksIds();

            for (int i : idsForRemove) {
                subtasks.remove(i);
                historyManager.remove(i);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Эпика с данным id не существует");
        }
    }

    @Override
    public void removeTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setTaskStatus(TaskStatus.NEW);
            epic.removeAllSubtasksIds();
        }
    }

    @Override
    public  List<Task> getHistory() {
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
