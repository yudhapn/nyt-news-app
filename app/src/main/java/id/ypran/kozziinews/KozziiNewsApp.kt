package id.ypran.kozziinews

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import id.ypran.kozziinews.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KozziiNewsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KozziiNewsApp)
            modules(appComponent)
        }
        val shared = getSharedPreferences("user_preference", MODE_PRIVATE)
        val themeMode = shared.getInt("THEME_MODE", 0)
        AppCompatDelegate.setDefaultNightMode(
            if (themeMode == 2)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}