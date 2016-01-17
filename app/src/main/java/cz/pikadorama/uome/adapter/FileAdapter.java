package cz.pikadorama.uome.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.UomeListAdapter;
import cz.pikadorama.uome.common.format.DateTimeFormatter;
import cz.pikadorama.uome.common.util.Views;

import static com.google.common.base.Preconditions.checkArgument;

public class FileAdapter extends UomeListAdapter<File> {

    private final DateTimeFormatter dateTimeFormatter;

    private File currentDirectory;

    public FileAdapter(Context context) {
        super(context);
        this.dateTimeFormatter = DateTimeFormatter.showDateTime(context).showYearAlways();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.item_file, parent, false);

        ImageView icon = Views.require(layout, R.id.fileIcon);
        TextView name = Views.require(layout, R.id.fileName);
        TextView lastModified = Views.require(layout, R.id.fileLastModified);
        TextView size = Views.require(layout, R.id.fileSize);

        File item = getItem(position);

        if (position == 0 && item.equals(currentDirectory.getParentFile())) {
            icon.setImageResource(R.drawable.ic_up_directory);
        } else if (item.isDirectory()) {
            icon.setImageResource(R.drawable.ic_directory);
            name.setText(item.getName());
            lastModified.setText(dateTimeFormatter.format(new Date(item.lastModified())));

            boolean canRead = item.canRead();
            name.setEnabled(canRead);
            lastModified.setEnabled(canRead);
        } else {
            icon.setImageResource(R.drawable.ic_file);
            name.setText(item.getName());
            lastModified.setText(dateTimeFormatter.format(new Date(item.lastModified())));
            size.setText(Formatter.formatFileSize(getContext(), item.length()));
        }

        return layout;
    }

    /**
     * Loads this adapter with contents of the given directory. First comes the parent directory (if exists), then all
     * the sub-directories in alphabetical order, followed by all the files in alphabetical order.
     *
     * @param directory A directory that can be read
     * @throws IllegalArgumentException if <b>directory</b> is not an actual directory or the current context doesn't
     *             have the permission to read it
     */
    public void setDirectory(File directory) {
        checkArgument(directory.isDirectory(), "%s is not a directory", directory);
        checkArgument(directory.canRead(), "cannot read directory %s", directory);

        currentDirectory = directory;
        reset(list(currentDirectory));
    }

    private static List<File> list(File directory) {
        List<File> contents = new ArrayList<>();

        File parent = directory.getParentFile();
        if (parent != null) {
            contents.add(parent);
        }
        contents.addAll(list(directory, isDirectory));
        contents.addAll(list(directory, isFile));

        return contents;
    }

    private static List<File> list(File directory, FileFilter filter) {
        File[] contents = directory.listFiles(filter);
        Arrays.sort(contents, caseInsensitiveOrder);
        return Arrays.asList(contents);
    }

    private static final FileFilter isDirectory = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private static final FileFilter isFile = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile();
        }
    };

    private static final Comparator<File> caseInsensitiveOrder = new Comparator<File>() {
        @Override
        public int compare(File file1, File file2) {
            return file1.getPath().compareToIgnoreCase(file2.getPath());
        }
    };
}
