package com.my.paysheet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.my.paysheet.R;
import com.my.paysheet.utils.OrderItem;
import com.my.paysheet.utils.SP_Manager;
import com.my.paysheet.utils.Utils;

import java.util.List;

public class OrderListActivity extends Activity {

    private MyAdapter mAdapter;
    private ListView mlvList;
    private List<OrderItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.orderlist_activity);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mlvList = (ListView) findViewById(R.id.lv_list);
        mlvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isEmpty(mDataList))
                    return;

                OrderItem or = mDataList.get(position);

                Intent i = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                i.putExtra("orderitem", or);
                startActivityForResult(i, 1001);

            }
        });
        mlvList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(OrderListActivity.this);
                        builder.setTitle("确认删除");
                        builder.setMessage("请确认是否删除订单");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteOrder(position);
                            }
                        });
                        builder.create().show();
                        return true;
            }
        });
        mlvList.setAdapter(mAdapter = new MyAdapter());

        refresh();

    }


    private void refresh() {
        mDataList = SP_Manager.Instance().getObject("orderlist");
        if (Utils.isEmpty(mDataList)) {
            mlvList.setVisibility(View.GONE);
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        } else {
            mlvList.setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.GONE);
            mAdapter.setData(mDataList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteOrder(int i){
        mDataList.remove(i);
        SP_Manager.Instance().setObject("orderlist",mDataList);
        refresh();
    }

    private class MyAdapter extends BaseAdapter {

        private List<OrderItem> mDataList;

        public void setData(List<OrderItem> list) {
            mDataList = list;
        }

        public List<OrderItem> getData() {
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
                OrderItem menu = mDataList.get(position);


                holder.tvname.setText(menu.mUsername);
                holder.tvmoney.setText(menu.mMoney+"元");
                String tmp = "";
                if (menu.mStatus == OrderItem.STATUS_DONE) {
                    tmp = "交易完成";
                } else if (menu.mStatus == OrderItem.STATUS_CLOSE) {
                    tmp = "交易关闭";
                } else if (menu.mStatus == OrderItem.STATUS_WAITING_SEND) {
                    tmp = "待发货";
                } else if (menu.mStatus == OrderItem.STATUS_WAITING_RECEIVE) {
                    tmp = "待收货";
                } else if (menu.mStatus == OrderItem.STATUS_APPLY_REFOUND) {
                    tmp = "已申请退款";
                }
                holder.tvstatus.setText(tmp);
            }
            return convertView;
        }

        class Holder {
            TextView tvname;
            TextView tvmoney;
            TextView tvstatus;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            refresh();
        }
    }


}
