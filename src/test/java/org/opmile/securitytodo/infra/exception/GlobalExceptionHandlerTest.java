package org.opmile.securitytodo.infra.exception;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.opmile.securitytodo.mapper.TaskMapper;
import org.opmile.securitytodo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {

        Long id = 999L;

        given(taskService.findById(id))
                .willThrow(new TaskNotFoundException("Task not found: " + id));

        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Task not found: " + id));
    }

    @Test
    void shouldReturn409WhenTaskAlreadyExists() throws Exception {

        String json = """
        {
          "title": "Duplicate Task",
          "description": "desc",
          "dueDate": "31/12/2026"
        }
        """;

        given(taskService.createTask(any()))
                .willThrow(new TaskAlreadyExistsException("Task exists"));

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenValidationFails() throws Exception {

        String invalidJson = """
        {
          "title": "",
          "description": "desc",
          "dueDate": "31/12/2026"
        }
        """;

        mockMvc.perform(post("/api/tasks")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {

        given(taskService.findAll()).willThrow(new RuntimeException("Database exploded"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }
}