package cn.garymb.ygomobile.ui.cards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.FastScrollLinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewItemListener;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tubb.smrv.SwipeMenuRecyclerView;

import java.io.IOException;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.loader.CardLoader;
import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.adapters.CardListAdapter;
import ocgcore.LimitManager;
import ocgcore.StringManager;
import ocgcore.data.Card;

public abstract class BaseCardsAcitivity extends BaseActivity implements CardLoader.CallBack {
    protected DrawerLayout mDrawerlayout;
    private SwipeMenuRecyclerView mListView;
    protected CardSearcher mCardSelector;
    protected CardListAdapter mCardListAdapater;
    protected CardLoader mCardLoader;
    protected boolean isLoad = false;
    private ImageLoader mImageLoader;
    protected StringManager mStringManager = StringManager.get();
    protected LimitManager mLimitManager = LimitManager.get();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_cards);
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        mImageLoader = new ImageLoader(this);
        mDrawerlayout = $(R.id.drawer_layout);
        ViewGroup group = $(R.id.layout_main);
        group.addView(getMainView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mListView = $(R.id.list_cards);
        mCardListAdapater = new CardListAdapter(this, mImageLoader);
        mCardListAdapater.setEnableSwipe(true);
        mListView.setLayoutManager(new FastScrollLinearLayoutManager(this));
        mListView.setAdapter(mCardListAdapater);
        setListeners();

        mCardLoader = new CardLoader(this);
        mCardLoader.setCallBack(this);
        mCardSelector = new CardSearcher($(R.id.nav_view_list), mCardLoader);
        //
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, toolbar, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener((v) -> {
            onBackHome();
        });
        toggle.syncState();
    }

    protected int getDimen(int id) {
        return (int) getResources().getDimension(id);
    }

    protected void setListeners() {
        mListView.addOnItemTouchListener(new RecyclerViewItemListener(mListView, new RecyclerViewItemListener.OnItemListener() {
            @Override
            public void onItemClick(View view, int pos) {
                onCardClick(view, mCardListAdapater.getItem(pos), pos);
            }

            @Override
            public void onItemLongClick(View view, int pos) {
                onCardLongClick(view, mCardListAdapater.getItem(pos), pos);
            }

            @Override
            public void onItemDoubleClick(View view, int pos) {

            }
        }));
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Glide.with(getContext()).resumeRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Glide.with(getContext()).pauseRequests();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Glide.with(getContext()).resumeRequests();
                        break;
                }
            }
        });
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    protected void onBackHome() {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
            return;
        }
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
            return;
        }
        finish();
    }


    protected abstract View getMainView();

    @Override
    public void onSearchResult(List<Card> cardInfos) {
//        Log.d("kk", "find " + (cardInfos == null ? -1 : cardInfos.size()));
        mCardListAdapater.set(cardInfos);
        mCardListAdapater.notifyDataSetChanged();
        if (cardInfos != null && cardInfos.size() > 0) {
            mListView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mImageLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onResetSearch() {

    }

    protected void hideDrawers() {
        if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerlayout.closeDrawer(Gravity.RIGHT);
        }
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    protected abstract void onCardClick(View view, Card cardInfo, int pos);

    protected abstract void onCardLongClick(View view, Card cardInfo, int pos);

    protected void showSearch(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
    }

    protected void showResult(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Gravity.LEFT);
        }
    }
}
