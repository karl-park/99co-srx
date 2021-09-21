package sg.searchhouse.agentconnect.dependency.module

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ViewModule(context: Context) {
    private var mContext: Context = context

    @Provides
    fun provideContext(): Context {
        return mContext
    }
}