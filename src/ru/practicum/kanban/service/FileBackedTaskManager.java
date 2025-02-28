package ru.practicum.kanban.service;

import ru.practicum.kanban.exception.ManagerLoadException;
import ru.practicum.kanban.exception.ManagerSaveException;
import ru.practicum.kanban.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс {@code FileBackedTaskManager} расширяет класс {@code InMemoryTaskManager} и
 * обеспечивает хранение информации о состоянии задач в файле.
 *
 * @author Smirnov Sergey
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path backup;


    public FileBackedTaskManager(HistoryManager historyManager, Path backup) {
        super(historyManager);
        this.backup = backup;
    }


    //--- Чтение файла бэкапа ------------------------------------------------------------------------------------------
    /**
     * Читает состояние менеджера задач из файла бэкапа.
     *
     * <p>Метод создает новый экземпляр {@code FileBackedTaskManager}, читает содержимое файла {@code backup},
     * и восстанавливает состояние менеджера задач на основе данных из файла. Первая строка (заголовок) пропускается.
     * Также устанавливает счетчик ID на основании максимального значения ID, найденного в файле.</p>
     *
     * @param backup путь до файла бэкапа
     * @return экземпляр {@code FileBackedTaskManager}, восстановленный из файла
     * @throws ManagerLoadException ошибка при чтении файла или его парсинге
     */
    public static FileBackedTaskManager loadFromFile(Path backup) {
        try {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), backup);
            List<String> lines = Files.readAllLines(backup, StandardCharsets.UTF_8);
            if (lines.size() > 1) {
                lines.removeFirst(); // удалить заголовок
                lines.forEach(line -> load(line, taskManager));
                setIdCounter(getMaxId(lines) + 1);
            }
            return taskManager;
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при чтении файла бэкапа: " + e.getMessage());
        }
    }

    //--- Создание задачи в менеджере ----------------------------------------------------------------------------------
    @Override
    public Task create(Task task) {
        Task createdTask = super.create(task);
        save();
        return createdTask;
    }

    //--- Обновление задачи в менеджере --------------------------------------------------------------------------------
    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return super.updateTask(updatedTask);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return super.updateEpic(updatedEpic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return super.updateSubtask(updatedSubtask);
    }

    //--- Удаление по идентификатору -----------------------------------------------------------------------------------
    @Override
    public Task deleteTask(int id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic deletedEpic = super.deleteEpic(id);
        save();
        return deletedEpic;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask deletedSubtask = super.deleteSubtask(id);
        save();
        return deletedSubtask;
    }

    //--- Удаление всех задач ------------------------------------------------------------------------------------------
    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    //--- Восстанавливает задачу в менеджере ---------------------------------------------------------------------------
    /**
     * Восстанавливает задачу в менеджере на основе строки.
     *
     * <p>Метод принимает строку {@code task}, содержащую информацию о задаче, десериализует её с помощью метода
     * {@code getTask}, создавая объект задачи, и добавляет его в указанный менеджер задач с помощью метода
     * {@code addTask}.</p>
     *
     * @param task строковое представление задачи
     * @param taskManager экземпляр менеджера задач, в который будет добавлена восстановленная задача
     */
    private static void load(String task, FileBackedTaskManager taskManager) {
        Task recoveredTask = getTask(task);
        taskManager.addTask(recoveredTask);
    }

    //--- Возвращает максимальное значение id из списка задач или 0 в противном случае ---------------------------------
    /**
     * Возвращает максимальное значение идентификатора (id) из списка строк.
     *
     * <p>Метод анализирует переданный список строк, где каждая строка содержит информацию о задаче. Извлекает
     * идентификатор (первое поле в строке, разделенной запятыми) для каждой задачи, находит и возвращает максимальное
     * значение среди них. Если список пуст или не содержит значений id, возвращает 0.</p>
     *
     * @param lines список строк, содержащих информацию о задачах
     * @return максимальное значение идентификатора (id) или 0
     */
    private static Integer getMaxId(List<String> lines) {
        return lines.stream()
                .mapToInt(line -> Integer.parseInt(line.split(",")[0]))
                .max()
                .orElse(0);
    }

    //--- Возвращает инстанс задачи по строке --------------------------------------------------------------------------
    /**
     * Возвращает экземпляр задачи на основе её строкового представления.
     *
     * <p>Метод принимает строку, содержащую информацию о задаче, разбивает её на компоненты по запятой и создает
     * соответствующий экземпляр задачи в зависимости от типа задачи (TASK, EPIC или SUBTASK).</p>
     *
     * @param line строковое представление задачи
     * @return экземпляр задачи ({@code Task}, {@code Epic} или {@code Subtask}) в зависимости от типа
     */
    private static Task getTask(String line) {
        String[] items = line.split(",", -1);

        int id = Integer.parseInt(items[0]);
        Type type = Type.valueOf(items[1]);
        String title = items[2];
        Status status = Status.valueOf(items[3]);
        String description = items[4];

        Instant startTime = items[5].isBlank() ? null : Instant.parse(items[5]);
        Duration duration = items[6].isBlank() ? null : Duration.parse(items[6]);
        int epicId = items[7].isBlank() ? 0 : Integer.parseInt(items[7]);

        return switch (type) {
            case TASK -> new Task(id, title, status, description, startTime, duration);
            case EPIC -> new Epic(id, title, status, description, startTime, duration);
            case SUBTASK -> new Subtask(id, title, status, description, startTime, duration, epicId);
        };
    }

    //--- Сохранение состояния менеджера -------------------------------------------------------------------------------
    /**
     * Сохраняет текущее состояние менеджера задач в файл.
     *
     * <p>Метод создает список строк, представляющий все задачи, преобразуя их в CSV-формат с помощью метода
     * {@code toCsvString}. Вызывает метод {@code writeBackup}, чтобы записать этот список строк в файл бэкапа.</p>
     *
     * <p>Сохранение производится для всех типов задач: {@code Task}, {@code Epic} и {@code Subtask}.</p>
     */
    private void save() {
        List<String> lines = new ArrayList<>();

        super.getAllTasks().forEach(task -> lines.add(task.toCsvString()));
        super.getAllEpics().forEach(epic -> lines.add(epic.toCsvString()));
        super.getAllSubtasks().forEach(subtask -> lines.add(subtask.toCsvString()));

        writeBackup(lines);
    }

    //--- Запись бэкапа в файл -----------------------------------------------------------------------------------------
    /**
     * Записывает данные бэкапа в файл.
     *
     * <p>Метод принимает список строк, представляющих задачи в формате CSV, и записывает их в файл бэкапа. Добавляет
     * заголовок, содержащий названия полей: "id,type,name,status,description,startTime,duration,epic". Каждая строка
     * из списка записывается в файл с новой строки.</p>
     *
     * @param lines список строк, представляющий задачи в формате CSV
     * @throws ManagerSaveException ошибка при записи данных в файл
     */
    private void writeBackup(List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(backup.toFile(), StandardCharsets.UTF_8))) {
            bw.append("id,type,name,status,description,startTime,duration,epic\n");
            for (String s : lines) {
                bw.append(s).append("\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла бэкапа: " + e.getMessage());
        }
    }
}
