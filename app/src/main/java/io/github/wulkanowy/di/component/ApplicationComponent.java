package io.github.wulkanowy.di.component;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.modules.ApplicationModule;
import io.github.wulkanowy.services.SyncJob;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ApplicationContext
    Context getContext();

    RepositoryContract getRepository();

    void inject(WulkanowyApp wulkanowyApp);

    void inject(SyncJob syncJob);
}
