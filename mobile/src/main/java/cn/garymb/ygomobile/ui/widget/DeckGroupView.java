package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.cards.deck.ImageTop;
import cn.garymb.ygomobile.ui.cards.deck.LabelInfo;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

public class DeckGroupView extends FrameLayout {
    private final DeckLabel mMainLabel, mExtraLabel, mSideLabel;
    private final LabelInfo mLabelInfo;
    private final DeckInfo mDeckInfo;
    private ImageTop mImageTop;
    private LimitList mLimitList;
    private int mainTop, extraTop, sideTop;
    int mCardWidth = 0;
    int mCardHeight = 0;
    private final SparseArray<CardView> mMainViews = new SparseArray<>();
    private final SparseArray<CardView> mExtraViews = new SparseArray<>();
    private final SparseArray<CardView> mSideViews = new SparseArray<>();

    private int mMainLimit = 15, mExtraLimit = 15, mSideLimit = 15;

    public DeckGroupView(@NonNull Context context) {
        this(context, null);
    }

    public DeckGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeckGroupView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLabelInfo = new LabelInfo(context);
        mDeckInfo = new DeckInfo();

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DeckView);
            if (array != null) {
                mCardWidth = array.getInteger(R.styleable.DeckView_card_width, 0);
            }
        }
        if (mCardWidth <= 0) {
            int width = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
            mCardWidth = width / 10;
        }
        if (mCardWidth <= 0) {
            mCardWidth = (getResources().getDisplayMetrics().widthPixels - getPaddingLeft() - getPaddingRight()) / 10;
        }
        mCardHeight = Math.round((255.0f / 177.0f) * mCardWidth);

        mMainLabel = new DeckLabel(context);
        mSideLabel = new DeckLabel(context);
        mExtraLabel = new DeckLabel(context);
        int labelHeight = (int) getResources().getDimension(R.dimen.deck_label_height);
        int top = 0;
        addView(mMainLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        mainTop = labelHeight;
        top = mainTop + 4 * mCardHeight;
        addView(mExtraLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        extraTop = labelHeight + top;
        top = extraTop + mCardHeight;
        addView(mSideLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        sideTop = top + labelHeight;

        int line, pos;
        for (int i = 0; i < Constants.DECK_MAIN_MAX; i++) {
            line = i / Constants.DECK_WIDTH_MAX_COUNT;
            pos = i % Constants.DECK_WIDTH_MAX_COUNT;
            CardView cardView = new CardView(context);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, mainTop + line * mCardHeight, pos * mCardWidth));
            mMainViews.put(i, cardView);
        }
        for (int i = 0; i < Constants.DECK_EXTRA_MAX; i++) {
            CardView cardView = new CardView(context);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, extraTop, i * mCardWidth));
            mExtraViews.put(i, cardView);
        }
        for (int i = 0; i < Constants.DECK_SIDE_MAX; i++) {
            CardView cardView = new CardView(context);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, sideTop, i * mCardWidth));
            mSideViews.put(i, cardView);
        }
    }

    private FrameLayout.LayoutParams makeLayoutParams(int w, int h, int top, int left) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w, h);
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        return layoutParams;
    }

    private FrameLayout.LayoutParams makeLayoutParams(int w, int h, int top) {
        return makeLayoutParams(w, h, top, 0);
    }

    private FrameLayout.LayoutParams makeLayoutParams(int top) {
        return makeLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, top);
    }

    public ImageTop getImageTop() {
        if (mImageTop == null) {
            mImageTop = new ImageTop(getContext());
        }
        return mImageTop;
    }

    public void updateAll(DeckInfo deckInfo) {
        mDeckInfo.update(deckInfo);
        mLabelInfo.update(deckInfo);

        mMainLimit = Math.max(10, mDeckInfo.getMainCount() / 4);
        mExtraLimit = Math.max(10, mDeckInfo.getExtraCount());
        mSideLimit = Math.max(10, mDeckInfo.getSideCount());
    }

    public void updateCard(Type type, int index, int count) {
        if (type == Type.Extra) {
            int all = mExtraViews.size();
            for (int i = 0; i < all; i++) {
                if (i < mDeckInfo.getExtraCount()) {
                    if (i == index && count > 0) {
                        mExtraViews.get(i).showCard(mDeckInfo.getExtraCard(i));
                        index++;
                        count--;
                    }
                    if (mLimitChanged) {
                        mExtraViews.get(i).updateLimit(mImageTop, mLimitList);
                    }
                } else {
                    mSideViews.get(i).setVisibility(INVISIBLE);
                }
            }
            resizePadding(Type.Extra, mExtraViews);
        } else if (type == Type.Main) {
            int targetIndex, orgPos, all = mMainViews.size();
            //59
            targetIndex = (index / mMainLimit) * Constants.DECK_WIDTH_MAX_COUNT + (index % mMainLimit);
            for (int i = 0; i < all; i++) {
                orgPos = i % Constants.DECK_WIDTH_MAX_COUNT;
                if (orgPos >= mMainLimit) {
                    mMainViews.get(i).setVisibility(INVISIBLE);
                } else {
//                    if (i < mDeckInfo.getMainCount()) {
//                        if (targetIndex == i && count > 0) {
//                            index++;
//                            mMainViews.get(i).updateLimit(mImageTop, mLimitList);
//                            targetIndex = (index / mMainLimit) * Constants.DECK_WIDTH_MAX_COUNT + (index % mMainLimit);
//                            count--;
////                            line = i / mMainLimit;
////                            pos = i - line * mMainLimit;
////                            int vindex = line * Constants.DECK_WIDTH_MAX_COUNT + pos;
//
//                        }
//                        if (mLimitChanged) {
//                            mMainViews.get(i).updateLimit(mImageTop, mLimitList);
//                        }
//                    } else {
//                        mMainViews.get(i).showCard(null);
//                    }
                }
            }
            resizePadding(Type.Main, mMainViews);
        } else if (type == Type.Side) {
            int all = mSideViews.size();
            for (int i = 0; i < all; i++) {
                if (i < mDeckInfo.getExtraCount()) {
                    if (i == index && count > 0) {
                        mSideViews.get(i).showCard(mDeckInfo.getSideCard(i));
                        index++;
                        count--;
                    }
                    if (mLimitChanged) {
                        mSideViews.get(i).updateLimit(mImageTop, mLimitList);
                    }
                } else {
                    mSideViews.get(i).setVisibility(INVISIBLE);
                }
            }
            resizePadding(Type.Side, mSideViews);
        }
    }

    private int getMaxWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void resizePadding(Type type, SparseArray<CardView> list) {
        int count = list.size();
        if (count <= 1) return;
        int needWidth, limit;
        if (type == Type.Main) {
            limit = mMainLimit;
        } else if (type == Type.Extra) {
            limit = mExtraLimit;
        } else {
            limit = mSideLimit;
        }
        needWidth = limit * mCardWidth;
        int maxWidth = getMaxWidth();
        int p = (needWidth > maxWidth) ? (maxWidth - needWidth) / (limit - 1) : 0;
        int top;
        if (type == Type.Side) {
            top = sideTop;
        } else if (type == Type.Extra) {
            top = extraTop;
        } else {
            top = mainTop;
        }
        int all = list.size();
        int orgLine, orgPos;
        for (int i = 0; i < all; i++) {
            View v = list.get(i);
            orgLine = i / Constants.DECK_WIDTH_MAX_COUNT;
            orgPos = i % Constants.DECK_WIDTH_MAX_COUNT;
            if (orgPos >= limit) {
                v.setVisibility(GONE);
                continue;
            } else {
                v.setVisibility(VISIBLE);
            }
            LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(mCardWidth, mCardHeight);
                layoutParams.topMargin = top + orgLine * mCardHeight;
            }
            layoutParams.setMargins(orgPos * (mCardWidth + p), layoutParams.topMargin, 0, 0);
            v.setLayoutParams(layoutParams);
        }
        postInvalidate();
    }

    public void notifyDataSetChanged() {
        updateCard(Type.Main, 0, Constants.DECK_MAIN_MAX);
        updateCard(Type.Extra, 0, Constants.DECK_EXTRA_MAX);
        updateCard(Type.Side, 0, Constants.DECK_SIDE_MAX);
        mMainLabel.setText(mLabelInfo.getMainString());
        mExtraLabel.setText(mLabelInfo.getExtraString());
        mSideLabel.setText(mLabelInfo.getSideString());
        mLimitChanged = false;
    }

    public enum Type {
        Main, Extra, Side
    }

    private boolean mLimitChanged = false;

    public void setLimitList(LimitList limitList) {
        mLimitList = limitList;
        mLimitChanged = true;
    }
}
