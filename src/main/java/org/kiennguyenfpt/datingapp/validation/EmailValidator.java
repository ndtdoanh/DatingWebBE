package org.kiennguyenfpt.datingapp.validation;

import java.util.regex.Pattern;

public class EmailValidator {
    /**
        Explanation of the regex pattern:
        ^                 # start-of-string
        $                 # end-of-string
        [A-Za-z0-9+_.-]+  # at least one of the following characters: A-Z, a-z, 0-9, +, _, ., -
        @                 # must contain the '@' character
        For example, the email kiennguyenfpt2711@gmail.com is valid because it contains only the characters A-Z, a-z, 0-9, +, _, ., -, and the '@' character.
     */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static ValidationResult validate(String email) {
        if (email == null) {
            return new ValidationResult(false, "Email cannot be null.");
        }
        if (!pattern.matcher(email).matches()) {
            return new ValidationResult(false, "Email must contain only allowed characters and include '@'.");
        }
        return new ValidationResult(true, "Email is valid.");
    }
}
