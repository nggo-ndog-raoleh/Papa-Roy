package arkavidia.ljkeyboard.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import arkavidia.ljkeyboard.Model.Sqlite.Customer;
import arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan;
import arkavidia.ljkeyboard.Model.Sqlite.City;

/**
 * Created by axellageraldinc on 07/12/17.
 */

public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "lj_db";
    private static final String TABLE_NAME = "city";
    private static final String CITY_ID = "CITY_ID";
    private static final String CITY_NAME = "CITY_NAME";

    private static final String TABLE_REKAP_PESANAN = "rekap_pesanan";
    private static final String NAMA_CUSTOMER = "nama_customer";
    private static final String NOMOR_TELEPON = "nomor_telepon";
    private static final String LOGISTIK = "logistik";
    private static final String ONGKIR = "ongkir";
    private static final String PRODUK = "produk";
    private static final String QUANTITY = "quantity";
    private static final String HARGA = "harga";
    private static final String BANK = "bank";

    public SqliteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTableCity = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                CITY_ID + " INTEGER PRIMARY KEY, " +
                CITY_NAME + " TEXT" + ")";
        String queryCreateTableRekapPesanan = "CREATE TABLE IF NOT EXISTS " + TABLE_REKAP_PESANAN + "(" +
                NAMA_CUSTOMER + " TEXT, " +
                NOMOR_TELEPON + " TEXT, " +
                LOGISTIK + " TEXT, " +
                ONGKIR + " INTEGER, " +
                PRODUK + " TEXT, " +
                QUANTITY + " INTEGER, " +
                HARGA + " INTEGER, " +
                BANK + " TEXT" + ")";
        sqLiteDatabase.execSQL(queryCreateTableCity);
        sqLiteDatabase.execSQL(queryCreateTableRekapPesanan);
    }

    public void addCity(City city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CITY_ID, city.getId());
        cv.put(CITY_NAME, city.getCityName());
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public void addRekapPesanan(RekapPesanan rekapPesanan){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAMA_CUSTOMER, rekapPesanan.getCustomer().getNama());
        cv.put(NOMOR_TELEPON, rekapPesanan.getCustomer().getNomorTelepon());
        cv.put(LOGISTIK, rekapPesanan.getKurirLogistik());
        cv.put(ONGKIR, rekapPesanan.getOngkir());
        cv.put(PRODUK, rekapPesanan.getProduk());
        cv.put(QUANTITY, rekapPesanan.getQuantity());
        cv.put(HARGA, rekapPesanan.getTotalHargaProduk());
        cv.put(BANK, rekapPesanan.getBank());
        db.insert(TABLE_REKAP_PESANAN, null, cv);
        db.close();
    }

    public List<String> getAllCityFromSqlite(){
        List<String> cityNameList = new ArrayList<>();
        String query = "SELECT " + CITY_NAME + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String cityName = cursor.getString(0);
                cityNameList.add(cityName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cityNameList;
    }

    public HashMap<String, String> getOriginIdAndDestinationId(String origin, String destination){
        HashMap<String, String> param = new HashMap<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(1).toUpperCase().equals(origin.toUpperCase())){
                    param.put("origin", cursor.getString(0));
                } if(cursor.getString(1).toUpperCase().equals(destination.toUpperCase())){
                    param.put("destination", cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return param;
    }

    public List<RekapPesanan> getRekapPesananFromSqlite(){
        List<RekapPesanan> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_REKAP_PESANAN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                RekapPesanan rekapPesanan = RekapPesanan.builder()
                        .customer(Customer.builder()
                                    .nama(cursor.getString(0))
                                    .nomorTelepon(cursor.getString(1))
                                    .build())
                        .kurirLogistik(cursor.getString(2))
                        .ongkir(cursor.getInt(3))
                        .produk(cursor.getString(4))
                        .quantity(cursor.getInt(5))
                        .totalHargaProduk(cursor.getInt(6))
                        .bank(cursor.getString(7))
                        .build();
                list.add(rekapPesanan);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteAllRecordsFromTableRekapPesanan(){
        String query = "DELETE FROM " + TABLE_REKAP_PESANAN;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

//    public int getKotaAsal(String kotaAsal){
//        int idKotaAsal=0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[] {CITY_ID}, CITY_NAME + "=?", new String[] {kotaAsal}, null, null, null, null);
//        if(cursor!=null){
//            cursor.moveToFirst();
//        }
//        idKotaAsal = Integer.parseInt(cursor.getString(0));
//        return idKotaAsal;
//    }
//    public int getKotaTujuan(String kotaTujuan){
//        int idKotaTujuan=0;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_NAME, new String[] {CITY_ID}, CITY_NAME + "=?", new String[] {kotaTujuan}, null, null, null, null);
//        if(cursor!=null){
//            cursor.moveToFirst();
//        }
//        idKotaTujuan = Integer.parseInt(cursor.getString(0));
//        return idKotaTujuan;
//    }

    public int countDataTableCity(){
        int count=0;
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count=cursor.getCount();
        cursor.close();
        db.close();
        // return count
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REKAP_PESANAN);
        onCreate(sqLiteDatabase);
    }
}
