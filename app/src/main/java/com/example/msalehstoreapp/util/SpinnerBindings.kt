package com.example.msalehstoreapp.util

import android.R
import android.widget.ArrayAdapter
import androidx.databinding.BindingAdapter
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import kotlinx.android.synthetic.main.admin_enrollfrag.*
import java.text.FieldPosition

    @BindingAdapter("entries")
    fun MaterialBetterSpinner.setEntries(entries: List<String>?) {
        var adapter = ArrayAdapter(this.context,
            R.layout.select_dialog_item,
            entries?: listOf(""))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.setAdapter(adapter)
    }

    @BindingAdapter("onItemSelected")
    fun MaterialBetterSpinner.setItemSelectedListener(doSomething :()->Runnable ) {
        doSomething()
    }

    @BindingAdapter("newValue")
    fun MaterialBetterSpinner.setNewValue(newValue: Any?) {
      //  setSpinnerValue(newValue)
    }
