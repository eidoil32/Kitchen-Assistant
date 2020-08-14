package com.kitchas.kitchenassistant.utils;

import android.text.Editable;
import android.text.TextWatcher;

public interface CustomTextWatcher extends TextWatcher {
    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        textChanged();
    }

    void textChanged();

    @Override
    default void afterTextChanged(Editable editable) { }
}
