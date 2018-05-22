/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.chinavvv.jwtoa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;

import com.ccit.www.interfacecallback.ApplyCertSynResultVo;
import com.ccit.www.interfacecallback.SignatureResultVo;
import com.ccit.www.usdkresult.ResultVo;
import com.ccit.www.usdkresult.SignResultVo;

import org.apache.cordova.*;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends CordovaActivity implements SignatureResultVo,ApplyCertSynResultVo
{
    WebView webView;
    DefaultRequestPermission defaultRequestPermission;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      final ApnUtils apnUtils = new ApnUtils(this);
        // Set by <content src="index.html" /> in config.xml
      defaultRequestPermission=new DefaultRequestPermission() {
        @Override
        public void approvedTodo() {
          apnUtils.init();

          loadUrl(launchUrl);
          //增加js调用方法
          webView = (WebView)appView.getView();
          webView.getSettings().setJavaScriptEnabled(true);
          //允许JS弹窗
          webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
          webView.getSettings().setSupportMultipleWindows(true);
          webView.addJavascriptInterface(new CertObject(webView.getContext()), "myObj");
          webView.addJavascriptInterface(new Help((Activity)webView.getContext()), "help");

        }

        @Override
        public void refusedTodo() {

        }

        @Override
        public void showExplanation() {

        }
      };

      defaultRequestPermission.requestPermission(this,new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);


    }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    defaultRequestPermission.permissionsResult(requestCode, permissions, grantResults);
  }

    /**
     * 证书申请回调函数
     * @param resultVo
     * @return
     *  0	成功
     *  1	证书审核中
     *  2	证书已存在
     */
    public void applyCertSynCallBack(ResultVo resultVo) {
        Map<String,Object> map = new HashMap<String,Object>();
        final String resultCode = resultVo.getResultCode();
        final String resultDesc = resultVo.getResultDesc();

        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:applyCertSynCallBack("+resultCode+",'"+resultDesc+"')");
            }
        });
    }

    /**
     * 签名回调
     * @param resultVo
     * @return
     */
    public void signatureCallBack(final SignResultVo resultVo) {
        Map<String,Object> map = new HashMap<String, Object>();
        final String resultCode = resultVo.getResultCode();
        final String resultDesc = resultVo.getResultDesc();

      Log.e("签名：---------------",resultDesc);
        //返回结果给html页面
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:signatureCallBack(" + resultCode + ",'" + resultVo.getSignCert() + "','" + resultVo.getSignData() + "')");
            }
        });
    }
}
