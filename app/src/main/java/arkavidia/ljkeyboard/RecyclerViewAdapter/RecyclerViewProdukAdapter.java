package arkavidia.ljkeyboard.RecyclerViewAdapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.R;

/**
 * Created by axellageraldinc on 02/02/18.
 */

public class RecyclerViewProdukAdapter extends RecyclerView.Adapter<RecyclerViewProdukAdapter.ItemViewHolder> {

    private List<Produk> produkList = new ArrayList<>();
    private Context context;

    public RecyclerViewProdukAdapter(List<Produk> produkList, Context context) {
        this.produkList = produkList;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_produk_adapter, parent, false);
        RecyclerViewProdukAdapter.ItemViewHolder itemViewHolder = new RecyclerViewProdukAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.txtIdProduk.setText(produkList.get(position).getId());
        holder.txtNamaProduk.setText(produkList.get(position).getNamaProduk());
        holder.txtHargaProduk.setText(String.valueOf(produkList.get(position).getHargaProduk()));
        holder.txtStockProduk.setText(String.valueOf(produkList.get(position).getStockProduk()));
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private static final String INFORMASI_TOKO = "informasi-toko";
        private static final String PRODUK = "produk";

        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;
        private FirebaseUser user;

        TextView txtIdProduk, txtNamaProduk, txtHargaProduk, txtStockProduk, txtIdProdukDialog;
        Dialog dialog;
        EditText inputNamaProduk, inputHargaProduk, inputStockProduk;
        Button btnSimpanProduk;
        public ItemViewHolder(final View itemView) {
            super(itemView);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            dialog = new Dialog(itemView.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_produk_toko);
            txtIdProduk = itemView.findViewById(R.id.txtIdProduk);
            txtNamaProduk = itemView.findViewById(R.id.txtNamaProduk);
            txtHargaProduk = itemView.findViewById(R.id.txtHargaProduk);
            txtStockProduk = itemView.findViewById(R.id.txtStockProduk);
            inputNamaProduk = dialog.findViewById(R.id.inputNamaProduk);
            inputHargaProduk = dialog.findViewById(R.id.inputHargaProduk);
            inputStockProduk = dialog.findViewById(R.id.inputStockProduk);
            txtIdProdukDialog = dialog.findViewById(R.id.txtIdProdukDialog);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtIdProdukDialog.setText(txtIdProduk.getText().toString());
                    inputNamaProduk.setText(txtNamaProduk.getText().toString());
                    inputHargaProduk.setText(txtHargaProduk.getText().toString());
                    inputStockProduk.setText(txtStockProduk.getText().toString());
                    dialog.show();
                }
            });
            btnSimpanProduk = dialog.findViewById(R.id.btnSimpanProdukDialog);
            btnSimpanProduk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProdukToFirebase();
                }
            });
        }
        private void saveProdukToFirebase(){
            Produk produkToBeSaved = Produk.builder()
                    .id(txtIdProdukDialog.getText().toString())
                    .namaProduk(inputNamaProduk.getText().toString())
                    .hargaProduk(Integer.parseInt(inputHargaProduk.getText().toString()))
                    .stockProduk(Integer.parseInt(inputStockProduk.getText().toString()))
                    .build();
            databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK).child(txtIdProdukDialog.getText().toString()).setValue(produkToBeSaved);
            dialog.dismiss();
        }
    }

}
