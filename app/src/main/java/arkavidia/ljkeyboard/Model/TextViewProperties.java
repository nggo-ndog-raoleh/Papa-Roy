package arkavidia.ljkeyboard.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by axellageraldinc on 06/02/18.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextViewProperties {
    private int color;
    private int left, top, right, bottom;
    private String text;
}
