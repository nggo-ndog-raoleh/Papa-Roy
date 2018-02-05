package arkavidia.ljkeyboard.Features.TemplateChat;

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
 * Created by axellageraldinc on 31/01/18.
 */

public class KirimNomorResi {

    private static final String TAG = "KirimNomorResi.class";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String KIRIM_NOMOR_RESI = "kirim-nomor-resi";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public KirimNomorResi() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void kirimNomorResiTemplateChat(final String namaCustomer,
                                           final String logistik,
                                           final String nomorResi,
                                           final InputConnection inputConnection){
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(KIRIM_NOMOR_RESI).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TemplateChat templateChat = dataSnapshot.getValue(TemplateChat.class);
                String kirimNomorResiMessage = templateChat.getTemplateContent();
                kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/nama/", namaCustomer);
                kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/logistik/", logistik);
                kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/nomor-resi/", nomorResi);
                inputConnection.commitText(kirimNomorResiMessage, 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
