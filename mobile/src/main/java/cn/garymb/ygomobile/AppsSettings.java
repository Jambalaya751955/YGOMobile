package cn.garymb.ygomobile;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.ui.preference.PreferenceFragmentPlus;
import cn.garymb.ygomobile.utils.SystemUtils;

import static cn.garymb.ygomobile.Constants.CORE_SYSTEM_PATH;
import static cn.garymb.ygomobile.Constants.DEF_PREF_FONT_SIZE;
import static cn.garymb.ygomobile.Constants.PREF_DEF_IMMERSIVE_MODE;
import static cn.garymb.ygomobile.Constants.PREF_DEF_SENSOR_REFRESH;
import static cn.garymb.ygomobile.Constants.PREF_FONT_SIZE;
import static cn.garymb.ygomobile.Constants.PREF_IMMERSIVE_MODE;
import static cn.garymb.ygomobile.Constants.PREF_LOCK_SCREEN;
import static cn.garymb.ygomobile.Constants.PREF_SENSOR_REFRESH;

public class AppsSettings {
    private static AppsSettings sAppsSettings;
    private Context context;
    private PreferenceFragmentPlus.SharedPreferencesPlus mSharedPreferences;
    private float mScreenHeight, mScreenWidth, mDensity;
    private static final String PREF_VERSION = "app_version";

    public static void init(Context context) {
        if (sAppsSettings == null) {
            sAppsSettings = new AppsSettings(context);
        }
    }

    public static AppsSettings get() {
        return sAppsSettings;
    }

    public File getSystemConfig() {
        return new File(getResourcePath(), String.format(CORE_SYSTEM_PATH, getCoreConfigVersion()));
    }

    private AppsSettings(Context context) {
        this.context = context;
        mSharedPreferences = PreferenceFragmentPlus.SharedPreferencesPlus.create(context, context.getPackageName() + ".settings");
        mSharedPreferences.setAutoSave(true);
        update(context);
    }

    public void update(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        if (isImmerSiveMode() && context instanceof Activity) {
            DisplayMetrics dm = SystemUtils.getHasVirtualDisplayMetrics((Activity) context);
            if (dm != null) {
                int height = Math.max(dm.widthPixels, dm.heightPixels);
                if (mScreenHeight == Math.max(mScreenHeight, mScreenWidth)) {
                    mScreenHeight = height;
                } else {
                    mScreenWidth = height;
                }
            }
        }
    }

    public int getAppVersion() {
        return mSharedPreferences.getInt(PREF_VERSION, 0);
    }

    public void setAppVersion(int ver) {
        mSharedPreferences.putInt(PREF_VERSION, ver);
    }


    public PreferenceFragmentPlus.SharedPreferencesPlus getSharedPreferences() {
        return mSharedPreferences;
    }

    public float getSmallerSize() {
        return mScreenHeight < mScreenWidth ? mScreenHeight : mScreenWidth;
    }

    public float getScreenWidth() {
        return Math.min(mScreenWidth, mScreenHeight);
    }

    public boolean isDialogDelete() {
        return true;// mSharedPreferences.getBoolean(PREF_DECK_DELETE_DILAOG, PREF_DEF_DECK_DELETE_DILAOG);
    }

    public int getFontSize() {
        return mSharedPreferences.getInt(PREF_FONT_SIZE, DEF_PREF_FONT_SIZE);
    }

    public float getXScale() {
        return getScreenHeight() / (float) Constants.CORE_SKIN_BG_SIZE[0];
    }

    public float getYScale() {
        return getScreenWidth() / (float) Constants.CORE_SKIN_BG_SIZE[1];
    }

    public float getScreenHeight() {
        return Math.max(mScreenWidth, mScreenHeight);
    }

    /**
     * 游戏配置
     */
    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options = new NativeInitOptions();
        options.mCacheDir = getResourcePath();
        options.mDBDir = getDataBasePath();
        options.mCoreConfigVersion = getCoreConfigVersion();
        options.mResourcePath = getResourcePath();
        options.mExternalFilePath = getResourcePath();
        options.mCardQuality = getCardQuality();
        options.mIsFontAntiAliasEnabled = isFontAntiAlias();
        options.mIsPendulumScaleEnabled = isPendulumScale();
        options.mIsSoundEffectEnabled = isSoundEffect();
        options.mOpenglVersion = getOpenglVersion();
        if (Constants.DEBUG) {
            Log.i("Irrlicht", "option=" + options);
        }
        return options;
    }

    /***
     * 音效
     */
    public boolean isSoundEffect() {
        return mSharedPreferences.getBoolean(Constants.PREF_SOUND_EFFECT, Constants.PREF_DEF_SOUND_EFFECT);
    }

    /***
     * 音效
     */
    public void setSoundEffect(boolean soundEffect) {
        mSharedPreferences.putBoolean(Constants.PREF_SOUND_EFFECT, soundEffect);
    }

    /***
     * 摇摆数字
     */
    public boolean isPendulumScale() {
        return mSharedPreferences.getBoolean(Constants.PREF_PENDULUM_SCALE, Constants.PREF_DEF_PENDULUM_SCALE);
    }

    /***
     * 摇摆数字
     */
    public void setPendulumScale(boolean pendulumScale) {
        mSharedPreferences.putBoolean(Constants.PREF_PENDULUM_SCALE, pendulumScale);
    }

    /***
     * opengl版本
     */
    public int getOpenglVersion() {
        try {
            return Integer.valueOf(mSharedPreferences.getString(Constants.PREF_OPENGL_VERSION, "" + Constants.PREF_DEF_OPENGL_VERSION));
        } catch (Exception e) {
            return Constants.PREF_DEF_OPENGL_VERSION;
        }
    }

    /***
     * opengl版本
     */
    public void setOpenglVersion(int openglVersion) {
        mSharedPreferences.putString(Constants.PREF_OPENGL_VERSION, "" + openglVersion);
    }

    /***
     * 字体抗锯齿
     */
    public boolean isFontAntiAlias() {
        return mSharedPreferences.getBoolean(Constants.PREF_FONT_ANTIALIAS, Constants.PREF_DEF_FONT_ANTIALIAS);
    }

    /***
     * 字体抗锯齿
     */
    public void setFontAntiAlias(boolean fontAntiAlias) {
        mSharedPreferences.putBoolean(Constants.PREF_FONT_ANTIALIAS, fontAntiAlias);
    }

    /***
     * 游戏版本
     */
    public String getCoreConfigVersion() {
        return mSharedPreferences.getString(Constants.PREF_GAME_VERSION, Constants.PREF_DEF_GAME_VERSION);
    }

    /***
     * 游戏版本
     */
    public void setCoreConfigVersion(String configVersion) {
        mSharedPreferences.putString(Constants.PREF_GAME_VERSION, configVersion);
    }

    /***
     * 图片质量
     */
    public int getCardQuality() {
        try {
            return Integer.valueOf(mSharedPreferences.getString(Constants.PREF_IMAGE_QUALITY, "" + Constants.PREF_DEF_IMAGE_QUALITY));
        } catch (Exception e) {
            return Constants.PREF_DEF_IMAGE_QUALITY;
        }
    }

    /***
     * 图片质量
     */
    public void setCardQuality(int quality) {
        mSharedPreferences.putString(Constants.PREF_IMAGE_QUALITY, "" + quality);
    }

    /***
     * 图片文件夹
     */
    public String getCardImagePath() {
        return new File(getResourcePath(), Constants.CORE_IMAGE_PATH).getAbsolutePath();
    }

    /***
     * 当前数据库文件夹
     */
    public String getDataBasePath() {
        if (isUseExtraCards()) {
            return getResourcePath();
        } else {
            return getDataBaseDefault();
        }
    }

    public boolean isLockSreenOrientation() {
        return mSharedPreferences.getBoolean(PREF_LOCK_SCREEN, Constants.PREF_DEF_LOCK_SCREEN);
    }

    public void setLockSreenOrientation(boolean lockSreenOrientation) {
        mSharedPreferences.putBoolean(PREF_LOCK_SCREEN, lockSreenOrientation);
    }

    /***
     * 内置数据库文件夹
     */
    public String getDataBaseDefault() {
        return context.getDatabasePath("test.db").getParent();
    }

    /***
     * 使用外置数据库文件夹
     */
    public boolean isUseExtraCards() {
        return mSharedPreferences.getBoolean(Constants.PREF_USE_EXTRA_CARD_CARDS, Constants.PREF_DEF_USE_EXTRA_CARD_CARDS);
    }

    public String getCoreSkinPath() {
        return new File(getResourcePath(), Constants.CORE_SKIN_PATH).getAbsolutePath();
    }

    /***
     * 使用外置数据库文件夹
     */
    public void setUseExtraCards(boolean useExtraCards) {
        mSharedPreferences.putBoolean(Constants.PREF_USE_EXTRA_CARD_CARDS, useExtraCards);
    }

    /***
     * 字体路径
     */
    public void setFontPath(String font) {
        mSharedPreferences.putString(Constants.PREF_GAME_FONT, font);
    }

    /***
     * 字体路径
     */
    public String getFontPath() {
        return mSharedPreferences.getString(Constants.PREF_GAME_FONT, getFontDefault());
    }

    /**
     * 默认字体
     */
    private String getFontDefault() {
        return new File(getFontDirPath(), Constants.DEFAULT_FONT_NAME).getAbsolutePath();
    }

    /***
     * 字体目录
     */
    public String getFontDirPath() {
        return new File(getResourcePath(), Constants.FONT_DIRECTORY).getAbsolutePath();
    }

    /***
     * 游戏根目录
     */
    public String getResourcePath() {
        String defPath;
        try {
            defPath = new File(Environment.getExternalStorageDirectory(), Constants.PREF_DEF_GAME_DIR).getAbsolutePath();
        } catch (Exception e) {
            defPath = new File(context.getFilesDir(), Constants.PREF_DEF_GAME_DIR).getAbsolutePath();
        }
        return mSharedPreferences.getString(Constants.PREF_GAME_PATH, defPath);
    }

    public void setResourcePath(String path) {
        if (TextUtils.equals(path, getResourcePath())) return;
        mSharedPreferences.putString(Constants.PREF_GAME_PATH, path);
    }

    public String getDeckDir() {
        return new File(getResourcePath(), Constants.CORE_DECK_PATH).getAbsolutePath();
    }

    /**
     * 隐藏底部导航栏
     */
    public boolean isImmerSiveMode() {
        return mSharedPreferences.getBoolean(PREF_IMMERSIVE_MODE, PREF_DEF_IMMERSIVE_MODE);
    }

    public boolean isSensorRefresh() {
        return mSharedPreferences.getBoolean(PREF_SENSOR_REFRESH, PREF_DEF_SENSOR_REFRESH);
    }

    /***
     * 最后卡组名
     */
    public void setLastDeck(String name) {
        if (TextUtils.equals(name, getCurLastDeck())) {
            //一样
            return;
        }
        mSharedPreferences.putString(Constants.PREF_LAST_YDK, name);
    }

    /***
     * 最后卡组名
     */
    public String getLastDeck() {
        return mSharedPreferences.getString(Constants.PREF_LAST_YDK, Constants.PREF_DEF_LAST_YDK);
    }

    public String getCurLastDeck() {
        return mSharedPreferences.getString(Constants.PREF_LAST_YDK, null);
    }

    public List<String> getLastRoomList() {
        List<String> names = new ArrayList<>();
        String json = mSharedPreferences.getString(Constants.PREF_LAST_ROOM_LIST, null);
        try {
            JSONArray array = new JSONArray(json);
            int count = array.length();
            for (int i = 0; i < count; i++) {
                names.add(array.optString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i("kk", "read:" + names);
        return names;
    }

    public void setLastRoomList(List<String> _names) {
        JSONArray array = new JSONArray();
        if (_names != null) {
            int count = _names.size();
            int max = Math.min(count, Constants.LAST_ROOM_MAX);
            for (int i = 0; i < max; i++) {
                array.put(_names.get(i));
            }
        }
//        Log.i("kk", "saveTemp:" + array);
        mSharedPreferences.putString(Constants.PREF_LAST_ROOM_LIST, array.toString());
    }
}
