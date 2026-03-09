package org.opmile.securitytodo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.opmile.securitytodo.domain.Task;
import org.opmile.securitytodo.dto.TaskRequest;
import org.opmile.securitytodo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@Transactional
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private TaskService taskService;

    @Test
    void shouldReturnAllTasks() throws Exception {

        taskService.createTask(new TaskRequest("Task 1", "Description 1", LocalDate.now().plusDays(1)));
        taskService.createTask(new TaskRequest("Task 2", "Description 2", LocalDate.now().plusDays(2)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

    }

    @Test
    void shouldReturnTaskById() throws Exception {
        Task created = taskService.createTask(new TaskRequest("Task 1", "Description 1", LocalDate.now().plusDays(1)));

        mockMvc.perform(get("/api/tasks/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    void shouldCreateTask() throws Exception {

        TaskRequest request = new TaskRequest("New Task", "New Description", LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("New Description"));

    }

    @Test
    void shouldUpdateTask() throws Exception {

        Task created = taskService.createTask(new TaskRequest("Task 1", "Description 1", LocalDate.now().plusDays(1)));

        TaskRequest update = new TaskRequest("Updated Task", "Updated Description", LocalDate.now().plusDays(5));

        mockMvc.perform(put("/api/tasks/" + created.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task created = taskService.createTask(new TaskRequest("Task 1", "Description 1", LocalDate.now().plusDays(1)));

        mockMvc.perform(delete("/api/tasks/" + created.getId()))
                .andExpect(status().isNoContent());
    }
}