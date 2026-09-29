package com.smartfarmer.procurement.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import com.smartfarmer.procurement.domain.models.AppLanguageCode
import java.util.Locale

object LocaleHelper {
    private const val PREFS_NAME = "smart_farmer_prefs"
    private const val KEY_LANG = "app_language"

    fun getPersistedLanguage(context: Context): AppLanguageCode {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_LANG, AppLanguageCode.ENGLISH.code) ?: AppLanguageCode.ENGLISH.code
        return AppLanguageCode.values().firstOrNull { it.code == code } ?: AppLanguageCode.ENGLISH
    }

    fun setLocale(context: Context, language: AppLanguageCode): Context {
        persistLanguage(context, language)
        return updateResources(context, language.code)
    }

    private fun persistLanguage(context: Context, language: AppLanguageCode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, language.code).apply()
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "hne" -> Locale("hi", "IN") // Use hi with local dialect override
            else -> Locale(languageCode)
        }
        Locale.setDefault(locale)

        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
