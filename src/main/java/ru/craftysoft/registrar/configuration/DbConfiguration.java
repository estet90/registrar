package ru.craftysoft.registrar.configuration;

import io.vertx.mutiny.pgclient.PgPool;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultDSLContext;
import ru.craftysoft.registrar.util.DbClient;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DbConfiguration {

    @ApplicationScoped
    DbClient dbClient(PgPool pgPool) {
        return new DbClient(pgPool);
    }

    @ApplicationScoped
    DSLContext dslContext() {
        return new DefaultDSLContext(SQLDialect.POSTGRES, new Settings().withRenderFormatted(true));
    }

}
