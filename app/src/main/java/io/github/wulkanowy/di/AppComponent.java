package io.github.wulkanowy.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import io.github.wulkanowy.WulkanowyApp;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        BuilderModule.class
})
public interface AppComponent extends AndroidInjector<WulkanowyApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<WulkanowyApp> {
    }
}
