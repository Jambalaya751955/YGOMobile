package ocgcore;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.MD5Util;
import ocgcore.bean.LimitList;

public class LimitManager {
    private static LimitManager sManager = new LimitManager();
    private final List<LimitList> mLimitLists = new ArrayList<>();
    private volatile boolean isLoad = false;
    private String lastMd5;
    private int mCount;

    private LimitManager() {

    }

    public boolean isLoad() {
        return isLoad;
    }

    public static LimitManager get() {
        return sManager;
    }

    public int getCount() {
        return mCount;
    }

    public List<LimitList> getLimitLists(){
        return mLimitLists;
    }
    public LimitList getLimit(int postion) {
        if (postion >= 0 && postion <= getCount()) {
            return mLimitLists.get(postion);
        }
        return null;
    }

    public boolean load() {
        File stringfile = new File(AppsSettings.get().getResourcePath(),
                String.format(Constants.CORE_LIMIT_PATH, AppsSettings.get().getCoreConfigVersion()));
        String md5 = MD5Util.getFileMD5(stringfile.getAbsolutePath());
        if (TextUtils.equals(md5, lastMd5)) {
            return true;
        }
        lastMd5 = md5;
        return loadFile(stringfile.getAbsolutePath());
    }

    public boolean loadFile(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            return false;
        }
        mLimitLists.clear();
        mLimitLists.add(new LimitList(null));
        isLoad = false;
        InputStreamReader in = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            in = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(in);
            String line = null;
            String name = null;
            LimitList tmp = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("!")) {
                    name = line.substring(1);
                    if (tmp != null) {
                        mLimitLists.add(tmp);
                    }
                    tmp = new LimitList(name);
                } else if (tmp != null) {
                    String[] words = line.trim().split("[\t| ]+");
                    if (words.length >= 2) {
                        long id = toNumber(words[0]);
                        int count = (int) toNumber(words[1]);
                        switch (count) {
                            case 0:
                                tmp.addForbidden(id);
                                break;
                            case 1:
                                tmp.addLimit(id);
                                break;
                            case 2:
                                tmp.addSemiLimit(id);
                                break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            Log.e("kk", "limit", e);
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(in);
        }
        mCount = mLimitLists.size();
        isLoad = true;
        return true;
    }

    private long toNumber(String str) {
        long i = 0;
        try {
            if (str.startsWith("0x")) {
                i = Long.parseLong(str.replace("0x", ""), 0x10);
            } else {
                i = Long.parseLong(str);
            }
        } catch (Exception e) {

        }
        return i;
    }
}
