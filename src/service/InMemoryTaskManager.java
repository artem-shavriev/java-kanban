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
                updateEpicTime(epic);
                if (subtask.getStartTime() != null) {
                    sortedByTimeTasks.add(subtask);
                }
                return subtask;
            } else {
                return null;
            }
        } else {
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
                    return null;
                }
            } else {
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
                updateEpicTime(epic);
                return epic;
            } else {
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
                        updateEpicTime(epics.get(subtask.getEpicId()));
                        return subtask;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
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
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            return null;
        }
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
            subtasksIds.forEach(id -> epicSubtasks.add(subtasks.get(id)));
            return epicSubtasks;
        } else {
            return null;
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            tasks.remove(id);
            historyManager.remove(id);
            sortedByTimeTasks.remove(task);
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
            updateEpicTime(epicOfSubtask);
        }
    }

    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            ArrayList<Integer> idsForRemove = epic.getSubtasksIds();
            idsForRemove.forEach(removeId -> {
                        subtasks.remove(id);
                        sortedByTimeTasks.remove(subtasks.get(id));
                        historyManager.remove(id);
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeTasks() {
        tasks.keySet().forEach(id -> {
                    historyManager.remove(id);
                    sortedByTimeTasks.remove(tasks.get(id));
                });
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.keySet().forEach(id -> historyManager.remove(id));
        subtasks.keySet().forEach(id -> {
                    historyManager.remove(id);
                    sortedByTimeTasks.remove(subtasks.get(id));
                });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.keySet().forEach(id -> {
                    historyManager.remove(id);
                    sortedByTimeTasks.remove(subtasks.get(id));
                });
        subtasks.clear();

        epics.values().forEach(epic -> {
                    epic.setTaskStatus(TaskStatus.NEW);
                    epic.removeAllSubtasksIds();
                    updateEpicTime(epic);
                });
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
                subtasksOfEpic.forEach(subtask -> taskStatuses.add(subtask.getTaskStatus()));
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

    private void updateEpicTime(Epic epic) {
        if (epic != null) {
            updateEpicStartTime(epic);
            updateEpicEndTime(epic);
            updateEpicDuration(epic);
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
        boolean iskIntersection = false;
        for (Task t : getPrioritizedTask()) {
            if (task.getStartTime().isAfter(t.getStartTime())
                    && task.getEndTime().isBefore(t.getEndTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isBefore(t.getStartTime())
                    && task.getEndTime().isAfter(t.getStartTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isAfter(t.getStartTime())
                    && task.getStartTime().isBefore(t.getEndTime())) {
                iskIntersection = true;
            } else if (task.getStartTime().isBefore(t.getStartTime())
                    && task.getEndTime().isAfter(t.getEndTime())) {
                iskIntersection = true;
            }
        }
        return iskIntersection;
    }
}
