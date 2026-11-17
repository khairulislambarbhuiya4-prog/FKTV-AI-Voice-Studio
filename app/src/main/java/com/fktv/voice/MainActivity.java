
package com.fktv.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.ViewGroup;
import android.widget.*;
import android.graphics.Color;
import java.util.Locale;

public class MainActivity extends Activity {

    TextToSpeech tts;
    EditText inputText;
    Spinner languageSpinner;

    String[] languages = {
        "বাংলা", "অসমীয়া", "हिन्दी", "English"
    };

    Locale[] locales = {
        new Locale("bn", "IN"),
        new Locale("as", "IN"),
        new Locale("hi", "IN"),
        new Locale("en", "IN")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 30, 24, 24);
        layout.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("FKTV Voice Studio");
        title.setTextSize(26);
        title.setTextColor(Color.BLACK);
        title.setGravity(17);
        layout.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("AI Text to Speech Studio");
        subtitle.setTextSize(16);
        subtitle.setGravity(17);
        layout.addView(subtitle);

        languageSpinner = new Spinner(this);
        languageSpinner.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages
        ));
        layout.addView(languageSpinner);

        inputText = new EditText(this);
        inputText.setHint("এখানে আপনার সংবাদ বা লেখা লিখুন...");
        inputText.setGravity(48);
        inputText.setMinLines(6);
        layout.addView(inputText);

        Button speakButton = new Button(this);
        speakButton.setText("▶ SPEAK");
        layout.addView(speakButton);

        Button stopButton = new Button(this);
        stopButton.setText("■ STOP");
        layout.addView(stopButton);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                setLanguage();
            }
        });

        languageSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                    AdapterView<?> parent,
                    android.view.View view,
                    int position,
                    long id
                ) {
                    setLanguage();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            }
        );

        speakButton.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();

            if (!text.isEmpty() && tts != null) {
                setLanguage();
                tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "FKTV_SPEECH"
                );
            } else {
                Toast.makeText(
                    this,
                    "প্রথমে কিছু লিখুন",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        stopButton.setOnClickListener(v -> {
            if (tts != null) {
                tts.stop();
            }
        });
    }

    private void setLanguage() {
        if (tts != null) {
            int position = languageSpinner.getSelectedItemPosition();
            if (position >= 0 && position < locales.length) {
                tts.setLanguage(locales[position]);
            }
        }
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
