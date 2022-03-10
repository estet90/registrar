package ru.craftysoft.registrar.service.dao;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.craftysoft.registrar.builder.UserBuilder;
import ru.craftysoft.registrar.dto.User;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;
import ru.craftysoft.registrar.util.DbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.craftysoft.registrar.rest.model.UsersFilterStatus.ALL;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class UserDao {

    private final DbClient dbClient;
    private final UserBuilder userBuilder;

    public Uni<Long> create(UsersCreateRequestData usersCreateRequestData) {
        var query = """
                INSERT INTO registrar.users (first_name, last_name, middle_name, passport, passport_expiry_date, birth_date,
                                            citizenship, email, password, visa_request_date_from, visa_request_date_to,
                                            visa_request_cities)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
                RETURNING id""";
        var listArgs = new ArrayList<>(12);
        listArgs.add(usersCreateRequestData.getFirstName());
        listArgs.add(usersCreateRequestData.getLastName());
        listArgs.add(usersCreateRequestData.getMiddleName());
        listArgs.add(usersCreateRequestData.getPassport());
        listArgs.add(usersCreateRequestData.getPassportExpiryDate());
        listArgs.add(usersCreateRequestData.getBirthDate());
        listArgs.add(usersCreateRequestData.getCitizenship());
        listArgs.add(usersCreateRequestData.getEmail());
        listArgs.add(usersCreateRequestData.getPassword());
        listArgs.add(usersCreateRequestData.getRequest().getFrom());
        listArgs.add(usersCreateRequestData.getRequest().getTo());
        listArgs.add(usersCreateRequestData.getRequest().getCities());
        var args = Tuple.tuple(listArgs);
        return dbClient.toUni(log, "UserDao.create", query, args, row -> row.getLong("id"));
    }

    @NotNull
    public User buildCreated(UsersCreateRequestData usersCreateRequestData) {
        var user = new User();
        user.setFirstName(usersCreateRequestData.getFirstName());
        user.setLastName(usersCreateRequestData.getLastName());
        user.setMiddleName(usersCreateRequestData.getMiddleName());
        user.setCitizenship(usersCreateRequestData.getCitizenship());
        user.setCreatedAt(OffsetDateTime.now());
        user.setBirthDate(usersCreateRequestData.getBirthDate());
        user.setEmail(usersCreateRequestData.getEmail());
        user.setPassword(usersCreateRequestData.getPassword());
        user.setPassport(usersCreateRequestData.getPassport());
        user.setPassportExpiryDate(usersCreateRequestData.getPassportExpiryDate());
        var request = usersCreateRequestData.getRequest();
        user.setVisaRequestCities(request.getCities());
        user.setVisaRequestDateFrom(request.getFrom());
        user.setVisaRequestDateTo(request.getTo());
        return user;
    }

    public Uni<List<User>> filter(UsersFilterStatus status) {
        var query = """
                SELECT id,
                       first_name,
                       last_name,
                       middle_name,
                       passport,
                       birth_date,
                       passport_expiry_date,
                       citizenship,
                       email,
                       password,
                       created_at,
                       completed_at,
                       rejected_at,
                       visa_request_date_from,
                       visa_request_date_to,
                       visa_request_cities,
                       visa_response_status,
                       visa_response_body,
                       status
                FROM registrar.users""";
        var args = Tuple.tuple();
        if (!ALL.equals(status)) {
            query = query + "\nWHERE status = $1";
            args = Tuple.of(status.toString());
        }
        return dbClient.toUniOfList(log, "UserDao.filter", query, args, row -> {
            var user = new User();
            user.setId(row.getLong("id"));
            user.setFirstName(row.getString("first_name"));
            user.setLastName(row.getString("last_name"));
            user.setMiddleName(row.getString("middle_name"));
            user.setBirthDate(row.getLocalDate("birth_date"));
            user.setCitizenship(row.getString("citizenship"));
            user.setEmail(row.getString("email"));
            user.setPassword(row.getString("password"));
            user.setPassport(row.getString("passport"));
            user.setPassportExpiryDate(row.getLocalDate("passport_expiry_date"));
            user.setCreatedAt(row.getOffsetDateTime("created_at"));
            user.setCompletedAt(row.getOffsetDateTime("completed_at"));
            user.setRejectedAt(row.getOffsetDateTime("rejected_at"));
            user.setStatus(User.Status.fromString(row.getString("status")));
            user.setVisaRequestDateFrom(row.getLocalDate("visa_request_date_from"));
            user.setVisaRequestDateTo(row.getLocalDate("visa_request_date_to"));
            user.setVisaRequestCities(Arrays.stream(row.getArrayOfStrings("visa_request_cities")).collect(Collectors.toSet()));
            user.setVisaResponseBody(row.getString("visa_response_body"));
            user.setVisaResponseStatus(row.getInteger("visa_response_status"));
            return user;
        });
    }

}
