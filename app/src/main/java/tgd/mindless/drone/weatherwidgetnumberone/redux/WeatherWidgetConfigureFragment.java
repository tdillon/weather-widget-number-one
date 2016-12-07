package tgd.mindless.drone.weatherwidgetnumberone.redux;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.List;

public class WeatherWidgetConfigureFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        final ListPreference listPreference = (ListPreference) findPreference("pref_theme");

        ThemesClass[] themes = ThemeDAO.getThemes(getActivity());
        List<CharSequence> entries = new ArrayList<>();

        for (ThemesClass theme : themes) {
            entries.add("[" + theme.type.toString() + "] - " + theme.name);
        }

        listPreference.setEntries(new CharSequence[entries.size()]);
        listPreference.setEntryValues(new CharSequence[entries.size()]);
    }
}

