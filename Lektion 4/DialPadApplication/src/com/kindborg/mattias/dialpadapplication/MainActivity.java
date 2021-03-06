package com.kindborg.mattias.dialpadapplication;

import android.app.*;
import android.os.*;
import android.view.*;

public class MainActivity extends Activity {

    private static final String INSTANCESTATE_KEYSOUNDTYPE = "KEYSOUNDTYPE";

    private DialPadView dialPadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialPadView = (DialPadView) findViewById(R.id.mainactivity_dialpadview);

        // Restore key sound setting
        if (hasInstanceState(savedInstanceState, INSTANCESTATE_KEYSOUNDTYPE)) {
            dialPadView.setKeySoundType((DialPadView.KeySoundType) savedInstanceState.getSerializable(INSTANCESTATE_KEYSOUNDTYPE));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Make sure menu items are in sync with DialPadView settings
        MenuItem selectedKeySoundTypeMenuItem = dialPadView.getKeySoundType() == DialPadView.KeySoundType.beep ?
            menu.findItem(R.id.mainmenu_keysoundtype_beep) :
            menu.findItem(R.id.mainmenu_keysoundtype_voice);

        selectedKeySoundTypeMenuItem.setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainmenu_keysoundtype_beep:
                dialPadView.setKeySoundType(DialPadView.KeySoundType.beep);
                item.setChecked(true);
                return true;

            case R.id.mainmenu_keysoundtype_voice:
                dialPadView.setKeySoundType(DialPadView.KeySoundType.voice);
                item.setChecked(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(INSTANCESTATE_KEYSOUNDTYPE, dialPadView.getKeySoundType());

    }

    /**
     * Gets value indicating whether specified {@link Bundle} has specified key.
     */
    private boolean hasInstanceState(Bundle bundle, String key) {
        if (bundle == null) {
            return false;
        }

        return bundle.containsKey(key);
    }
}