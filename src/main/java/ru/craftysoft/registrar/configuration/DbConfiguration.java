package ru.craftysoft.registrar.configuration;

import io.vertx.mutiny.pgclient.PgPool;
import ru.craftysoft.registrar.util.DbClient;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DbConfiguration {

    @ApplicationScoped
    DbClient dbClient(PgPool pgPool) {
        return new DbClient(pgPool);
    }

}
