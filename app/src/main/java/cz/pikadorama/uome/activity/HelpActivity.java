package cz.pikadorama.uome.activity;

import android.os.Bundle;
import android.widget.TextView;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.format.MessageFormatter;

public class HelpActivity extends UomeActivity {

    private final MessageFormatter messageFormatter = new MessageFormatter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView simpleDebtsText = requireView(R.id.simpleDebtsText);
        simpleDebtsText.setText(messageFormatter.getHtml(R.string.help_simple_debts_text));

        TextView groupDebtsText = requireView(R.id.groupDebtsText);
        groupDebtsText.setText(messageFormatter.getHtml(R.string.help_group_debts_text));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.help;
    }

}
