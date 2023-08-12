package com.myflexbox.beans;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Bean only to remove vaadin internationalization error/warning to keep console cleaner
 */
@Component
public class CustomI18NProvider implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("fi", "FI"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        // Translation logic here
        return key;
    }
}