package sg.searchhouse.agentconnect.view.activity.agent.client

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.layout_action_success.*
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.ActivityClientSendMessageBinding
import sg.searchhouse.agentconnect.enumeration.app.ClientModeOption
import sg.searchhouse.agentconnect.model.api.agent.SRXPropertyUserPO
import sg.searchhouse.agentconnect.enumeration.status.ApiStatus
import sg.searchhouse.agentconnect.util.*
import sg.searchhouse.agentconnect.view.activity.base.ViewModelActivity
import sg.searchhouse.agentconnect.view.adapter.agent.client.ClientPhotoAdapter
import sg.searchhouse.agentconnect.viewmodel.activity.agent.client.ClientSendMessageViewModel

class ClientSendMessageActivity :
    ViewModelActivity<ClientSendMessageViewModel, ActivityClientSendMessageBinding>() {

    private lateinit var adapter: ClientPhotoAdapter

    companion object {
        private const val EXTRA_KEY_CLIENT_DIALOG_OPTION = "EXTRA_KEY_CLIENT_DIALOG_OPTION"
        private const val EXTRA_KEY_CLIENTS = "EXTRA_KEY_CLIENTS"

        private const val MAX_LIMIT_IMAGES = 5

        private const val REQUEST_CODE_GALLERY = 100
        private const val REQUEST_CODE_CAMERA = 101

        fun launch(
            activity: Activity,
            optionMode: ClientModeOption,
            clients: String?,
            requestCode: Int
        ) {
            val intent = Intent(activity, ClientSendMessageActivity::class.java)
            intent.putExtra(EXTRA_KEY_CLIENT_DIALOG_OPTION, optionMode)
            clients?.run { intent.putExtra(EXTRA_KEY_CLIENTS, clients) }
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupExtraParams()
        setupEditText()
        performRequest()
        setupListAndAdapter()
        observeLiveData()
        handleListeners()
    }

    private fun setupExtraParams() {
        intent.getSerializableExtra(EXTRA_KEY_CLIENT_DIALOG_OPTION)?.run {
            viewModel.mode.value = this as ClientModeOption
        } ?: throw IllegalArgumentException("Missing client dialog option")

        intent.getStringExtra(EXTRA_KEY_CLIENTS)?.run {
            viewModel.clients.value = JsonUtil.parseListOrNull(this, SRXPropertyUserPO::class.java)
        }
    }

    private fun setupEditText() {
        binding.etMessage.requestFocus()
    }

    private fun performRequest() {
        viewModel.getAgentDetails()
    }

    private fun setupListAndAdapter() {
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.alignItems = AlignItems.STRETCH

        adapter = ClientPhotoAdapter(viewModel.photos, onRemovePhoto = { uri, position ->
            val tempUris = viewModel.uploadedPhotoUris.value ?: return@ClientPhotoAdapter
            viewModel.uploadedPhotoUris.value = tempUris - uri
            viewModel.photos.remove(uri)
            adapter.notifyItemRemoved(position)
        })
        binding.listImages.layoutManager = layoutManager
        binding.listImages.adapter = adapter
    }

    private fun onImagePickerClicked() {
        dialogUtil.showImagePickerDialog { _, selectedOption ->
            when (selectedOption) {
                resources.getString(R.string.label_gallery) -> IntentUtil.openGallery(
                    activity = this,
                    requestCode = REQUEST_CODE_GALLERY
                )
                resources.getString(R.string.label_camera) -> IntentUtil.openCamera(
                    this,
                    REQUEST_CODE_CAMERA
                )
                else -> throw Throwable("Wrong image picker type")
            }
        }
    }

    private fun observeLiveData() {
        viewModel.mode.observe(this, Observer {
            if (it != null) {
                binding.collapsingToolbar.title = getString(it.title)
            }
        })

        viewModel.clients.observe(this, Observer { list ->
            val items = list ?: return@Observer
            if (items.isNotEmpty()) {
                val names = items.map { it.name }
                when (names.size) {
                    1 -> {
                        viewModel.clientNames.value = names.first()
                    }
                    2 -> {
                        viewModel.clientNames.value = "${names.first()} and ${names.last()}"
                    }
                    else -> {
                        viewModel.clientNames.value =
                            "${names.first()}, ${names[1]} and ${names.size - 2} others"
                    }
                }
            }
        })

        viewModel.sendMessageResponse.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.SUCCESS -> {
                    val items = viewModel.clients.value ?: return@Observer
                    val modeLabel = viewModel.mode.value ?: return@Observer
                    viewModel.successMessage.value = resources.getQuantityString(
                        R.plurals.msg_success_client_send_messages,
                        items.size,
                        NumberUtil.formatThousand(items.size),
                        getString(modeLabel.label)
                    )
                }
                ApiStatus.StatusKey.ERROR -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })

        viewModel.isLinkShowed.observe(this, Observer { isShowed ->
            if (isShowed == null || !isShowed) {
                viewModel.link.value = null
            }
        })

        viewModel.uploadedPhotoUris.observe(this, Observer { uris ->
            if (uris != null) {
                viewModel.isAttachedPhotosShowed.value = uris.isNotEmpty()
            }
        })

        viewModel.getAgentDetailStatus.observe(this, Observer {
            when (it.key) {
                ApiStatus.StatusKey.FAIL -> {
                    ViewUtil.showMessage(it.error?.error)
                }
                else -> {
                    //do nothing
                }
            }
        })
    }

    private fun handleListeners() {
        binding.btnLink.setOnClickListener {
            viewModel.isLinkShowed.value = !(viewModel.isLinkShowed.value ?: false)
        }
        binding.btnPhoto.setOnClickListener {
            val uris = viewModel.uploadedPhotoUris.value ?: emptyList()
            if (uris.size < MAX_LIMIT_IMAGES) {
                onImagePickerClicked()
            }
        }
        btn_back.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    data?.run {
                        when {
                            this.data != null -> {
                                //single select
                                this.data?.let { uri -> uploadPhotos(listOf(uri)) }
                            }
                            this.clipData != null -> {
                                //multi select
                                val itemCount = this.clipData?.itemCount ?: return@run
                                val tempUris = arrayListOf<Uri>()
                                for (i in 0 until itemCount) {
                                    this.clipData?.getItemAt(i)
                                        ?.let { data -> tempUris.add(data.uri) }
                                }
                                uploadPhotos(tempUris)
                            }
                            else -> println("do nothing")
                        }
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    data?.extras?.run {
                        val bitmap = this.get("data") as Bitmap
                        val file = ImageUtil.getFileFromBitmap(
                            context = this@ClientSendMessageActivity,
                            bitmap = bitmap
                        )
                        ImageUtil.getUriFromFile(this@ClientSendMessageActivity, file)?.let {
                            uploadPhotos(listOf(it))
                        }
                    }
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun uploadPhotos(uris: List<Uri>) {
        val savedPhotos = viewModel.uploadedPhotoUris.value ?: listOf()
        if (uris.isNotEmpty() && uris.size <= MAX_LIMIT_IMAGES && savedPhotos.size + uris.size <= MAX_LIMIT_IMAGES) {
            viewModel.photos.addAll(uris)
            adapter.notifyDataSetChanged()
            viewModel.uploadedPhotoUris.value = savedPhotos + uris
            viewModel.maxPhotosError.value = null
        } else {
            viewModel.maxPhotosError.value = getString(
                R.string.error_msg_max_photo_limit,
                MAX_LIMIT_IMAGES
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionUtil.REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    IntentUtil.openGallery(
                        activity = this,
                        requestCode = REQUEST_CODE_GALLERY
                    )
                }
            }
            PermissionUtil.REQUEST_CODE_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    IntentUtil.openCamera(this, REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_client_send_message
    }

    override fun getViewModelClass(): Class<ClientSendMessageViewModel> {
        return ClientSendMessageViewModel::class.java
    }

    override fun bindViewModelXml() {
        binding.viewModel = viewModel
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }
}