package com.chinavvv.apn.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class ApnUtility {

  private Context context;

  private ConnectivityManager cm;

  public ApnUtility(Context context) {
    super();
    this.context = context;
    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  public JSONObject getCurrentApn() throws JSONException {
    NetworkInfo ni = cm.getActiveNetworkInfo();
    if(ni!=null){
      return processApn(ni);
    }else{
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      ni = cm.getActiveNetworkInfo();
      return processApn(ni);
    }
  }

  private JSONObject processApn(NetworkInfo ni) throws JSONException{
    JSONObject result = new JSONObject();
    if(ni!=null){
      result.put("apn", ni.getExtraInfo());
      result.put("typeName", ni.getTypeName());
      result.put("subTypeName", ni.getSubtypeName());
      result.put("type", ni.getType());
    }
    return result;
  }

  /**
   * 关闭WIFI网络状态
   */
  public boolean closeWifi(){
		WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
		Log.v("WIFI_STATE", String.valueOf(wifiManager.getWifiState()));

		if(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
      boolean result = wifiManager.setWifiEnabled(false);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return result;
    }
    return true;
  }

  //检测GPRS是否打开
  public boolean gprsIsOpenMethod(String methodName)
  {
    Class cmClass  = cm.getClass();
    Class[] argClasses  = null;
    Object[] argObject  = null;

    Boolean isOpen = false;
    try
    {
      Method method = cmClass.getMethod(methodName, argClasses);

      isOpen = (Boolean) method.invoke(cm, argObject);
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    return isOpen;
  }

}
