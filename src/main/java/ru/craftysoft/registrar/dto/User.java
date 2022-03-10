package ru.craftysoft.registrar.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String passport;
    private LocalDate birthDate;
    private LocalDate passportExpiryDate;
    private String citizenship;
    private String email;
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime rejectedAt;
    private LocalDate visaRequestDateFrom;
    private LocalDate visaRequestDateTo;
    private Set<String> visaRequestCities;
    private Integer visaResponseStatus;
    private String visaResponseBody;
    private Status status;

    @RequiredArgsConstructor
    public enum Status {
        NEW("new"), COMPLETED("completed"), REJECTED("rejected"),
        ;

        private final String value;

        public static Status fromString(String value) {
            for (Status status : Status.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
