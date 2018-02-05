package arkavidia.ljkeyboard.Features;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by axellageraldinc on 31/01/18.
 */

public class RekapPesanan {

    private SqliteDbHelper sqliteDbHelper;
    private Context context;

    public RekapPesanan(Context context){
        this.context = context;
        sqliteDbHelper = new SqliteDbHelper(context);
    }

    /**
    * Insert rekap pesanan baru yang diinput oleh user di lj keyboard ke sqlite
    **/
    public void addNewRekapPesananToSqlite(arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan rekapPesanan){
        sqliteDbHelper.addRekapPesanan(rekapPesanan);
    }

    /**
    * Mengambil semua rekap pesanan yang ada di sqlite
    **/
    public List<arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan> getRekapPesananFromSqlite(){
        return sqliteDbHelper.getRekapPesananFromSqlite();
    }

    /**
    * Menghapus semua records yang ada di sqlite
    * Method ini dieksekusi setelah semua records yg ada di sqlite dikirim ke google spreadsheet
    **/
    public void deleteAllRecordsFromTableRekapPesananSqlite(){
        sqliteDbHelper.deleteAllRecordsFromTableRekapPesanan();
    }
}
