SET search_path TO public;

BEGIN;

INSERT INTO post ("id", "title") VALUES
(1,	'Post1'),
(2,	'Post2');

INSERT INTO post_comment ("id", "review", "post_id") VALUES
(11,	'Post 1 Review 1',	1),
(12,	'Post 1 Review 2',	1),
(21,	'Post 2 Review 1',	2),
(22,	'Post 2 Review 2',	2);

COMMIT;

