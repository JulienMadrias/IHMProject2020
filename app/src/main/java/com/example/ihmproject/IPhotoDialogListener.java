package com.example.ihmproject;

import androidx.fragment.app.DialogFragment;

public interface IPhotoDialogListener {
    public void onPhotoClik(DialogFragment dialog);
    public void onImportPhotoClick(DialogFragment dialog);
    public void onCancel(DialogFragment dialog);
}
