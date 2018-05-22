package com.chinavvv.jwtoa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.huawei.android.app.admin.DeviceNetworkManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApnUtils {

  private Activity context;
  private DevicePolicyManager mDevicePolicyManager = null;
  private ComponentName mAdminName = null;

  public ApnUtils(Activity context) {
    this.context = context;
    if(mAdminName == null){
      mAdminName = new ComponentName(context, SampleDeviceReceiver.class);
    }
    if(mDevicePolicyManager == null){
      mDevicePolicyManager = (DevicePolicyManager)
        context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }
  }

  public void init(){
    new SampleEula(context, mDevicePolicyManager, mAdminName).show();
  }

  /** SIM卡是中国移动 */
  public static boolean isChinaMobile(Context context) {
    String imsi = getSimOperator(context);
    if (imsi == null) return false;
    return imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007");
  }

  /** SIM卡是中国联通 */
  public static boolean isChinaUnicom(Context context) {
    String imsi = getSimOperator(context);
    if (imsi == null) return false;
    return imsi.startsWith("46001");
  }
  @SuppressLint("MissingPermission")
  private static String getSimOperator(Context context) {
    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getSubscriberId();
  }

  public void setApn(Activity context){
    DeviceNetworkManager myDeviceNetworkManager = new DeviceNetworkManager();
    Map<String, String> params = new HashMap<String, String>();
    params.put("mcc","460");
    // params.put("mnc","01");
    //myDeviceNetworkManager.deleteApn(mAdminName,2565+"");
    List<String> apns = myDeviceNetworkManager.queryApn(mAdminName,params);
    boolean hasApn = false;
    for(String id : apns){
      Map<String,String> apn = myDeviceNetworkManager.getApnInfo(mAdminName,id);
      String apnName = apn.get("apn");
      if(apnName.indexOf("JCYJWT")!=-1){
        hasApn = true;
        myDeviceNetworkManager.setPreferApn(mAdminName,id);
      }
    }
    if(!hasApn){
      Map<String,String> apnInfo = new HashMap<String,String>();
      apnInfo.put("mcc","460");
      apnInfo.put("name","检务通");
      //apnInfo.put("身份验证类型","PAP");
      if(isChinaMobile(context)){
        apnInfo.put("apn","JCYJWT.HA");
        apnInfo.put("mnc","02");
      }else{
        apnInfo.put("apn","JCYJWT.HAAPN");
        apnInfo.put("mnc","01");
      }
      myDeviceNetworkManager.addApn(mAdminName,apnInfo);
      setApn(context);
    }
  }

  public void setInternet(Activity context){
    DeviceNetworkManager myDeviceNetworkManager = new DeviceNetworkManager();
    Map<String, String> params = new HashMap<String, String>();
    params.put("mcc","460");
    // params.put("mnc","01");
    List<String> apns = myDeviceNetworkManager.queryApn(mAdminName,params);
    boolean hasApn = false;
    for(String id : apns){
      Map<String,String> apn = myDeviceNetworkManager.getApnInfo(mAdminName,id);
      String apnName = apn.get("apn");

      if(isChinaMobile(context)){
        if(apnName.indexOf("3gnet") > -1){//联通网络
          myDeviceNetworkManager.setPreferApn(mAdminName,id);
        }
      }else{
        if(apnName.indexOf("cmnet") > -1){//移动网络
          myDeviceNetworkManager.setPreferApn(mAdminName,id);
        }
      }
    }
  }

  public boolean isActiveMe() {
    if(mDevicePolicyManager == null || !mDevicePolicyManager.isAdminActive(mAdminName)) {
      return false;
    } else {
      return true;
    }
  }

  public void activeProcess() {
    if (mDevicePolicyManager != null
      && !mDevicePolicyManager.isAdminActive(mAdminName)) {
      Intent intent = new Intent(
        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
      intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
      context.startActivityForResult(intent, 1);
      Log.d("JWT","activeProcess");
    }
  }
}
