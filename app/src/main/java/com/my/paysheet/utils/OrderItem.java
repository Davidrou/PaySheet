package com.my.paysheet.utils;

import java.io.Serializable;

public class OrderItem implements Serializable{

    public static final int STATUS_WAITING_SEND = 1;
    public static final int STATUS_WAITING_RECEIVE = 2;
    public static final int STATUS_CLOSE = 3;
    public static final int STATUS_DONE = 4;
    public static final int STATUS_APPLY_REFOUND = 5;
    private static final long serialVersionUID = 6068556631698916207L;

    public String mUsername;
	public float mMoney;
    public int mStatus;
    public long mTime;
    public String mReason;
    public String mbeizhu;
    public String mSheetID;

}
