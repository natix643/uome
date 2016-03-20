package cz.pikadorama.uome.model;

import java.io.Serializable;
import java.util.Date;

import cz.pikadorama.uome.model.Transaction.Direction;

public class TransactionData implements Serializable {

    public static final String KEY = TransactionData.class.getName();

    private Long id;
    private Long personId;
    private Long groupId;
    private String value;
    private Boolean financial;
    private Direction direction;
    private String description;
    private Date dateTime;

    public static TransactionData from(Transaction transaction) {
        return new TransactionData(
                transaction.getId(),
                transaction.getPersonId(),
                transaction.getGroupId(),
                transaction.getValue(),
                transaction.isFinancial(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getDateTime());
    }

    public TransactionData() {}

    private TransactionData(
            Long id,
            Long personId,
            Long groupId,
            String value,
            Boolean financial,
            Direction direction,
            String description,
            Date dateTime) {
        this.id = id;
        this.personId = personId;
        this.groupId = groupId;
        this.value = value;
        this.financial = financial;
        this.direction = direction;
        this.description = description;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isFinancial() {
        return financial;
    }

    public void setFinancial(Boolean financial) {
        this.financial = financial;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

}
