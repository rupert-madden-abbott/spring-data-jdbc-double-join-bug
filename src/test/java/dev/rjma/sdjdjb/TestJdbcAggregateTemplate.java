package dev.rjma.sdjdjb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = {
        "spring.sql.init.mode=always"
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TestJdbcAggregateTemplate {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:13.4");

    public TestJdbcAggregateTemplate(JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    public static class TestSubEntity {
        @Id
        String id;
    }

    public static class TestEntity {
        @Id
        String id;

        TestSubEntity subEntity;
    }

    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    @AfterEach
    void tearDown() {
        jdbcAggregateTemplate.deleteAll(TestEntity.class);
    }

    @Test
    void shouldFindAll() {
        insertTestEntity();

        assertThat(jdbcAggregateTemplate.findAll(TestEntity.class)).hasSize(1);
    }

    @Test
    void shouldFindAllWithCustomCriteria() {
        TestEntity testEntity = insertTestEntity();

        Query query = Query.query(Criteria.where("id").is(testEntity.id));

        assertThat(jdbcAggregateTemplate.findAll(query, TestEntity.class)).hasSize(1);
    }

    @Test
    void shouldFindAllWithCustomSort() {
        insertTestEntity();

        Query query = Query.empty().sort(Sort.by(Sort.Order.asc("id")));

        assertThat(jdbcAggregateTemplate.findAll(query, TestEntity.class)).hasSize(1);
    }

    @Test
    void shouldFindAllWithCustomCriteriaAndSort() {
        TestEntity testEntity = insertTestEntity();

        Query query = Query.query(Criteria.where("id").is(testEntity.id)).sort(Sort.by(Sort.Order.asc("id")));

        assertThat(jdbcAggregateTemplate.findAll(query, TestEntity.class)).hasSize(1);
    }

    private TestEntity insertTestEntity() {
        TestEntity entity = new TestEntity();
        entity.id = "1";
        entity.subEntity = new TestSubEntity();
        entity.subEntity.id = "2";
        return jdbcAggregateTemplate.insert(entity);
    }
}
