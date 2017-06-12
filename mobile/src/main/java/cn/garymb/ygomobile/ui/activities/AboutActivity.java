package cn.garymb.ygomobile.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.fragments.AboutFragment;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        getFragmentManager().beginTransaction().replace(R.id.fragment, new AboutFragment()).commit();
    }
}
