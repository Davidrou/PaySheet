package com.my.paysheet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.my.paysheet.R;
import com.my.paysheet.utils.BillItem;
import com.my.paysheet.utils.SP_Manager;
import com.my.paysheet.utils.Utils;

import java.util.ArrayList;

public class RechargeActivity extends Activity {


    private int mType = 0; //0位充值，1位提现

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.recharge_activity);

        mType = getIntent().getIntExtra("type", 0);

        TextView tvtitle = (TextView)findViewById(R.id.tv_toptitle);
        TextView tvName = (TextView) findViewById(R.id.btn_chongzhi);
        if (mType == 0) {
            tvtitle.setText("充值");
            tvName.setText("充值金额(元)");
        } else {
            tvtitle.setText("提现");
            tvName.setText("提现金额(元)");
        }

        final EditText etJe = (EditText) findViewById(R.id.et_je);
        final EditText etMm = (EditText) findViewById(R.id.et_mm);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String je = etJe.getText().toString().trim();
                String mm = etMm.getText().toString().trim();
                if (Utils.isEmpty(je)) {
                    Utils.showToast("金额不能为空");
                    return;
                }

                if (Utils.isEmpty(mm)) {
                    Utils.showToast("密码不能为空");
                    return;
                }

                float money = Float.valueOf(je);
                if (money < 1 || money > 10000) {
                    if (mType == 0) {
                        Utils.showToast("充值金额范围为1-10000，请输入正确数值");
                    } else if (mType == 1) {
                        Utils.showToast("提现金额范围为1-10000，请输入正确数值");
                    }
                    return;
                }

                if (mType == 1) {
                    float ye = SP_Manager.Instance().getMoney();
                    if (money > ye) {
                        Utils.showToast("提现金额超过账户余额"+ye+"，无法提现");
                        return;
                    }
                }

                SP_Manager.Instance().writeMoney(mType == 0 ? money : -money);

                ArrayList<BillItem> billlist = SP_Manager.Instance().getObject("billlist");
                if (null == billlist) {
                    billlist = new ArrayList<BillItem>();
                }
                BillItem bi = new BillItem();
                if (mType == 0) {
                    bi.mMoney = money;
                    bi.mUsername = "充值";
                } else {
                    bi.mMoney = -money;
                    bi.mUsername = "提现";
                }
                bi.mTime = System.currentTimeMillis();

                billlist.add(0, bi);
                SP_Manager.Instance().setObject("billlist", billlist);

                if (mType == 0) {
                    Utils.showToast("充值成功");
                } else if (mType == 1) {
                    Utils.showToast("提现成功");
                }

                finish();
            }
        });

    }



}
