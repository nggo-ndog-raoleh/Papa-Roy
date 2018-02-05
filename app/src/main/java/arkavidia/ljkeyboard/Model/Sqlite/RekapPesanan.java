package arkavidia.ljkeyboard.Model.Sqlite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 01/02/18.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RekapPesanan {
    private String spreadsheetUrl, spreadsheetId;
    private String kurirLogistik;
    private Customer customer;
    private Integer totalHargaProduk;
    private String produk;
    private Integer quantity;
    private String bank;
    private Integer ongkir;
}
