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

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.programonks.bluetoothtoolkit.R;
import com.programonks.bluetoothtoolkit.managers.BleBaseDeviceManager;
import com.programonks.bluetoothtoolkit.managers.BpmDeviceManager;
import com.programonks.bluetoothtoolkit.managers.BpmDeviceManagerUiCallback;
import com.programonks.bluetoothtoolkit.adapters.BpmMultipleDevicesAdapter;

public class BpmMultipleDevicesActivity extends
        BleBaseMultipleDevicesActivity implements
        BpmDeviceManagerUiCallback {
    private final static String TAG = "BpmMultipleDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,
                R.layout.activity_multiple_devices);

        initialiseDialogAbout(getResources().getString(
                R.string.about_multiple_bloodpressure));
        initialiseDialogFoundDevices("BPM", getResources().getDrawable(R.drawable.ic_toolbar_multiple_bpm));

        setHintTextValue(R.string.about_multiple_bloodpressure);
    }

    @Override
    public void setAdapters() {
        mListConnectedDevicesAdapter = new BpmMultipleDevicesAdapter(
                this);
        super.setAdapters();
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
        final BluetoothDevice device = mFoundBtDevicesDialog.getListFoundDevicesHandler()
                .getDevice(position);
        if (device == null)
            return;

        getBtAdapterHelper().getBleSearchBaseHelper().stopScan();
        mFoundBtDevicesDialog.dismiss();

        BleBaseDeviceManager bpmManager = new BpmDeviceManager(
                this, mActivity);
        boolean isDeviceAddedToTheList = mListConnectedDevicesAdapter
                .addDevice(bpmManager);

        if (isDeviceAddedToTheList) {
            bpmManager.connect(device, false);
            uiInvalidateViewsState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.multiple_bpm, menu);
        if (getActionBar() != null) {
            getActionBar().setIcon(R.drawable.ic_toolbar_multiple_bpm);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    public void onUiBpmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            "Blood pressure measurement "
                                    + "characteristic was not found",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUIBloodPressureRead(float mValueBloodPressureSystolicResult,
                                      float mValueBloodPressureDiastolicResult,
                                      float mValueBloodPressureArterialPressureResult) {
        uiInvalidateViewsState();
    }


}