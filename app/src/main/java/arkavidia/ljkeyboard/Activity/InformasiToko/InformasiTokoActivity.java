package arkavidia.ljkeyboard.Activity.InformasiToko;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import arkavidia.ljkeyboard.R;

public class InformasiTokoActivity extends AppCompatActivity {

    private static final String TAG = "InformasiTokoActivity";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String NAMA_TOKO = "namaToko";

    private Toolbar toolbar;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private CardView cardViewNamaToko, cardViewAkunBank, cardViewListProduk;
    private Dialog dialogNamaToko, dialogAkunBank;
    private EditText txtNamaToko;
    private Button btnSimpanNamaToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi_toko);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialogNamaToko = new Dialog(InformasiTokoActivity.this);
        dialogNamaToko.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNamaToko.setContentView(R.layout.dialog_nama_toko);
        txtNamaToko = dialogNamaToko.findViewById(R.id.txtNamaToko);
        btnSimpanNamaToko = dialogNamaToko.findViewById(R.id.btnSimpanNamaToko);
        btnSimpanNamaToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanNamaTokoKeFirebase();
            }
        });

        cardViewNamaToko = findViewById(R.id.cardViewNamaToko);
        cardViewNamaToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNamaToko();
            }
        });

        cardViewAkunBank = findViewById(R.id.cardViewAkunBank);
        cardViewAkunBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AkunBankActivity.class);
                startActivity(intent);
            }
        });

        cardViewListProduk = findViewById(R.id.cardViewListProduk);
        cardViewListProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListProdukActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDialogNamaToko(){
        dialogNamaToko.show();
        getNamaTokoFromFirebase();
    }
    private void getNamaTokoFromFirebase(){
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(NAMA_TOKO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String namaToko = dataSnapshot.getValue(String.class);
                if(namaToko != null){
                    txtNamaToko.setText(namaToko);
                    txtNamaToko.setSelection(txtNamaToko.getText().length());
                } else {
                    Toast.makeText(InformasiTokoActivity.this, "Belum ada nama toko, silakan isi nama toko", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void simpanNamaTokoKeFirebase(){
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(NAMA_TOKO).setValue(txtNamaToko.getText().toString()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(InformasiTokoActivity.this, "Nama toko berhasil disimpan ke database!", Toast.LENGTH_SHORT).show();
                dialogNamaToko.dismiss();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InformasiTokoActivity.this, "Nama toko gagal disimpan ke database : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
