package sg.searchhouse.agentconnect.view.activity.watchlist

import android.app.Activity
import android.os.Bundle
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.dsl.launchActivity
import sg.searchhouse.agentconnect.dsl.observeNotNull
import sg.searchhouse.agentconnect.enumeration.app.CriteriaFormType
import sg.searchhouse.agentconnect.enumeration.app.WatchlistEnum
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.ViewUtil

class AddWatchlistCriteriaActivity :
    WatchlistCriteriaFormActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtras()
        setupParams()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.mainStatus.observeNotNull(this) {
            when (it) {
                ApiStatus.StatusKey.SUCCESS -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_add_criteria_success,
                            viewModel.name.value ?: ""
                        )
                    )
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_add_criteria_fail,
                            viewModel.name.value ?: ""
                        )
                    )
                }
                ApiStatus.StatusKey.ERROR -> {
                    ViewUtil.showMessage(
                        getString(
                            R.string.toast_add_criteria_error,
                            viewModel.name.value ?: ""
                        )
                    )
                }
                else -> {
                }
            }
        }
    }

    private fun setupParams() {
        setDefaultRadius()
    }

    private fun setupExtras() {
        when (intent.extras?.getSerializable(EXTRA_WATCHLIST_TYPE) as WatchlistEnum.WatchlistType?) {
            WatchlistEnum.WatchlistType.LISTINGS -> {
                viewModel.hasListings.postValue(true)
                viewModel.hasTransactions.postValue(false)
            }
            WatchlistEnum.WatchlistType.TRANSACTIONS -> {
                viewModel.hasListings.postValue(false)
                viewModel.hasTransactions.postValue(true)
            }
            else -> {
                viewModel.hasListings.postValue(true)
                viewModel.hasTransactions.postValue(true)
            }
        }
    }

    override fun getFormType(): CriteriaFormType {
        return CriteriaFormType.ADD
    }

    companion object {
        private const val EXTRA_WATCHLIST_TYPE = "EXTRA_WATCHLIST_TYPE"

        fun launch(activity: Activity, watchlistType: WatchlistEnum.WatchlistType?) {
            val extras = Bundle()
            extras.putSerializable(EXTRA_WATCHLIST_TYPE, watchlistType)
            activity.launchActivity(AddWatchlistCriteriaActivity::class.java, extras)
        }
    }
}
