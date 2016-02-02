package cz.pikadorama.uome.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewInflator;

import java.util.ArrayList;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.view.AvatarView;
import cz.pikadorama.uome.common.view.Views;
import cz.pikadorama.uome.model.Person;

public class PersonMultichoiceViewHolder implements ViewHolder<Person> {

    public static final ViewHoldingListAdapter<Person> createAdapter(Context context) {
        return createAdapter(context, new ArrayList<Person>());
    }

    public static final ViewHoldingListAdapter<Person> createAdapter(Context context, List<Person> persons) {
        return new ViewHoldingListAdapter<>(
                persons,
                ViewInflator.viewInflatorFor(context, R.layout.item_balance),
                new PersonMultichoiceViewHolderFactory());
    }

    private final AvatarView avatar;
    private final TextView nameText;
    private final TextView emailText;

    private PersonMultichoiceViewHolder(View root) {
        this.avatar = Views.require(root, R.id.avatar);
        this.nameText = Views.require(root, R.id.nameText);
        this.emailText = Views.require(root, R.id.emailText);
    }

    @Override
    public void updateViewFor(Person person) {
        avatar.setPerson(person);
        nameText.setText(person.getName());

        if (!person.getEmail().isEmpty()) {
            emailText.setText(person.getEmail());
            emailText.setVisibility(View.VISIBLE);
        } else {
            emailText.setVisibility(View.GONE);
        }
    }

    private static final class PersonMultichoiceViewHolderFactory implements ViewHolderFactory<Person> {

        @Override
        public ViewHolder<Person> createViewHolderFor(View view) {
            return new PersonMultichoiceViewHolder(view);
        }

        @Override
        public Class<? extends ViewHolder<Person>> getHolderClass() {
            return PersonMultichoiceViewHolder.class;
        }
    }
}
