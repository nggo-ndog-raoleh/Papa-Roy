package arkavidia.ljkeyboard;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by axellageraldinc on 02/02/18.
 */

public class JaldiBoldText extends TextView {

    public JaldiBoldText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/jaldi_bold.ttf"));
    }

}
