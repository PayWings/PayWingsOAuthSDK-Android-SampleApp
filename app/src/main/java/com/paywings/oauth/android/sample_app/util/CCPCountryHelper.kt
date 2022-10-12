package com.paywings.oauth.android.sample_app.util

import android.content.Context
import android.telephony.TelephonyManager
import com.hbb20.CCPCountry
import com.hbb20.CountryCodePicker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for com.hbb20:ccp library.
 */
@Singleton
class CCPCountryHelper @Inject constructor(@ApplicationContext val context: Context) {

    /**
     * Get country based on sim card or app locale.
     *
     * @param language set language for localization of countries names.
     * @return detected country
     */
    fun getAutoDetectCountry(language: CountryCodePicker.Language = getDefaultLanguage()): CCPCountry {
        detectSIMCountry(language)?.let {
            return it
        } ?: run {
            return detectLocaleCountry(language) ?: run {
                CCPCountry.getLibraryMasterCountriesEnglish()[0]
            }
        }
    }

    /**
     * Get list of all countries.
     *
     * @param language set language for localization of countries names.
     * @return list of countries.
     */
    fun getCountryList(language: CountryCodePicker.Language = getDefaultLanguage()): List<CCPCountry> {
        return CCPCountry.getLibraryMasterCountryList(context, language)
    }

    private fun detectSIMCountry(language: CountryCodePicker.Language): CCPCountry? {
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simCountryIso?.takeIf { it.isNotBlank() }?.let {
            return CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, language, it)
        }
        return null
    }

    private fun detectLocaleCountry(language: CountryCodePicker.Language): CCPCountry? {
        return CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, language, context.resources.configuration.locales[0].country)
    }

    private fun getDefaultLanguage(): CountryCodePicker.Language {
        val currentLocale = context.resources.configuration.locales[0]
        for (language in CountryCodePicker.Language.values()) {
            if (language.code.equals(other = currentLocale.language, ignoreCase = true)
                && (language.country == null || language.country.equals(other = currentLocale.country, ignoreCase = true)
                        || language.script == null || language.script.equals(other = currentLocale.script, ignoreCase = true)
                        )) {
                return language
            }
        }
        return CountryCodePicker.Language.ENGLISH
    }
}

fun CCPCountry.getCountryCodeWithPlus(): String {
    return "+${this.phoneCode}"
}