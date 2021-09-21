package sg.searchhouse.agentconnect.view.fragment.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentQrCodeBinding
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.fragment.base.ViewModelFullWidthDialogFragment
import sg.searchhouse.agentconnect.viewmodel.fragment.common.QrCodeDialogViewModel

class QrCodeDialogFragment :
    ViewModelFullWidthDialogFragment<QrCodeDialogViewModel, DialogFragmentQrCodeBinding>() {
    companion object {
        private const val TAG = "InviteAgentClientDialogFragment"
        private const val ARGUMENT_URL = "ARGUMENT_URL"

        fun newInstance(url: String): QrCodeDialogFragment {
            val fragment = QrCodeDialogFragment()
            val arguments = Bundle()
            arguments.putString(ARGUMENT_URL, url)
            fragment.arguments = arguments
            return fragment
        }

        fun launch(supportFragmentManager: FragmentManager, url: String) {
            newInstance(url).show(supportFragmentManager, TAG)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArguments()
        setOnClickListeners()
        observeLiveData()
    }

    private fun setOnClickListeners() {
        binding.btnLink.setOnClickListener {
            viewModel.url.value?.run { IntentUtil.visitUrl(it.context, this) }
        }
        binding.ivQrCode.setOnLongClickListener {
            showDialogToSaveQRCode()
            return@setOnLongClickListener true
        }
    }

    private fun showDialogToSaveQRCode() {
        dialogUtil.showActionDialog(
            R.string.msg_save_qr_to_photo_album,
            null,
            R.string.label_save,
            R.string.label_cancel
        ) {
            viewModel.qrCode.value?.run {
                val newBitmap = generateQRCodeBitmap(this)
                var fileName = getString(R.string.file_name_client_invite)
                SessionUtil.getCurrentUser()?.let { userPO ->
                    fileName += "_" + userPO.name
                }
                ImageUtil.saveImageToGallery(requireContext(), newBitmap, fileName, fileName) {
                    ViewUtil.showMessage(R.string.msg_save_qr_code_in_gallery)
                }
            }
        }
    }

    private fun generateQRCodeBitmap(bitmap: Bitmap): Bitmap {
        val newBitmap = Bitmap.createBitmap(bitmap)
        val canvas = Canvas(newBitmap)
        val paint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 12f
        paint.color = Color.BLACK
        canvas.drawText(getString(R.string.title_qr_code), 20f, 25f, paint)
        return newBitmap
    }

    private fun observeLiveData() {
        viewModel.url.observe(viewLifecycleOwner, {
            viewModel.generateQrCode(it)
        })
        viewModel.qrCode.observe(viewLifecycleOwner, { bitmap ->
            bitmap?.run { binding.ivQrCode.setImageBitmap(this) }
        })
    }

    private fun setupArguments() {
        val url = arguments?.getString(ARGUMENT_URL, null)
            ?: throw IllegalArgumentException("Missing `ARGUMENT_URL` in `QrCodeDialogFragment`!")
        viewModel.url.postValue(url)
    }

    override fun getLayoutResId(): Int {
        return R.layout.dialog_fragment_qr_code
    }

    override fun getViewModelClass(): Class<QrCodeDialogViewModel> {
        return QrCodeDialogViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getViewModelKey(): String? {
        return null
    }
}