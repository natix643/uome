package cz.pikadorama.uome.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static cz.pikadorama.uome.model.Person.Column.*;

public class Person implements Entity {

    static final String TABLE_NAME = "person_table";
    static final String TEMP_TABLE_NAME = "person_table_temp";

    static class Column {
        static final String ID = "_id";
        static final String GROUP_ID = "groupId";
        static final String NAME = "name";
        static final String EMAIL = "email";
        static final String IMAGE_URI = "imageUri";
        static final String DESCRIPTION = "description";

        private static final List<String> ALL = ImmutableList.of(ID, GROUP_ID, NAME, EMAIL, IMAGE_URI, DESCRIPTION);

        static int indexOf(String column) {
            return ALL.indexOf(column);
        }
    }

    static class Sql {
        static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + GROUP_ID + " integer not null, "
                + NAME + " text not null collate nocase, "
                + EMAIL + " text, "
                + IMAGE_URI + " text, "
                + DESCRIPTION + " text, "
                + "foreign key(" + GROUP_ID + ") references " + Group.TABLE_NAME + "(" + Group.Column.ID
                + ") on delete cascade"
                + ");";

        static final String ADD_EMAIL_COLUMN = "alter table " + TABLE_NAME + " add column " + EMAIL + " text;";
        static final String ADD_IMAGE_URI_COLUMN = "alter table " + TABLE_NAME + " add column " + IMAGE_URI + " text;";
        static final String RENAME_TABLE_TO_TEMP = "alter table " + TABLE_NAME + " rename to " + TEMP_TABLE_NAME + ";";
        static final String COPY_FROM_TEMP_TABLE = "insert into " + TABLE_NAME
                + " select * from " + TEMP_TABLE_NAME + ";";
        static final String DROP_TEMP_TABLE = "drop table " + TEMP_TABLE_NAME + ";";
    }

    private Long id;
    private long groupId;
    private String name;
    private String description;
    private String email;
    private String imageUri;

    public Person(long groupId, String name, String email, String imageUri, String description) {
        this.groupId = groupId;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
        this.description = description;
    }

    public Person(Long id, long groupId, String name, String email, String imageUri, String description) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
        this.description = description;
    }

    @Override
    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        return Objects.equal(id, other.id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("groupId", groupId)
                .add("name", name)
                .add("description", description)
                .add("email", email)
                .add("imageUri", imageUri)
                .toString();
    }

}
