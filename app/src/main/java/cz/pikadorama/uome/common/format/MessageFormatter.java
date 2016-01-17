package cz.pikadorama.uome.common.format;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import java.text.MessageFormat;

import cz.pikadorama.uome.common.util.CharSequences;

/**
 * Helper for formatting localized messages.
 */
public class MessageFormatter {

    private final Context context;

    /**
     * @param context the context that will be used to retrieve the localized strings
     */
    public MessageFormatter(Context context) {
        this.context = context;
    }

    /**
     * Formats the given arguments using a locale specific pattern as in {@link MessageFormat}.
     *
     * @param patternStringId resource ID of the string used as the pattern
     * @param arguments the arguments for the pattern
     * @return the formatted localized message
     */
    public String format(int patternStringId, Object... arguments) {
        return MessageFormat.format(context.getString(patternStringId), arguments);
    }

    /**
     * Renders a styled localized message from a string with HTML tags.
     *
     * @param stringId resource ID of the string
     * @return the HTML-styled localized message
     */
    public CharSequence getHtml(int stringId) {
        Spanned html = Html.fromHtml(context.getString(stringId));
        return CharSequences.trim(html);
    }

    /**
     * Formats the given arguments using a locale specific pattern as in {@link MessageFormat} and then styles the
     * formatted text using the HTML tags in it.
     *
     * @param patternStringId resource ID of the string with HTML tags that is used as the pattern
     * @param arguments the arguments for the pattern
     * @return the HTML-styled, formatted localized message
     */
    public CharSequence formatHtml(int patternStringId, Object... arguments) {
        String raw = format(patternStringId, arguments);
        Spanned html = Html.fromHtml(raw);
        return CharSequences.trim(html);
    }

}
