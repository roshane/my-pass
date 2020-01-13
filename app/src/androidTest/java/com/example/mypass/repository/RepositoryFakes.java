package com.example.mypass.repository;

import com.example.mypass.model.Password;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class RepositoryFakes {

    private static final String FakeTitle1 = "XXX@google.com";
    static final String FakeTitle2 = "YYY@google.com";
    static final String FakeNote1 = "XXX's google account password";
    static final String FakeNote2 = "XXX's google account password";
    static final String FakePasswordString = "2fUtznu549ZWxGTW";
    static final String FakeWebsite = "http://www.google.com";
    static final LocalDateTime FakeCreatedAt = LocalDateTime.of(LocalDate.of(2019, 10, 10), LocalTime.of(10, 10, 10));
    static final boolean FakeIsActive = true;

    static final Password FakePassword1 = new Password(FakeTitle1, FakeTitle1, FakePasswordString, FakeWebsite, FakeNote1, FakeCreatedAt, FakeIsActive);
    static final Password FakePassword2 = new Password(FakeTitle2, FakeTitle2, FakePasswordString, FakeWebsite, FakeNote2, FakeCreatedAt, FakeIsActive);
}
