package ru.craftysoft.registrar.logic;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.craftysoft.registrar.rest.model.CreatedResponseData;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.craftysoft.registrar.service.dao.UserDao;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class UserCreateOperation {

    private final UserDao userDao;

    public Uni<CreatedResponseData> process(UsersCreateRequestData usersCreateRequestData) {
        log.info("UserCreateOperation.process.in");
        return userDao.create(usersCreateRequestData)
                .map(id -> {
                    var responseData = new CreatedResponseData().id(id);
                    log.info("UserCreateOperation.process.out id={}", id);
                    return responseData;
                })
                .onFailure()
                .invoke(e -> log.error("UserCreateOperation.process.thrown {}", e.getMessage()));
    }

}
