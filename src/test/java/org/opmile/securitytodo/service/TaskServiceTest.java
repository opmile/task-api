package org.opmile.securitytodo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opmile.securitytodo.domain.Status;
import org.opmile.securitytodo.domain.Task;
import org.opmile.securitytodo.dto.TaskRequest;
import org.opmile.securitytodo.dto.TaskUpdateRequest;
import org.opmile.securitytodo.infra.exception.TaskAlreadyExistsException;
import org.opmile.securitytodo.infra.exception.TaskNotFoundException;
import org.opmile.securitytodo.mapper.TaskMapper;
import org.opmile.securitytodo.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldThrowWhenTaskNotFound() {
        Long id = 99L;

        given(taskRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findById(99L));
    }

    @Test
    void shouldReturnTaskWhenFound() {
        Long id = 99L;

        Task task = new Task();
        task.setId(id);
        task.setTitle("Test Task");


        given(taskRepository.findById(id)).willReturn(Optional.of(task));

        Task foundTask = taskService.findById(id);

        assertNotNull(foundTask);
        assertEquals(id, foundTask.getId());
        assertEquals("Test Task", foundTask.getTitle());

        then(taskRepository).should().findById(id);
    }

    @Test
    void shouldCreateTask_whenValidRequest() {
        // given

        TaskRequest request = new TaskRequest("Study", "Spring", LocalDate.now());

        Task taskMapped = new Task();
        taskMapped.setTitle(request.title());

        Task taskSaved = new Task();
        taskSaved.setId(1L);
        taskSaved.setTitle(request.title());

        given(taskMapper.toEntity(request)).willReturn(taskMapped);
        given(taskRepository.save(taskMapped)).willReturn(taskSaved);

        // when
        Task result = taskService.createTask(request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(request.title(), result.getTitle());

        then(taskMapper).should().toEntity(request);
        then(taskRepository).should().save(taskMapped);
    }

    @Test
    void shouldThrowException_whenCreatingDuplicateTask() {
        // given
        TaskRequest request = new TaskRequest("Study", "Spring", LocalDate.now());

        Task taskMapped = new Task();
        taskMapped.setTitle(request.title());

        given(taskMapper.toEntity(request)).willReturn(taskMapped);
        given(taskRepository.save(taskMapped)).willThrow(DataIntegrityViolationException.class);

        // when & then
        assertThrows(TaskAlreadyExistsException.class, () -> taskService.createTask(request));

        then(taskMapper).should().toEntity(request);
        then(taskRepository).should().save(taskMapped);
    }

    @Test
    void shouldUpdateTask_whenValidRequest() {
        // given
        Long id = 1L;

        Task existingTask = new Task();
        existingTask.setId(id);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(Status.fromString("pending"));


        TaskUpdateRequest request = new TaskUpdateRequest("New Title", "Updated Description", LocalDate.now().plusDays(1), "approved");

        given(taskRepository.findById(id)).willReturn(Optional.of(existingTask));

        // when
        Task updatedTask = taskService.updateTask(id, request);

        // then
        assertNotNull(updatedTask);
        assertEquals(id, updatedTask.getId());
        assertEquals(request.title(), updatedTask.getTitle());
        assertEquals(request.description(), updatedTask.getDescription());
        assertEquals(request.dueDate(), updatedTask.getDueDate());
        assertEquals(Status.fromString(request.status()), updatedTask.getStatus());

        then(taskRepository).should().findById(id);
    }

    @Test
    void shouldThrowException_whenUpdatingExistingTask() {
        // given
        Long id = 99L;
        TaskUpdateRequest request = new TaskUpdateRequest("t", "d", LocalDate.now(), "approved");

        given(taskRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(id, request));
    }

    @Test
    void shouldDeleteTask_whenValidRequest() {
        // given
        Long id = 1L;

        Task existingTask = new Task();
        existingTask.setId(id);

        given(taskRepository.findById(id)).willReturn(Optional.of(existingTask));

        // when
        taskService.deleteTask(id);

        // then
        then(taskRepository).should().findById(id);
        then(taskRepository).should().delete(existingTask);
    }

    @Test
    void shouldThrowException_whenDeletingNonExistingTask() {
        // given
        Long id = 99L;

        given(taskRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(id));

        then(taskRepository).should().findById(id);
    }

}