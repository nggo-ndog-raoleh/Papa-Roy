package arkavidia.ljkeyboard.Model.Firebase;

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
public class AkunBank {
    private String id, namaBank, namaPemilikRekening, nomorRekening;
}
