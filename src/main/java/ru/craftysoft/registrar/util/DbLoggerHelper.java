package ru.craftysoft.registrar.util;

import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DbLoggerHelper {

    static void logIn(Logger log, String point, String queryId, String sql, Tuple args) {
        withQueryId(queryId, () -> {
            if (log.isDebugEnabled()) {
                if (log.isTraceEnabled()) {
                    log.trace("{}.in\nsql={}\nargs={}", point, sql, getParameters(args));
                } else {
                    log.debug("{}.in args={}", point, getParameters(args));
                }
            }
        });
    }

    static void logIn(Logger log, String point, String queryId, String sql, List<Tuple> args) {
        withQueryId(queryId, () -> {
            if (log.isDebugEnabled()) {
                if (log.isTraceEnabled()) {
                    log.trace("{}.in\nsql={}\nargs={}", point, sql, getParameters(args));
                } else {
                    log.debug("{}.in args={}", point, getParameters(args));
                }
            }
        });
    }

    static void logOutCount(Logger log, String point, String queryId, RowSet<Row> rows) {
        withQueryId(queryId, () -> {
            if (log.isDebugEnabled()) {
                log.debug("{}.out size={}", point, rows.rowCount());
            }
        });
    }

    static void logOutCount(Logger log, String point, String queryId, Integer count) {
        withQueryId(queryId, () -> {
            if (log.isDebugEnabled()) {
                log.debug("{}.out size={}", point, count);
            }
        });
    }

    static <T> void logOut(Logger log, String point, String queryId, RowSet<Row> rows, T result) {
        withQueryId(queryId, () -> {
            if (log.isDebugEnabled()) {
                if (log.isTraceEnabled()) {
                    var resultForLogging = result instanceof Optional<?> optional && optional.isPresent()
                            ? optional.get()
                            : result;
                    if (resultForLogging instanceof Collection<?> results) {
                        var listOfResults = new ArrayList<String>();
                        for (var res : results) {
                            listOfResults.add(Objects.toString(res));
                        }
                        log.trace("{}.out result={}", point, listOfResults);
                    } else {
                        log.trace("{}.out result={}", point, result);
                    }
                } else {
                    log.debug("{}.out size={}", point, rows.rowCount());
                }
            }
        });
    }

    static void logError(Logger log, String point, String queryId, Throwable e) {
        withQueryId(queryId, () -> log.error("{}.thrown {}", point, e.getMessage()));
    }

    static List<Object> getParameters(Tuple args) {
        var size = args.size();
        var list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            var value = args.getValue(i);
            var loggedValue = value != null && value.getClass().isArray()
                    ? Arrays.asList((Object[]) value)
                    : value;
            list.add(loggedValue);
        }
        return list;
    }

    static List<List<Object>> getParameters(List<Tuple> list) {
        var result = new ArrayList<List<Object>>(list.size());
        for (var tuple : list) {
            result.add(getParameters(tuple));
        }
        return result;
    }

    static void withQueryId(String queryId, Runnable callback) {
        try (var ignored = MDC.putCloseable("queryId", queryId)) {
            callback.run();
        }
    }

}
