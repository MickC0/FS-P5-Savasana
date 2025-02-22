INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');


INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');

INSERT INTO SESSIONS (name, description, date, teacher_id) VALUES
    ('Yoga Matinal', 'Une séance de yoga énergisante pour bien commencer la journée.', '2025-03-03 10:00:00', 1);


INSERT INTO PARTICIPATE (user_id, session_id) VALUES
    (1, 1);