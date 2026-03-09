package org.opmile.securitytodo.controller;

import jakarta.validation.Valid;
import org.opmile.securitytodo.domain.Task;
import org.opmile.securitytodo.dto.TaskRequest;
import org.opmile.securitytodo.dto.TaskResponse;
import org.opmile.securitytodo.dto.TaskUpdateRequest;
import org.opmile.securitytodo.mapper.TaskMapper;
import org.opmile.securitytodo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.findAll().stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskMapper.toDto(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest request) {
        Task createdTask = taskService.createTask(request);
        return ResponseEntity.ok(taskMapper.toDto(createdTask));

    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(taskMapper.toDto(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
