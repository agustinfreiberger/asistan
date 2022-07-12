package ar.edu.unicen.isistan.asistan.utils.autostarter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoStartPermissionHelper {

    /***
     * Xiaomi
     */
    private static final String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private static final String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";
    private static final List<String> XIAOMI_PACKAGES = Collections.singletonList(PACKAGE_XIAOMI_COMPONENT);

    /***
     * Letv
     */
    private static final String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private static final String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity";
    private static final List<String> LETV_PACKAGES = Collections.singletonList(PACKAGE_LETV_COMPONENT);

    /***
     * ASUS ROG
     */
    private static final String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private static final String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";
    private static final String PACKAGE_ASUS_COMPONENT_FALLBACK = "com.asus.mobilemanager.autostart.AutoStartActivity";
    private static final List<String> ASUS_PACKAGES =  Arrays.asList(PACKAGE_ASUS_COMPONENT, PACKAGE_ASUS_COMPONENT_FALLBACK);

    /***
     * Huawei
     */
    private static final String PACKAGE_HUAWEI_MAIN = "com.huawei.systemmanager";
    private static final String PACKAGE_HUAWEI_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";
    private static final String PACKAGE_HUAWEI_COMPONENT_FALLBACK = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";
    private static final List<String> HUAWEI_PACKAGES = Arrays.asList(PACKAGE_HUAWEI_COMPONENT, PACKAGE_HUAWEI_COMPONENT_FALLBACK);

    /**
     * Oppo
     */
    private static final String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private static final String PACKAGE_OPPO_MAIN_FALLBACK = "com.oppo.safe";
    private static final String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";
    private static final List<String> OPPO_PACKAGES = Arrays.asList(PACKAGE_OPPO_COMPONENT,PACKAGE_OPPO_COMPONENT_FALLBACK,PACKAGE_OPPO_COMPONENT_FALLBACK_A);

    /**
     * Vivo
     */

    private static final String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private static final String PACKAGE_VIVO_MAIN_FALLBACK = "com.vivo.permissionmanager";
    private static final String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";
    private static final List<String> VIVO_PACKAGES = Arrays.asList(PACKAGE_VIVO_COMPONENT,PACKAGE_VIVO_COMPONENT_FALLBACK,PACKAGE_VIVO_COMPONENT_FALLBACK_A);

    /**
     * Nokia
     */
    private static final String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private static final String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";
    private static final List<String> NOKIA_PACKAGES = Collections.singletonList(PACKAGE_NOKIA_COMPONENT);

    /**
     * Samsung
     */
    private static final String PACKAGE_SAMSUNG_MAIN = "com.samsung.android.lool";
    private static final String PACKAGE_SAMSUNG_COMPONENT = "com.samsung.android.sm.ui.battery.BatteryActivity";
    private static final List<String> SAMSUNG_PACKAGES = Collections.singletonList(PACKAGE_SAMSUNG_COMPONENT);

    /***
     * One plus
     */
    private static final String PACKAGE_ONE_PLUS_MAIN = "com.oneplus.security";
    private static final String PACKAGE_ONE_PLUS_COMPONENT = "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity";
    private static final List<String> ONE_PLUS_PACKAGES = Collections.singletonList(PACKAGE_ONE_PLUS_COMPONENT);

    /**
     * Packages and components mapper
     */
    private static final Map<String, List<String>> PACKAGES  = new HashMap<String, List<String>>() {{
        put(PACKAGE_XIAOMI_MAIN, XIAOMI_PACKAGES);
        //put(PACKAGE_LETV_MAIN, LETV_PACKAGES);
        //put(PACKAGE_ASUS_MAIN, ASUS_PACKAGES);
        //put(PACKAGE_HUAWEI_MAIN, HUAWEI_PACKAGES);
        //put(PACKAGE_OPPO_MAIN, OPPO_PACKAGES);
        //put(PACKAGE_OPPO_MAIN_FALLBACK, OPPO_PACKAGES);
        //put(PACKAGE_VIVO_MAIN, VIVO_PACKAGES);
        //put(PACKAGE_VIVO_MAIN_FALLBACK, VIVO_PACKAGES);
        //put(PACKAGE_NOKIA_MAIN, NOKIA_PACKAGES);
        //put(PACKAGE_SAMSUNG_MAIN, SAMSUNG_PACKAGES);
        //put(PACKAGE_ONE_PLUS_MAIN,ONE_PLUS_PACKAGES);
    }};

    private static AutoStartPermissionHelper INSTANCE = null;

    private AutoStartPermissionHelper() {

    }

    public static AutoStartPermissionHelper getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AutoStartPermissionHelper();
        return INSTANCE;
    }

    public boolean isAutoStartPermissionAvailable(Context context) {
        List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo appInfo: packages) {
            if (PACKAGES.containsKey(appInfo.packageName))
                return true;
        }
        return false;
    }


    public void getAutoStartPermission(Context context) {
        List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo appInfo: packages) {
            String mainPackage = appInfo.packageName;
            if (PACKAGES.containsKey(mainPackage)) {
                List<String> components = PACKAGES.get(mainPackage);
                if (components != null && startIntent(context, mainPackage,components))
                    return;
            }
        }
    }

    private boolean startIntent(Context context, String mainPackage, List<String> components) {
        Intent intent = new Intent();

        for (String component: components) {
            try {
                intent.setComponent(new ComponentName(mainPackage, component));
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {

            }
        }

        return false;
    }

}
