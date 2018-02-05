package arkavidia.ljkeyboard.Model.Firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 08/12/17.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateChat {
    private String id, templateTitle, templateContent;
    private String namaCustomer;
    private InformasiToko informasiToko;
}
