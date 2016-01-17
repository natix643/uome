package cz.pikadorama.uome.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.util.Views;
import cz.pikadorama.uome.model.Person;

public class AvatarView extends RelativeLayout {

    private ImageView image;
    private TextView text;

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.avatar_view, this);

        image = Views.require(this, R.id.avatarImage);
        text = Views.require(this, R.id.avatarText);
    }

    public void setPerson(Person person) {
        boolean hasImage = false;

        if (!Strings.isNullOrEmpty(person.getImageUri())) {
            Uri uri = Uri.parse(person.getImageUri());
            image.setImageURI(uri);
            hasImage = image.getDrawable() != null;
        }

        if (hasImage) {
            image.setVisibility(View.VISIBLE);
            text.setVisibility(View.INVISIBLE);
        } else {
            text.setVisibility(View.VISIBLE);
            text.setText(person.getName().substring(0, 1));
            text.setBackgroundColor(getColor(person.getId()));

            image.setVisibility(View.INVISIBLE);
        }
    }

    private int getColor(long id) {
        TypedArray colors = getResources().obtainTypedArray(R.array.material_colors_500);
        try {
            int index = Ints.saturatedCast(id) % colors.length();
            return colors.getColor(index, 0);
        } finally {
            colors.recycle();
        }
    }

}
