package com.my.paysheet.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.my.paysheet.R;
import com.my.paysheet.utils.BillItem;
import com.my.paysheet.utils.SP_Manager;
import com.my.paysheet.utils.Utils;

import java.util.List;

public class BillListActivity extends Activity {

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.billlist_activity);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView lv = (ListView) findViewById(R.id.lv_list);
        lv.setAdapter(mAdapter = new MyAdapter());
        List<BillItem> bi = SP_Manager.Instance().getObject("billlist");
        if (Utils.isEmpty(bi)) {
            lv.setVisibility(View.GONE);
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.GONE);
            mAdapter.setData(bi);
            mAdapter.notifyDataSetChanged();
        }

    }


    private class MyAdapter extends BaseAdapter {

        private List<BillItem> mDataList;

        public void setData(List<BillItem> list) {
            mDataList = list;
        }

        public List<BillItem> getData() {
            return mDataList;
        }

        @Override
        public int getCount() {
            if (null != mDataList) {
                return mDataList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(getApplicationContext(),
                        R.layout.order_lis_item, null);

                holder.tvname = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tvmoney = (TextView) convertView.findViewById(R.id.tv_money);
                holder.tvstatus = (TextView) convertView.findViewById(R.id.tv_status);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            if (null != mDataList && mDataList.size() > 0 && position < mDataList.size()) {
                BillItem menu = mDataList.get(position);

                holder.tvname.setText(Utils.timedate("HH:mm:ss", menu.mTime));
                String tmp = "";
                if (menu.mMoney > 0) {
                    tmp = "+"+menu.mMoney+"元";
                } else {
                    tmp = menu.mMoney+"元";
                }

                holder.tvmoney.setText(tmp);

                holder.tvstatus.setText(menu.mUsername);
            }
            return convertView;
        }

        class Holder {
            TextView tvname;
            TextView tvmoney;
            TextView tvstatus;

        }

    }


}
