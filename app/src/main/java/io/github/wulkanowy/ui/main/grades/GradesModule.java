package io.github.wulkanowy.ui.main.grades;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;

@Module
public abstract class GradesModule {

    @Binds
    abstract GradesContract.Presenter provideGradesPresenter(GradesPresenter gradesPresenter);

    @Provides
    static FlexibleAdapter<GradesHeader> provideGradesAdapter() {
        return new FlexibleAdapter<>(null);
    }

    @Provides
    static FlexibleAdapter<GradesSummarySubItem> provideGradesSummaryAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
