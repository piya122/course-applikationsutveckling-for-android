package com.kindborg.mattias.mediaplayingrobbertranslator;

import com.kindborg.mattias.mediaplayingrobbertranslator.translator.*;

import android.os.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import android.support.v4.app.*;
import android.text.*;
import android.annotation.*;
import android.content.SharedPreferences.*;

public class TranslateActivity extends ActivityBase {

    public static final String EXTRA_ISTRANSLATINGTOROBBER = "com.kindborg.mattias.robbertranslator.TranslateActivity.EXTRA_ISTRANSLATINGTOROBBER";
    private static final String PREFERENCE_TEXTTRANSLATEDTOROBBERLANGUAGE = "preference_texttranslatedtorobberlanguage";
    private static final String PREFERENCE_TEXTTRANSLATEDFROMROBBERLANGUAGE = "preference_texttranslatedfromrobberlanguage";

    private EditText textToTranslate;
    private TextView translatedText;
    private ScrollView translatedTextScrollView;
    private Translator translator;
    private boolean isTranslatingToRobber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_translate);
        textToTranslate = (EditText) findViewById(R.id.translateactivity_texttotranslate);
        translatedText = (TextView) findViewById(R.id.translateactivity_translatedtext);
        translatedTextScrollView = (ScrollView) findViewById(R.id.translateactivity_translatedtextscrollview);

        // Show the Up button in the action bar.
        setupActionBar();

        // Register context menu on translated text
        registerForContextMenu(translatedText);

        translator = new Translator();

        // Determine whether the activity should translate to or from Robber
        // Language
        isTranslatingToRobber = getIsTranslatingToRobber();

        // Set title
        setTitle(isTranslatingToRobber ? R.string.translateto : R.string.translatefrom);

        // Set input description
        TextView inputDescription = (TextView) findViewById(R.id.translateactivity_inputdescription);
        inputDescription.setText(isTranslatingToRobber ? R.string.translateactivity_inputdescription_translatetorobber : R.string.translateactivity_inputdescription_translatefromrobber);

        // Load translated text
        loadTranslatedText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_translate_clear:
                clearTranslatedText();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.contextmenu_translatedtext, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contextmenu_translatedtext_action_clear:
                clearTranslatedText();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    public void onTranslate(View view) {
        String input = textToTranslate.getText().toString();

        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, R.string.translateactivity_noinput, Toast.LENGTH_SHORT).show();
            return;
        }

        String output = isTranslatingToRobber ? translator.toRobberLanguage(input) : translator.fromRobberLanguage(input);

        // Append text and scroll if necessary
        setTranslatedText(translatedText.getText() + output + "\n");
        translatedTextScrollView.fullScroll(View.FOCUS_DOWN);

        // Clear input
        clearText();
    }

    private void loadTranslatedText() {
        String text = isTranslatingToRobber ?
            getPreferences(this).getString(PREFERENCE_TEXTTRANSLATEDTOROBBERLANGUAGE, "") :
            getPreferences(this).getString(PREFERENCE_TEXTTRANSLATEDFROMROBBERLANGUAGE, "");

        translatedText.setText(text);
    }

    private void saveTranslatedText() {
        String key = isTranslatingToRobber ?
            PREFERENCE_TEXTTRANSLATEDTOROBBERLANGUAGE :
            PREFERENCE_TEXTTRANSLATEDFROMROBBERLANGUAGE;

        Editor editor = getPreferencesEditor(this);
        editor.putString(key, translatedText.getText().toString());
        editor.commit();
    }

    private void clearText() {
        textToTranslate.setText("");
    }

    private void setTranslatedText(String text) {
        translatedText.setText(text);

        // Make sure translated text is saved
        saveTranslatedText();
    }

    private void clearTranslatedText() {
        setTranslatedText("");
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean getIsTranslatingToRobber() {
        Bundle bundle = getIntent().getExtras();

        if (!bundle.containsKey(EXTRA_ISTRANSLATINGTOROBBER)) {
            throw new RuntimeException("Cannot start this activity without specifying EXTRA_ISTRANSLATINGTOROBBER");
        }

        return bundle.getBoolean(EXTRA_ISTRANSLATINGTOROBBER);
    }
}