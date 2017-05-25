package com.my.paysheet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.my.paysheet.R;
import com.my.paysheet.utils.BillItem;
import com.my.paysheet.utils.OrderItem;
import com.my.paysheet.utils.SP_Manager;
import com.my.paysheet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_activity);


        final EditText etZh = (EditText) findViewById(R.id.et_zh);//交易对象
        final EditText etje = (EditText) findViewById(R.id.et_je);//交易金额
        final EditText etyy = (EditText) findViewById(R.id.et_yy);//交易原因
        final EditText etmm = (EditText) findViewById(R.id.et_mm);//交易备注

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
                String je = etje.getText().toString().trim();
                String yy = etyy.getText().toString().trim();
                String zh = etZh.getText().toString().trim();
                String mm = etmm.getText().toString().trim();
                if (Utils.isEmpty(zh)) {
                    Utils.showToast("对象不能为空");
                    return;
                }
                if (Utils.isEmpty(je)) {
                    Utils.showToast("金额不能为空");
                    return;
                }
                if (Utils.isEmpty(yy)) {
                    Utils.showToast("原因不能为空");
                    return;
                }

                float money = Float.valueOf(je);

                if (money <= 0) {
                    Utils.showToast("金额不对，请重新输入");
                    return;
                }


                ArrayList<BillItem> billlist = SP_Manager.Instance().getObject("billlist");
                if (null == billlist) {
                    billlist = new ArrayList<BillItem>();
                }




                //生成订单数据
                OrderItem or = new OrderItem();
                or.mStatus = OrderItem.STATUS_WAITING_SEND;
                or.mUsername = zh;
                if (yy != null && yy.length() > 0) {
                    or.mReason = yy;
                }
                if (mm != null && mm.length() > 0) {
                    or.mbeizhu = mm;
                }
                or.mTime = System.currentTimeMillis();
                or.mMoney = money;

                List<OrderItem> datalist = SP_Manager.Instance().getObject("orderlist");
                if (null == datalist) {
                    datalist = new ArrayList<OrderItem>();
                }
                datalist.add(0, or);
                SP_Manager.Instance().setObject("orderlist", datalist);



                Utils.showToast("创建成功");

                finish();
            }
        });


    }



}
