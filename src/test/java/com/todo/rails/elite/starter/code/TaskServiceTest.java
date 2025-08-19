package com.todo.rails.elite.starter.code;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.repository.TaskRepository;
import com.todo.rails.elite.starter.code.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleTask = new Task("Homework", "Finish section 4 in page 5", false, LocalDate.now());
    }

    @Test
    void testAddTask_success() {
        when(taskRepository.findByTitle(sampleTask.getTitle())).thenReturn(Optional.empty());
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);

        Task result = taskService.addTask(sampleTask);

        assertNotNull(result);
        assertEquals(sampleTask, result);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void addTask_failure_taskAlreadyExists() {
        when(taskRepository.findByTitle(sampleTask.getTitle())).thenReturn(Optional.of(sampleTask));

        Exception exception = assertThrows(RuntimeException.class, () -> taskService.addTask(sampleTask));

        assertEquals("Task already exists", exception.getMessage());
        verify(taskRepository, never()).save(sampleTask);
    }

    @Test
    void testUpdateTask_success() {
        when(taskRepository.findByTitle(sampleTask.getTitle())).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);

        Task result = taskService.updateTask(sampleTask);

        assertNotNull(result);
        assertEquals(sampleTask, result);
    }

    @Test
    void testDeleteTask_success() {
        when(taskRepository.findByTitle(sampleTask.getTitle())).thenReturn(Optional.of(sampleTask));

        taskService.deleteTask(sampleTask);

        verify(taskRepository, times(1)).delete(sampleTask);
    }

}
