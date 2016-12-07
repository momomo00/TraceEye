package momomo00.traceeye;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by momomo00 on 16/12/07.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    public MyDatabaseHelper(Context context) {
        super(context, DbParameter.DATABASE_NAME, null, DbParameter.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbParameter.SqlQuery.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DbParameter.SqlQuery.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public Long insertData(String userName, Location location, Date date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbParameter.ColumnEntry.COLUMN_USER_NAME, userName);
        values.put(DbParameter.ColumnEntry.COLUMN_LATITUDE, location.getLatitude());
        values.put(DbParameter.ColumnEntry.COLUMN_LONGITUDE, location.getLongitude());

        SimpleDateFormat updateDayFormat = new SimpleDateFormat("yyyyMMdd");
        int updateDay = Integer.valueOf(updateDayFormat.format(date));
        values.put(DbParameter.ColumnEntry.COLUMN_UPDATE_DAY, updateDay);

        SimpleDateFormat updateTimeFormat = new SimpleDateFormat("HHmmss");
        int updateTime = Integer.valueOf(updateTimeFormat.format(date));
        values.put(DbParameter.ColumnEntry.COLUMN_UPDATE_TIME, updateTime);

        return db.insert(DbParameter.TABLE_NAME, null, values);
    }

    public void showData() {
        Cursor cursor = selectData();

        for(boolean isEof = cursor.moveToFirst(); isEof; isEof = cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry._ID));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry.COLUMN_USER_NAME));
            double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry.COLUMN_LONGITUDE));
            int updateDay = cursor.getInt(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry.COLUMN_UPDATE_DAY));
            int updateTime = cursor.getInt(cursor.getColumnIndexOrThrow(DbParameter.ColumnEntry.COLUMN_UPDATE_TIME));

            Log.d("TEST", "id: " + String.valueOf(id)
                    + ", userName: " + userName
                    + ", latitude: " + String.valueOf(latitude) + ", longitude: " + String.valueOf(longitude)
                    + ", updateDay: " + String.valueOf(updateDay) + ", updateTime: " + String.valueOf(updateTime));
        }

        cursor.close();
    }

    public Cursor selectData() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                DbParameter.ColumnEntry._ID,
                DbParameter.ColumnEntry.COLUMN_USER_NAME,
                DbParameter.ColumnEntry.COLUMN_LATITUDE,
                DbParameter.ColumnEntry.COLUMN_LONGITUDE,
                DbParameter.ColumnEntry.COLUMN_UPDATE_DAY,
                DbParameter.ColumnEntry.COLUMN_UPDATE_TIME
        };

        String sortOrder = DbParameter.ColumnEntry._ID + " ASC";

        return db.query(DbParameter.TABLE_NAME, projection, null, null, null, null, sortOrder);
    }

}
