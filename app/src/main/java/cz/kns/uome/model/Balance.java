package cz.kns.uome.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;

import com.google.common.base.Objects;

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

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("person", person)
				.add("amount", amount)
				.toString();
	}
}
