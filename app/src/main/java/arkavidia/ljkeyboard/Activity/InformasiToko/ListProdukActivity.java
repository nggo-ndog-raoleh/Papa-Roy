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
import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.R;
import arkavidia.ljkeyboard.RecyclerViewAdapter.RecyclerViewAkunBankAdapter;
import arkavidia.ljkeyboard.RecyclerViewAdapter.RecyclerViewProdukAdapter;

public class ListProdukActivity extends AppCompatActivity {

    private static final String TAG = "ListProdukActivity";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String PRODUK = "produk";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private List<Produk> produkList = new ArrayList<>();

    private RecyclerView recyclerViewProduk;
    private RecyclerViewProdukAdapter recyclerViewProdukAdapter;
    private Toolbar toolbar;
    private FloatingActionButton btnAddProduk;

    private ProgressDialog progressDialog;
    private Dialog dialog;
    EditText inputNamaProduk, inputHargaProduk, inputStockProduk;
    Button btnSimpanProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_produk);

        dialog = new Dialog(ListProdukActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_produk_toko);
        inputNamaProduk = dialog.findViewById(R.id.inputNamaProduk);
        inputHargaProduk = dialog.findViewById(R.id.inputHargaProduk);
        inputStockProduk = dialog.findViewById(R.id.inputStockProduk);
        btnSimpanProduk = dialog.findViewById(R.id.btnSimpanProdukDialog);
        btnSimpanProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProdukToFirebase();
            }
        });

        progressDialog = new ProgressDialog(ListProdukActivity.this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewProduk = findViewById(R.id.recyclerViewListProduk);
        List<Produk> produkList = getProdukListFromFirebase();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewProduk.setLayoutManager(linearLayoutManager);
        recyclerViewProdukAdapter = new RecyclerViewProdukAdapter(produkList, getApplicationContext());

        btnAddProduk = findViewById(R.id.btnAddProduk);
        btnAddProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputAddProdukDialog();
            }
        });
    }

    private List<Produk> getProdukListFromFirebase(){
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Retrieving Produk from database...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produkList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()
                        ) {
                    Produk produk = data.getValue(Produk.class);
                    produkList.add(produk);
                }
                recyclerViewProduk.setAdapter(recyclerViewProdukAdapter);
                recyclerViewProdukAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, databaseError.getCode() + ", " + databaseError.getMessage() + ", " + databaseError.getDetails());
                Toast.makeText(ListProdukActivity.this, "databaseError.getCode() + \", \" + databaseError.getMessage() + \", \" + databaseError.getDetails()", Toast.LENGTH_SHORT).show();
            }
        });
        return produkList;
    }

    private void showInputAddProdukDialog(){
        inputNamaProduk.setText("");
        inputNamaProduk.requestFocus();
        inputHargaProduk.setText("");
        inputStockProduk.setText("");
        dialog.show();
    }

    private void saveProdukToFirebase(){
        String id = UUID.randomUUID().toString();
        Produk produk = Produk.builder()
                .id(id)
                .namaProduk(inputNamaProduk.getText().toString())
                .hargaProduk(Integer.parseInt(inputHargaProduk.getText().toString()))
                .stockProduk(Integer.parseInt(inputStockProduk.getText().toString()))
                .build();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK).child(id).setValue(produk);
        dialog.dismiss();
    }

}
