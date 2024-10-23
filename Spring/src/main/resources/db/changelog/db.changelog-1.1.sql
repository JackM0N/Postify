--liquibase formatted sql
--changeset Stanislaw:4 labels:schema,fix

ALTER TABLE user_role
    ADD COLUMN role_name varchar(50) NOT NULL DEFAULT 'USER';

UPDATE user_role
SET role_name = (
    SELECT r.role_name
    FROM role r
    WHERE r.role_id = user_role.role_id
);

ALTER TABLE user_role
    DROP COLUMN role_id;

DROP TABLE role;


--changeset Stanislaw:5 labels:schema,fix
ALTER TABLE post
    ADD COLUMN like_count    BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN comment_count BIGINT NOT NULL DEFAULT 0;

ALTER TABLE comment
    ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0;

UPDATE post
SET like_count = (SELECT count(*)
                  FROM post_like
                  WHERE post.post_id = post_like.post_id);

UPDATE post
SET comment_count = (SELECT count(*)
                     FROM comment
                     WHERE post.post_id = comment.post_id);

UPDATE comment
SET like_count = (SELECT COUNT(*)
                  FROM comment_like
                  WHERE comment.comment_id = comment_like.comment_id);


--changeset Stanislaw:6 labels:triggers
--post
CREATE OR REPLACE FUNCTION increment_post_like_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE post
        SET like_count = like_count + 1
        WHERE post_id = NEW.post_id;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrement_post_like_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE post
        SET like_count = like_count - 1
        WHERE post_id = OLD.post_id;
        RETURN OLD;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER increment_post_like_count_trigger
    AFTER INSERT
    ON post_like
    FOR EACH ROW
EXECUTE FUNCTION increment_post_like_count();

CREATE TRIGGER decrement_post_like_count_trigger
    AFTER DELETE
    ON post_like
    FOR EACH ROW
EXECUTE FUNCTION decrement_post_like_count();

CREATE OR REPLACE FUNCTION increment_comment_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE post
        SET comment_count = comment_count + 1
        WHERE post_id = NEW.post_id;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrement_comment_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE post
        SET comment_count = comment_count - 1
        WHERE post_id = OLD.post_id;
        RETURN OLD;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER increment_comment_count_trigger
    AFTER INSERT
    ON post_like
    FOR EACH ROW
EXECUTE FUNCTION increment_comment_count();

CREATE TRIGGER decrement_comment_count_trigger
    AFTER DELETE
    ON post_like
    FOR EACH ROW
EXECUTE FUNCTION decrement_comment_count();

--comment
CREATE OR REPLACE FUNCTION increment_comment_like_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE comment
        SET like_count = like_count + 1
        WHERE comment_id = NEW.comment_id;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrement_comment_like_count()
    RETURNS TRIGGER AS
'
    BEGIN
        UPDATE comment
        SET like_count = like_count - 1
        WHERE comment_id = OLD.comment_id;
        RETURN OLD;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER increment_comment_like_count_trigger
    AFTER INSERT
    ON comment_like
    FOR EACH ROW
EXECUTE FUNCTION increment_comment_like_count();

CREATE TRIGGER decrement_comment_like_count_trigger
    AFTER DELETE
    ON comment_like
    FOR EACH ROW
EXECUTE FUNCTION decrement_comment_like_count();


--changeset Stanislaw:7 labels:data,fix
UPDATE notification
    SET notification_type = 'POST_LIKE'
    WHERE notification_type = 'like';

UPDATE notification
    SET notification_type = 'COMMENT'
    WHERE notification_type = 'comment';

UPDATE notification
    SET notification_type = 'FOLLOW'
    WHERE notification_type = 'follow';


--changeset Stanislaw:8 labels:triggers,fix
DROP TRIGGER increment_comment_count_trigger ON post_like;
DROP TRIGGER decrement_comment_count_trigger ON post_like;

CREATE TRIGGER increment_comment_count_trigger
    AFTER INSERT
    ON comment
    FOR EACH ROW
EXECUTE FUNCTION increment_comment_count();

CREATE TRIGGER decrement_comment_count_trigger
    AFTER DELETE
    ON comment
    FOR EACH ROW
EXECUTE FUNCTION decrement_comment_count();

UPDATE post
SET comment_count = (SELECT count(*)
                     FROM comment
                     WHERE post.post_id = comment.post_id);


--changeset Stanislaw:9 labels:unique_checks,fix
-- Ensure users cannot follow themselves
ALTER TABLE follow
    ADD CONSTRAINT check_self_follow CHECK (follower_id != followed_id);

-- Ensure a user can follow another user only once
ALTER TABLE follow
    ADD CONSTRAINT unique_follow UNIQUE (follower_id, followed_id);

-- Ensure a user can like a post only once
ALTER TABLE post_like
    ADD CONSTRAINT unique_post_like UNIQUE (user_id, post_id);

-- Ensure a user can like a comment only once
ALTER TABLE comment_like
    ADD CONSTRAINT unique_comment_like UNIQUE (user_id, comment_id);

-- Ensure a post cannot have duplicate hashtags
ALTER TABLE hashtag
    ADD CONSTRAINT unique_post_hashtag UNIQUE (post_id, hashtag);

-- Ensure a user cannot trigger notifications on themselves
ALTER TABLE notification
    ADD CONSTRAINT check_self_notification CHECK (user_id != triggered_by_id);

-- Ensure unique notifications for the same event between users
ALTER TABLE notification
    ADD CONSTRAINT unique_notification UNIQUE (user_id, triggered_by_id, notification_type, post_id, comment_id);

-- Ensure message sender and receiver are different
ALTER TABLE message
    ADD CONSTRAINT check_self_message CHECK (sender_id != receiver_id);


--changeset Stanislaw:10 labels:media
INSERT INTO medium (POST_ID, MEDIUM_URL, MEDIUM_TYPE)
VALUES (1, '../Media/posts/1/post_1_media_0.png', 'image/png'),
       (2, '../Media/posts/2/post_2_media_0.png', 'image/png'),
       (2, '../Media/posts/2/post_2_media_1.png', 'image/png'),
       (3, '../Media/posts/3/post_3_media_0.png', 'image/png'),
       (4, '../Media/posts/4/post_4_media_0.png', 'image/png'),
       (5, '../Media/posts/5/post_5_media_0.png', 'image/png');


--changeset Stanislaw:11 labels:additionalComments
INSERT INTO comment (user_id, post_id, text)
VALUES ((SELECT user_id FROM website_user WHERE username = 'john_doe'),
        (SELECT post_id FROM post WHERE description LIKE 'Coffee is life%'), 'Smacznej kawusi');

INSERT INTO comment (user_id, post_id, parent_comment_id, text)
VALUES (5, 2, (SELECT comment_id FROM comment c WHERE c.post_id = 2 AND c.user_id = 1), 'What?');

INSERT INTO comment (user_id, post_id, parent_comment_id, text)
VALUES (1,
        2,
        (SELECT comment_id FROM comment c WHERE c.post_id = 2 AND c.user_id = 5 and c.parent_comment_id IS NOT NULL),
        'May your coffee taste good');

