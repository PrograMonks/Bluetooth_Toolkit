/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 PrograMonks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.programonks.bluetoothtoolkit.ui.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.programonks.bluetoothtoolkit.R;
import com.programonks.bluetoothtoolkit.helpers.bt.BtAdapterHelper;
import com.programonks.bluetoothtoolkit.helpers.bt.BtAdapterHelperCallback;
import com.programonks.bluetoothtoolkit.ui.dialogs.FoundBtDevicesDialog;

/**
 * Base class for generic BT functionalities
 *
 * @author PrograMonks
 */
public abstract class BtBaseActivity extends BaseActivity implements BtAdapterHelperCallback,
        FoundBtDevicesDialog.DialogFoundDevicesCallbacks {

    private static final String TAG = "BtBaseActivity";
    protected FoundBtDevicesDialog mFoundBtDevicesDialog;
    protected Button mBtnScan;

    private BtAdapterHelper mBtAdapterHelper;

    public BtAdapterHelper getBtAdapterHelper() {
        return mBtAdapterHelper;
    }


    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState, layoutResID);
        mBtAdapterHelper = new BtAdapterHelper(this);
        getBtAdapterHelper().setCallback(this);
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
    }

    @Override
    public void onDialogFoundDevicesCancel(DialogInterface dialogInterface) {
        uiInvalidateViewsState();
    }

    @Override
    public void onBackPressed() {
        uiInvalidateViewsState();
        finish();
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        mBtnScan = (Button) findViewById(R.id.btn_scan);
    }

    /**
     * Popup dialog to ask the user if
     * they want to enable bluetooth
     */
    protected void askToEnableBluetooth() {
        if (!getBtAdapterHelper().getBtChecker().isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_BT_REQUEST_ID);
        }
    }

    public class ScanBtnListener implements View.OnClickListener {
        public boolean onClick() {
            return isBtEnabled();
        }

        @Override
        public void onClick(View v) {
        }

    }

    /**
     * checks if the BT hardware is currently enabled
     *
     * @return true if enabled, otherwise false
     */
    public boolean isBtEnabled() {
        boolean isBtEnabled;

        if (getBtAdapterHelper().getBtChecker().isEnabled()) {
            isBtEnabled = true;
        } else {
            Log.i(TAG, "Bluetooth must be on to start scanning");
            Toast.makeText(mActivity,
                    R.string.bt_not_enabled,
                    Toast.LENGTH_SHORT).show();
            isBtEnabled = false;
        }
        return isBtEnabled;
    }

    /**
     * Add the found devices to the listView in the dialog.
     *
     * @param device     the device to add
     * @param rssi       the rssi value
     * @param scanRecord the advertised packet data that the device holds
     */
    protected void handleFoundDevice(final BluetoothDevice device,
                                     final int rssi, final byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFoundBtDevicesDialog.getListFoundDevicesHandler().addDevice(device, rssi, scanRecord);
            }
        });
    }

    /**
     * Add the found devices to the listView in the dialog.
     *
     * @param device the device to add
     * @param rssi   the rssi value
     */
    protected void handleFoundDevice(final BluetoothDevice device,
                                     final int rssi) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFoundBtDevicesDialog.getListFoundDevicesHandler().addDevice(device, rssi);
            }
        });
    }

    protected void initialiseDialogFoundDevices(String headerTitle, Drawable headerIcon) {
        mFoundBtDevicesDialog = new FoundBtDevicesDialog(this, this, headerTitle, headerIcon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * check that BT is enabled as the user could have turned it off during onPause.
		 */
        askToEnableBluetooth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isEnabled = false;

        switch (requestCode) {
            case ENABLE_BT_REQUEST_ID:
                if (resultCode == Activity.RESULT_CANCELED) {
                    isEnabled = false;
                } else if (resultCode == Activity.RESULT_OK) {
                    isEnabled = true;
                }
                onEnableBtRequestCallback(isEnabled);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onEnableBtRequestCallback(boolean isEnabled) {
    }

    @Override
    public void uiInvalidateViewsState() {
        if (mFoundBtDevicesDialog != null) {
            mFoundBtDevicesDialog.shouldShowProgressBar(getBtAdapterHelper());
        }
        super.uiInvalidateViewsState();
    }

    @Override
    public void onBleStopScan() {
        if (mFoundBtDevicesDialog.getListFoundDevicesHandler().getCount() <= 0) {
            mFoundBtDevicesDialog.dismiss();
        }
        uiInvalidateViewsState();
    }

    @Override
    public void onBleDeviceFound(BluetoothDevice device, int rssi,
                                 byte[] scanRecord) {
        handleFoundDevice(device, rssi, scanRecord);
    }

    @Override
    public void onDiscoveryStop() {
        // dismiss' dialog if no devices are found.
        if (mFoundBtDevicesDialog.getListFoundDevicesHandler().getCount() <= 0) {
            mFoundBtDevicesDialog.dismiss();
        }
        uiInvalidateViewsState();
    }

    @Override
    public void onDiscoveryDeviceFound(BluetoothDevice device, int rssi) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (device.getType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                handleFoundDevice(device, rssi);
            }
        } else {
            handleFoundDevice(device, rssi);
        }
    }
}