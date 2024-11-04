package org.kiennguyenfpt.datingapp.validation;

import java.util.Objects;

public final class ValidationResult {
    private final boolean valid;
    private final String message;

    ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean valid() {
        return valid;
    }

    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ValidationResult) obj;
        return this.valid == that.valid &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, message);
    }

    @Override
    public String toString() {
        return "ValidationResult[" +
                "valid=" + valid + ", " +
                "message=" + message + ']';
    }
}

