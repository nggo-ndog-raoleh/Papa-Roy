package arkavidia.ljkeyboard.Features.TemplateChat;

import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputConnection;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import arkavidia.ljkeyboard.LJKeyboard;
import arkavidia.ljkeyboard.Model.Firebase.AkunBank;
import arkavidia.ljkeyboard.Model.Firebase.InformasiToko;
import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.Model.Firebase.TemplateChat;
import arkavidia.ljkeyboard.Model.ProdukYangDibeli;

/**
 * Created by axellageraldinc on 30/01/18.
 */

public class Pembayaran {

    private static final String TAG = "Pembayaran.class";
    private static final String TEMPLATE_CHAT = "template-chat";
    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String PRODUK_LIST = "produk";
    private static final String ID_PRODUK = "id";
    private static final String AKUN_BANK = "akunBank";
    private static final String NAMA_TOKO = "namaToko";
    private static final String PEMBAYARAN = "pembayaran";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    String daftarRekening="", pembayaranMessage="";
    List<AkunBank> akunBankList = new ArrayList<>();
    List<ProdukYangDibeli> produkYangDibeliList = new ArrayList<>();
    StringBuilder daftarProdukYangDibeli = new StringBuilder();
    StringBuilder daftarRekeningBuilder = new StringBuilder();

    public Pembayaran(FirebaseUser user) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        this.user = user;
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(AKUN_BANK).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                akunBankList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()
                     ) {
                    AkunBank akunBank = data.getValue(AkunBank.class);
                    akunBankList.add(akunBank);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void kirimPembayaranMessage(final String namaCustomer,
                                       final String ongkirPembayaran,
                                       final InputConnection inputConnection){
        databaseReference.child(TEMPLATE_CHAT).child(user.getUid()).child(PEMBAYARAN).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pembayaranMessage = dataSnapshot.getValue(TemplateChat.class).getTemplateContent();
                pembayaranMessage = pembayaranMessage.replaceAll("/nama/", namaCustomer);
                databaseReference.child(INFORMASI_TOKO).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        InformasiToko informasiToko = dataSnapshot.getValue(InformasiToko.class);
                        String namaToko = informasiToko.getNamaToko();
                        daftarRekeningBuilder.setLength(0);
                        for (AkunBank item:akunBankList
                             ) {
                            daftarRekeningBuilder.append(item.getNamaBank() + "\n" +
                                    item.getNomorRekening() + "\n" +
                                    item.getNamaPemilikRekening() + "\n\n");
                        }
                        daftarRekeningBuilder.setLength(daftarRekeningBuilder.length()-1);
                        daftarRekening = daftarRekeningBuilder.toString();
                        pembayaranMessage = pembayaranMessage.replaceAll("/nama-toko/", namaToko);
                        pembayaranMessage = pembayaranMessage.replaceAll("/ongkir/", "Rp " + ongkirPembayaran);
                        pembayaranMessage = pembayaranMessage.replaceAll("/list-produk/", populateListProdukYangDibeli());
                        int totalHargaPlusOngkir = getTotalHarga() + Integer.parseInt(ongkirPembayaran);
                        pembayaranMessage = pembayaranMessage.replaceAll("/total-harga/", "Rp " + totalHargaPlusOngkir);
                        pembayaranMessage = pembayaranMessage.replaceAll("/list-rekening/", daftarRekening);
                        inputConnection.commitText(pembayaranMessage, 1);
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

    private String populateListProdukYangDibeli(){
        daftarProdukYangDibeli.setLength(0);
        for (ProdukYangDibeli item:produkYangDibeliList
             ) {
            daftarProdukYangDibeli.append(item.getNamaProduk() + " " + item.getQtyProduk() + "x" + " Rp " + item.getHargaProduk() + "\n");
        }
        daftarProdukYangDibeli.setLength(daftarProdukYangDibeli.length()-1);
        return daftarProdukYangDibeli.toString();
    }

    private int getTotalHarga(){
        int totalHarga=0;
        for (ProdukYangDibeli item:produkYangDibeliList
             ) {
            totalHarga+=item.getHargaProduk();
        }
        return totalHarga;
    }

    public List<ProdukYangDibeli> populateProdukYangDibeliList(List<String> idProdukYangDibeliList,
                                                               List<String> namaProdukYangDibeliList,
                                                               List<Integer> hargaProdukYangDibeliList,
                                                               List<Integer> qtyProdukYangDibeliList,
                                                               List<Integer> stockTersediaProdukYangDibeliList){
        Iterator<String> iteratorIdProduk = idProdukYangDibeliList.iterator();
        Iterator<String> iteratorNamaProduk = namaProdukYangDibeliList.iterator();
        Iterator<Integer> iteratorHargaProduk = hargaProdukYangDibeliList.iterator();
        Iterator<Integer> iteratorQtyProduk = qtyProdukYangDibeliList.iterator();
        Iterator<Integer> iteratorStockTersediaProduk = stockTersediaProdukYangDibeliList.iterator();
        produkYangDibeliList.clear();
        while(iteratorIdProduk.hasNext() &&
                iteratorNamaProduk.hasNext() &&
                iteratorHargaProduk.hasNext() &&
                iteratorQtyProduk.hasNext() &&
                iteratorStockTersediaProduk.hasNext()){
            ProdukYangDibeli produkYangDibeli = ProdukYangDibeli.builder()
                    .idProduk(iteratorIdProduk.next())
                    .namaProduk(iteratorNamaProduk.next())
                    .hargaProduk(iteratorHargaProduk.next())
                    .qtyProduk(iteratorQtyProduk.next())
                    .stockProduk(iteratorStockTersediaProduk.next())
                    .build();
            produkYangDibeliList.add(produkYangDibeli);
        }
        return produkYangDibeliList;
    }

    public void kurangiStockProdukYangAdaDiFirebase(final String idProduk,
                                                    final Integer stockProdukSaatIni,
                                                    final Integer stockProdukYangDibeli){
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK_LIST).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()
                     ) {
                    Produk produk = data.getValue(Produk.class);
                    if(produk.getId().equals(idProduk)){
                        String keyList = data.getKey();
                        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK_LIST).child(keyList).child("stockProduk").setValue(stockProdukSaatIni-stockProdukYangDibeli);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
