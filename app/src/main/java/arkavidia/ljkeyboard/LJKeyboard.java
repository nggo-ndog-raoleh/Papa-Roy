package arkavidia.ljkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

/**
 * Created by axellageraldinc on 06/12/17.
 */

public class LJKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv; //KeyboardView itu adalah rujukan untuk tampilan keyboardnya
    private Keyboard keyboard; //keyboard yang ditugaskan ke KeyboardView
    private boolean caps = false; //Memberitahu apakah caps lock ON atau OFF

    //Method onCreateInputView untuk menginisialisasi apapun yang dibutuhkan
    //Ada keyboardView (view keyboardnya), keyboard yaitu yang berisi tombol2 spesifik keyboardnya
    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
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
        InputConnection ic = getCurrentInputConnection(); //getCurrentInputConnection digunakan untuk mendapatkan koneksi ke bidang input aplikasi lain.
        switch(i){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0); //deleteSurroundingText untuk menghapus satu atau lebih karakter input
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)); //sendKeyEvent untuk mengirim event
                break;
            default:
                char code = (char)i;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1); //commitText untuk mengirim teks
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
