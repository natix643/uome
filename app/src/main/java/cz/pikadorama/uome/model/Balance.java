package cz.pikadorama.uome.model;

import java.math.BigDecimal;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

public class Balance {

    private final Person person;
    private final BigDecimal amount;
    private final Date lastSettleDate;

    public Balance(Person person, BigDecimal amount) {
        this(person, amount, new Date(0L));
    }

    public Balance(Person person, BigDecimal amount, Date lastSettleDate) {
        this.person = checkNotNull(person);
        this.amount = checkNotNull(amount);
        this.lastSettleDate = checkNotNull(lastSettleDate);
    }

    public Person getPerson() {
        return person;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getLastSettleDate() {
        return lastSettleDate;
    }

    public Long getGroupId() {
        return person.getGroupId();
    }

    public static Balance cloneWithAbsoluteAmount(Balance balance) {
        return new Balance(balance.getPerson(), balance.getAmount().abs(), balance.getLastSettleDate());
    }
}
