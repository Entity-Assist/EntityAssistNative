package com.entityassist.nativeimage;

import com.entityassist.enumerations.Operand;
import org.hibernate.boot.MetadataSources;
import org.hibernate.bytecode.internal.none.BytecodeProviderImpl;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.provider.ReactiveServiceRegistryBuilder;

import java.time.Duration;
import java.util.UUID;

final class PostgresSmoke
{
    private PostgresSmoke()
    {
    }

    static void run()
    {
        String id = UUID.randomUUID().toString();
        var registry = new ReactiveServiceRegistryBuilder()
                .applySetting("jakarta.persistence.jdbc.url", requiredEnvironment("EA_POSTGRES_URL"))
                .applySetting("jakarta.persistence.jdbc.user", requiredEnvironment("EA_POSTGRES_USER"))
                .applySetting("jakarta.persistence.jdbc.password", requiredEnvironment("EA_POSTGRES_PASSWORD"))
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("jakarta.persistence.validation.mode", "none")
                .addService(BytecodeProvider.class, new BytecodeProviderImpl())
                .build();

        try (var factory = new MetadataSources(registry)
                .addAnnotatedClass(SmokeRow.class)
                .buildMetadata()
                .buildSessionFactory())
        {
            var reactive = factory.unwrap(Mutiny.SessionFactory.class);
            var row = reactive.withTransaction((session, transaction) ->
                    session.createNativeQuery("""
                            create temporary table entityassist_native_smoke (
                                id varchar(36) primary key, label varchar(80) not null
                            ) on commit drop
                            """).executeUpdate()
                            .chain(() -> session.createNativeQuery("""
                                    insert into entityassist_native_smoke (id, label)
                                    values (:id, 'native smoke'), ('decoy', 'decoy')
                                    """).setParameter("id", id).executeUpdate())
                            .chain(() -> {
                                var builder = new SmokeRow.Builder(factory.getCriteriaBuilder());
                                builder.setSession(session);
                                builder.setEntity(new SmokeRow());
                                return builder.where("id", Operand.Equals, id).get();
                            })
            ).await().atMost(Duration.ofSeconds(30));

            if (!id.equals(row.getId()) || !"native smoke".equals(row.label()))
            {
                throw new IllegalStateException("EntityAssist returned an unexpected PostgreSQL row");
            }
        }
        finally
        {
            ReactiveServiceRegistryBuilder.destroy(registry);
        }
        System.out.println("EntityAssist PostgreSQL query passed");
    }

    private static String requiredEnvironment(String name)
    {
        String value = System.getenv(name);
        if (value == null || value.isBlank())
        {
            throw new IllegalArgumentException("Set " + name + " before running --postgres");
        }
        return value;
    }
}
