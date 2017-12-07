package arkavidia.ljkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by axellageraldinc on 06/12/17.
 */

public class LJKeyboard2 extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    String api_key = "f669819773a9f6b26a3ae4188a3f1ab9";
    String url_ongkir = "https://api.rajaongkir.com/starter/cost";
    String url_city = "https://api.rajaongkir.com/starter/city";
    private final String USER_AGENT = "Mozilla/5.0";
    StringBuilder stringBuilder = new StringBuilder();
    int index_pesan =0;
    String kotaAsal, kotaTujuan, kurir;
    int berat;
    ArrayList<String> pesan = new ArrayList<>();

    private KeyboardView kv; //KeyboardView itu adalah rujukan untuk tampilan keyboardnya
    private Keyboard keyboardFormat, keyboardOngkir, keyboardQwerty; //keyboardFormat yang ditugaskan ke KeyboardView
    private boolean format = true;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //Method onCreateInputView untuk menginisialisasi apapun yang dibutuhkan
    //Ada keyboardView (view keyboardnya), keyboardFormat yaitu yang berisi tombol2 spesifik keyboardnya
    @Override
    public View onCreateInputView() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboardFormat = new Keyboard(this, R.xml.format);
        keyboardOngkir = new Keyboard(this, R.xml.ongkir);
        keyboardQwerty = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboardFormat);
        kv.invalidateAllKeys();
        kv.setOnKeyboardActionListener(this);
        kv.setPreviewEnabled(false);
        return kv;
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
        switch(i){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0); //deleteSurroundingText untuk menghapus satu atau lebih karakter input
                if(pesan.size()!=0){
                    pesan.remove(index_pesan -1);
                    index_pesan--;
                }
                break;
            case Keyboard.KEYCODE_DONE:
                if(pesan.size()!=0) {
                    for (int index = 0; index < pesan.size(); index++) {
                        stringBuilder.append(pesan.get(index));
                    }
                    String[] split = stringBuilder.toString().split("-");
                    kotaAsal = split[0];
                    kotaTujuan = split[1];
                    kurir = split[2];
                    berat = Integer.parseInt(split[3]);
                    System.out.println(kotaAsal + "," + kotaTujuan + " | " + kurir + "," + berat);

                    //Cek ongkir disini
                    PerformNetworkRequest pnr = new PerformNetworkRequest(url_ongkir, USER_AGENT, kotaAsal, kotaTujuan, kurir, berat, api_key);
                    pnr.execute();

                    //RESET EVERYTHING
                    stringBuilder.setLength(0);
                    stringBuilder = new StringBuilder();
                    pesan.clear();
                    index_pesan = 0;
                }
                break;
            default:
                if(i==1){
                    databaseReference.child("format").child(user.getUid()).child("pemesanan").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String code = dataSnapshot.getValue(String.class);
                            ic.commitText(code,1); //commitText untuk mengirim teks
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if(i==2){
                    databaseReference.child("format").child(user.getUid()).child("pembayaran").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String code = dataSnapshot.getValue(String.class);
                            ic.commitText(code,1); //commitText untuk mengirim teks
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if(i==3) {
                    InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                } else if(i==4){
                    if(!format){
                        kv.setKeyboard(keyboardFormat);
                        kv.invalidateAllKeys();
                        format = true;
                    }
                } else if(i==5){
                    if(format){
                        kv.setKeyboard(keyboardOngkir);
                        kv.invalidateAllKeys();
                        format = false;
                    }
                } else if(i==6){
                    kv.setKeyboard(keyboardQwerty);
                    kv.invalidateAllKeys();
                } else if(i==-2){
                    //tanda strip, pemisah antara 2 kota
                    ic.commitText("-", 1);
                    pesan.add("-");
                    index_pesan++;
                } else {
                    char code = (char)i;
                    pesan.add(String.valueOf(code));
                    index_pesan++;
                    ic.commitText(String.valueOf(code),1); //commitText untuk mengirim teks
                }
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

    private class PerformNetworkRequest extends AsyncTask<Void, Void, Boolean>{

        String url_ongkir, USER_AGENT;
        String kotaAsal, kotaTujuan, kurir;
        int berat;
        String api_key;

        PerformNetworkRequest(String url_ongkir, String USER_AGENT, String kotaAsal, String kotaTujuan, String kurir, int berat, String api_key){
            this.url_ongkir = url_ongkir;
            this.USER_AGENT = USER_AGENT;
            this.kotaAsal = kotaAsal;
            this.kotaTujuan = kotaTujuan;
            this.kurir = kurir;
            this.berat = berat;
            this.api_key = api_key;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                if(dbHelper.countAllData()==0){
                    getCity();
                    System.out.println("GetCity via internet");
                } else{
                    String kotaAsalId = new String(), kotaTujuanId = new String();
                    HashMap<String, String> param = dbHelper.GetAllCity(kotaAsal, kotaTujuan);
                    System.out.println("GetCity via sqlite");
                    for(Map.Entry m:param.entrySet()){
                        if(m.getKey().toString().toUpperCase().equals("KOTAASAL")){
                            kotaAsalId = m.getValue().toString();
                        } if(m.getKey().toString().toUpperCase().equals("KOTATUJUAN")){
                            kotaTujuanId = m.getValue().toString();
                        }
                    }
                    cekOngkir(kotaAsalId, kotaTujuanId, berat, kurir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void getCity() throws Exception {
            String kotaAsalId = new String(), kotaTujuanId = new String();
            URL obj = new URL(url_city);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("key", api_key);
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url_city);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject rajaongkir = jsonObject.getJSONObject("rajaongkir");
            JSONArray results = rajaongkir.getJSONArray("results");
            for (int i=0; i<results.length(); i++){
                JSONObject results_object = results.getJSONObject(i);
                if(kotaAsal.toUpperCase().equals(results_object.getString("city_name").toUpperCase())){
                    kotaAsalId = results_object.getString("city_id");
                } if(kotaTujuan.toUpperCase().equals(results_object.getString("city_name").toUpperCase())){
                    kotaTujuanId = results_object.getString("city_id");
                }
            }
            cekOngkir(kotaAsalId, kotaTujuanId, berat, kurir);
        }

        // HTTP POST request
        private void cekOngkir(String kotaAsalId, String kotaTujuanId, int berat, String kurir) throws Exception {

            URL obj = new URL(url_ongkir);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("key", api_key);
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

            String urlParameters = "origin=" + kotaAsalId + "&destination=" + kotaTujuanId + "&weight=" + berat + "&courier=" + kurir;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url_ongkir);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject rajaongkir = jsonObject.getJSONObject("rajaongkir");
            JSONArray results = rajaongkir.getJSONArray("results");
            String pesan = new String();
            for (int i=0; i<results.length(); i++){
                JSONObject resulst_object = results.getJSONObject(i);
                JSONArray costs = resulst_object.getJSONArray("costs");
                for (int j=0; j<costs.length(); j++){
                    JSONObject costs_object = costs.getJSONObject(j);
                    JSONArray cost = costs_object.getJSONArray("cost");
                    for (int k=0; k<cost.length(); k++){
                        JSONObject cost_object = cost.getJSONObject(k);
                        pesan = "\n" +
                                costs_object.getString("service") + " | " + cost_object.getString("value");
                    }
                    getCurrentInputConnection().commitText(pesan, 1);
                }
            }

        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
