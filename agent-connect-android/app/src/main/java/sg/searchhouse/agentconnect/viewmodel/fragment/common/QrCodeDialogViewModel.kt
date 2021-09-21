package sg.searchhouse.agentconnect.viewmodel.fragment.common

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.google.zxing.WriterException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.util.ViewUtil
import sg.searchhouse.agentconnect.viewmodel.base.CoreViewModel

class QrCodeDialogViewModel constructor(application: Application) : CoreViewModel(application) {
    val url = MutableLiveData<String>()
    val qrCode = MutableLiveData<Bitmap>()

    fun generateQrCode(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = QRCode.from(url).withSize(350, 350).bitmap()
                qrCode.postValue(bitmap)
            } catch (e: WriterException) {
                ViewUtil.showMessage(R.string.toast_error_generate_qr_code)
            }
        }
    }
}