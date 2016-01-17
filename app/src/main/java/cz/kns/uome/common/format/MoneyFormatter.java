package cz.kns.uome.common.format;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyFormatter {

    public static final int DECIMAL_PLACES = 2;

    /**
     * Format used for values that are > 0.
     */
    private final DecimalFormat positiveFormat;

    /**
     * Format used for values that are <= 0.
     */
    private final DecimalFormat nonPositiveFormat;

    private MoneyFormatter(DecimalFormat positiveFormat, DecimalFormat nonPositiveFormat) {
        this.positiveFormat = positiveFormat;
        this.nonPositiveFormat = nonPositiveFormat;
    }

    public String format(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return positiveFormat.format(amount);
        } else {
            return nonPositiveFormat.format(amount);
        }
    }

    public static MoneyFormatter withPlusPrefix() {
        DecimalFormat positiveFormat = newDecimalFormat();
        positiveFormat.setPositivePrefix("+");
        DecimalFormat nonPositiveFormat = newDecimalFormat();

        return new MoneyFormatter(positiveFormat, nonPositiveFormat);
    }

    public static MoneyFormatter withoutPlusPrefix() {
        DecimalFormat format = newDecimalFormat();
        return new MoneyFormatter(format, format);
    }

    private static DecimalFormat newDecimalFormat() {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(DECIMAL_PLACES);
        format.setMaximumFractionDigits(DECIMAL_PLACES);
        return format;
    }

}
