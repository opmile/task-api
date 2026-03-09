package org.opmile.securitytodo.service;

import jakarta.transaction.Transactional;
import org.opmile.securitytodo.domain.Status;
import org.opmile.securitytodo.domain.Task;
import org.opmile.securitytodo.dto.TaskRequest;
import org.opmile.securitytodo.dto.TaskUpdateRequest;
import org.opmile.securitytodo.infra.exception.TaskAlreadyExistsException;
import org.opmile.securitytodo.infra.exception.TaskNotFoundException;
import org.opmile.securitytodo.mapper.TaskMapper;
import org.opmile.securitytodo.repository.TaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }

    @Transactional
    public Task createTask(TaskRequest request) {
        Task task = taskMapper.toEntity(request);

        try { // avoid race condition delegating to database unique constraint
            return taskRepository.save(task);
        } catch (DataIntegrityViolationException e) {
            throw new TaskAlreadyExistsException("Task with the same title already exists: " + request.title());
        }
    }

    @Transactional
    public Task updateTask(Long id, TaskUpdateRequest request) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));

        existingTask.setTitle(request.title());
        existingTask.setDescription(request.description());
        existingTask.setDueDate(request.dueDate());
        existingTask.setStatus(Status.fromString(request.status()));

        return existingTask;
    }

    @Transactional
    public void deleteTask(Long id) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));

        taskRepository.delete(existingTask);
    }
}
