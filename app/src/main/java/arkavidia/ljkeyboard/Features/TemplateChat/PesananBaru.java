package arkavidia.ljkeyboard.Features.TemplateChat;

import android.util.Log;
import android.view.inputmethod.InputConnection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;

/**
 * Created by axellageraldinc on 30/01/18.
 */

public class PesananBaru {

    private static final String TAG = "PesananBaru.class";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String PESANAN_BARU = "pesanan-baru";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public PesananBaru() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void kirimPesananBaruMessage(final InputConnection inputConnection){
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(PESANAN_BARU).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TemplateChat templateChat = dataSnapshot.getValue(TemplateChat.class);
                String pesananBaruMessage = templateChat.getTemplateContent();
                inputConnection.commitText(pesananBaruMessage, 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
