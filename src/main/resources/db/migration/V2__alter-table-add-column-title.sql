ALTER TABLE tasks
    ADD COLUMN title VARCHAR(255) NOT NULL;

ALTER TABLE tasks
    ADD CONSTRAINT uk_tasks_title UNIQUE(title);

