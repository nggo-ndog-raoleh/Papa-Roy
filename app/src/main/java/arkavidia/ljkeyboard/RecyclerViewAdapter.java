package arkavidia.ljkeyboard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import arkavidia.ljkeyboard.Model.Template;

/**
 * Created by axellageraldinc on 07/12/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {
    Context context;
    List<Template> templateList = new ArrayList<>();

    public RecyclerViewAdapter(Context context, List<Template> templateList) {
        this.context = context;
        this.templateList = templateList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_adapter, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.txtJudulTemplate.setText(templateList.get(position).getJudulTemplate());
        holder.txtIdTemplate.setText(templateList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return templateList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView txtIdTemplate;
        TextView txtJudulTemplate;
        public ItemViewHolder(final View itemView) {
            super(itemView);
            txtIdTemplate = itemView.findViewById(R.id.txtIdTemplate);
            txtJudulTemplate = itemView.findViewById(R.id.txtJudulTemplate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), AddTemplate.class);
                    intent.putExtra("idTemplate", txtIdTemplate.getText().toString());
                    System.out.println("ID TEMPLATE : " + txtIdTemplate.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
