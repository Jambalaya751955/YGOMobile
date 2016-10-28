package cn.garymb.ygomobile.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.webkit.WebView;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.model.Settings;
import cn.garymb.ygomobile.widget.BaseDialog;
import cn.garymb.ygomobile.widget.WebViewDialog;


public class AboutSettingsFragment extends EventDialogPreferenceFragment
		implements OnPreferenceClickListener {

	private Preference mVersionPref;
	private Preference mOpensourcePref;
	private Preference mProjectLocPref;
	private Preference mCheckUpdatePref;
	private Preference mFeedBackPerf;

	private static final int DIALOG_TYPE_VERSION = 0;
	private static final int DIALOG_TYPE_OPEN_SOURCE = 1;
	private static final int DIALOG_TYPE_APP_UPDATE = 2;

	private static final int MSG_TYPE_CHECK_UPDATE = 0;
	PackageInfo packageInfo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_about);
		mVersionPref = findPreference(Settings.KEY_PREF_ABOUT_VERSION);
		mVersionPref.setOnPreferenceClickListener(this);
		mOpensourcePref = findPreference(Settings.KEY_PREF_ABOUT_OPENSOURCE);
		mOpensourcePref.setOnPreferenceClickListener(this);
		mCheckUpdatePref = findPreference(Settings.KEY_PREF_ABOUT_CHECK_UPDATE);
		mCheckUpdatePref.setOnPreferenceClickListener(this);
		mProjectLocPref = findPreference(Settings.KEY_PREF_ABOUT_PROJ_LOC);
		mProjectLocPref.setOnPreferenceClickListener(this);
		mFeedBackPerf = findPreference(Settings.KEY_PREF_ABOUT_FEED_BACK);
		mFeedBackPerf.setOnPreferenceClickListener(this);
		try {
			packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
		}catch (Exception e){

		}
		mVersionPref.setSummary(packageInfo.versionName);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.equals(mOpensourcePref)) {
			Bundle bundle = new Bundle();
			bundle.putString("url", "file:///android_asset/licenses.html");
			bundle.putInt("titleRes", R.string.settings_about_opensource_pref);
			showDialog(DIALOG_TYPE_OPEN_SOURCE, bundle);

		} else if (preference.equals(mVersionPref)) {
			Bundle bundle = new Bundle();
			bundle.putString("url", "file:///android_asset/changelog.html");
			bundle.putInt("titleRes", R.string.settings_about_change_log);
			showDialog(DIALOG_TYPE_VERSION, bundle);
		} else if (preference.equals(mCheckUpdatePref)) {
		} else if (preference.equals(mProjectLocPref)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse((String) preference.getSummary()));
			startActivity(intent);
		} else if (preference.equals(mFeedBackPerf)) {
		}
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (super.handleMessage(msg)) {
			return true;
		}
		int type = msg.what;
		if (type == MSG_TYPE_CHECK_UPDATE) {
//			VersionInfo info = (VersionInfo) msg.obj;
//			if (info != null) {
//				if (info.version <= StaticApplication.get()
//						.getVersionCode()) {
//					Toast.makeText(
//							getActivity(),
//							getResources().getString(
//									R.string.settings_about_no_update),
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Bundle bundle = new Bundle();
//					bundle.putInt("version", info.version);
//					bundle.putInt("titleRes",
//							R.string.settings_about_new_version);
//					bundle.putString("url", info.url);
//					showDialog(DIALOG_TYPE_APP_UPDATE, bundle);
//				}
//			}
		}
		return false;
	}

	@Override
	public BaseDialog onCreateDialog(int type, Bundle param) {
		if (type == DIALOG_TYPE_OPEN_SOURCE || type == DIALOG_TYPE_VERSION) {
			return new WebViewDialog(getActivity(), new WebView(getActivity()),
					null, param);
		}
		return null;
	}
}
