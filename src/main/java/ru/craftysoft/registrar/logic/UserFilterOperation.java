package ru.craftysoft.registrar.logic;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.craftysoft.registrar.rest.model.FilteredUser;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;
import ru.craftysoft.registrar.service.dao.UserDao;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserFilterOperation {

    private final UserDao userDao;

    public Uni<List<FilteredUser>> process(UsersFilterStatus status) {
        log.info("UserFilterOperation.process.in status={}", status.toString());
        return userDao.filter(status)
                .map(users -> {
                    var result = users.stream()
                            .map(user -> new FilteredUser())
                            .toList();
                    log.info("UserFilterOperation.process.out count={}", result.size());
                    return result;
                })
                .onFailure()
                .invoke(e -> log.error("UserCreateOperation.process.thrown {}", e.getMessage()));
    }

}
