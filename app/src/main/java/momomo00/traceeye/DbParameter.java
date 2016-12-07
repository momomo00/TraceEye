package momomo00.traceeye;

import android.provider.BaseColumns;

/**
 * Created by momomo00 on 16/12/07.
 */

public class DbParameter {
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "entry";

    public static class ColumnEntry implements BaseColumns {
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_UPDATE_DAY = "update_day";
        public static final String COLUMN_UPDATE_TIME = "update_time";
    }

    public static class SqlQuery {
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " ("
                    + ColumnEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ColumnEntry.COLUMN_USER_NAME + " TEXT, "
                    + ColumnEntry.COLUMN_LATITUDE + " REAL, "
                    + ColumnEntry.COLUMN_LONGITUDE + " REAL, "
                    + ColumnEntry.COLUMN_UPDATE_DAY + " INTEGER, "
                    + ColumnEntry.COLUMN_UPDATE_TIME + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
