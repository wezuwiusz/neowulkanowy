package io.github.wulkanowy.ui.main.exams.tab;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.di.scopes.PerChildFragment;

@Module
public abstract class ExamsTabModule {

    @PerChildFragment
    @Binds
    abstract ExamsTabContract.Presenter provideExamsTabPresenter(ExamsTabPresenter examsTabPresenter);

    @PerChildFragment
    @Provides
    static FlexibleAdapter<ExamsSubItem> provideAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
