package arkavidia.ljkeyboard.Model.Firebase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 05/02/18.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spreadsheet {
    private String spreadsheetId, spreadsheetUrl;
}
