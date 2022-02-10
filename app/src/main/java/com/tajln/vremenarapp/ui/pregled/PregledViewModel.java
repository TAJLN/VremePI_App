package com.tajln.vremenarapp.ui.pregled;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PregledViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PregledViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Osveži za podatke");
    }

    public LiveData<String> getText() {
        return mText;
    }
}