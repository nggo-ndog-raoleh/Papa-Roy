package arkavidia.ljkeyboard.Model.Firebase;

import java.util.List;

import arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 29/01/18.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformasiToko {
    private String id, namaToko;
    private Produk produk;
    private AkunBank akunBank;
    private RekapPesanan rekapPesanan;
    private Spreadsheet spreadsheet;
}
