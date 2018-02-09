package arkavidia.ljkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import arkavidia.ljkeyboard.Api.Retrofit.ApiUtil.RajaOngkirApiUtil;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;
import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.Features.CekOngkir;
import arkavidia.ljkeyboard.Features.TemplateChat.KirimNomorResi;
import arkavidia.ljkeyboard.Features.TemplateChat.Pembayaran;
import arkavidia.ljkeyboard.Features.TemplateChat.PesananBaru;
import arkavidia.ljkeyboard.Features.TemplateChat.TerimaKasih;
import arkavidia.ljkeyboard.Model.Firebase.AkunBank;
import arkavidia.ljkeyboard.Model.Firebase.Produk;
import arkavidia.ljkeyboard.Model.ProdukYangDibeli;
import arkavidia.ljkeyboard.Model.Sqlite.Customer;
import arkavidia.ljkeyboard.Model.Sqlite.RekapPesanan;
import arkavidia.ljkeyboard.RecyclerViewAdapter.RecyclerViewPilihProdukAdapter;
import arkavidia.ljkeyboard.SpinnerAdapter.SpinnerCourierAdapter;

/**
 * Created by axellageraldinc on 06/12/17.
 */

public class LJKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private static final String TAG = "LJKeyboard.class";

    private static final String INFORMASI_TOKO = "informasi-toko";
    private static final String PRODUK_LIST = "produk";
    private static final String AKUN_BANK = "akunBank";

    private boolean isInputConnectionExternalLJKeyboard;
    StringBuilder typedCharacters = new StringBuilder();
    String focusedEditText="";
    private boolean capslock=false;
    private boolean punctuation=false;

    private RajaOngkirService rajaOngkirService;
    private SqliteDbHelper sqliteDbHelper;

    /**
     * MENU UTAMA ELEMENTS
     **/
    private Button btnCekOngkir, btnTemplateChat, btnRekapPesanan, btnKeluar;
    private LinearLayout linearLayoutMenuUtama;

    /**
     * CEK ONGKIR ELEMENTS
    **/
    private CekOngkir cekOngkir;
    private LinearLayout linearLayoutCekOngkirFields;
    private EditText txtOrigin, txtDestination, txtWeightInGrams;
    private Spinner spinnerCourier;
    private Button btnDoCekOngkir;
    String origin, destination, courier;
    int weightInGrams;
    private List<String> cityNameList = new ArrayList<>();

    /**
     * MENU TEMPLATE CHAT
    **/
    private LinearLayout linearLayoutMenuTemplateChat;
    private Button btnPesananBaru, btnPembayaran, btnTerimaKasih, btnNoResi;

    /**
    * PESANAN BARU
    **/
    private PesananBaru pesananBaru;

    /**
     * PEMBAYARAN ELEMENTS
     **/
    private LinearLayout linearLayoutPembayaranFields, linearLayoutPilihProduk;
    private EditText txtNameCustPembayaran, txtOngkirPembayaran;
    private Button btnKirimTemplatePembayaran, btnPilihProduk, btnOkPilihProduk;
    private String namaCustomer="", ongkirPembayaran="";
    private RecyclerView recyclerViewPilihProduk;
    private RecyclerViewPilihProdukAdapter recyclerViewAdapter;
    private Pembayaran pembayaran;
    List<Produk> produkList = new ArrayList<>();
    List<ProdukYangDibeli> produkYangDibeliList = new ArrayList<>();

    /**
    * TERIMA KASIH ELEMENTS
    **/
    private TerimaKasih terimaKasih;
    private LinearLayout linearLayoutTerimaKasih;
    private EditText txtNamaCustomerTerimakasih;
    private Button btnSendTerimakasihTemplateChat;

    /**
    * KIRIM NOMOR RESI ELEMENTS
    **/
    private KirimNomorResi kirimNomorResi;
    private LinearLayout linearLayoutKirimNoResi, linearLayoutHorizontalKirimNomorResi;
    private EditText txtNamaCustNoResi, txtNoResi;
    private Button btnKirimNoResi;
    private Spinner spinnerCourierNoResi;

    /**
    * REKAP PESANAN ELEMENTS
    **/
    private LinearLayout linearLayoutRekapPesanan, linearLayoutHorizontalRekapPesanan;
    private EditText txtNamaCustomerRekapPesanan, txtNomorTelpCust, txtOngkirRekapPesanan;
    private Spinner spinnerCourierRekapPesanan, spinnerBank;
    private Button btnPilihProdukRekapPesanan, btnSubmitRekapPesanan;
    private arkavidia.ljkeyboard.Features.RekapPesanan rekapPesanan;

    private boolean isPembayaran=false;
    private boolean isRekapPesanan=false;

    /**
     * KEYBOARD ELEMENTS
     **/
    private KeyboardView kv;
    private View root;
    private Keyboard keyboardQwerty, keyboardPunctuation;

    /**
     * FIREBASE
     **/
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //Method onCreateInputView untuk menginisialisasi apapun yang dibutuhkan
    //Ada keyboardView (view keyboardnya), keyboardFormat yaitu yang berisi tombol2 spesifik keyboardnya
    @Override
    public View onCreateInputView() {
        rekapPesanan = new arkavidia.ljkeyboard.Features.RekapPesanan(LJKeyboard.this);
        initiateFirebase();
        rajaOngkirService = RajaOngkirApiUtil.getRajaOngkirService();
        sqliteDbHelper = new SqliteDbHelper(getApplicationContext());
        isInputConnectionExternalLJKeyboard = true;
        root = getLayoutInflater().inflate(R.layout.keyboard_utama, null);
        initiateKeyboardView();
        try{
            initiateMenu();
            initiateCekOngkirElements();
            initiateMenuTemplateChat();
            initiatePesananBaruTemplateChat();
            initiateTemplatePembayaranElements();
            initiateTerimaKasih();
            initiateKirimNoResi();
            initiateRekapPesanan();
        } catch (Exception ex){
            Log.i(TAG, ex.toString());
        }
        return root;
    }

    private void initiateFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    /**
    * KEYBOARD STUFF
    **/
    private void initiateKeyboardView(){
        keyboardQwerty = new Keyboard(this, R.xml.qwerty);
        keyboardPunctuation = new Keyboard(this, R.xml.punctuation);
        kv = (KeyboardView) root.findViewById(R.id.keyboard);
        kv.setKeyboard(keyboardQwerty);
        kv.setOnKeyboardActionListener(this);
        kv.setPreviewEnabled(false);
    }
    // TODO : Ini kayaknya bisa dibuat ke class sendiri
    private void deleteKeyPressed(InputConnection ic, CharSequence selectedText){
        if(isInputConnectionExternalLJKeyboard){
            if(TextUtils.isEmpty(selectedText)) {
                ic.deleteSurroundingText(1, 0);
            } else {
                ic.commitText("", 1);
            }
        } else if(!isInputConnectionExternalLJKeyboard){
            switch (focusedEditText) {
                case "txtOrigin":
                    int txtOriginLength = txtOrigin.getText().length();
                    if (txtOriginLength > 0) {
                        txtOrigin.getText().delete(txtOriginLength - 1, txtOriginLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtOriginLength - 1);
                        }
                    }
                    txtOrigin.setSelection(txtOrigin.getText().length());
                    break;
                case "txtDestination":
                    int txtDestinationLength = txtDestination.getText().length();
                    if (txtDestinationLength > 0) {
                        txtDestination.getText().delete(txtDestinationLength - 1, txtDestinationLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtDestinationLength - 1);
                        }
                    }
                    txtDestination.setSelection(txtDestination.getText().length());
                    break;
                case "txtWeightInGrams":
                    int txtWeightInGramsLength = txtWeightInGrams.getText().length();
                    if (txtWeightInGramsLength > 0) {
                        txtWeightInGrams.getText().delete(txtWeightInGramsLength - 1, txtWeightInGramsLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtWeightInGramsLength - 1);
                        }
                    }
                    txtWeightInGrams.setSelection(txtWeightInGrams.getText().length());
                    break;
                case "txtNameCustPembayaran":
                    int txtNameCustPembayaranLength = txtNameCustPembayaran.getText().length();
                    if (txtNameCustPembayaranLength > 0) {
                        txtNameCustPembayaran.getText().delete(txtNameCustPembayaranLength - 1, txtNameCustPembayaranLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNameCustPembayaranLength - 1);
                        }
                    }
                    break;
                case "txtOngkirPembayaran":
                    int txtOngkirPembayaranLength = txtOngkirPembayaran.getText().length();
                    if (txtOngkirPembayaranLength > 0) {
                        txtOngkirPembayaran.getText().delete(txtOngkirPembayaranLength - 1, txtOngkirPembayaranLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtOngkirPembayaranLength - 1);
                        }
                    }
                    break;
                case "txtNamaCustomerTerimakasih":
                    int txtNamaCustomerTerimakasihLength = txtNamaCustomerTerimakasih.getText().length();
                    if (txtNamaCustomerTerimakasihLength > 0) {
                        txtNamaCustomerTerimakasih.getText().delete(txtNamaCustomerTerimakasihLength - 1, txtNamaCustomerTerimakasihLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNamaCustomerTerimakasihLength - 1);
                        }
                    }
                    break;
                case "txtNamaCustNoResi":
                    int txtNamaCustNoResiLength = txtNamaCustNoResi.getText().length();
                    if (txtNamaCustNoResiLength > 0) {
                        txtNamaCustNoResi.getText().delete(txtNamaCustNoResiLength - 1, txtNamaCustNoResiLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNamaCustNoResiLength - 1);
                        }
                    }
                    break;
                case "txtNoResi":
                    int txtNoResiLength = txtNoResi.getText().length();
                    if (txtNoResiLength > 0) {
                        txtNoResi.getText().delete(txtNoResiLength - 1, txtNoResiLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNoResiLength - 1);
                        }
                    }
                    break;
                case "txtNamaCustomerRekapPesanan":
                    int txtNamaCustomerRekapPesananLength = txtNamaCustomerRekapPesanan.getText().length();
                    if (txtNamaCustomerRekapPesananLength > 0) {
                        txtNamaCustomerRekapPesanan.getText().delete(txtNamaCustomerRekapPesananLength - 1, txtNamaCustomerRekapPesananLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNamaCustomerRekapPesananLength - 1);
                        }
                    }
                    break;
                case "txtNoTelpCust":
                    int txtNomorTelpCustLength = txtNomorTelpCust.getText().length();
                    if (txtNomorTelpCustLength > 0) {
                        txtNomorTelpCust.getText().delete(txtNomorTelpCustLength - 1, txtNomorTelpCustLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtNomorTelpCustLength - 1);
                        }
                    }
                    break;
                case "txtOngkirRekapPesanan":
                    int txtOngkirRekapPesananLength = txtOngkirRekapPesanan.getText().length();
                    if (txtOngkirRekapPesananLength > 0) {
                        txtOngkirRekapPesanan.getText().delete(txtOngkirRekapPesananLength - 1, txtOngkirRekapPesananLength);
                        if(typedCharacters.length()>0){
                            typedCharacters.deleteCharAt(txtOngkirRekapPesananLength - 1);
                        }
                    }
                    break;
            }
        }
    }
    // TODO : Ini kayaknya bisa dibuat ke class sendiri
    private void commitTextToLjKeyboardEditText(String character){
        typedCharacters.append(character);

        switch (focusedEditText) {
            case "txtOrigin":
                txtOrigin.setText(typedCharacters);
                txtOrigin.setSelection(txtOrigin.getText().length());
                break;
            case "txtDestination":
                txtDestination.setText(typedCharacters);
                txtDestination.setSelection(txtDestination.getText().length());
                break;
            case "txtWeightInGrams":
                txtWeightInGrams.setText(typedCharacters);
                txtWeightInGrams.setSelection(txtWeightInGrams.getText().length());
                break;
            case "txtNameCustPembayaran":
                txtNameCustPembayaran.setText(typedCharacters);
                txtNameCustPembayaran.setSelection(txtNameCustPembayaran.getText().length());
                break;
            case "txtOngkirPembayaran":
                txtOngkirPembayaran.setText(typedCharacters);
                txtOngkirPembayaran.setSelection(txtOngkirPembayaran.getText().length());
                break;
            case "txtNamaCustomerTerimakasih":
                txtNamaCustomerTerimakasih.setText(typedCharacters);
                txtNamaCustomerTerimakasih.setSelection(txtNamaCustomerTerimakasih.getText().length());
                break;
            case "txtNamaCustNoResi":
                txtNamaCustNoResi.setText(typedCharacters);
                txtNamaCustNoResi.setSelection(txtNamaCustNoResi.getText().length());
                break;
            case "txtNoResi":
                txtNoResi.setText(typedCharacters);
                txtNoResi.setSelection(txtNoResi.getText().length());
                break;
            case "txtNamaCustomerRekapPesanan":
                txtNamaCustomerRekapPesanan.setText(typedCharacters);
                txtNamaCustomerRekapPesanan.setSelection(txtNamaCustomerRekapPesanan.getText().length());
                break;
            case "txtNoTelpCust":
                txtNomorTelpCust.setText(typedCharacters);
                txtNomorTelpCust.setSelection(txtNomorTelpCust.getText().length());
                break;
            case "txtOngkirRekapPesanan":
                txtOngkirRekapPesanan.setText(typedCharacters);
                txtOngkirRekapPesanan.setSelection(txtOngkirRekapPesanan.getText().length());
                break;
            default:
                InputConnection inputConnection = getCurrentInputConnection();
                inputConnection.commitText(character, 1);
                break;
        }

    }
    private void changeKeyboardLayout(){
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imeManager.showInputMethodPicker();
    }
    private void changeLayoutStatus(Boolean menuUtamaStatus){
        this.isInputConnectionExternalLJKeyboard = menuUtamaStatus;
    }
    private void setCapslock(boolean isCapslock){
        kv.setShifted(isCapslock);
        kv.invalidateAllKeys();
    }
    private void changeToPunctuationKeyboardLayout(boolean isPunctuation){
        if(!isPunctuation) {
            kv.setKeyboard(keyboardPunctuation);
            kv.invalidateAllKeys();
        } else {
            kv.setKeyboard(keyboardQwerty);
            kv.invalidateAllKeys();
        }
    }

    /**
    * MENU UTAMA
    **/
    private void initiateMenu(){
        linearLayoutMenuUtama = root.findViewById(R.id.linearLayoutMenuUtama);
        btnCekOngkir = root.findViewById(R.id.btnCekOngkir);
        btnTemplateChat = root.findViewById(R.id.btnTemplateChat);
        btnRekapPesanan = root.findViewById(R.id.btnRekapPesanan);

        btnKeluar = root.findViewById(R.id.btnKeluar);
        btnKeluar.setVisibility(View.GONE);

        btnCekOngkir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCekOngkirFields();
            }
        });

        btnTemplateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuTemplateChat();
            }
        });

        btnRekapPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRekapPesananLayout();
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keluar();
            }
        });

    }
    private void keluar(){
        linearLayoutCekOngkirFields.setVisibility(View.GONE);
        linearLayoutMenuTemplateChat.setVisibility(View.GONE);
        linearLayoutPembayaranFields.setVisibility(View.GONE);
        linearLayoutTerimaKasih.setVisibility(View.GONE);
        linearLayoutKirimNoResi.setVisibility(View.GONE);
        linearLayoutPilihProduk.setVisibility(View.GONE);
        linearLayoutRekapPesanan.setVisibility(View.GONE);
        linearLayoutMenuUtama.setVisibility(View.VISIBLE);
        btnKeluar.setVisibility(View.GONE);
        isRekapPesanan=false;
        isPembayaran=false;
        changeLayoutStatus(true);
        clearAllFields(linearLayoutCekOngkirFields);
        clearAllFields(linearLayoutPembayaranFields);
        clearAllFields(linearLayoutTerimaKasih);
        clearAllFields(linearLayoutKirimNoResi);
        clearAllFields(linearLayoutHorizontalKirimNomorResi);
        clearAllFields(linearLayoutHorizontalRekapPesanan);
        focusedEditText="none";
    }
    private void clearAllFields(ViewGroup group){
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearAllFields((ViewGroup)view);
        }
    }

    /**
    * CEK ONGKIR
    **/
    private void initiateCekOngkirElements(){
        cekOngkir = new CekOngkir(this);

        linearLayoutCekOngkirFields = root.findViewById(R.id.linearLayoutCekOngkirFields);
        txtOrigin = root.findViewById(R.id.txtOrigin);
        txtOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    focusedEditText = "txtOrigin";
                    typedCharacters.setLength(0);
                }
            }
        });
        txtDestination = root.findViewById(R.id.txtDestination);
        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    focusedEditText = "txtDestination";
                    typedCharacters.setLength(0);
                }
            }
        });

        txtWeightInGrams = root.findViewById(R.id.txtWeightInGrams);
        txtWeightInGrams.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    focusedEditText = "txtWeightInGrams";
                    typedCharacters.setLength(0);
                }
            }
        });
        spinnerCourier = root.findViewById(R.id.spinnerCourier);
        populateSpinnerCourier();

        btnDoCekOngkir = root.findViewById(R.id.btnDoCekOngkir);
        btnDoCekOngkir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekOngkir();
            }
        });
    }
    private void populateSpinnerCourier(){
        List<String> courierList = new ArrayList<>();
        courierList.add("JNE");
        courierList.add("TIKI");
        SpinnerCourierAdapter spinnerCourierAdapter = new SpinnerCourierAdapter(getApplicationContext(), courierList);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(LJKeyboard.this, R.layout.spinner_courier_adapter, courierList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourier.setAdapter(arrayAdapter);
    }
    private void showCekOngkirFields(){
        linearLayoutCekOngkirFields.setVisibility(View.VISIBLE);
        linearLayoutMenuUtama.setVisibility(View.GONE);
        btnKeluar.setVisibility(View.VISIBLE);
        changeLayoutStatus(false);
    }
    private void cekOngkir(){
        origin = txtOrigin.getText().toString();
        destination = txtDestination.getText().toString();
        courier = spinnerCourier.getSelectedItem().toString().toUpperCase();
        weightInGrams = Integer.parseInt(txtWeightInGrams.getText().toString());
        InputConnection inputConnection = getCurrentInputConnection();
        cekOngkir.execute(origin, destination, courier, weightInGrams, inputConnection);
    }

    /**
    * MENU TEMPLATE CHAT
    **/
    private void initiateMenuTemplateChat(){
        linearLayoutMenuTemplateChat = root.findViewById(R.id.linearLayoutMenuTemplateChat);

        btnPesananBaru = root.findViewById(R.id.btnPesananBaru);
        btnPesananBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesananBaruTemplateChat();
            }
        });

        btnPembayaran = root.findViewById(R.id.btnPembayaran);
        btnPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPembayaranTemplateChat();
            }
        });

        btnTerimaKasih = root.findViewById(R.id.btnTerimaKasih);
        btnTerimaKasih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTerimaKasihTemplateChat();
            }
        });

        btnNoResi = root.findViewById(R.id.btnNoResi);
        btnNoResi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKirimNoResiLayout();
            }
        });
    }
    private void showMenuTemplateChat(){
        linearLayoutMenuTemplateChat.setVisibility(View.VISIBLE);
        linearLayoutMenuUtama.setVisibility(View.GONE);
        btnKeluar.setVisibility(View.VISIBLE);
        changeLayoutStatus(false);
    }

    /**
    * PESANAN BARU
    **/
    private void initiatePesananBaruTemplateChat(){
        pesananBaru = new PesananBaru();
    }
    private void kirimPesananBaruTemplateChat(){
        InputConnection inputConnection = getCurrentInputConnection();
        pesananBaru.kirimPesananBaruMessage(inputConnection);
    }

    /**
    * TEMPLATE CHAT PEMBAYARAN
    **/
    private void initiateTemplatePembayaranElements(){
        pembayaran = new Pembayaran(user);
        linearLayoutPembayaranFields = root.findViewById(R.id.linearLayoutPembayaranFields);
        linearLayoutPilihProduk = root.findViewById(R.id.linearLayoutPilihProduk);

        txtNameCustPembayaran = root.findViewById(R.id.txtNameCustPembayaran);
        txtNameCustPembayaran.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNameCustPembayaran";
                    typedCharacters.setLength(0);
                }
            }
        });
        txtOngkirPembayaran = root.findViewById(R.id.txtOngkirPembayaran);
        txtOngkirPembayaran.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    focusedEditText = "txtOngkirPembayaran";
                    typedCharacters.setLength(0);
                }
            }
        });

        btnPilihProduk = root.findViewById(R.id.btnPilihProduk);
        btnPilihProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPilihProduk(true, false);
            }
        });

        btnOkPilihProduk = root.findViewById(R.id.btnOkPilihProduk);
        btnOkPilihProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedBtnOkPilihProduk();
            }
        });

        btnKirimTemplatePembayaran = root.findViewById(R.id.btnKirimTemplatePembayaran);
        btnKirimTemplatePembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPembayaranTemplateChat();
            }
        });

    }
    private void showPembayaranTemplateChat(){
        linearLayoutMenuTemplateChat.setVisibility(View.GONE);
        linearLayoutPembayaranFields.setVisibility(View.VISIBLE);
    }
    private void showPilihProduk(boolean isPembayaran, boolean isRekapPesanan){
        this.isPembayaran=isPembayaran;
        this.isRekapPesanan=isRekapPesanan;
        if(isPembayaran){
            List<Produk> produkList = getProdukFromFirebase();
            recyclerViewPilihProduk = root.findViewById(R.id.recyclerViewPilihProduk);
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            recyclerViewPilihProduk.setLayoutManager(llm);
            recyclerViewAdapter = new RecyclerViewPilihProdukAdapter(getApplicationContext(), produkList, false);
        } else {
            List<Produk> produkList = getProdukFromFirebase();
            recyclerViewPilihProduk = root.findViewById(R.id.recyclerViewPilihProduk);
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            recyclerViewPilihProduk.setLayoutManager(llm);
            recyclerViewAdapter = new RecyclerViewPilihProdukAdapter(getApplicationContext(), produkList, true);
        }
        linearLayoutPembayaranFields.setVisibility(View.GONE);
        linearLayoutPilihProduk.setVisibility(View.VISIBLE);
    }
    private List<Produk> getProdukFromFirebase(){
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(PRODUK_LIST).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produkList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Produk produk = dataSnapshot1.getValue(Produk.class);
                    produkList.add(produk);
                }
                recyclerViewPilihProduk.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return produkList;
    }
    private void clickedBtnOkPilihProduk() {
        if(isPembayaran) {
            linearLayoutPilihProduk.setVisibility(View.GONE);
            linearLayoutPembayaranFields.setVisibility(View.VISIBLE);
            isPembayaran=false;
        } else if(isRekapPesanan){
            linearLayoutPilihProduk.setVisibility(View.GONE);
            linearLayoutRekapPesanan.setVisibility(View.VISIBLE);
            isRekapPesanan=false;
        }

        produkYangDibeliList.clear();
        produkYangDibeliList = pembayaran.populateProdukYangDibeliList(recyclerViewAdapter.getIdProdukYangDibeliList(),
                recyclerViewAdapter.getNamaProdukYangDibeliList(),
                recyclerViewAdapter.getHargaProdukYangDibeliList(),
                recyclerViewAdapter.getQtyProdukYangDibeliList(),
                recyclerViewAdapter.getStockTersediaProdukYangDibeliList());
        recyclerViewAdapter.clearIdProdukYangDibeliList();
        recyclerViewAdapter.clearNamaProdukYangDibeliList();
        recyclerViewAdapter.clearQtyProdukYangDibeliList();
        recyclerViewAdapter.clearHargaProdukYangDibeliList();
        recyclerViewAdapter.clearStockTersediaProdukYangDibeliList();
    }
    private void kirimPembayaranTemplateChat(){
        namaCustomer = txtNameCustPembayaran.getText().toString();
        ongkirPembayaran = txtOngkirPembayaran.getText().toString();
        InputConnection ic = getCurrentInputConnection();
        pembayaran.kirimPembayaranMessage(namaCustomer, ongkirPembayaran, ic);
        for (ProdukYangDibeli item:produkYangDibeliList
             ) {
            pembayaran.kurangiStockProdukYangAdaDiFirebase(item.getIdProduk(), item.getStockProduk(), item.getQtyProduk());
        }
    }

    /**
    * TERIMA KASIH
    **/
    private void initiateTerimaKasih(){
        terimaKasih = new TerimaKasih();
        linearLayoutTerimaKasih = root.findViewById(R.id.linearLayoutTerimaKasih);
        txtNamaCustomerTerimakasih = root.findViewById(R.id.txtCustNameTerimakasih);
        txtNamaCustomerTerimakasih.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNamaCustomerTerimakasih";
                    typedCharacters.setLength(0);
                }
            }
        });
        btnSendTerimakasihTemplateChat = root.findViewById(R.id.btnSendTerimakasihTemplateChat);
        btnSendTerimakasihTemplateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimTerimakasihTemplateChat();
            }
        });
    }
    private void showTerimaKasihTemplateChat(){
        linearLayoutTerimaKasih.setVisibility(View.VISIBLE);
        linearLayoutMenuTemplateChat.setVisibility(View.GONE);
    }
    private void kirimTerimakasihTemplateChat(){
        String namaCustomer = txtNamaCustomerTerimakasih.getText().toString();
        InputConnection inputConnection = getCurrentInputConnection();
        terimaKasih.kirimTerimaKasihTemplateChat(namaCustomer, inputConnection);
    }

    /**
    * KIRIM NOMOR RESI
    **/
    private void initiateKirimNoResi(){
        kirimNomorResi = new KirimNomorResi();
        linearLayoutKirimNoResi = root.findViewById(R.id.linearLayoutKirimNoResi);
        linearLayoutHorizontalKirimNomorResi = root.findViewById(R.id.linearLayoutHorizontalKirimNomorResi);

        txtNamaCustNoResi = root.findViewById(R.id.txtNamaCustKirimNoResi);
        txtNamaCustNoResi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNamaCustNoResi";
                    typedCharacters.setLength(0);
                }
            }
        });

        spinnerCourierNoResi = root.findViewById(R.id.spinnerCourierNoResi);
        populateSpinnerCourierNoResi();

        txtNoResi = root.findViewById(R.id.txtNoResi);
        txtNoResi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNoResi";
                    typedCharacters.setLength(0);
                }
            }
        });

        btnKirimNoResi = root.findViewById(R.id.btnKirimNoResi);
        btnKirimNoResi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaCustomer = txtNamaCustNoResi.getText().toString();
                String logistik = spinnerCourierNoResi.getSelectedItem().toString();
                String noResi = txtNoResi.getText().toString();
                InputConnection inputConnection = getCurrentInputConnection();
                kirimNomorResi.kirimNomorResiTemplateChat(namaCustomer, logistik, noResi, inputConnection);
            }
        });
    }
    private void populateSpinnerCourierNoResi(){
        List<String> courierList = new ArrayList<>();
        courierList.add("JNE");
        courierList.add("TIKI");
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(LJKeyboard.this, R.layout.spinner_courier_adapter, courierList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourierNoResi.setAdapter(arrayAdapter);
    }
    private void showKirimNoResiLayout(){
        linearLayoutKirimNoResi.setVisibility(View.VISIBLE);
        linearLayoutMenuTemplateChat.setVisibility(View.GONE);
        changeLayoutStatus(false);
    }

    /**
    * REKAP PESANAN
    **/
    private void initiateRekapPesanan(){
        linearLayoutRekapPesanan = root.findViewById(R.id.linearLayoutRekapPesanan);
        linearLayoutHorizontalRekapPesanan = root.findViewById(R.id.linearLayoutHorizontalRekapPesanan);

        txtNamaCustomerRekapPesanan = root.findViewById(R.id.txtNamaCustRekapPesanan);
        txtNamaCustomerRekapPesanan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNamaCustomerRekapPesanan";
                    typedCharacters.setLength(0);
                }
            }
        });
        txtNomorTelpCust = root.findViewById(R.id.txtNoTelpCust);
        txtNomorTelpCust.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtNoTelpCust";
                    typedCharacters.setLength(0);
                }
            }
        });
        txtOngkirRekapPesanan = root.findViewById(R.id.txtOngkirRekapPesanan);
        txtOngkirRekapPesanan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusedEditText = "txtOngkirRekapPesanan";
                    typedCharacters.setLength(0);
                }
            }
        });
        spinnerCourierRekapPesanan = root.findViewById(R.id.spinnerCourierRekapPesanan);
        populateSpinnerCourierRekapPesanan();
        spinnerBank = root.findViewById(R.id.spinnerBank);
        populateSpinnerBankRekapPesanan();

        btnPilihProdukRekapPesanan = root.findViewById(R.id.btnPilihProdukRekapPesanan);
        btnPilihProdukRekapPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPilihProduk(false, true);
                linearLayoutRekapPesanan.setVisibility(View.GONE);
            }
        });
        btnSubmitRekapPesanan = root.findViewById(R.id.btnSubmitRekapPesanan);
        btnSubmitRekapPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRekapPesananToSqlite();
            }
        });
    }
    private void populateSpinnerCourierRekapPesanan(){
        List<String> courierList = new ArrayList<>();
        courierList.add("JNE");
        courierList.add("TIKI");
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(LJKeyboard.this, R.layout.spinner_courier_adapter, courierList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourierRekapPesanan.setAdapter(arrayAdapter);
    }
    private void populateSpinnerBankRekapPesanan(){
        final List<String> namaBankList = new ArrayList<>();
        databaseReference.child(INFORMASI_TOKO).child(user.getUid()).child(AKUN_BANK).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namaBankList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()
                     ) {
                    AkunBank akunBank = data.getValue(AkunBank.class);
                    namaBankList.add(akunBank.getNamaBank());
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(LJKeyboard.this, R.layout.spinner_courier_adapter, namaBankList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBank.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void showRekapPesananLayout(){
        isRekapPesanan=true;
        linearLayoutRekapPesanan.setVisibility(View.VISIBLE);
        linearLayoutMenuUtama.setVisibility(View.GONE);
        btnKeluar.setVisibility(View.VISIBLE);
        changeLayoutStatus(false);
    }
    private void addRekapPesananToSqlite(){
        Customer customer = Customer.builder()
                .nama(txtNamaCustomerRekapPesanan.getText().toString())
                .nomorTelepon(txtNomorTelpCust.getText().toString())
                .build();
        String kurirLogistik = spinnerCourierRekapPesanan.getSelectedItem().toString();
        for (ProdukYangDibeli item:produkYangDibeliList
             ) {
            RekapPesanan rekapPesanan = RekapPesanan.builder()
                    .customer(customer)
                    .kurirLogistik(kurirLogistik)
                    .ongkir(Integer.parseInt(txtOngkirRekapPesanan.getText().toString()))
                    .produk(item.getNamaProduk())
                    .quantity(item.getQtyProduk())
                    .totalHargaProduk(item.getHargaProduk())
                    .bank(spinnerBank.getSelectedItem().toString())
                    .build();
            this.rekapPesanan.addNewRekapPesananToSqlite(rekapPesanan);
        }
        List<RekapPesanan> rekapPesananList = rekapPesanan.getRekapPesananFromSqlite();
        for (RekapPesanan item:rekapPesananList
             ) {
            Log.i(TAG, item.getCustomer().getNama() + "\n" + item.getProduk() + " " + item.getQuantity() + " " + item.getTotalHargaProduk());
        }
        clearAllFields(linearLayoutHorizontalRekapPesanan);
    }

    @Override
    public void onPress(int i) {

    }
    @Override
    public void onRelease(int i) {

    }
    //onKey untuk dapat berkomunikasi dengan bidang masukan (biasanya tampilan EditText) dari aplikasi lain.
    @Override
    public void onKey(int i, int[] ints) {
        final InputConnection ic = getCurrentInputConnection(); //getCurrentInputConnection digunakan untuk mendapatkan koneksi ke bidang input aplikasi lain.
        CharSequence selectedText = ic.getSelectedText(0);
        switch(i){
            case KeyboardKey.CAPSLOCK:
                capslock = !capslock;
                setCapslock(capslock);
                break;
            case KeyboardKey.KEYCODE_DELETE :
                deleteKeyPressed(ic, selectedText);
                break;
            case KeyboardKey.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case KeyboardKey.CHANGE_KEYBOARD:
                changeKeyboardLayout();
                break;
            case KeyboardKey.PUNCTUATION:
                punctuation = !punctuation;
                changeToPunctuationKeyboardLayout(punctuation);
                break;
            default:
                char code = (char)i;
                if(Character.isLetter(code) && capslock){
                    code = Character.toUpperCase(code);
                }
                commitTextToLjKeyboardEditText(String.valueOf(code));
        }
    }
    @Override
    public void onText(CharSequence charSequence) {

    }
    @Override
    public void swipeLeft() {

    }
    @Override
    public void swipeRight() {

    }
    @Override
    public void swipeDown() {

    }
    @Override
    public void swipeUp() {

    }
}
