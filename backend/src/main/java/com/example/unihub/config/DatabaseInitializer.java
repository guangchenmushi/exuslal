package com.example.unihub.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        addColumnIfNotExists("ALTER TABLE users ADD COLUMN stu_id VARCHAR(50) DEFAULT NULL AFTER avatar");
        addColumnIfNotExists("ALTER TABLE users ADD COLUMN security_question VARCHAR(255) DEFAULT NULL AFTER stu_id");
        addColumnIfNotExists(
                "ALTER TABLE users ADD COLUMN security_answer VARCHAR(255) DEFAULT NULL AFTER security_question");
        addColumnIfNotExists("ALTER TABLE users ADD COLUMN class_id INT DEFAULT NULL AFTER security_answer");
        createTableIfNotExists("CREATE TABLE IF NOT EXISTS class_info ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(100) NOT NULL,"
                + "code VARCHAR(50),"
                + "grade VARCHAR(20),"
                + "major VARCHAR(100),"
                + "teacher_id INT,"
                + "created_at DATETIME,"
                + "updated_at DATETIME"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        createTableIfNotExists("CREATE TABLE IF NOT EXISTS semesters ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(100) NOT NULL,"
                + "code VARCHAR(50),"
                + "start_date DATE,"
                + "end_date DATE,"
                + "is_current BOOLEAN DEFAULT FALSE,"
                + "created_at DATETIME,"
                + "updated_at DATETIME"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    private void addColumnIfNotExists(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            // column may already exist, ignore
        }
    }

    private void createTableIfNotExists(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException e) {
            // table may already exist, ignore
        }
    }
}
