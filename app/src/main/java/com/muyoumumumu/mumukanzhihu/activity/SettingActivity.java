package com.muyoumumumu.mumukanzhihu.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.muyoumumumu.mumukanzhihu.R;

public class SettingActivity extends AppCompatActivity {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSupportActionBar((Toolbar) findViewById(R.id.tool_bar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //记
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_container,new SettingPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public static class SettingPreferenceFragment extends PreferenceFragmentCompat {
        /**
         * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
         * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
         * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
         *
         * @param savedInstanceState If the fragment is being re-created from
         *                           a previous saved state, this is the state.
         * @param rootKey            If non-null, this preference fragment should be rooted at the
         *                           {@link PreferenceScreen} with this key.
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.setting_preference_fragment);

            sp = getActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
            editor = sp.edit();

            //保存设置信息
            findPreference("no_pic_mode").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
                    editor.putBoolean("no_pic_mode",preference.getSharedPreferences().getBoolean("no_pic_mode",false));
                    editor.apply();
                    return false;
                }
            });

            findPreference("use_other_browser").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
                    editor.putBoolean("use_other_browser",preference.getSharedPreferences().getBoolean("use_other_browser",false));
                    editor.apply();
                    return false;
                }
            });

            findPreference("not_load_splash").setOnPreferenceClickListener(new android.support.v7.preference.Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(android.support.v7.preference.Preference preference) {
                    editor.putBoolean("not_load_splash",preference.getSharedPreferences().getBoolean("not_load_splash",false));
                    editor.apply();
                    return false;
                }
            });
        }
    }
}
