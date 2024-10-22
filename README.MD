## Description

This repo reproduces an issue in Spring Data JDBC where specifying both criteria and a sort via a
`Query` results in related tables being joined twice in the resolved SQL query.

I have only tested them using Java 21 but earlier versions to 17 should replicate
the issue as well.

## Running Tests

To run the tests, run the following command:

```bash
mvn test
```

All tests should pass. However, `shouldFindAllWithCustomCriteriaAndSort` fails with the following error:

```
org.springframework.jdbc.BadSqlGrammarException: PreparedStatementCallback; bad SQL grammar [SELECT "test_entity"."id" AS "id", "subEntity"."id" AS "subentity_id" FROM "test_entity" LEFT OUTER JOIN "test_sub_entity" "subEntity" ON "subEntity"."test_entity" = "test_entity"."id" LEFT OUTER JOIN "test_sub_entity" "subEntity" ON "subEntity"."test_entity" = "test_entity"."id" AND "subEntity"."test_entity" = "test_entity"."id" WHERE "test_entity"."id" = ? ORDER BY "test_entity"."id" ASC]
	at org.springframework.jdbc.support.SQLStateSQLExceptionTranslator.doTranslate(SQLStateSQLExceptionTranslator.java:112)
	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:107)
	at org.springframework.jdbc.support.AbstractFallbackSQLExceptionTranslator.translate(AbstractFallbackSQLExceptionTranslator.java:116)
	at org.springframework.jdbc.core.JdbcTemplate.translateException(JdbcTemplate.java:1548)
	at org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:677)
	at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:723)
	at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:748)
	at org.springframework.jdbc.core.JdbcTemplate.query(JdbcTemplate.java:804)
	at org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate.query(NamedParameterJdbcTemplate.java:218)
	at org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy.findAll(DefaultDataAccessStrategy.java:369)
	at org.springframework.data.jdbc.core.JdbcAggregateTemplate.findAll(JdbcAggregateTemplate.java:327)
	at dev.rjma.sdjdjb.TestJdbcAggregateTemplate.shouldFindAllWithCustomCriteriaAndSort(TestJdbcAggregateTemplate.java:83)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1597)
Caused by: org.postgresql.util.PSQLException: ERROR: table name "subEntity" specified more than once
	at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(QueryExecutorImpl.java:2733)
	at org.postgresql.core.v3.QueryExecutorImpl.processResults(QueryExecutorImpl.java:2420)
	at org.postgresql.core.v3.QueryExecutorImpl.execute(QueryExecutorImpl.java:372)
	at org.postgresql.jdbc.PgStatement.executeInternal(PgStatement.java:517)
	at org.postgresql.jdbc.PgStatement.execute(PgStatement.java:434)
	at org.postgresql.jdbc.PgPreparedStatement.executeWithFlags(PgPreparedStatement.java:194)
	at org.postgresql.jdbc.PgPreparedStatement.executeQuery(PgPreparedStatement.java:137)
	at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeQuery(ProxyPreparedStatement.java:52)
	at com.zaxxer.hikari.pool.HikariProxyPreparedStatement.executeQuery(HikariProxyPreparedStatement.java)
	at org.springframework.jdbc.core.JdbcTemplate$1.doInPreparedStatement(JdbcTemplate.java:732)
	at org.springframework.jdbc.core.JdbcTemplate.execute(JdbcTemplate.java:658)
	... 10 more
```