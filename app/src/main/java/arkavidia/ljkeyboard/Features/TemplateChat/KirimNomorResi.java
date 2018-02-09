package arkavidia.ljkeyboard.Features.TemplateChat;

import android.view.inputmethod.InputConnection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import arkavidia.ljkeyboard.Model.Firebase.InformasiToko;
import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;

/**
 * Created by axellageraldinc on 31/01/18.
 */

public class KirimNomorResi {

    private static final String TAG = "KirimNomorResi.class";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String ID = "id";
    private static final String NAMA_TOKO = "namaToko";
    private static final String AKUN_BANK = "akunBank";
    private static final String NAMA_BANK = "namaBank";
    private static final String NAMA_PEMILIK_REKENING = "namaPemilikRekening";
    private static final String NOMOR_REKENING = "nomorRekening";
    private static final String PRODUK = "produk";
    private static final String HARGA_PRODUK = "hargaProduk";
    private static final String NAMA_PRODUK = "namaProduk";
    private static final String STOCK_PRODUK = "stockProduk";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String KIRIM_NOMOR_RESI = "kirim-nomor-resi";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private String namaToko;

    public KirimNomorResi() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void kirimNomorResiTemplateChat(final String namaCustomer,
                                           final String logistik,
                                           final String nomorResi,
                                           final InputConnection inputConnection){
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                InformasiToko informasiToko = dataSnapshot.getValue(InformasiToko.class);
                namaToko = informasiToko.getNamaToko();
                databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(KIRIM_NOMOR_RESI).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TemplateChat templateChat = dataSnapshot.getValue(TemplateChat.class);
                        String kirimNomorResiMessage = templateChat.getTemplateContent();
                        kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/nama/", namaCustomer);
                        kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/logistik/", logistik);
                        kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/nomor-resi/", nomorResi);
                        kirimNomorResiMessage = kirimNomorResiMessage.replaceAll("/nama-toko/", namaToko);
                        inputConnection.commitText(kirimNomorResiMessage, 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
