package arkavidia.ljkeyboard.Activity.InformasiToko;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import arkavidia.ljkeyboard.Model.Firebase.AkunBank;
import arkavidia.ljkeyboard.R;
import arkavidia.ljkeyboard.RecyclerViewAdapter.RecyclerViewAkunBankAdapter;

public class AkunBankActivity extends AppCompatActivity {

    private static final String TAG = "AkunBankActivity";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String AKUN_BANK = "akunBank";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private RecyclerView recyclerViewAkunBank;
    private RecyclerViewAkunBankAdapter recyclerViewAkunBankAdapter;
    private FloatingActionButton btnAddAkunBank;
    private List<AkunBank> akunBankList = new ArrayList<>();

    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    EditText inputNamaBank, inputNamaPemilikRekening, inputNomorRekening;
    Button btnSimpanAkunBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_bank);

        progressDialog = new ProgressDialog(AkunBankActivity.this);

        dialog = new Dialog(AkunBankActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_akun_bank);
        inputNamaBank = dialog.findViewById(R.id.inputNamaBank);
        inputNamaPemilikRekening = dialog.findViewById(R.id.inputNamaPemilikRekening);
        inputNomorRekening = dialog.findViewById(R.id.inputNomorRekening);
        btnSimpanAkunBank = dialog.findViewById(R.id.btnSimpanAkunBankDialog);
        btnSimpanAkunBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAkunBankToFirebase();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerViewAkunBank = findViewById(R.id.recyclerViewListAkunBank);
        List<AkunBank> akunBankList = getAkunBankListFromFirebase();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAkunBank.setLayoutManager(linearLayoutManager);
        recyclerViewAkunBankAdapter = new RecyclerViewAkunBankAdapter(akunBankList, getApplicationContext());

        btnAddAkunBank = findViewById(R.id.btnAddAkunBank);
        btnAddAkunBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputAkunBankDialog();
            }
        });
    }

    private List<AkunBank> getAkunBankListFromFirebase(){
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Retrieving Akun Bank from database...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(AKUN_BANK).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                akunBankList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()
                     ) {
                    AkunBank akunBank = data.getValue(AkunBank.class);
                    akunBankList.add(akunBank);
                }
                recyclerViewAkunBank.setAdapter(recyclerViewAkunBankAdapter);
                recyclerViewAkunBankAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getCode() + ", " + databaseError.getMessage() + ", " + databaseError.getDetails());
                Toast.makeText(AkunBankActivity.this, "databaseError.getCode() + \", \" + databaseError.getMessage() + \", \" + databaseError.getDetails()", Toast.LENGTH_SHORT).show();
            }
        });
        return akunBankList;
    }

    private void showInputAkunBankDialog(){
        inputNamaBank.setText("");
        inputNamaBank.requestFocus();
        inputNamaPemilikRekening.setText("");
        inputNomorRekening.setText("");
        dialog.show();
    }

    private void saveAkunBankToFirebase(){
        String id = UUID.randomUUID().toString();
        AkunBank akunBankToBeSaved = AkunBank.builder()
                .id(id)
                .namaBank(inputNamaBank.getText().toString())
                .namaPemilikRekening(inputNamaPemilikRekening.getText().toString())
                .nomorRekening(inputNomorRekening.getText().toString())
                .build();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(AKUN_BANK).child(id).setValue(akunBankToBeSaved);
        dialog.dismiss();
    }

}
