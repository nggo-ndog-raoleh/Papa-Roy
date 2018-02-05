package arkavidia.ljkeyboard.Activity.TemplateChat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;
import arkavidia.ljkeyboard.R;

public class EditTemplateChatActivity extends AppCompatActivity {

    private static final String TAG = "EditTemplateChat.class";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String TEMPLATE_CONTENT = "templateContent";

    private Toolbar toolbar;
    private Intent intent;

    private TextView txtTemplateTitle;
    private EditText txtTemplateChat;
    private Button btnSimpanTemplateChat;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private String jenisTemplate="", judulTemplate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template_chat);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(EditTemplateChatActivity.this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtTemplateChat = findViewById(R.id.txtTemplateChat);
        txtTemplateTitle = findViewById(R.id.txtTemplateTitle);
        btnSimpanTemplateChat = findViewById(R.id.btnSimpanTemplateChat);

        intent = getIntent();
        if(intent.getStringExtra("judul_template") != null ||
                !intent.getStringExtra("judul_template").isEmpty()){
            judulTemplate = "Template " + intent.getStringExtra("judul_template");
            jenisTemplate = intent.getStringExtra("jenis_template");
            txtTemplateTitle.setText(judulTemplate);
        }

        getTemplateChatFromFirebaseDatabase(jenisTemplate);

        btnSimpanTemplateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanTemplateChat(jenisTemplate, txtTemplateChat.getText().toString());
            }
        });

    }

    private void getTemplateChatFromFirebaseDatabase(final String jenisTemplate){
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving template chat from database...");
        progressDialog.show();
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(jenisTemplate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TemplateChat templateChat = dataSnapshot.getValue(TemplateChat.class);
                if(templateChat != null){
                    txtTemplateChat.setText(templateChat.getTemplateContent());
                } else{
                    Toast.makeText(EditTemplateChatActivity.this, "No template chat with type " + jenisTemplate + " found on database!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getCode() + ", " + databaseError.getMessage() + "\n" + databaseError.getDetails());
            }
        });
    }

    private void simpanTemplateChat(String jenisTemplate, String templateChatContent){
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving template chat to database...");
        progressDialog.show();
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(jenisTemplate).child(TEMPLATE_CONTENT).setValue(templateChatContent).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(EditTemplateChatActivity.this, "Template chat is saved on database!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditTemplateChatActivity.this, "Failed to save template chat to database : " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
