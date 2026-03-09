package org.opmile.securitytodo.mapper;

import org.opmile.securitytodo.domain.Status;
import org.opmile.securitytodo.domain.Task;
import org.opmile.securitytodo.dto.TaskRequest;
import org.opmile.securitytodo.dto.TaskResponse;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toEntity(TaskRequest request) {
        Task task = new Task();

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setStatus(Status.PENDING);

        return task;
    }

    public TaskResponse toDto(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getDueDate(),
                task.getStatus().getStatusApi()
        );
    }
}
