DROP TABLE IF EXISTS PARTICIPATE;
DROP TABLE IF EXISTS SESSIONS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS TEACHERS;

CREATE TABLE TEACHERS (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          last_name VARCHAR(40),
                          first_name VARCHAR(40),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE SESSIONS (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(50),
                          description VARCHAR(2000),
                          date TIMESTAMP,
                          teacher_id INT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE USERS (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       last_name VARCHAR(40),
                       first_name VARCHAR(40),
                       admin BOOLEAN NOT NULL DEFAULT false,
                       email VARCHAR(255),
                       password VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE PARTICIPATE (
                             user_id INT,
                             session_id INT,
                             CONSTRAINT fk_participate_user FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
                             CONSTRAINT fk_participate_session FOREIGN KEY (session_id) REFERENCES SESSIONS(id) ON DELETE CASCADE
);

ALTER TABLE SESSIONS ADD CONSTRAINT fk_sessions_teacher FOREIGN KEY (teacher_id) REFERENCES TEACHERS(id) ON DELETE SET NULL;
