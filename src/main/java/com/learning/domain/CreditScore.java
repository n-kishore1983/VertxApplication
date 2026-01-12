package com.learning.domain;

import java.io.Serializable;
import java.util.Objects;

public class CreditScore implements Serializable {
    private String customerId;
    private int score;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditScore that = (CreditScore) o;
        return score == that.score && Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, score);
    }
}
