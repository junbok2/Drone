package com.example.droen.runtime.util;

/**
 *
 * This interface exposes a callback method, onInitialize(),
 * that will be called by the Form activity after the app
 * is initialized.  Components must register for the callback
 * by calling Form.registerForOnInitialize().
 *
 * @author rmorelli
 *
 */
public interface OnInitializeListener {

    public void onInitialize();

}
