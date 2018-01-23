package arkavidia.ljkeyboard.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import arkavidia.ljkeyboard.Model.CityModel;

/**
 * Created by axellageraldinc on 07/12/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "city_db";
    private static final String TABLE_NAME = "city";
    private static final String city_id = "city_id";
    private static final String city_name = "city_name";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query_create_table_city = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                city_id + " INTEGER PRIMARY KEY, " +
                city_name + " TEXT" + ")";
        sqLiteDatabase.execSQL(query_create_table_city);
//        String query_create_index = "CREATE INDEX ON " + TABLE_NAME;
//        sqLiteDatabase.execSQL(query_create_index);
    }

    public void AddCity(CityModel cityModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(city_id, cityModel.getCity_id());
        cv.put(city_name, cityModel.getCity_name());
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public HashMap<String, String> GetAllCity(String kotaAsal, String kotaTujuan){
        HashMap<String, String> param = new HashMap<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(1).toUpperCase().equals(kotaAsal.toUpperCase())){
                    param.put("kotaAsal", cursor.getString(0));
                } if(cursor.getString(1).toUpperCase().equals(kotaTujuan.toUpperCase())){
                    param.put("kotaTujuan", cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return param;
    }

//    public int getKotaAsal(String kotaAsal){
//        int idKotaAsal=0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[] {city_id}, city_name + "=?", new String[] {kotaAsal}, null, null, null, null);
//        if(cursor!=null){
//            cursor.moveToFirst();
//        }
//        idKotaAsal = Integer.parseInt(cursor.getString(0));
//        return idKotaAsal;
//    }
//    public int getKotaTujuan(String kotaTujuan){
//        int idKotaTujuan=0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[] {city_id}, city_name + "=?", new String[] {kotaTujuan}, null, null, null, null);
//        if(cursor!=null){
//            cursor.moveToFirst();
//        }
//        idKotaTujuan = Integer.parseInt(cursor.getString(0));
//        return idKotaTujuan;
//    }

    public int countAllData(){
        int count=0;
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count=cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
