CREATE TABLE "test_entity"
(
    "id" VARCHAR PRIMARY KEY NOT NULL
);

CREATE TABLE "test_sub_entity"
(
    "id" VARCHAR PRIMARY KEY NOT NULL,
    "test_entity" VARCHAR REFERENCES "test_entity" ("id")
);
