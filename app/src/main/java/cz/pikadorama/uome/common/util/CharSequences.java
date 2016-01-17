package cz.pikadorama.uome.common.util;

public class CharSequences {

    private CharSequences() {}

    public static CharSequence trim(CharSequence sequence) {
        int start = 0;
        while (start < sequence.length() && Character.isWhitespace(sequence.charAt(start))) {
            start++;
        }

        int end = sequence.length();
        while (end > start && Character.isWhitespace(sequence.charAt(end - 1))) {
            end--;
        }

        return sequence.subSequence(start, end);
    }
}
