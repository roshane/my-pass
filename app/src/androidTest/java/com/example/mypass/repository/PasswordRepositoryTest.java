package com.example.mypass.repository;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.example.mypass.model.Password;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.example.mypass.repository.RepositoryFakes.FakePassword1;
import static com.example.mypass.repository.RepositoryFakes.FakePassword2;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

public class PasswordRepositoryTest {

    private static final Context testContext = ApplicationProvider.getApplicationContext();
    private static String LOG_TAG = PasswordRepositoryTest.class.getName();

    private PasswordRepository createClassUnderTest() {
        return new PasswordRepository(new PasswordDbHelper(testContext));
    }

    @BeforeClass
    @AfterClass
    public static void beforeAll() throws Exception {
        Log.d(LOG_TAG, "Deleting database");
        final Field field = PasswordDbHelper.class.getDeclaredField("DATABASE_NAME");
        field.setAccessible(true);
        testContext.deleteDatabase((String) field.get(null));
    }

    @Before
    public void beforeEach() throws Exception {
        Log.d(LOG_TAG, "cleaning table");
        final PasswordRepository classUnderTest = createClassUnderTest();
        classUnderTest.deleteAll();
    }

    @Test
    public void findAllShouldBeEmpty() {
        final List<Password> passwords = createClassUnderTest().findAll();
        assertThat(passwords, emptyCollectionOf(Password.class));
    }

    @Test
    public void findAllShouldSucceed() {
        PasswordRepository classUnderTest = createClassUnderTest();
        assertThat(classUnderTest.findAll(), emptyCollectionOf(Password.class));
        classUnderTest.save(FakePassword1);
        final List<Password> passwords = classUnderTest.findAll();
        assertThat(passwords, iterableWithSize(1));
        assertThat(passwords.get(0), is(FakePassword1));
    }

    @Test
    public void findAllShouldSucceedForMultiplePassword() {
        PasswordRepository classUnderTest = createClassUnderTest();
        assertThat(classUnderTest.findAll(), emptyCollectionOf(Password.class));
        classUnderTest.save(FakePassword1);
        classUnderTest.save(FakePassword2);

        final List<Password> passwords = classUnderTest.findAll();
        assertThat(passwords, iterableWithSize(2));
        assertThat(passwords.get(0), is(FakePassword1));
        assertThat(passwords.get(1), is(FakePassword2));
    }

    @Test
    public void searchPasswordSucceeds() {
        PasswordRepository classUnderTest = createClassUnderTest();
        assertThat(classUnderTest.findAll(), emptyCollectionOf(Password.class));

        classUnderTest.save(FakePassword1);
        classUnderTest.save(FakePassword2);

        final List<Password> result = classUnderTest.searchByTitle(FakePassword1.getTitle());
        assertThat(result, iterableWithSize(1));
        assertThat(result.get(0), is(FakePassword1));
    }

    @Test
    public void deletePassword() {
        final PasswordRepository passwordRepository = createClassUnderTest();
        assertThat(passwordRepository.findAll(), emptyCollectionOf(Password.class));

        long id1 = passwordRepository.save(FakePassword1);
        passwordRepository.save(FakePassword2);

        final List<Password> allPasswords = passwordRepository.findAll();
        assertThat(allPasswords, iterableWithSize(2));
        assertThat(allPasswords.get(0), is(FakePassword1));
        assertThat(allPasswords.get(1), is(FakePassword2));

        passwordRepository.delete(id1);

        final List<Password> allAfterDelete = passwordRepository.findAll();
        assertThat(allAfterDelete, iterableWithSize(1));
        assertThat(allAfterDelete.get(0), is(FakePassword2));

    }

    @Test
    public void updatePassword() {
        final PasswordRepository repository = createClassUnderTest();
        assertThat(repository.findAll(), emptyCollectionOf(Password.class));

        repository.save(FakePassword1);
        final List<Password> passwords = repository.searchByTitle(FakePassword1.getTitle());
        assertThat(passwords.size(), is(1));
        assertThat(passwords.get(0), is(FakePassword1));

        Password updatedPassword = new Password(FakePassword1.getId(), FakePassword1.getTitle(),
                FakePassword1.getUsername(), FakePassword1.getPassword(),
                FakePassword1.getNotes(), FakePassword1.getWebsite(),
                FakePassword1.getCreateAt(), FakePassword1.isActive());
        final String newPasswordString = "ABCDEFG";
        updatedPassword.setPassword(newPasswordString);

        assertThat(repository.update(updatedPassword), is(1));
        final List<Password> allPasswords = repository.findAll();
        assertThat(allPasswords.size(), is(1));
        assertThat(allPasswords.get(0), is(updatedPassword));
    }
}