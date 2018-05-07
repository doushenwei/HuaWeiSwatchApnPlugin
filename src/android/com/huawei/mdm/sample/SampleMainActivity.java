/*
 * IMPORTANT:  This Huawei software is supplied to you by Huawei Technologies Co., Ltd. 
 * ("Huawei") in consideration of your agreement to the following
 * terms, and your use, copy, installation, modification or redistribution of
 * this Huawei software constitutes acceptance of these terms.  If you do
 * not agree with these terms, please do not use, copy, install, modify or
 * redistribute this Huawei software.

 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, Huawei grants you a personal, non-exclusive
 * license, under Huawei's copyrights in this original Huawei software(hereinafter referred as “Huawei Software”), to use, reproduce, modify and redistribute the Huawei Software, with or without modifications, in source and/or binary forms;
 * provided that if you redistribute the Huawei Software in its entirety and
 * without modifications, you must retain this notice and the following
 * text and disclaimers in all such redistributions of the Huawei Software.
 * Neither the name, trademarks, service marks or logos of Huawei Technologies Co.. Ltd. may
 * be used to endorse or promote products derived from the Huawei Software
 * without specific prior written permission from Huawei.  Except as
 * expressly stated in this notice, no other rights or licenses, express or
 * implied, are granted by Huawei herein, including but not limited to any
 * patent rights that may be infringed by your derivative works or by other
 * works in which the Huawei Software may be incorporated.

 * The Huawei Software is provided by Huawei on an "AS IS" basis.  Huawei
 * MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE, REGARDING THE HUAWEI SOFTWARE OR ITS USE AND
 * OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.

 * IN NO EVENT SHALL HUAWEI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 * MODIFICATION AND/OR DISTRIBUTION OF THE HUAWEI SOFTWARE, HOWEVER CAUSED
 * AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 * STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.

 * Copyright (C) 2015 Huawei Technologies Co., Ltd. All Rights Reserved.

 * Trademarks and Permissions
 * Huawei and other Huawei trademarks are trademarks of Huawei Technologies Co., Ltd.
 * All other trademarks and trade names mentioned in this document are the property of their respective holders.
 */
package com.huawei.mdm.sample;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;

import com.huawei.android.app.admin.DeviceRestrictionManager;
import com.huawei.mdm.sample.SampleDeviceReceiver;

public class SampleMainActivity extends Activity {
    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    private TextView mStatusText;
    private Button wifiDisableBtn;
    private Button wifiEnableBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceRestrictionManager = new DeviceRestrictionManager();
        mAdminName = new ComponentName(this, SampleDeviceReceiver.class);

        initSampleView();
        updateState();
        new SampleEula(this, mDevicePolicyManager, mAdminName).show();
    }

    private void initSampleView() {
        mStatusText = (TextView) findViewById(R.id.wifiStateTxt);
        wifiDisableBtn = (Button) findViewById(R.id.disableWifi);
        wifiEnableBtn = (Button) findViewById(R.id.enableWifi);

        wifiDisableBtn.setOnClickListener(new SampleOnClickListener());
        wifiEnableBtn.setOnClickListener(new SampleOnClickListener());
    }
    

    private void updateState() {
        if(!isActiveMe()) {
            mStatusText.setText(getString(R.string.state_not_actived));
            return;
        }

        boolean isWifiDisabled = false;
        try {
            isWifiDisabled = mDeviceRestrictionManager.isWifiDisabled(mAdminName);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        if (isWifiDisabled) {
            mStatusText.setText(R.string.state_restricted);
        } else {
            mStatusText.setText(getString(R.string.state_nomal));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateState();
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private boolean isActiveMe() {
        if(mDevicePolicyManager == null || !mDevicePolicyManager.isAdminActive(mAdminName)) {
            return false;
        } else {
            return true;
        }
    }

    private class SampleOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            boolean disable = false;
            
            if (v.getId() == R.id.disableWifi) {
                disable = true;
            } else if (v.getId() == R.id.enableWifi) {
                disable = false;
            }
            
            try {
                if (mDeviceRestrictionManager != null) {
                    mDeviceRestrictionManager.setWifiDisabled(mAdminName, disable);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            updateState();
        }
    }
}