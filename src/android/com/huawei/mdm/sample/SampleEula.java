/*
 * IMPORTANT:  This Huawei software is supplied to you by Huawei Technologies Co., Ltd.
 * ("Huawei") in consideration of your agreement to the following
 * terms, and your use, copy, installation, modification or redistribution of
 * this Huawei software constitutes acceptance of these terms.  If you do
 * not agree with these terms, please do not use, copy, install, modify or
 * redistribute this Huawei software.

 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, Huawei grants you a personal, non-exclusive
 * license, under Huawei's copyrights in this original Huawei software(hereinafter referred as ��Huawei Software��), to use, reproduce, modify and redistribute the Huawei Software, with or without modifications, in source and/or binary forms;
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
package com.chinavvv.jwtoa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.chinavvv.jwtoa.R;

public class SampleEula {
    private static final int REQUEST_ENABLE = 1;
    private Activity mActivity = null;
    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    boolean notShowAgain = false;

    public SampleEula(Activity context, DevicePolicyManager devicePolicyManager, ComponentName adminName) {
        mActivity = context;
        mDevicePolicyManager = devicePolicyManager;
        mAdminName = adminName;
    }

    @SuppressLint("InflateParams")
    public void show() {
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(mActivity);
        notShowAgain = sharedPreferenceUtil.hasUserAccepted();
        if (notShowAgain == false) {
            // Show the Eula
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setPositiveButton(mActivity.getString(R.string.accept_btn),
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(
                                        DialogInterface dialogInterface, int i) {
                                    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(mActivity);
                                    sharedPreferenceUtil.saveUserChoice(notShowAgain);
                                    dialogInterface.dismiss();
                                    activeProcess();
                                }
                            })
                    .setNegativeButton(mActivity.getString(R.string.exit_btn),
                            new Dialog.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // Close the activity as they have declined
                                    // the EULA
                                    mActivity.finish();
                                }

                            });
            AlertDialog eulaDialog = builder.create();
            eulaDialog.setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View layout = inflater.inflate(R.layout.eula_layout, null);
            TextView permissionText = (TextView)layout.findViewById(R.id.content_permissions);
            String filename = "huawei_permission_statement.html";
            String content = Utils.getStringFromHtmlFile(mActivity, filename);
            permissionText.setText(Html.fromHtml(content));

            TextView statementText = (TextView)layout.findViewById(R.id.read_statement);
            statementText.setMovementMethod(LinkMovementMethod.getInstance());
            CharSequence text = statementText.getText();
            if (text instanceof Spannable) {
                int end = text.length();
                Spannable sp = (Spannable) text;
                URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
                SpannableStringBuilder style = new SpannableStringBuilder(text);
                style.clearSpans();// should clear old spans
                for (URLSpan url : urls) {
                    MyURLSpan myURLSpan = new MyURLSpan();
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
                statementText.setText(style);
            }
            CheckBox checkbox = (CheckBox)layout.findViewById(R.id.not_show_check);
            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        notShowAgain = true;
                    } else {
                        notShowAgain = false;
                    }

                }
            });
            eulaDialog.setView(layout);
            eulaDialog.show();
        } else {
            activeProcess();
        }
    }

    private class MyURLSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            widget.setBackgroundColor(Color.parseColor("#00000000"));

            Intent intent = new Intent(mActivity, LicenseActivity.class);
            mActivity.startActivity(intent);
        }
    }

    protected void activeProcess() {
        if (mDevicePolicyManager != null
                && !mDevicePolicyManager.isAdminActive(mAdminName)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            mActivity.startActivityForResult(intent, REQUEST_ENABLE);
            Log.d("SAMPLE","activeProcess");
        }
    }
}
