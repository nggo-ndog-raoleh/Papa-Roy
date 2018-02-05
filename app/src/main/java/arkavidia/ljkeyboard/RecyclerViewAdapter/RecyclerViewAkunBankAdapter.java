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
import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import arkavidia.ljkeyboard.Model.Firebase.AkunBank;
import arkavidia.ljkeyboard.R;

/**
 * Created by axellageraldinc on 02/02/18.
 */

public class RecyclerViewAkunBankAdapter extends RecyclerView.Adapter<RecyclerViewAkunBankAdapter.ItemViewHolder> {

    private Context context;
    private List<AkunBank> akunBankList = new ArrayList<>();

    public RecyclerViewAkunBankAdapter(List<AkunBank> akunBankList, Context context) {
        this.context = context;
        this.akunBankList = akunBankList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_akun_bank_adapter, parent, false);
        RecyclerViewAkunBankAdapter.ItemViewHolder itemViewHolder = new RecyclerViewAkunBankAdapter.ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.txtIdAkunBank.setText(akunBankList.get(position).getId());
        holder.txtNamaBank.setText(akunBankList.get(position).getNamaBank());
        holder.txtNamaPemilikRekening.setText(akunBankList.get(position).getNamaPemilikRekening());
        holder.txtNomorRekening.setText(akunBankList.get(position).getNomorRekening());
    }

    @Override
    public int getItemCount() {
        return akunBankList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private static final String INFORMASI_TOKO = "informasi-toko";
        private static final String AKUN_BANK = "akunBank";

        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;
        private FirebaseUser user;

        TextView txtIdAkunBank, txtNamaBank, txtNamaPemilikRekening, txtNomorRekening, txtIdAkunBankDialog;
        Dialog dialog;
        EditText inputNamaBank, inputNamaPemilikRekening, inputNomorRekening;
        Button btnSimpanAkunBank;
        public ItemViewHolder(final View itemView) {
            super(itemView);
            databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            dialog = new Dialog(itemView.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_akun_bank);
            txtIdAkunBank = itemView.findViewById(R.id.txtIdAkunBank);
            txtNamaBank = itemView.findViewById(R.id.txtNamaBank);
            txtNamaPemilikRekening = itemView.findViewById(R.id.txtNamaPemilikRekening);
            txtNomorRekening = itemView.findViewById(R.id.txtRekeningBank);
            inputNamaBank = dialog.findViewById(R.id.inputNamaBank);
            inputNamaPemilikRekening = dialog.findViewById(R.id.inputNamaPemilikRekening);
            inputNomorRekening = dialog.findViewById(R.id.inputNomorRekening);
            txtIdAkunBankDialog = dialog.findViewById(R.id.txtIdAkunBankDialog);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtIdAkunBankDialog.setText(txtIdAkunBank.getText().toString());
                    inputNamaBank.setText(txtNamaBank.getText().toString());
                    inputNamaPemilikRekening.setText(txtNamaPemilikRekening.getText().toString());
                    inputNomorRekening.setText(txtNomorRekening.getText().toString());
                    dialog.show();
                }
            });
            btnSimpanAkunBank = dialog.findViewById(R.id.btnSimpanAkunBankDialog);
            btnSimpanAkunBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAkunBankToFirebase();
                }
            });
        }
        private void saveAkunBankToFirebase(){
            AkunBank akunBankToBeSaved = AkunBank.builder()
                    .id(txtIdAkunBankDialog.getText().toString())
                    .namaBank(inputNamaBank.getText().toString())
                    .namaPemilikRekening(inputNamaPemilikRekening.getText().toString())
                    .nomorRekening(inputNomorRekening.getText().toString())
                    .build();
            databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(AKUN_BANK).child(txtIdAkunBankDialog.getText().toString()).setValue(akunBankToBeSaved);
            dialog.dismiss();
        }
    }

}
