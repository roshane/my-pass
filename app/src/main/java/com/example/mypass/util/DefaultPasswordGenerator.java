package com.example.mypass.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

import java.util.Arrays;
import java.util.List;

public class DefaultPasswordGenerator {

    private int passwordLength = 16;

    private static final PasswordGenerator defaultGenerator = new PasswordGenerator();

    private static final List<CharacterRule> defaultCharacter = Arrays.asList(
            new CharacterRule(EnglishCharacterData.UpperCase, 5),
            new CharacterRule(EnglishCharacterData.LowerCase, 2),
            new CharacterRule(EnglishCharacterData.Digit, 5),
            new CharacterRule(EnglishCharacterData.Alphabetical, 4)
    );

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    public String generatePassword() {
        return defaultGenerator.generatePassword(passwordLength, defaultCharacter);
    }
}
