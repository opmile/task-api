package org.opmile.securitytodo.repository;

import jakarta.validation.constraints.NotBlank;
import org.opmile.securitytodo.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTitle(@NotBlank String title);

    Task findByTitle(@NotBlank String title);

}
