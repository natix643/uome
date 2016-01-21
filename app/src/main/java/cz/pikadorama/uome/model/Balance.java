package cz.pikadorama.uome.model;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class Balance {

    private final Person person;
    private final BigDecimal amount;

    public Balance(Person person, BigDecimal amount) {
        this.person = checkNotNull(person);
        this.amount = checkNotNull(amount);
    }

    public Person getPerson() {
        return person;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getGroupId() {
        return person.getGroupId();
    }

}
