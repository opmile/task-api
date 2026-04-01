ALTER TABLE tasks
    ADD COLUMN user_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id);