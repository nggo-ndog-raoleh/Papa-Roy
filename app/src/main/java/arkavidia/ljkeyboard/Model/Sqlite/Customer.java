package arkavidia.ljkeyboard.Model.Sqlite;

import java.util.List;

import arkavidia.ljkeyboard.Model.ProdukYangDibeli;
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
public class Customer {
    private String nama, nomorTelepon;
}
