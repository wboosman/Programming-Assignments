    private final String endPlatform;
/**
 * Task class represents an individual scheduling task with
 * a start and end time, and a start and end platform.
 * Each task has a unique identifier (task number).
 *
 * Author: Wessel Boosman
 */

public class Task {
    private final int taskNumber;
    private final int startTime;
    private final int endTime;
    private final String startPlatform;

    public Task(int taskNumber, int startTime, int endTime, String startPlatform, String endPlatform) {
        /**
         * Constructs a Task with time interval and platform information.
         *
         * @param taskNumber    unique identifier of the task
         * @param startTime     start time of the task
         * @param endTime       end time of the task
         * @param startPlatform starting platform
         * @param endPlatform   ending platform
         */
        this.taskNumber = taskNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPlatform = startPlatform;
        this.endPlatform = endPlatform;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getStartPlatform() {
        return startPlatform;
    }

    public String getEndPlatform() {
        return endPlatform;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskNumber=" + taskNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startPlatform='" + startPlatform + '\'' +
                ", endPlatform='" + endPlatform + '\'' +
                '}';
    }
}
