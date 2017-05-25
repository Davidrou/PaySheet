package com.my.paysheet.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.my.paysheet.R;
import com.my.paysheet.utils.SP_Manager;

public class MainActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        mContext = this;

        findViewById(R.id.btn_chongzhi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RechargeActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_tixian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RechargeActivity.class);
                i.putExtra("type", 1);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_fukuan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, PayActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_zhangdan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, BillListActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_dingdan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, OrderListActivity.class);
                startActivity(i);
            }
        });


    }

    public void onResume() {
        super.onResume();

        float money = SP_Manager.Instance().getMoney();
        ((TextView)findViewById(R.id.tv_text)).setText("账户余额：" + String.valueOf(money));
    }



}
