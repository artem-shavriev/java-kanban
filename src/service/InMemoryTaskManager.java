package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private int generateId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private TreeSet<Task> sortedByTimeTasks = new TreeSet<>((Task task1, Task task2) -> {
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return -1;
        } else {
            return 1;
        }
    });

    public int generateNewId() {
        generateId++;
        return generateId;
    }

    public void setGenerateId(int generateId) {
        this.generateId = generateId;
    }

    @Override
    public Task addTask(Task task) {
        if (!checkIntersectionTasks(task)) {
            int id;
            if (task.getId() == null) {
                id = generateNewId();
                task.setId(id);
            } else {
                id = task.getId();
            }
            tasks.put(id, task);
            if (task.getStartTime() != null) {
                sortedByTimeTasks.add(task);
            }
            return task;
        } else {
            System.out.println("Задача не добавлена. "
                    + "Есть пересечение по времени выполнения с другими задачами.");
            return null;
        }
    }

    public Epic addEpic(Epic epic) {
        int id;
        if (epic.getId() == null) {
            id = generateNewId();
            epic.setId(id);
        } else {
            id = epic.getId();
        }
        epics.put(id, epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        int id;
        Epic epic = epics.get(subtask.getEpicId());

        if (subtask != null && subtask.getId() != epic.getId()) {
            if (!checkIntersectionTasks(subtask)) {
                if (subtask.getId() == null) {
                    id = generateNewId();
                    subtask.setId(id);
                } else {
                    id = subtask.getId();
                }
                subtask.setEpicId(epic.getId());
                subtasks.put(id, subtask);
                epic.setSubtaskId(id);
                updateEpicStatus(epics.get(subtask.getEpicId()));
                updateEpicStartTime(epic);
                updateEpicDuration(epic);
                updateEpicEndTime(epic);
                if (subtask.getStartTime() != null) {
                    sortedByTimeTasks.add(subtask);
                }
                return subtask;
            } else {
                System.out.println("Подзадача не добавлена. "
                        + "Есть пересечение по времени выполнения с другими задачами.");
                return null;
            }
        } else {
            System.out.println("id подзадачи не может совпадать с id ее эпика, подзадача не добавлена.");
            return null;
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (task != null) {
            if (!checkIntersectionTasks(task)) {
                int id = task.getId();
                if (tasks.containsKey(id)) {
                    tasks.put(id, task);
                    if (task.getStartTime() != null) {
                        sortedByTimeTasks.add(task);
                    }
                    return task;
                } else {
                    System.out.println("Задачи с данным id не существует");
                    return null;
                }
            } else {
                System.out.println("Задача не обновлена. "
                        + "Есть пересечение по времени выполнения с другими задачами.");
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
                updateEpicStartTime(epic);
                updateEpicDuration(epic);
                updateEpicEndTime(epic);
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
            if (!checkIntersectionTasks(subtask)) {
                int id = subtask.getId();
                if (subtasks.containsKey(id)) {
                    if (subtasks.get(id).getEpicId() == subtask.getEpicId()) {
                        subtasks.put(subtask.getId(), subtask);
                        if (subtask.getStartTime() != null) {
                            sortedByTimeTasks.add(subtask);
                        }
                        updateEpicStatus(epics.get(subtask.getEpicId()));
                        updateEpicStartTime(epics.get(subtask.getEpicId()));
                        updateEpicDuration(epics.get(subtask.getEpicId()));
                        updateEpicEndTime(epics.get(subtask.getEpicId()));
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
                System.out.println("Подзадача не обновлена. "
                        + "Есть пересечение по времени выполнения с другими задачами.");
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
            sortedByTimeTasks.remove(task);
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
            sortedByTimeTasks.remove(subtask);
            epicOfSubtask.removeSubtaskIdById(id);
            updateEpicStatus(epicOfSubtask);
            updateEpicStartTime(epicOfSubtask);
            updateEpicDuration(epicOfSubtask);
            updateEpicEndTime(epicOfSubtask);

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
                sortedByTimeTasks.remove(subtasks.get(i));
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
            sortedByTimeTasks.remove(tasks.get(id));
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
            sortedByTimeTasks.remove(subtasks.get(id));
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
            sortedByTimeTasks.remove(subtasks.get(id));
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setTaskStatus(TaskStatus.NEW);
            epic.removeAllSubtasksIds();
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
            updateEpicEndTime(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
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

    private void updateEpicStartTime(Epic epic) {
        if (epic != null) {
            LocalDateTime epicStartTime;

            if (!subtasks.isEmpty()) {
                ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
                epicStartTime = subtasksOfEpic.get(0).getStartTime();

                for (int i = 1; i < subtasksOfEpic.size(); i++) {
                    if (subtasksOfEpic.get(i).getStartTime().isBefore(epicStartTime)) {
                        epicStartTime = subtasksOfEpic.get(i).getStartTime();
                    }
                }
                epic.setStartTime(epicStartTime);
            }
        }
    }

    private void updateEpicDuration(Epic epic) {
        if (epic != null) {
            Duration epicDuration;

            if (!subtasks.isEmpty()) {
                ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
                epicDuration = subtasksOfEpic.get(0).getDuration();

                for (int i = 1; i < subtasksOfEpic.size(); i++) {
                    epicDuration = epicDuration.plus(subtasksOfEpic.get(i).getDuration());
                }
                epic.setDuration(epicDuration);
            }
        }
    }

    private void updateEpicEndTime(Epic epic) {
        if (epic != null) {
            LocalDateTime epicEndTime;

            if (!subtasks.isEmpty()) {
                ArrayList<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
                epicEndTime = subtasksOfEpic.get(0).getEndTime();

                for (int i = 1; i < subtasksOfEpic.size(); i++) {
                    if (subtasksOfEpic.get(i).getEndTime().isAfter(epicEndTime)) {
                        epicEndTime = subtasksOfEpic.get(i).getEndTime();
                    }
                }
                epic.setEpicEndTime(epicEndTime);
            }
        }
    }

    @Override
    public ArrayList<Task> getPrioritizedTask() {
        ArrayList<Task> list = new ArrayList<>();
        list.addAll(sortedByTimeTasks);
        return list;
    }

    @Override
    public boolean checkIntersectionTasks(Task task) {
        ArrayList<Task> prioritizedTask = getPrioritizedTask();
        boolean iskIntersection = false;
        for (int i = 0; i < prioritizedTask.size(); i++) {
            if (task.getStartTime().isAfter(prioritizedTask.get(i).getStartTime())
                    && task.getEndTime().isBefore(prioritizedTask.get(i).getEndTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isBefore(prioritizedTask.get(i).getStartTime())
                    && task.getEndTime().isAfter(prioritizedTask.get(i).getStartTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isAfter(prioritizedTask.get(i).getStartTime())
                    && task.getStartTime().isBefore(prioritizedTask.get(i).getEndTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isBefore(prioritizedTask.get(i).getStartTime())
                    && task.getEndTime().isAfter(prioritizedTask.get(i).getEndTime())) {
                iskIntersection = true;
            }
        }
        return iskIntersection;
    }
}
