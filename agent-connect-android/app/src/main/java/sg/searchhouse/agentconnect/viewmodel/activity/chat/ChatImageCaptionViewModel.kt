package sg.searchhouse.agentconnect.viewmodel.activity.chat

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.searchhouse.agentconnect.util.ImageUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel
import javax.inject.Inject

class ChatImageCaptionViewModel(application: Application) : CoreViewModel(application) {
    val title = MutableLiveData<String>()
    val imageUriString = MutableLiveData<String>()

    val bitmap = MediatorLiveData<Bitmap>()

    val angle = MediatorLiveData<Float>()

    @Inject
    lateinit var applicationContext: Context

    init {
        viewModelComponent.inject(this)
        setupBitmap()
    }

    private fun setupBitmap() {
        bitmap.addSource(imageUriString) {
            updateBitmap(it)
        }
        bitmap.addSource(angle) {
            imageUriString.value?.run {
                updateBitmap(this, it)
            }
        }
    }

    private fun updateBitmap(imageUriString: String, angle: Float = 0f) {
        CoroutineScope(Dispatchers.IO).launch {
            val thisBitmap =
                ImageUtil.getBitmapFromImageUrl(applicationContext, imageUriString) ?: return@launch
            val maybeRotatedBitmap = if (angle > 0f) {
                ImageUtil.rotateBitmap(thisBitmap, angle)
            } else {
                thisBitmap
            }
            bitmap.postValue(maybeRotatedBitmap)
        }
    }
}
