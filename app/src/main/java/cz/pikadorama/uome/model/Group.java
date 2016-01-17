package cz.pikadorama.uome.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import cz.pikadorama.uome.common.Constants;

import static cz.pikadorama.uome.model.Group.Column.*;

public class Group implements Entity {

    static final String TABLE_NAME = "group_table";

    static class Column {
        static final String ID = "_id";
        static final String NAME = "name";
        static final String DESCRIPTION = "description";

        private static final List<String> ALL = ImmutableList.of(ID, NAME, DESCRIPTION);

        static int indexOf(String column) {
            return ALL.indexOf(column);
        }
    }

    static class Sql {
        static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + NAME + " text not null unique collate nocase, "
                + DESCRIPTION + " text"
                + ");";

        static final String INSERT_SIMPLE_DEBTS_GROUP = "insert into " + TABLE_NAME + " ("
                + ID + ", " + NAME + ", " + DESCRIPTION
                + ") values (" + Constants.SIMPLE_GROUP_ID + ", 'Simple Debts', '');";
    }

    private Long id;
    private String name;
    private String description;

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Group(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Group)) {
            return false;
        }
        Group other = (Group) object;
        return Objects.equal(id, other.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .toString();
    }

}
