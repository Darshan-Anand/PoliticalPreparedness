package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import timber.log.Timber

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        val context = view.context
        Timber.d("Photo Uri: $uri")
        Glide.with(context)
            .load(src)
            .error(R.drawable.ic_profile)
            .placeholder(R.drawable.ic_profile)
            .centerCrop()
            .into(view)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: MutableLiveData<String>) {
    Timber.d("spinner value: ${value.value}")
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value.value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)

    }
}

@InverseBindingAdapter(attribute = "stateValue")
fun Spinner.getSelectedValue(): String = this.selectedItem.toString()


@BindingAdapter("app:stateValueAttrChanged")
fun Spinner.setListeners(listener: InverseBindingListener?) {
    listener?.let {
        this.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listener.onChange()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do Nothing
            }
        }

    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

