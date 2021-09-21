package sg.searchhouse.agentconnect.dependency.component

import dagger.Component
import sg.searchhouse.agentconnect.dependency.module.ApiModule
import sg.searchhouse.agentconnect.dependency.module.AppModule
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.ClientSendMessageViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.InviteAgentClientsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.MySgHomeClientsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.SearchClientViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.agent.cv.*
import sg.searchhouse.agentconnect.viewmodel.activity.agent.profile.AgentProfileViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.agent.profile.SubscriptionPackageDetailsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.auth.SignInViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.auth.UpdateCredentialsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.calculator.*
import sg.searchhouse.agentconnect.viewmodel.activity.cea.*
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatImageCaptionViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatMessagingInfoViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.chat.ChatMessagingViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.common.LocationSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.*
import sg.searchhouse.agentconnect.viewmodel.activity.listing.community.CommunityHyperTargetViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.community.SearchPlanningAreasViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.portal.PortalListingsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.portal.PortalSettingsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.listing.user.FeaturesCreditApplicationViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.main.MainViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.main.SplashViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.FilterProjectViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectDirectoryViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.projectinfo.ProjectSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.propertynews.PropertyNewsDetailsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.propertynews.PropertyNewsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.report.flashreport.MarketFlashReportFullListViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.report.homereport.HomeReportViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.FilterNewLaunchesViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.NewLaunchesReportsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.report.newlaunches.SendNewLaunchesReportsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.DistrictSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.HdbTownSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.MrtSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.searchoption.SchoolSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.transaction.GroupTransactionsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.transaction.ProjectTransactionsViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.ListingsWatchlistViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.TransactionsWatchlistViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.watchlist.WatchlistCriteriaFormViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueAddressSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.activity.xvalue.XValueViewModel
import sg.searchhouse.agentconnect.viewmodel.base.BaseViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditGeneralInfoFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditTestimonialFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.agent.cv.CvEditTrackRecordFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesGoogleFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.amenity.AmenitiesYourLocationFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.ForgotPasswordViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.MobileVerificationViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.auth.NonSubscriberViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.calculator.CalculatorPropertyDetailsDialogViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.calculator.SellingCalculatorFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting.CertifiedListingViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.certifiedlisting.HomeOwnerDetailViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.community.HyperTargetTemplatesDialogViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.CreateUpdateListingViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.ListingAddressSearchViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.create.PostListingViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.portal.*
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.search.ExportListingsDialogViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.search.SelectedListingsDialogViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.BookAppointmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.FeaturedListingTypesDialogViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.FeaturedPromptViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.listing.user.MyListingsFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.main.FindAgentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.main.chat.*
import sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard.*
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.LoginAsAgentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.MenuViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.main.menu.SwitchServerViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.project.PlanningDecisionsViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.project.ProjectInfoFragmentViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchCommonViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.search.SearchXValueViewModel
import sg.searchhouse.agentconnect.viewmodel.fragment.transaction.*
import javax.inject.Singleton

@Component(modules = [AppModule::class, ApiModule::class])
@Singleton
interface ViewModelComponent {
    // Common
    fun inject(baseViewModel: BaseViewModel)

    //User authentication
    fun inject(signInViewModel: SignInViewModel)

    fun inject(updateCredentialsViewModel: UpdateCredentialsViewModel)
    fun inject(mobileVerificationViewModel: MobileVerificationViewModel)
    fun inject(forgotPasswordViewModel: ForgotPasswordViewModel)
    fun inject(nonSubscriberViewModel: NonSubscriberViewModel)

    //Main
    fun inject(mainViewModel: MainViewModel)

    fun inject(menuViewModel: MenuViewModel)
    fun inject(switchServerViewModel: SwitchServerViewModel)
    fun inject(loginAsAgentViewModel: LoginAsAgentViewModel)

    // Dashboard
    fun inject(dashboardSearchViewModel: DashboardSearchViewModel)

    fun inject(dashboardConversationsViewModel: DashboardConversationsViewModel)
    fun inject(dashboardUserListingsViewModel: DashboardUserListingsViewModel)
    fun inject(dashboardWatchlistTransactionsViewModel: DashboardWatchlistTransactionsViewModel)
    fun inject(dashboardWatchlistListingsViewModel: DashboardWatchlistListingsViewModel)
    fun inject(chatCustomerEnquiryViewModel: ChatCustomerEnquiryViewModel)
    fun inject(dashboardMarketDataViewModel: DashboardMarketDataViewModel)

    // Chat
    fun inject(chatViewModel: ChatViewModel)

    fun inject(allConversationListViewModel: AllConversationListViewModel)
    fun inject(srxAgentsDialogViewModel: SRXAgentsDialogViewModel)
    fun inject(publicConversationListViewModel: PublicConversationListViewModel)
    fun inject(srxConversationListViewModel: SRXConversationListViewModel)
    fun inject(agentConversationListViewModel: AgentConversationListViewModel)
    fun inject(chatMessagingViewModel: ChatMessagingViewModel)
    fun inject(chatMessagingInfoViewModel: ChatMessagingInfoViewModel)
    fun inject(chatImageCaptionViewModel: ChatImageCaptionViewModel)

    // Transaction
    fun inject(groupTransactionsViewModel: GroupTransactionsViewModel)

    fun inject(groupTransactionsFragmentViewModel: GroupTransactionsFragmentViewModel)
    fun inject(projectTransactionsFragmentViewModel: ProjectTransactionsFragmentViewModel)
    fun inject(projectTransactionsViewModel: ProjectTransactionsViewModel)
    fun inject(projectRentalOfficialTransactionsViewModel: ProjectRentalOfficialTransactionsViewModel)
    fun inject(transactionProjectTowerViewModel: TransactionProjectTowerViewModel)
    fun inject(transactionProjectsViewModel: TransactionProjectsViewModel)

    // Listing
    fun inject(searchCommonViewModel: SearchCommonViewModel)

    fun inject(listingsViewModel: ListingsViewModel)
    fun inject(filterListingViewModel: FilterListingViewModel)
    fun inject(listingDetailsViewModel: ListingDetailsViewModel)
    fun inject(listingMediaViewModel: ListingMediaViewModel)
    fun inject(amenitiesViewModel: AmenitiesViewModel)
    fun inject(exportListingsDialogViewModel: ExportListingsDialogViewModel)
    fun inject(selectedListingsDialogViewModel: SelectedListingsDialogViewModel)
    fun inject(featuredListingTypesDialogViewModel: FeaturedListingTypesDialogViewModel)
    fun inject(featuredPromptViewModel: FeaturedPromptViewModel)

    // Search options
    fun inject(hdbTownViewModel: HdbTownSearchViewModel)

    fun inject(districtViewModel: DistrictSearchViewModel)
    fun inject(mrtSearchViewModel: MrtSearchViewModel)
    fun inject(schoolSearchViewModel: SchoolSearchViewModel)

    //Find agent
    fun inject(findAgentViewModel: FindAgentViewModel)

    //listing management
    fun inject(createUpdateListingViewModel: CreateUpdateListingViewModel)

    fun inject(listingAddressSearchViewModel: ListingAddressSearchViewModel)
    fun inject(postListingViewModel: PostListingViewModel)
    fun inject(homeOwnerDetailViewModel: HomeOwnerDetailViewModel)
    fun inject(certifiedListingViewModel: CertifiedListingViewModel)

    fun inject(myListingsViewModel: MyListingsViewModel)
    fun inject(myListingsFragmentViewModel: MyListingsFragmentViewModel)
    fun inject(featuresCreditApplicationViewModel: FeaturesCreditApplicationViewModel)
    fun inject(bookAppointmentViewModel: BookAppointmentViewModel)

    fun inject(pgImportDialogViewModel: PGImportDialogViewModel)
    fun inject(portalListingsViewModel: PortalListingsViewModel)
    fun inject(portalImportSummaryDialogViewModel: PortalImportSummaryDialogViewModel)
    fun inject(portalSettingsViewModel: PortalSettingsViewModel)

    // Amenities
    fun inject(locationSearchViewModel: LocationSearchViewModel)

    fun inject(amenitiesYourLocationFragmentViewModel: AmenitiesYourLocationFragmentViewModel)
    fun inject(amenitiesFragmentViewModel: AmenitiesFragmentViewModel)
    fun inject(amenitiesGoogleFragmentViewModel: AmenitiesGoogleFragmentViewModel)

    // Property News
    fun inject(propertyNewsViewModel: PropertyNewsViewModel)

    fun inject(propertyNewsDetailsViewModel: PropertyNewsDetailsViewModel)

    //Agent cv and profile
    fun inject(cvEditGeneralInfoFragmentViewModel: CvEditGeneralInfoFragmentViewModel)

    fun inject(cvEditTrackRecordFragmentViewModel: CvEditTrackRecordFragmentViewModel)
    fun inject(cvTestimonialsViewModel: CvTestimonialsViewModel)
    fun inject(cvEditTestimonialFragmentViewModel: CvEditTestimonialFragmentViewModel)
    fun inject(cvCreateUrlViewModel: CvCreateUrlViewModel)
    fun inject(agentProfileViewModel: AgentProfileViewModel)
    fun inject(subscriptionPackageDetailsViewModel: SubscriptionPackageDetailsViewModel)
    fun inject(cvListingsViewModel: CvListingsViewModel)
    fun inject(agentCvViewModel: AgentCvViewModel)
    fun inject(mySgHomeClientsViewModel: MySgHomeClientsViewModel)
    fun inject(inviteAgentClientsViewModel: InviteAgentClientsViewModel)
    fun inject(searchClientViewModel: SearchClientViewModel)
    fun inject(clientSendMessageViewModel: ClientSendMessageViewModel)

    // X-value
    fun inject(searchXValueViewModel: SearchXValueViewModel)

    fun inject(xValueAddressSearchViewModel: XValueAddressSearchViewModel)
    fun inject(xValueViewModel: XValueViewModel)
    fun inject(xValueSearchViewModel: XValueSearchViewModel)
    fun inject(cvTransactionsViewModel: CvTransactionsViewModel)
    fun inject(dashboardPropertyNewsViewModel: DashboardPropertyNewsViewModel)
    fun inject(splashViewModel: SplashViewModel)

    //CEA View models
    fun inject(ceaExclusiveMainSectionsViewModel: CeaExclusiveMainSectionsViewModel)

    fun inject(ceaExclusiveDetailSectionsViewModel: CeaExclusiveDetailSectionsViewModel)
    fun inject(ceaExclusiveAgentOwnerSectionsViewModel: CeaExclusiveAgentOwnerSectionsViewModel)
    fun inject(ceaExclusiveAgentPartySectionsViewModel: CeaExclusiveAgentPartySectionsViewModel)
    fun inject(ceaExclusiveConfirmationSectionsViewModel: CeaExclusiveConfirmationSectionsViewModel)

    // Home report
    fun inject(homeReportViewModel: HomeReportViewModel)

    // Project info
    fun inject(projectInfoFragmentViewModel: ProjectInfoFragmentViewModel)
    fun inject(planningDecisionsViewModel: PlanningDecisionsViewModel)

    fun inject(projectDirectoryViewModel: ProjectDirectoryViewModel)
    fun inject(filterProjectViewModel: FilterProjectViewModel)

    // Market watch
    fun inject(marketWatchGraphViewModel: MarketWatchGraphViewModel)

    fun inject(dashboardMarketFlashReportViewModel: DashboardMarketFlashReportViewModel)
    fun inject(marketFlashReportFullListViewModel: MarketFlashReportFullListViewModel)
    fun inject(marketFlashReportViewModel: MarketFlashReportViewModel)

    // Cobroke SMS
    fun inject(smsBlastViewModel: SmsBlastViewModel)
    fun inject(srxChatViewModel: SrxChatViewModel)

    fun inject(newLaunchesReportsViewModel: NewLaunchesReportsViewModel)
    fun inject(filterNewLaunchesViewModel: FilterNewLaunchesViewModel)
    fun inject(sendNewLaunchesReportsViewModel: SendNewLaunchesReportsViewModel)
    fun inject(projectSearchViewModel: ProjectSearchViewModel)

    // Calculators
    fun inject(affordabilityCalculatorViewModel: AffordabilityCalculatorViewModel)
    fun inject(advancedAffordabilityCalculatorViewModel: AdvancedAffordabilityCalculatorViewModel)
    fun inject(buyerStampDutyCalculatorViewModel: BuyerStampDutyCalculatorViewModel)
    fun inject(rentalStampDutyCalculatorViewModel: RentalStampDutyCalculatorViewModel)
    fun inject(sellingCalculatorFragmentViewModel: SellingCalculatorFragmentViewModel)
    fun inject(calculatorPropertyDetailsDialogViewModel: CalculatorPropertyDetailsDialogViewModel)
    fun inject(advancedAffordabilityDetailsViewModel: AdvancedAffordabilityDetailsViewModel)

    // Watchlist
    fun inject(watchlistCriteriaFormViewModel: WatchlistCriteriaFormViewModel)
    fun inject(listingsWatchlistViewModel: ListingsWatchlistViewModel)
    fun inject(transactionsWatchlistViewModel: TransactionsWatchlistViewModel)

    // Sponsor listing
    fun inject(sponsorListingViewModel: SponsorListingViewModel)
    fun inject(searchPlanningAreasViewModel: SearchPlanningAreasViewModel)
    fun inject(communityHyperTargetViewModel: CommunityHyperTargetViewModel)
    fun inject(hyperTargetTemplatesDialogViewModel: HyperTargetTemplatesDialogViewModel)

    //Portal Auto Import
    fun inject(autoImportPortalListingsViewModel: AutoImportPortalListingsViewModel)
    fun inject(autoImportListingsPreviewViewModel: AutoImportListingsPreviewViewModel)
    fun inject(autoImportCertifiedListingsViewModel: AutoImportCertifiedListingsViewModel)
}
