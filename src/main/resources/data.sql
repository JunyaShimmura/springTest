INSERT INTO users (username, password, enabled)
VALUES ('user1', '$2a$10$SfjGgRG0lp1.h/j1BPk7LeZCzbKf4mmyJ1PLDa4w2/WwZk14sk5AC', true)
ON CONFLICT (username) DO NOTHING;