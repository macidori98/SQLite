package info.androidhive.sqlite.database.model;



public class Hobby {
    public static final String TABLE_NAME = "hobbies";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HOBBY = "hobby";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String hobby;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_HOBBY + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Hobby() {
    }

    public Hobby(int id, String hobby, String timestamp) {
        this.id = id;
        this.hobby = hobby;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
