package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.repository.TaskRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
	private final TaskRepository taskRepository;

	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

	@Autowired
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	/**
	 * Adds a new task to the task repository and avoids any duplicate task title
	 * @param task the new task to be added
	 * @return returns the task added
	 * @throws RuntimeException in the event the task title is already present in the repository
	 */
	public Task addTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		if (taskRepository.findByTitle(task.getTitle()).isPresent()) {
			logger.error("Task already exists");
			throw new RuntimeException("Task already exists");
		}
		return taskRepository.save(task);
	}

	/**
	 * Searches the task repository and finds the task of interest using its id.
	 * @param id the identifier unique to each task
	 * @return returns the task with matching id
	 * @throws RuntimeException in the event the task with the given id is not found
	 */
	public Task getTaskById(@NotNull(message = "Id cannot be null") Long id) throws RuntimeException {
		return taskRepository.findById(id)
				.orElseThrow(
						() -> new RuntimeException("Task not found")
				);
	}

	/**
	 * Searches for and returns the task using its title
	 * @param title the title of the task
	 * @return the task with the given title
	 * @throws RuntimeException in the event the task with the given title is not found
	 */
	public Task getTaskByTitle(
			@NotNull(message = "Title cannot be null")
			@NotBlank(message = "Title cannot be blank")
			String title) throws RuntimeException {
		return taskRepository.findByTitle(title)
				.orElseThrow(
						() -> new RuntimeException("Task not found")
				);
	}

	/**
	 * Gets all the tasks stored in the repository
	 * @return a list of all the tasks stored
	 */
	public List<Task> getAllTasks() {
		if (taskRepository.findAll().isEmpty()) {
			return List.of();
		}
		return taskRepository.findAll();
	}

	/**
	 * Updates an existing task with new task details
	 * @param task the task with updated information
	 * @return the task with updated details
	 * @throws RuntimeException in the event the task to be updated is not found
	 */
	public Task updateTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		Optional<Task> existingTask = taskRepository.findByTitle(task.getTitle());
		if (existingTask.isEmpty()) {
			logger.error("Task not found while updating task");
			throw new RuntimeException("Task not found");
		}
		Task taskToUpdate = existingTask.get();
		taskToUpdate.setTitle(task.getTitle());
		taskToUpdate.setDescription(task.getDescription());
		taskToUpdate.setCompleted(task.isCompleted());
		taskToUpdate.setDueDate(task.getDueDate());
		return taskRepository.save(taskToUpdate);
	}

	/**
	 * Deletes a task with the given title
	 * @param task a Task object that is being deleted
	 * @throws RuntimeException in the event the task is not found
	 */
	public void deleteTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		Optional<Task> existingTask = taskRepository.findByTitle(task.getTitle());
		if (existingTask.isEmpty()) {
			logger.error("Task not found while deleting task");
			throw new RuntimeException("Task not found");
		}
		taskRepository.delete(task);
	}

	/**
	 * Fetches all the pending tasks left by first fetching all the tasks and comparing which tasks are not completed
	 * @return a list of unfinished tasks
	 */
	public List<Task> getPendingTasks() {
		List<Task> allTasks = getAllTasks();
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(task -> !task.isCompleted())
				.toList();
	}

	/**
	 * Fetches all the completed tasks by first getting all the tasks and comparing which tasks are completed
	 * @return a list of completed tasks
	 */
	public List<Task> getCompletedTasks() {
		List<Task> allTasks = getAllTasks();
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(Task::isCompleted)
				.toList();
	}

	/**
	 * Finds all the tasks that are due today by first fetching all the tasks and comparing which tasks have the due date of today
	 * @return a list of tasks that are due today
	 */
	public List<Task> getTodayTasks() {
		List<Task> allTasks = getAllTasks();
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(
						task -> !task.isCompleted()
				)
				.filter(
						task -> task.getDueDate()
								.isEqual(LocalDate.now())
				)
				.toList();
	}
}
