package arkavidia.ljkeyboard.Model;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 30/01/18.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdukYangDibeli {
    private String idProduk;
    private String namaProduk;
    private int qtyProduk, stockProduk;
    private Integer hargaProduk;
}
