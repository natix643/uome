package cz.pikadorama.uome.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.fragment.BaseDialogFragment;

public class ListLibrariesDialog extends BaseDialogFragment {

    private static final List<Library> LIBRARIES = ImmutableList.of(
            new Library("Android Support Library", "http://developer.android.com/tools/support-library/index.html"),
            new Library("Guava", "https://github.com/google/guava"),
            new Library("android-viewholder-listviews", "https://github.com/rtyley/android-viewholder-listviews"),
            new Library("Java CSV", "http://www.csvreader.com/java_csv.php"));

    private static final String[] LIBRARY_NAMES = Lists.transform(LIBRARIES, new Function<Library, String>() {
        @Override
        public String apply(Library library) {
            return library.name;
        }
    }).toArray(new String[0]);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about_libraries)
                .setItems(LIBRARY_NAMES, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse(LIBRARIES.get(which).uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        getActivity().startActivity(intent);
                    }
                })
                .create();
    }

    private static class Library {
        final String name;
        final String uri;

        Library(String name, String uri) {
            this.name = name;
            this.uri = uri;
        }
    }

}
