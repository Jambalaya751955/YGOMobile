package cn.garymb.ygomobile.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.model.Settings;
import cn.garymb.ygomobile.widget.BaseDialog;
import cn.garymb.ygomobile.widget.preference.MyBooleanValuePreference;
import cn.garymb.ygomobile.lite.R;

public class GameSettingsFragment extends EventDialogPreferenceFragment
        implements OnPreferenceChangeListener, OnPreferenceClickListener,
        OnClickListener, DialogInterface.OnClickListener {

    private static final int DIALOG_TYPE_IMAGE_PREVIEW = 0;
    private static final int DIALOG_TYPE_CARD_DB_DIY = 1;
    private static final int DIALOG_TYPE_CARD_DB_RESET = 2;
    private static final int DIALOG_TYPE_SELECT_PATH = 3;
    private ListPreference mOGLESPreference;

    private ListPreference mCardQualityPreference;

    private ListPreference mFontNamePreference;

    private Preference mCoverDiyPreference;

    private MyBooleanValuePreference mCardDBDiyPreference;

    private Preference mCardBackDiyPreference;

    private Preference mCardDBResetPreference;

    private Bundle mImageBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_game);
        if (savedInstanceState != null) {
            mImageBundle = savedInstanceState.getParcelable("image_param");
        }
        mOGLESPreference = (ListPreference) findPreference(Settings.KEY_PREF_GAME_OGLES_CONFIG);
        mOGLESPreference.setSummary(mOGLESPreference.getEntry());
        mOGLESPreference.setOnPreferenceChangeListener(this);

        mCardQualityPreference = (ListPreference) findPreference(Settings.KEY_PREF_GAME_IMAGE_QUALITY);
        mCardQualityPreference.setSummary(mCardQualityPreference.getEntry());
        mCardQualityPreference.setOnPreferenceChangeListener(this);

        mFontNamePreference = (ListPreference) findPreference(Settings.KEY_PREF_GAME_FONT_NAME);

//        ArrayList<String> fontlist = StaticApplication.get().getFontList();
//        String[] entryList = new String[fontlist.size()];
//        int i = 0;
//        for (String path : fontlist) {
//            entryList[i++] = path.substring(
//                    path.lastIndexOf(File.separator) + 1, path.length());
//        }
//        mFontNamePreference.setEntries(entryList);
//        mFontNamePreference.setEntryValues(fontlist.toArray(new String[fontlist
//                .size()]));
        mFontNamePreference.setOnPreferenceChangeListener(this);

        String currentPath = GameSettings.get().getFontPath();
        mFontNamePreference.setValue(currentPath);
        mFontNamePreference.setSummary(mFontNamePreference.getEntry());

        mCoverDiyPreference = findPreference(Settings.KEY_PREF_GAME_DIY_COVER);
        mCoverDiyPreference.setOnPreferenceClickListener(this);

        mCardBackDiyPreference = findPreference(Settings.KEY_PREF_GAME_DIY_CARD_BACK);
        mCardBackDiyPreference.setOnPreferenceClickListener(this);

        mCardDBDiyPreference = (MyBooleanValuePreference) findPreference(Settings.KEY_PREF_GAME_DIY_CARD_DB);
        mCardDBDiyPreference.setOnPreferenceClickListener(this);

        mCardDBResetPreference = findPreference(Settings.KEY_PREF_GAME_RESET_CARD_DB);
        mCardDBResetPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(Settings.KEY_PREF_GAME_OGLES_CONFIG)) {
            mOGLESPreference.setValue((String) newValue);
            mOGLESPreference.setSummary(mOGLESPreference.getEntry());
        } else if (preference.getKey().equals(
                Settings.KEY_PREF_GAME_IMAGE_QUALITY)) {
            mCardQualityPreference.setValue((String) newValue);
            mCardQualityPreference
                    .setSummary(mCardQualityPreference.getEntry());
        } else if (preference.getKey().equals(Settings.KEY_PREF_GAME_FONT_NAME)) {
            mFontNamePreference.setValue((String) newValue);
            mFontNamePreference.setSummary(mFontNamePreference.getEntry());
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("image_param", mImageBundle);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
//        if (preference.getKey().equals(Settings.KEY_PREF_GAME_DIY_COVER)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("url", StaticApplication.get()
//                    .getCoreSkinPath()
//                    + File.separator
//                    + Constants.CORE_SKIN_COVER);
//            bundle.putInt("title_res", R.string.settings_game_cover);
//            bundle.putIntArray("orig_size", Constants.CORE_SKIN_COVER_SIZE);
//            bundle.putBoolean("force_resize", false);
//            mImageBundle = bundle;
//            showDialog(DIALOG_TYPE_IMAGE_PREVIEW, bundle);
//        } else if (preference.getKey().equals(
//                Settings.KEY_PREF_GAME_DIY_CARD_DB)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("root", StaticApplication.get().getResourcePath());
//            bundle.putInt("mode", FileBrowser.BROWSE_MODE_FILES);
//            showDialog(DIALOG_TYPE_CARD_DB_DIY, bundle);
//        } else if (preference.getKey().equals(
//                Settings.KEY_PREF_GAME_DIY_CARD_BACK)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("url", StaticApplication.get()
//                    .getCoreSkinPath()
//                    + File.separator
//                    + Constants.CORE_SKIN_CARD_BACK);
//            bundle.putBoolean("force_resize", true);
//            bundle.putInt("title_res", R.string.settings_game_card_back);
//            bundle.putIntArray("orig_size", Constants.CORE_SKIN_CARD_BACK_SIZE);
//            mImageBundle = bundle;
//            showDialog(DIALOG_TYPE_IMAGE_PREVIEW, bundle);
//        } else if (preference.getKey().equals(
//                Settings.KEY_PREF_GAME_RESET_CARD_DB)) {
//            Bundle bundle = new Bundle();
//            bundle.putString(
//                    "message",
//                    getResources().getString(
//                            R.string.settings_game_reset_card_db_confirm));
//            bundle.putString(
//                    "title",
//                    getResources().getString(
//                            R.string.settings_game_reset_card_db));
//            showDialog(DIALOG_TYPE_CARD_DB_RESET, bundle);
//        } else if (Settings.KEY_PREF_GAME_LAB_PENDULUM_SCALE.equals(preference.getKey())) {
//            if (mPendulumPreference.isChecked()) {
//                new File(GameSettings.get().getResourcePath(), GameSettings.CORE_SKIN_PATH + "/.extra")
//                        .renameTo(new File(GameSettings.get().getResourcePath(), GameSettings.CORE_SKIN_PATH + "/extra"));
//            } else {
//                new File(GameSettings.get().getResourcePath(), GameSettings.CORE_SKIN_PATH + "/extra")
//                        .renameTo(new File(GameSettings.get().getResourcePath(), GameSettings.CORE_SKIN_PATH + "/.extra"));
//            }
//        } else if (preference.getKey().equals(Settings.KEY_PREF_GAME_RESOURCE_PATH)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("root", GameSettings.get().getResourcePath());
//            bundle.putString("current", GameSettings.get().getResourcePath());
//            showDialog(DIALOG_TYPE_SELECT_PATH, bundle);
//        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
//        if (which == DialogInterface.BUTTON_POSITIVE) {
//            if (dialog instanceof FileChooseDialog) {
//                String newUrl = ((FileChooseController) getDialog()
//                        .getController()).getUrl();
//                CardDBCopyTask task = new CardDBCopyTask(getActivity());
//                task.setCardDBCopyListener(this);
//                if (Build.VERSION.SDK_INT >= 11) {
//                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                            newUrl);
//                } else {
//                    task.execute(newUrl);
//                }
//            } else if (dialog instanceof SimpleDialog) {
//                CardDBResetTask task = new CardDBResetTask(getActivity());
//                task.setCardDBResetListener(this);
//                if (Build.VERSION.SDK_INT >= 11) {
//                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                } else {
//                    task.execute();
//                }
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        dismissDialog();
        Crop.pickImage(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK
                && resultCode == Activity.RESULT_OK) {
            beginCrop(result.getData(), mImageBundle);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            setNewImage(Crop.getOutput(result), result.getExtras());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void beginCrop(Uri source, Bundle param) {
        Uri outputUri = Uri.fromFile(new File(getActivity().getCacheDir(),
                "cropped"));
        int[] sizeArray = param.getIntArray("orig_size");
        Crop.of(source, outputUri)
                .withAspect(sizeArray[0], sizeArray[1])
                .start(getActivity(), this);
    }

    private void setNewImage(Uri uri, Bundle param) {
//        String path = uri.toString();
//        if (path.startsWith(ImageItemInfoHelper.FILE_PREFIX)) {
//            path = FileOpsUtils.getFilePathFromUrl(path);
//        } else if (path.startsWith(ImageItemInfoHelper.MEIDA_PREFIX)) {
//            ContentResolver cr = StaticApplication.get()
//                    .getContentResolver();
//            String[] projection = {MediaStore.MediaColumns.DATA};
//            Cursor cursor = cr.query(uri, projection, null, null, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                path = cursor.getString(0);
//            } else {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//        ImageResizeCopyTask task = new ImageResizeCopyTask(getActivity());
//        task.setImageCopyListener(this);
//        param.putString("src_url", path);
//        if (Build.VERSION.SDK_INT >= 11) {
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param);
//        } else {
//            task.execute(param);
//        }
    }

    @SuppressLint("InflateParams")
    @Override
    public BaseDialog onCreateDialog(int type, Bundle param) {
        BaseDialog dlg = null;
//        if (type == DIALOG_TYPE_IMAGE_PREVIEW) {
//            View view = LayoutInflater.from(getActivity()).inflate(
//                    R.layout.image_preview_content, null);
//            dlg = new ImagePreviewDialog(getActivity(), view, this, this, param);
//        } else if (type == DIALOG_TYPE_CARD_DB_DIY) {
//            View view = LayoutInflater.from(getActivity()).inflate(
//                    R.layout.file_browser_layout, null);
//            dlg = new FileChooseDialog(getActivity(), view, this, param);
//        } else if (type == DIALOG_TYPE_CARD_DB_RESET) {
//            dlg = new SimpleDialog(getActivity(), this, null, param);
//        }else if(type == DIALOG_TYPE_SELECT_PATH){
//
//        }
        return dlg;
    }

    public void onImageCopyFinished(Bundle dstPath) {
        showDialog(DIALOG_TYPE_IMAGE_PREVIEW, dstPath);
    }

    public void onCardDBCopyFinished(int result) {
//        final Resources res = getResources();
//        String errorMessage;
//        if (result == CardDBCopyTask.COPY_DB_TASK_FAILED) {
//            errorMessage = res.getString(R.string.loading_card_failed);
//            mCardDBDiyPreference.setChecked(false);
//        } else if (result == CardDBCopyTask.COPY_DB_TASK_FILE_NOT_EXIST) {
//            errorMessage = res.getString(R.string.loading_card_file_not_found);
//            mCardDBDiyPreference.setChecked(false);
//        } else {
//            errorMessage = res.getString(R.string.loading_card_success);
//            mCardDBDiyPreference.setChecked(true);
//        }
//        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
//        mImageBundle = null;
    }

    public void onCardDBResetFinished(Boolean result) {
        Toast.makeText(
                getActivity(),
                (result ? getResources().getString(R.string.reset_card_success)
                        : getResources().getString(R.string.reset_card_failed)),
                Toast.LENGTH_SHORT).show();
        if (result) {
            mCardDBDiyPreference.setChecked(false);
        }
        mImageBundle = null;
    }
}
