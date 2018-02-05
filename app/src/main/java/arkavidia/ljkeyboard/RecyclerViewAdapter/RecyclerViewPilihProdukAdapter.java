package arkavidia.ljkeyboard.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.R;

/**
 * Created by axellageraldinc on 29/01/18.
 */

public class RecyclerViewPilihProdukAdapter extends RecyclerView.Adapter<RecyclerViewPilihProdukAdapter.ItemViewHolder> {

    Context context;
    List<Produk> produkList = new ArrayList<>();
    List<String> namaProdukYangDibeliList = new ArrayList<>();
    List<Integer> hargaProdukYangDibeliList = new ArrayList<>();
    List<Integer> qtyProdukYangDibeliList = new ArrayList<>();
    List<String> idProdukYangDibeliList = new ArrayList<>();
    List<Integer> stockTersediaProdukYangDibeliList = new ArrayList<>();
    boolean isRekapPesanan;

    public RecyclerViewPilihProdukAdapter(Context context, List<Produk> produkList, boolean isRekapPesanan) {
        this.context = context;
        this.produkList = produkList;
        this.isRekapPesanan = isRekapPesanan;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_pilih_produk_adapter, parent, false);
        RecyclerViewPilihProdukAdapter.ItemViewHolder itemViewHolder = new RecyclerViewPilihProdukAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if(isRekapPesanan){
            holder.numberPickerQtyProduk.setMaxValue(1000);
        } else{
            holder.numberPickerQtyProduk.setMaxValue(produkList.get(position).getStockProduk());
        }
        String namaProduk = produkList.get(position).getNamaProduk();
        if(produkList.get(position).getStockProduk() != 0){
            holder.txtNamaProduk.setText(namaProduk);
            holder.numberPickerQtyProduk.setEnabled(true);
        } else {
            if(!isRekapPesanan) {
                namaProduk += "(stok habis)";
            }
            holder.txtNamaProduk.setText(namaProduk);
            holder.numberPickerQtyProduk.setEnabled(false);
        }
        holder.numberPickerQtyProduk.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String idProdukYangDibeli = produkList.get(holder.getAdapterPosition()).getId();
                String namaProdukYangDibeli = produkList.get(holder.getAdapterPosition()).getNamaProduk();
                int qtyProdukYangDibeli = holder.numberPickerQtyProduk.getValue();
                int hargaProdukYangDibeli = produkList.get(holder.getAdapterPosition()).getHargaProduk();
                if(!namaProdukYangDibeliList.contains(namaProdukYangDibeli)){
                    idProdukYangDibeliList.add(idProdukYangDibeli);
                    namaProdukYangDibeliList.add(namaProdukYangDibeli);
                    hargaProdukYangDibeliList.add(hargaProdukYangDibeli*qtyProdukYangDibeli);
                    qtyProdukYangDibeliList.add(qtyProdukYangDibeli);
                    stockTersediaProdukYangDibeliList.add(produkList.get(holder.getAdapterPosition()).getStockProduk());
                } else{
                    int indexNamaProdukYangDiupdateQtyNya = namaProdukYangDibeliList.indexOf(namaProdukYangDibeli);
                    if(qtyProdukYangDibeli == 0){
                        idProdukYangDibeliList.remove(indexNamaProdukYangDiupdateQtyNya);
                        qtyProdukYangDibeliList.remove(indexNamaProdukYangDiupdateQtyNya);
                        hargaProdukYangDibeliList.remove(indexNamaProdukYangDiupdateQtyNya);
                        namaProdukYangDibeliList.remove(indexNamaProdukYangDiupdateQtyNya);
                    } else {
                        hargaProdukYangDibeliList.set(indexNamaProdukYangDiupdateQtyNya, hargaProdukYangDibeli * qtyProdukYangDibeli);
                        qtyProdukYangDibeliList.set(indexNamaProdukYangDiupdateQtyNya, qtyProdukYangDibeli);
                    }
                }
            }
        });
    }

    public List<String> getIdProdukYangDibeliList(){
        return this.idProdukYangDibeliList;
    }
    public void clearIdProdukYangDibeliList(){
        this.idProdukYangDibeliList.clear();
    }
    public List<String> getNamaProdukYangDibeliList(){
        return this.namaProdukYangDibeliList;
    }
    public void clearNamaProdukYangDibeliList(){
        this.namaProdukYangDibeliList.clear();
    }
    public List<Integer> getHargaProdukYangDibeliList(){
        return this.hargaProdukYangDibeliList;
    }
    public void clearHargaProdukYangDibeliList(){
        this.hargaProdukYangDibeliList.clear();
    }
    public List<Integer> getQtyProdukYangDibeliList(){
        return this.qtyProdukYangDibeliList;
    }
    public void clearQtyProdukYangDibeliList(){
        this.qtyProdukYangDibeliList.clear();
    }
    public List<Integer> getStockTersediaProdukYangDibeliList(){
        return this.stockTersediaProdukYangDibeliList;
    }
    public void clearStockTersediaProdukYangDibeliList(){
        this.stockTersediaProdukYangDibeliList.clear();
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView txtNamaProduk;
        NumberPicker numberPickerQtyProduk;
        public ItemViewHolder(final View itemView) {
            super(itemView);
            txtNamaProduk = itemView.findViewById(R.id.txtNamaProduk);
            numberPickerQtyProduk = itemView.findViewById(R.id.numberPickerQtyProduk);
        }
    }

}
