package cz.pikadorama.uome.common.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class Animations {

    private Animations() {}

    private static final long DURATION_MILLIS = 150;

    public static void expand(final View view) {
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(DURATION_MILLIS)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    public static void collapse(final View view) {
        view.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(DURATION_MILLIS)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }


}
