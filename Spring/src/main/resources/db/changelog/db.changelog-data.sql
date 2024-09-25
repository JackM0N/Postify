--liquibase formatted sql
--changeset Stanislaw:1 labels:init,data

INSERT INTO role (role_name)
VALUES ('USER');
INSERT INTO role (role_name)
VALUES ('ADMIN');

INSERT INTO website_user (username, email, password, full_name, bio, profile_picture_url)
VALUES ('john_doe', 'john@example.com', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'John Doe',
        'Love traveling and photography',
        'https://static.wikia.nocookie.net/lobotomycorp/images/b/bf/CentralCommandTeam2Clerk.png/revision/latest?cb=20180901090026'),
       ('jane_smith', 'jane@example.com', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'Jane Smith',
        'Coffee addict and web developer',
        'https://static.wikia.nocookie.net/lobotomycorp/images/0/0c/DisciplinaryTeamClerk.png/revision/latest?cb=20180825191813'),
       ('mike_the_builder', 'mike@example.com', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO',
        'Mike The Builder', 'Building things is my passion!', null),
       ('anna_walker', 'anna@example.com', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO',
        'Anna Walker', 'Enjoying nature and good books.', null),
       ('lucas_perez', 'lucas@example.com', '$2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO',
        'Lucas Perez', 'Foodie and tech enthusiast.', null),
       ('testadmin', 'testadmin@localhost', '2a$10$o9OcOIN.1bmooaFQJ.1PAOyZxHBA9IXo8qj2dECXrj8Vk9YpvxstO', 'testadmin',
        'Delete after testing',
        'https://static.wikia.nocookie.net/lobotomycorp/images/2/29/NothingTherePortrait.png/revision/latest/scale-to-width-down/50?cb=20180730025749');

INSERT INTO user_role (user_id, role_id)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT role_id FROM role WHERE role_name = 'USER')),
       ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT role_id FROM role WHERE role_name = 'USER')),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT role_id FROM role WHERE role_name = 'USER')),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT role_id FROM role WHERE role_name = 'USER')),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT role_id FROM role WHERE role_name = 'USER'));

INSERT INTO post (user_id, description)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        'Visited the Grand Canyon today, breathtaking views!'),
       ((SELECT user_id FROM website_user WHERE username = 'jane_smith'), 'Coffee is life ☕ #morningvibes'),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        'Built a new treehouse for the kids today, super fun project!'),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        'A perfect day for a hike in the mountains.'),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        'Just tried the new fusion restaurant in town, amazing food!');

INSERT INTO comment (user_id, post_id, text)
VALUES ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT post_id FROM post WHERE description LIKE 'Visited the Grand Canyon today%'),
        'Wow, that sounds amazing!'),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT post_id FROM post WHERE description LIKE 'Coffee is life%'), 'I could use a cup right now!'),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT post_id FROM post WHERE description LIKE 'Built a new treehouse%'),
        'That’s so cool, my kids would love that!'),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT post_id FROM post WHERE description LIKE 'A perfect day for a hike%'), 'Nature is the best therapy!'),
       ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT post_id FROM post WHERE description LIKE 'Just tried the new fusion%'),
        'I need to check that place out!');

INSERT INTO post_like (user_id, post_id)
VALUES ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT post_id FROM post WHERE description LIKE 'Visited the Grand Canyon today%')),
       ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT post_id FROM post WHERE description LIKE 'Coffee is life%')),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT post_id FROM post WHERE description LIKE 'Built a new treehouse%')),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT post_id FROM post WHERE description LIKE 'A perfect day for a hike%')),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT post_id FROM post WHERE description LIKE 'Just tried the new fusion%'));

INSERT INTO follow (follower_id, followed_id)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT user_id FROM website_user WHERE username = 'jane_smith')),
       ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT user_id FROM website_user WHERE username = 'anna_walker')),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT user_id FROM website_user WHERE username = 'lucas_perez')),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT user_id FROM website_user WHERE username = 'mike_the_builder')),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT user_id FROM website_user WHERE username = 'john_doe'));

INSERT INTO notification (user_id, triggered_by_id, notification_type, post_id)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT user_id FROM website_user WHERE username = 'jane_smith'), 'like',
        (SELECT post_id FROM post WHERE description LIKE 'Visited the Grand Canyon today%')),
       ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT user_id FROM website_user WHERE username = 'john_doe'), 'comment',
        (SELECT post_id FROM post WHERE description LIKE 'Coffee is life%')),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT user_id FROM website_user WHERE username = 'lucas_perez'), 'follow', NULL),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT user_id FROM website_user WHERE username = 'mike_the_builder'), 'comment',
        (SELECT post_id FROM post WHERE description LIKE 'A perfect day for a hike%')),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT user_id FROM website_user WHERE username = 'john_doe'), 'like',
        (SELECT post_id FROM post WHERE description LIKE 'Just tried the new fusion%'));

INSERT INTO message (sender_id, receiver_id, message_text)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT user_id FROM website_user WHERE username = 'jane_smith'), 'Hey Jane, loved your recent post!'),
       ((SELECT user_id FROM website_user WHERE username = 'jane_smith'),
        (SELECT user_id FROM website_user WHERE username = 'john_doe'), 'Thanks, John! Glad you liked it!'),
       ((SELECT user_id FROM website_user WHERE username = 'mike_the_builder'),
        (SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        'Hey Lucas, let’s grab some food at that new place!'),
       ((SELECT user_id FROM website_user WHERE username = 'anna_walker'),
        (SELECT user_id FROM website_user WHERE username = 'mike_the_builder'), 'Loved the treehouse idea!'),
       ((SELECT user_id FROM website_user WHERE username = 'lucas_perez'),
        (SELECT user_id FROM website_user WHERE username = 'john_doe'), 'You need to check out that new restaurant!');


--changeset Stanislaw:3 labels:init,data
INSERT INTO user_role (user_id, role_id)
VALUES ((SELECT user_id FROM website_user WHERE username = 'testadmin'),
        (SELECT role_id FROM role WHERE role_name = 'USER')),
       ((SELECT user_id FROM website_user WHERE username = 'testadmin'),
        (SELECT role_id FROM role WHERE role_name = 'ADMIN'));