
package com.fktv.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.*;
import android.graphics.Typeface;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private boolean ready = false;
    private EditText editor;
    private Spinner languages;
    private TextView status;

    private final String[] names = {
        "বাংলা (Bengali)",
        "অসমীয়া (Assamese)",
        "हिन्दी (Hindi)",
        "English"
    };

    private final Locale[] locales = {
        new Locale("bn", "IN"),
        new Locale("as", "IN"),
        new Locale("hi", "IN"),
        Locale.US
    };

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(1);
        root.setPadding(24, 28, 24, 20);

        TextView title = new TextView(this);
        title.setText("FKTV AI Voice Studio");
        title.setTextSize(25);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Multilingual AI News Reader");
        sub.setTextSize(15);
        root.addView(sub);

        TextView label = new TextView(this);
        label.setText("ভাষা নির্বাচন করুন / Select language");
        root.addView(label);

        languages = new Spinner(this);
        languages.setAdapter(new ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            names
        ));
        root.addView(languages);

        editor = new EditText(this);
        editor.setHint("এখানে সংবাদ বা লেখা লিখুন");
        editor.setGravity(48);
        editor.setMinLines(8);

        root.addView(editor,
            new LinearLayout.LayoutParams(-1, 0, 1));

        Button speak = new Button(this);
        speak.setText("Speak / পড়ে শোনান");
        root.addView(speak);

        Button stop = new Button(this);
        stop.setText("Stop");
        root.addView(stop);

        status = new TextView(this);
        status.setText("Voice engine চালু হচ্ছে...");
        root.addView(status);

        setContentView(root);

        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                ready = true;
                status.setText("প্রস্তুত। লেখা পড়াতে পারেন।");
            } else {
                status.setText("TTS চালু করা যায়নি।");
            }
        });

        speak.setOnClickListener(v -> readText());

        stop.setOnClickListener(v -> {
            if (tts != null) tts.stop();
            status.setText("পাঠ বন্ধ।");
        });
    }

    private void readText() {
        if (!ready) {
            status.setText("Voice engine প্রস্তুত নয়।");
            return;
        }

        String s = editor.getText().toString().trim();

        if (s.isEmpty()) {
            editor.setError("লেখা লিখুন");
            return;
        }

        int i = tts.setLanguage(
            locales[languages.getSelectedItemPosition()]
        );

        if (i == TextToSpeech.LANG_MISSING_DATA ||
            i == TextToSpeech.LANG_NOT_SUPPORTED) {
            status.setText("এই ভাষার Voice Data ফোনে নেই।");
            return;
        }

        tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, "FKTV_NEWS");
        status.setText("পড়া হচ্ছে...");
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
