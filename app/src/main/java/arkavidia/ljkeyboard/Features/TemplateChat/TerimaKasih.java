package arkavidia.ljkeyboard.Features.TemplateChat;

import android.view.inputmethod.InputConnection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by axellageraldinc on 30/01/18.
 */

public class TerimaKasih {

    private static final String TAG = "TerimaKasih.class";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String TERIMA_KASIH = "terima-kasih";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public TerimaKasih() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void kirimTerimaKasihTemplateChat(final String namaCustomer,
                                             final InputConnection inputConnection){
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(TERIMA_KASIH).child("templateContent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String terimaKasihMessage = dataSnapshot.getValue(String.class);
                terimaKasihMessage = terimaKasihMessage.replaceAll("/nama/", namaCustomer);
                inputConnection.commitText(terimaKasihMessage, 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
