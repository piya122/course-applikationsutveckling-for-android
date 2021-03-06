package com.kindborg.mattias.dialpadapplication;

import com.kindborg.mattias.dialpadapplication.DialPadView.*;

import android.content.*;
import android.net.Uri;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends BaseActivity implements DialPadView.IOnDialNumberListener {

    private static final String INSTANCESTATE_KEYSOUNDTYPE = "KEYSOUNDTYPE";
    private static final String INSTANCESTATE_NUMBER = "NUMBER";
    private static final String DOWNLOAD_URL = "http://dt031g.programvaruteknik.nu/dialpad/sounds/";
    private static final String DOWNLOAD_DIRECTORY = ExternalStorage.createPath("dialpad/sounds/");

    private DialPadView dialPadView;
    private boolean isExternalStorageReadable;
    private boolean isExternalStorageWritable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialPadView = (DialPadView) findViewById(R.id.mainactivity_dialpadview);
        dialPadView.setOnDialNumberListener(this);

        // Restore instance state
        if (hasInstanceState(savedInstanceState, INSTANCESTATE_KEYSOUNDTYPE)) {
            dialPadView.setKeySoundType((DialPadView.KeySoundType) savedInstanceState.getSerializable(INSTANCESTATE_KEYSOUNDTYPE));
        }
        if (hasInstanceState(savedInstanceState, INSTANCESTATE_NUMBER)) {
            dialPadView.setNumber(savedInstanceState.getString(INSTANCESTATE_NUMBER));
        }

        isExternalStorageReadable = ExternalStorage.isExternalStorageReadable();
        isExternalStorageWritable = ExternalStorage.isExternalStorageWritable();

        // Inform user if external storage isn't readable
        if (!isExternalStorageReadable) {
            Toast
                .makeText(this, R.string.mainactivity_nosdcard, Toast.LENGTH_SHORT)
                .show();
        }
    }

    @Override
    public void onDialNumber(String telephoneNumber) {
        // Create intent opening the phone dialer
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(telephoneNumber)));

        if (IntentExtensions.isIntentAvailable(this, intent)) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Disable download menu item if external storage isn't writable
        if (!isExternalStorageWritable) {
            MenuItem downloadMenuItem = menu.findItem(R.id.mainmenu_download);
            downloadMenuItem.setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainmenu_download:
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra(DownloadActivity.EXTRA_SOURCEURL, DOWNLOAD_URL);
                intent.putExtra(DownloadActivity.EXTRA_TARGETDIRECTORY, DOWNLOAD_DIRECTORY);
                startActivity(intent);
                return true;

            case R.id.mainmenu_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_VOICEDIRECTORY, DOWNLOAD_DIRECTORY);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateDialPadSound();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(INSTANCESTATE_KEYSOUNDTYPE, dialPadView.getKeySoundType());
        outState.putString(INSTANCESTATE_NUMBER, dialPadView.getNumber());
    }

    private void updateDialPadSound() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String soundType = sharedPreferences.getString(SettingsActivity.KEY_SOUNDTYPE, null);
        String voiceFile = sharedPreferences.getString(SettingsActivity.KEY_VOICEFILE, null);

        if (soundType == null || soundType.equals("beep") || !ExternalStorage.directoryExists(voiceFile)) {
            dialPadView.setKeySoundType(KeySoundType.beep);
        } else {
            dialPadView.setVoiceFile(voiceFile);
            dialPadView.setKeySoundType(KeySoundType.voice);
        }
    }
}