package sg.searchhouse.agentconnect.view.activity.searchoption

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity

abstract class LookupBaseActivity<T : ViewModel, U : ViewDataBinding> : ViewModelActivity<T, U>() {
    private lateinit var returnOption: ReturnOption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnOption = intent.extras?.getSerializable(EXTRA_RETURN_OPTION) as ReturnOption?
            ?: ReturnOption.SEARCH
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lookup_search, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItemSearch = menu?.findItem(R.id.menu_item_search)
        val menuItemSave = menu?.findItem(R.id.menu_item_save)
        when (returnOption) {
            ReturnOption.SEARCH -> {
                menuItemSearch?.isVisible = true
                menuItemSave?.isVisible = false
            }
            ReturnOption.SAVE -> {
                menuItemSearch?.isVisible = false
                menuItemSave?.isVisible = true
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_search, R.id.menu_item_save -> {
                onReturnMenuItemClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @IdRes
    fun getReturnMenuItemResId(): Int {
        return when (returnOption) {
            ReturnOption.SEARCH -> R.id.menu_item_search
            ReturnOption.SAVE -> R.id.menu_item_save
        }
    }

    abstract fun onReturnMenuItemClicked()

    enum class ReturnOption {
        SEARCH, SAVE
    }

    companion object {
        const val EXTRA_RETURN_OPTION = "EXTRA_RETURN_OPTION"
    }
}