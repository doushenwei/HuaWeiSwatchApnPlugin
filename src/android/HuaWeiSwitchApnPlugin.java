package cordova.plugin.huaweiswitchapn;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.admin.DevicePolicyManager;

import com.chinavvv.apn.util.ApnUtility;
import com.chinavvv.jwtoa.SampleDeviceReceiver;
import com.chinavvv.jwtoa.SampleEula;
import com.huawei.android.app.admin.DeviceNetworkManager;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class HuaWeiSwitchApnPlugin extends CordovaPlugin {

    private DevicePolicyManager mDevicePolicyManager = null;
    private ComponentName mAdminName = null;
    private ApnUtility apnUtility = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Activity context = cordova.getActivity();
        mAdminName = new ComponentName(context, SampleDeviceReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager)
        context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        apnUtility = new ApnUtility(context);
        if(action.equals("setApn")){//设置APN
            if(isActiveMe()) {
                apnUtility.closeWifi();
                setApn(context);
                return true;
            }
            return false;
        }else if(action.equals("setInternet")){//设置互联网
            if(isActiveMe()) {
              setInternet(context);
                return true;
            }
            return false;
        }else if(action.equals("initApnConfig")){//初始化APN切换环境

            new SampleEula(context, mDevicePolicyManager, mAdminName).show();
            return true;
        }else if(action.equals("loginout")){//退出
          setInternet(context);

          Intent intent = new Intent(Intent.ACTION_MAIN);
          intent.addCategory(Intent.CATEGORY_HOME);
          context.startActivity(intent);
        }
        return false;
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

    private boolean isActiveMe() {
        if(mDevicePolicyManager == null || !mDevicePolicyManager.isAdminActive(mAdminName)) {
            return false;
        } else {
            return true;
        }
    }

    private void setInternet(Activity context){
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

    private void setApn(Activity context){
        DeviceNetworkManager myDeviceNetworkManager = new DeviceNetworkManager();
        Map<String, String> params = new HashMap<String, String>();
        params.put("mcc","460");
        // params.put("mnc","01");
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
            if(isChinaMobile(context)){
                apnInfo.put("apn","JCYJWT.HA");
                apnInfo.put("mnc","07");
            }else{
                apnInfo.put("apn","JCYJWT.HAAPN");
                apnInfo.put("mnc","01");
            }
            myDeviceNetworkManager.addApn(mAdminName,apnInfo);
            setApn(context);
        }
    }
}
