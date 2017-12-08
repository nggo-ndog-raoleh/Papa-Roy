package arkavidia.ljkeyboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import arkavidia.ljkeyboard.Model.Template;

public class AddTemplate extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    EditText txtJudulTemplate, txtIsiTemplate;
    Button btnSimpan;
    Spinner spinnerTipeTemplate;
    ArrayAdapter<String> dataAdapter;

    Intent intent;
    String idTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        txtJudulTemplate = findViewById(R.id.txtJudul);
        txtIsiTemplate = findViewById(R.id.txtIsiTemplate);
        btnSimpan = findViewById(R.id.btnSave);
        spinnerTipeTemplate = findViewById(R.id.spinnerTipe);

        intent = getIntent();
        idTemplate = intent.getStringExtra("idTemplate");
        if(idTemplate!=null){
            databaseReference.child("format").child(user.getUid()).child(idTemplate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    txtJudulTemplate.setText(dataSnapshot.getValue(Template.class).getJudulTemplate());
                    txtIsiTemplate.setText(dataSnapshot.getValue(Template.class).getIsiTemplate());
                    PopulateSpinnerTipeTemplateList();
                    spinnerTipeTemplate.setSelection(dataAdapter.getPosition(dataSnapshot.getValue(Template.class).getTipeTemplate()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else{
            PopulateSpinnerTipeTemplateList();
        }
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(idTemplate==null) {
                    if (!txtJudulTemplate.getText().toString().isEmpty() || txtIsiTemplate.getText().toString().isEmpty()) {
                        SimpanTemplate(txtJudulTemplate.getText().toString(), txtIsiTemplate.getText().toString());
                    } else {
                        Toast.makeText(AddTemplate.this, "Isi semua field yang ada!", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    if (!txtJudulTemplate.getText().toString().isEmpty() || txtIsiTemplate.getText().toString().isEmpty()) {
                        UpdateTemplate(idTemplate, txtJudulTemplate.getText().toString(), txtIsiTemplate.getText().toString());
                    } else {
                        Toast.makeText(AddTemplate.this, "Isi semua field yang ada!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void PopulateSpinnerTipeTemplateList(){
        List<String> tipeTemplateList = new ArrayList<>();
        tipeTemplateList.add("pemesanan");
        tipeTemplateList.add("pembayaran");
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(AddTemplate.this, android.R.layout.simple_spinner_item, tipeTemplateList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerTipeTemplate.setAdapter(dataAdapter);
    }

    private void UpdateTemplate(String id, String judul, String isi){
        Template template = new Template(id, judul, isi, spinnerTipeTemplate.getSelectedItem().toString());
        databaseReference.child("format").child(user.getUid()).child(id).setValue(template).addOnSuccessListener(AddTemplate.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddTemplate.this, "Berhasil update template!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddTemplate.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void SimpanTemplate(String judul, String isi){
        UUID idTemplate = UUID.randomUUID();
        Template template = new Template(idTemplate.toString(), judul, isi, spinnerTipeTemplate.getSelectedItem().toString());
        databaseReference.child("format").child(user.getUid()).child(idTemplate.toString()).setValue(template).addOnSuccessListener(AddTemplate.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddTemplate.this, "Sukses menambahkan template!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
