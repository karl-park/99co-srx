package sg.searchhouse.agentconnect.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.databinding.DialogFragmentDatePickerBinding
import sg.searchhouse.agentconnect.databinding.DialogFullScreenImageBinding
import sg.searchhouse.agentconnect.databinding.DialogTextBoxBinding
import sg.searchhouse.agentconnect.dsl.widget.setupLayoutAnimation
import java.util.*
import javax.inject.Inject

class DialogUtil @Inject constructor(val context: Context) {
    // Input [list] of label res IDs to show simple clickable list dialog with optional [title]
    fun showListDialog(
        @StringRes list: List<Int>,
        onClickListener: ((dialogInterface: DialogInterface, position: Int) -> Unit)? = null,
        @StringRes title: Int? = null
    ): AlertDialog {
        val stringList = list.map { context.getString(it) }
        return showStringListDialog(stringList, onClickListener, title)
    }

    // Input [list] of strings to show simple clickable list dialog with optional [title]
    fun showStringListDialog(
        stringList: List<String>,
        onClickListener: ((dialogInterface: DialogInterface, position: Int) -> Unit)? = null,
        @StringRes title: Int? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title?.let { context.getString(it) })

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, stringList)
        builder.setAdapter(adapter) { dialogInterface, position ->
            onClickListener?.invoke(dialogInterface, position)
        }

        return builder.show()
    }

    fun showWheelPickerDialog(
        lists: List<String>,
        onClickListener: ((dialogInterface: DialogInterface, position: Int) -> Unit)? = null,
        @StringRes title: Int? = null,
        selectedValue: Int? = null
    ): AlertDialog {
        return showWheelPickerDialog(
            lists,
            onClickListener,
            title?.let { context.getString(it) },
            selectedValue
        )
    }

    fun showWheelPickerDialog(
        lists: List<String>,
        onClickListener: ((dialogInterface: DialogInterface, position: Int) -> Unit)? = null,
        title: String? = null,
        selectedValue: Int? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_wheel_picker, null)
        //Set up picker
        val picker: NumberPicker = view.findViewById(R.id.number_picker)
        picker.minValue = 0
        picker.maxValue = lists.size - 1
        picker.displayedValues = lists.toTypedArray()
        picker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        selectedValue?.let { value ->
            picker.value = if (value < 0) {
                0
            } else {
                value
            }
        }
        //builder
        builder.setCancelable(false)
        builder.setTitle(title)
        builder.setNegativeButton(context.getString(R.string.label_cancel), null)
        builder.setPositiveButton(context.getString(R.string.label_done)) { dialogInterface, _ ->
            onClickListener?.invoke(dialogInterface, picker.value)
        }
        builder.setView(view)
        return builder.show()
    }

    fun showImagePickerDialog(
        onClickListener: ((dialogInterface: DialogInterface, selectedOption: String) -> Unit)? = null
    ): AlertDialog {
        val options = listOf(
            context.getString(R.string.label_camera),
            context.getString(R.string.label_gallery)
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.title_upload_image))
        builder.setCancelable(false)
        builder.setNegativeButton(context.getString(R.string.label_cancel), null)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, options)
        builder.setAdapter(adapter) { dialogInterface, position ->
            onClickListener?.invoke(dialogInterface, options[position])
        }
        return builder.show()
    }

    fun showActionDialog(
        @StringRes message: Int,
        @StringRes title: Int? = null,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ) {
        showActionDialog(
            context.getString(message),
            title?.run { context.getString(this) },
            onPositiveButtonClickListener
        )
    }

    fun showActionDialog(
        message: String,
        title: String? = null,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.confirmation_label_yes) { dialogInterface, _ ->
            onPositiveButtonClickListener?.invoke()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.confirmation_label_no) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showActionDialog(
        @StringRes message: Int,
        @StringRes title: Int? = null,
        @StringRes positiveButtonLabel: Int,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        title?.let { builder.setTitle(it) }
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(positiveButtonLabel) { dialogInterface, _ ->
            onPositiveButtonClickListener?.invoke()
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showActionDialog(
        @StringRes message: Int,
        @StringRes title: Int? = null,
        @StringRes positiveButtonLabel: Int,
        @StringRes negativeButtonLabel: Int,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        title?.let { builder.setTitle(it) }
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(positiveButtonLabel) { dialogInterface, _ ->
            onPositiveButtonClickListener?.invoke()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(negativeButtonLabel) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showInformationDialog(
        @StringRes message: Int,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ): AlertDialog {
        return showInformationDialog(context.getString(message), onPositiveButtonClickListener)
    }

    fun showInformationDialog(
        message: String,
        onPositiveButtonClickListener: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(R.string.label_ok)) { dialogInterface, _ ->
            onPositiveButtonClickListener?.invoke()
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showInformationDialogWithSpanned(
        message: Spanned
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(R.string.label_ok)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showProgressDialog(
        @StringRes messageResId: Int? = null,
        isCancellable: Boolean = false
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(messageResId ?: R.string.progress_dialog_message)
        builder.setCancelable(isCancellable)
        return builder.show()
    }

    fun showFullScreenImageDialog(
        url: String,
        caption: String? = null
    ) {
        val dialog = Dialog(context, R.style.FullScreenAlertDialog)
        val binding: DialogFullScreenImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_full_screen_image,
            null,
            false
        )
        ViewUtil.setToolbarHomeIconColor(binding.toolbar, R.color.white)
        binding.url = url
        binding.caption = caption
        binding.toolbar.setNavigationOnClickListener { dialog.dismiss() }
        binding.layoutContainer.setupLayoutAnimation()
        binding.ivFullScreen.setOnClickListener {
            binding.isHideCaption = binding.isHideCaption != true
            binding.executePendingBindings()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

    //show when notification is turn off
    fun showNotificationInfoDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.msg_notification_alert)
        builder.setPositiveButton(context.getString(R.string.label_turn_on)) { _, _ ->
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            }
            context.startActivity(intent)
        }
        return builder.show()
    }

    // Calender style
    fun showDatePickerDialog(
        initialDate: Date? = null,
        onDateChangedListener: ((year: Int, month: Int, dayOfMonth: Int) -> Unit)
    ) {
        val calendar = Calendar.getInstance()
        initialDate?.let { calendar.time = it }
        DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                onDateChangedListener.invoke(year, monthOfYear, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Rolling picker style
    fun showDatePickerDialog2(
        initialDate: Date? = null,
        @StringRes title: Int? = null,
        onDateChangedListener: (year: Int, month: Int, dayOfMonth: Int) -> Unit
    ): AlertDialog {
        val initCalendar = Calendar.getInstance()
        initialDate?.let { initCalendar.time = it }
        val builder = AlertDialog.Builder(context)
        val binding = DialogFragmentDatePickerBinding.inflate(LayoutInflater.from(context))
        binding.datePicker.setDefaultDate(initCalendar)
        builder.setTitle(title ?: R.string.dialog_title_select_date)
        builder.setView(binding.root)
        builder.setPositiveButton(context.getString(R.string.label_ok)) { dialogInterface, _ ->
            val calendar = binding.datePicker.getSelectedDate()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            onDateChangedListener.invoke(year, month, dayOfMonth)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.label_cancel) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return builder.show()
    }

    fun showTextBoxDialog(
        @StringRes title: Int,
        inputType: Int = EditorInfo.TYPE_NULL,
        onSubmit: (text: String) -> Unit
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = DialogTextBoxBinding.inflate(LayoutInflater.from(context))
        builder.setTitle(title)
        builder.setView(binding.root)
        binding.editText.inputType = inputType
        builder.setPositiveButton(context.getString(R.string.label_ok)) { dialogInterface, _ ->
            onSubmit.invoke(binding.editText.text.toString())
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.label_cancel) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        return builder.show()
    }
}