package sg.searchhouse.agentconnect.dependency.component

import dagger.Component
import sg.searchhouse.agentconnect.dependency.module.ApiModule
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.service.GenerateGeneralReportService
import sg.searchhouse.agentconnect.service.GenerateHomeReportService
import sg.searchhouse.agentconnect.service.GenerateXValueReportService
import javax.inject.Singleton

@Component(modules = [AppModule::class, ApiModule::class])
@Singleton
interface ServiceComponent {
    fun inject(generateHomeReportService: GenerateHomeReportService)
    fun inject(generateXValueReportService: GenerateXValueReportService)
    fun inject(generateGeneralReportService: GenerateGeneralReportService)
}