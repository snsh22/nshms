package music.streaming.dev.snsh.musicstreaming.utly;

import com.orhanobut.hawk.Hawk;

import music.streaming.dev.snsh.musicstreaming.App;

/**
 * Created by androiddeveloper on 22-Feb-17.
 */

public class MLanguage {

    public static boolean getSelectedLanguage() {
        Hawk.init(App.getApplication()).build();
        return Hawk.get(MConstants.SELECTED_LANGUAGE, true);
    }

    public static void setSelectedLanguage(boolean selectedLanguage) {
        Hawk.init(App.getApplication()).build();
        if (Hawk.contains(MConstants.SELECTED_LANGUAGE)) {
            Hawk.delete(MConstants.SELECTED_LANGUAGE);
        }
        Hawk.put(MConstants.SELECTED_LANGUAGE, selectedLanguage);
    }
}
