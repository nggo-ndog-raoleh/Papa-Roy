package arkavidia.ljkeyboard.SpinnerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import arkavidia.ljkeyboard.R;

/**
 * Created by axellageraldinc on 02/02/18.
 */

public class SpinnerCourierAdapter extends BaseAdapter {

    Context context;
    List<String> courierList = new ArrayList<>();
    LayoutInflater layoutInflater;

    public SpinnerCourierAdapter(Context context, List<String> courierList) {
        this.context = context;
        this.courierList = courierList;
        layoutInflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return courierList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.spinner_courier_adapter, null);
//        TextView txtCourier = convertView.findViewById(R.id.txtCourier);
//        txtCourier.setText(courierList.get(position));
        return convertView;
    }
}
