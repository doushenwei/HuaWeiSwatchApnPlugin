package cordova.plugin.huaweiswitchapn;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.chinavvv.apn.util.ApnUtility;
import com.chinavvv.jwtoa.ApnUtils;
import com.chinavvv.jwtoa.SampleDeviceReceiver;
import com.chinavvv.jwtoa.SampleEula;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class HuaWeiSwitchApnPlugin extends CordovaPlugin {

    private ApnUtility apnUtility = null;
  private ApnUtils apnUtils =null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Activity context = cordova.getActivity();
      apnUtils = new ApnUtils(context);

        apnUtility = new ApnUtility(context);
        if(action.equals("setApn")){//设置APN
            if(apnUtils.isActiveMe()) {
                apnUtility.closeWifi();
                try{
                  setApn(context);
                }catch (Exception e){
                  Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
                  cordova.startActivityForResult(this,intent,1001);
                }
                return true;
            }
            return false;
        }else if(action.equals("setInternet")){//设置互联网
            if(apnUtils. isActiveMe()) {
              setInternet(context);

                return true;
            }
            return false;
        }else if(action.equals("initApnConfig")){//初始化APN切换环境

            // new SampleEula(context, mDevicePolicyManager, mAdminName).show();
            return true;
        }else if(action.equals("loginout")){//退出
          try{
            setInternet(context);
          }catch (Exception e){
            Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
            cordova.startActivityForResult(this,intent,1001);
          }

          Intent intent = new Intent(Intent.ACTION_MAIN);
          intent.addCategory(Intent.CATEGORY_HOME);
          context.startActivity(intent);
        }
        return false;
    }

    private void setInternet(Activity context){
      apnUtils.setInternet(context);
    }

    private void setApn(Activity context){
      apnUtils.setApn(context);
    }
}
