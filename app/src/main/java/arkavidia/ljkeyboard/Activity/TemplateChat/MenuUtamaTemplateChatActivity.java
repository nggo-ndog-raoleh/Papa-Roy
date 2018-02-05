package arkavidia.ljkeyboard.Activity.TemplateChat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import arkavidia.ljkeyboard.R;

public class MenuUtamaTemplateChatActivity extends AppCompatActivity {

    private static final String JUDUL_PESANAN_BARU = "Pesanan Baru";
    private static final String JENIS_PESANAN_BARU = "pesanan-baru";
    private static final String JUDUL_PEMBAYARAN = "Pembayaran";
    private static final String JENIS_PEMBAYARAN = "pembayaran";
    private static final String JUDUL_TERIMAKASIH = "Terima Kasih";
    private static final String JENIS_TERIMAKASIH = "terima-kasih";
    private static final String JUDUL_KIRIM_NOMOR_RESI = "Kirim Nomor Resi";
    private static final String JENIS_KIRIM_NOMOR_RESI = "kirim-nomor-resi";
    private static final String JUDUL_CUSTOM = "Custom";
    private static final String JENIS_CUSTOM = "custom";

    private Toolbar toolbar;
    private CardView cardViewPesananBaru, cardViewPembayaran, cardViewTerimakasih, cardViewKirimNomorResi, cardViewCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama_template_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cardViewPesananBaru = findViewById(R.id.cardViewPesananBaru);
        cardViewPesananBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActity(JUDUL_PESANAN_BARU, JENIS_PESANAN_BARU);
            }
        });

        cardViewPembayaran = findViewById(R.id.cardViewPembayaran);
        cardViewPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActity(JUDUL_PEMBAYARAN, JENIS_PEMBAYARAN);
            }
        });

        cardViewTerimakasih = findViewById(R.id.cardViewTerimakasih);
        cardViewTerimakasih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActity(JUDUL_TERIMAKASIH, JENIS_TERIMAKASIH);
            }
        });

        cardViewKirimNomorResi = findViewById(R.id.cardViewKirimNomorResi);
        cardViewKirimNomorResi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActity(JUDUL_KIRIM_NOMOR_RESI, JENIS_KIRIM_NOMOR_RESI);
            }
        });

        cardViewCustom = findViewById(R.id.cardViewCustom);
        cardViewCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActity(JUDUL_CUSTOM, JENIS_CUSTOM);
            }
        });

    }

    private void moveToActity(String judulTemplate, String jenisTemplate){
        Intent intent = new Intent(getApplicationContext(), EditTemplateChatActivity.class);
        intent.putExtra("judul_template", judulTemplate);
        intent.putExtra("jenis_template", jenisTemplate);
        startActivity(intent);
    }

}
