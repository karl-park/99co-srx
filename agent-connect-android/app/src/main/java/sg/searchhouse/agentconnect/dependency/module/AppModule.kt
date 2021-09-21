package sg.searchhouse.agentconnect.dependency.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(application: Application) {
    private var mApplication: Application = application

    @Provides
    fun provideApplication(): Application {
        return mApplication
    }

    @Provides
    fun provideApplicationContext(): Context {
        return mApplication.applicationContext
    }
}