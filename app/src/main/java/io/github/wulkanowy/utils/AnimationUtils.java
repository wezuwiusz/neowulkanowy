package io.github.wulkanowy.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public final class AnimationUtils {

    public static void slideDown(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.f);

        view.setTranslationY(-(view.getHeight() / 2));
        view.animate()
                .translationY(0)
                .alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                        view.setAlpha(1.f);
                    }
                });
    }

    public static void slideUp(final View view) {
        view.animate()
                .translationY(-(view.getHeight() / 2))
                .alpha(0.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // superfluous restoration
                        view.setVisibility(View.GONE);
                        view.setAlpha(1.f);
                        view.setTranslationY(0.f);
                    }
                });
    }
}
