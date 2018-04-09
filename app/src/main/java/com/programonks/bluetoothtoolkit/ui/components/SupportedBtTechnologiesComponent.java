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

package com.programonks.bluetoothtoolkit.ui.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.programonks.bluetoothtoolkit.R;
import com.programonks.bluetoothtoolkit.helpers.UtilHelper;
import com.programonks.bluetoothtoolkit.helpers.bt.BtCheckerHelper;


public class SupportedBtTechnologiesComponent extends LinearLayout {

    private BtCheckerHelper mBtCheckerHelper;
    private TextView mIsBtClassicSupportedLabel;
    private TextView mIsBleCentralSupportedLabel;
    private TextView mIsBlePeripheralSupportedLabel;

    public SupportedBtTechnologiesComponent(Context context) {
        this(context, null);
    }

    public SupportedBtTechnologiesComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SupportedBtTechnologiesComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SupportedBtTechnologiesComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(getContext());
        View view = inflater.inflate(R.layout.component_supported_bt_technologies, this, true);
        bindViews(view);
    }

    private void bindViews(View view) {
        mIsBtClassicSupportedLabel = (TextView) view.findViewById(R.id.is_bt_classic_supported_label);
        mIsBleCentralSupportedLabel = (TextView) view.findViewById(R.id.is_central_supported_label);
        mIsBlePeripheralSupportedLabel = (TextView) view.findViewById(R.id.is_peripheral_supported_label);
    }

    public void checkBtSupportedFunctionalities(BtCheckerHelper btCheckerHelper) {
        mBtCheckerHelper = btCheckerHelper;

        checkBtClassicSupport();
        checkBleCentralSupport();
        checkBlePeripheralSupport();
    }

    private void checkBtClassicSupport() {
        if (mBtCheckerHelper.checkBtClassicSupport()) {
            mIsBtClassicSupportedLabel.setText(getResources().getString(R.string.label_bt_classic_mode_supported));
            mIsBtClassicSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBtClassicSupportedLabel.setText(getResources().getString(R.string.label_bt_classic_mode_not_supported));
            mIsBtClassicSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void checkBleCentralSupport() {
        if (mBtCheckerHelper.checkBleCentralSupport()) {
            mIsBleCentralSupportedLabel.setText(getResources().getString(R.string.label_central_mode_supported));
            mIsBleCentralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBleCentralSupportedLabel.setText(getResources().getString(R.string.label_central_mode_not_supported));
            mIsBleCentralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void checkBlePeripheralSupport() {
        if (mBtCheckerHelper.checkBlePeripheralSupport()) {
            mIsBlePeripheralSupportedLabel.setText(getResources().getString(R.string.label_peripheral_mode_supported));
            mIsBlePeripheralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBlePeripheralSupportedLabel.setText(getResources().getString(R.string.label_peripheral_mode_not_supported));
            mIsBlePeripheralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
