package arkavidia.ljkeyboard.Model.Firebase;

import java.math.BigInteger;

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
public class Produk {
    private String id, namaProduk;
    private Integer hargaProduk, stockProduk;
}
