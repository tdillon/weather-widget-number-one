package tgd.mindless.drone.weatherwidgetnumberone.redux

import android.content.Context
import groovy.json.JsonSlurper

class ThemeDAO2 {

//    static ThemesClass[] getThemes(Context context) {
//        def jsonSlurper = new JsonSlurper()
//
//        ThemesClass[] object = jsonSlurper.parse( new InputStreamReader(context.getAssets().open("themes.json")))
//
//        return object
//    }

    Theme[] getThemes2(InputStream is) {
        def jsonSlurper = new JsonSlurper()

        Theme[] object = jsonSlurper.parse(is)

        return object
    }
}
