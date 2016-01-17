package cz.kns.uome.common.util;

import android.view.View;

public class Views {

    private Views() {}

    /**
     * Static generic version of {@link View#findViewById(int)} which automatically casts the returned view to the type
     * of the reference to which it is assigned. This cast is unchecked.
     *
     * @param root a view whose children will be searched for the given ID
     * @param resourceId The ID to search for
     * @return the view if found or null otherwise
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T find(View root, int resourceId) {
        return (T) root.findViewById(resourceId);
    }

    /**
     * Variant of {@link #find(View, int)} which never returns null if no view with the given ID is found in the given
     * root. Instead, it throws {@link IllegalStateException} in such case.
     *
     * @param root a view whose children will be searched for the given ID
     * @param resourceId The ID to search for
     * @return the view, never null
     * @throws IllegalStateException if no view with the given ID is found
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T require(View root, int resourceId) {
        T view = (T) root.findViewById(resourceId);
        if (view == null) {
            throw new IllegalStateException("View with ID " + resourceId + " not found in the root " + root);
        }
        return view;
    }

}
