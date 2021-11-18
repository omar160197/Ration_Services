package com.appz.qrcode.seller_tasks.adapters;

import android.widget.TextView;

public interface StoreOnClickItem {
    void onClickPlus(int pos, TextView view, TextView v2);

    void onClickMinus(int pos, TextView view, TextView v2);

    void onUpdate(int pos);

    void ondelete(int pos);
}
