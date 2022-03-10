package ru.craftysoft.registrar.service.dao;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import ru.craftysoft.registrar.builder.UserBuilder;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;
import ru.craftysoft.registrar.util.DbClient;
import ru.tsc.crm.customer.model.jooq.enums.Statuses;
import ru.tsc.crm.customer.model.jooq.tables.records.UsersRecord;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static ru.craftysoft.registrar.rest.model.UsersFilterStatus.ALL;
import static ru.tsc.crm.customer.model.jooq.Tables.USERS;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class UserDao {

    private final DbClient dbClient;
    private final DSLContext dslContext;
    private final UserBuilder userBuilder;

    public Uni<Long> create(UsersCreateRequestData usersCreateRequestData) {
        var user = userBuilder.buildCreated(usersCreateRequestData);
        var query = dslContext.insertInto(USERS)
                .set(user)
                .returning(USERS.ID);
        return dbClient.toUni(log, "UserDao.create", query, row -> row.getLong("id"));
    }

    public Uni<List<UsersRecord>> filter(UsersFilterStatus status) {
        var query = dslContext
                .select(
                        USERS.ID,
                        USERS.FIRST_NAME,
                        USERS.LAST_NAME,
                        USERS.MIDDLE_NAME,
                        USERS.BIRTH_DATE,
                        USERS.CITIZENSHIP,
                        USERS.EMAIL,
                        USERS.PASSPORT,
                        USERS.PASSPORT_EXPIRY_DATE,
                        USERS.CREATED_AT,
                        USERS.COMPLETED_AT,
                        USERS.REJECTED_AT,
                        USERS.STATUS,
                        USERS.VISA_REQUEST_DATE_FROM,
                        USERS.VISA_REQUEST_DATE_TO,
                        USERS.VISA_REQUEST_CITIES,
                        USERS.VISA_RESPONSE_BODY,
                        USERS.VISA_RESPONSE_STATUS
                )
                .from(USERS);
        if (!ALL.equals(status)) {
            var registrarStatus = Statuses.lookupLiteral(status.toString());
            query.where(USERS.STATUS.eq(registrarStatus));
        }
        return dbClient.toUniOfList(log, "UserDao.filter", query, row -> {
            var user = new UsersRecord();
            user.setId(row.getLong(USERS.ID.getName()));
            user.setFirstName(row.getString(USERS.FIRST_NAME.getName()));
            user.setLastName(row.getString(USERS.LAST_NAME.getName()));
            user.setMiddleName(row.getString(USERS.MIDDLE_NAME.getName()));
            user.setBirthDate(row.getLocalDate(USERS.BIRTH_DATE.getName()));
            user.setCitizenship(row.getString(USERS.CITIZENSHIP.getName()));
            user.setEmail(row.getString(USERS.EMAIL.getName()));
            user.setPassport(row.getString(USERS.PASSPORT.getName()));
            user.setPassportExpiryDate(row.getLocalDate(USERS.PASSPORT_EXPIRY_DATE.getName()));
            user.setCreatedAt(row.getOffsetDateTime(USERS.CREATED_AT.getName()));
            user.setCompletedAt(row.getOffsetDateTime(USERS.COMPLETED_AT.getName()));
            user.setRejectedAt(row.getOffsetDateTime(USERS.REJECTED_AT.getName()));
            user.setStatus(Statuses.lookupLiteral(row.getString(USERS.STATUS.getName())));
            user.setVisaRequestDateFrom(row.getLocalDate(USERS.VISA_REQUEST_DATE_FROM.getName()));
            user.setVisaRequestDateTo(row.getLocalDate(USERS.VISA_REQUEST_DATE_TO.getName()));
            user.setVisaRequestCities(row.getArrayOfStrings(USERS.VISA_REQUEST_CITIES.getName()));
            user.setVisaResponseBody(row.getString(USERS.VISA_RESPONSE_BODY.getName()));
            user.setVisaResponseStatus(row.getInteger(USERS.VISA_RESPONSE_STATUS.getName()));
            return user;
        });
    }

}
