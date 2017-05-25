package com.my.paysheet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.my.paysheet.R;
import com.my.paysheet.utils.BillItem;
import com.my.paysheet.utils.OrderItem;
import com.my.paysheet.utils.SP_Manager;
import com.my.paysheet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends Activity {


    private OrderItem mOrderItem;
    private AlertDialog mDialog;
    private String mSelectedStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.orderdetail_activity);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mOrderItem = (OrderItem) getIntent().getSerializableExtra("orderitem");

        ((TextView)findViewById(R.id.tv_name)).setText("对象：" + mOrderItem.mUsername);
        ((TextView)findViewById(R.id.tv_time)).setText("时间：" + Utils.timedate("yyyy-MM-dd HH:mm:ss", mOrderItem.mTime));

        String tmp = "";
        if (!Utils.isEmpty(mOrderItem.mReason)) {
            tmp = mOrderItem.mReason;
        }
        ((TextView)findViewById(R.id.tv_reason)).setText("原因：" + tmp);

        if (!Utils.isEmpty(mOrderItem.mbeizhu)) {
            tmp = mOrderItem.mbeizhu;
            ((TextView)findViewById(R.id.tv_beizhu)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_beizhu)).setText("备注：" + tmp);
        } else {
            ((TextView)findViewById(R.id.tv_sheet)).setVisibility(View.GONE);
        }
        if (!Utils.isEmpty(mOrderItem.mSheetID)) {
            tmp = mOrderItem.mSheetID;
            ((TextView)findViewById(R.id.tv_sheet)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_sheet)).setText("快递单号：" + tmp);
        } else {
            ((TextView)findViewById(R.id.tv_sheet)).setVisibility(View.GONE);
        }


        final int status = mOrderItem.mStatus;

        Button btnLeft = (Button)findViewById(R.id.btn_left);
        btnLeft.setVisibility(View.GONE);
        String buttontext = "";
        if (status == OrderItem.STATUS_CLOSE) {
            tmp = "交易关闭";
            buttontext = "确定";
        } else if (status == OrderItem.STATUS_WAITING_RECEIVE) {
            tmp = "待收货";
            buttontext = "收货";
            btnLeft.setVisibility(View.VISIBLE);
            btnLeft.setText("申请退货");
        } else if (status == OrderItem.STATUS_WAITING_SEND) {
            tmp = "待发货";
            buttontext = "发货";
        } else if (status == OrderItem.STATUS_APPLY_REFOUND) {
            tmp = "已申请退款";
            buttontext = "同意退款";
        } else {
            tmp = "交易完成";
            buttontext = "确定";
        }
        ((TextView)findViewById(R.id.tv_status)).setText("状态：" + tmp);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDlg(mOrderItem); //退款
            }
        });

        Button btn = (Button)findViewById(R.id.btn_right);
        btn.setText(buttontext);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == OrderItem.STATUS_WAITING_RECEIVE) { //收货
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                    builder.setMessage("确认收货？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            mOrderItem.mStatus = OrderItem.STATUS_DONE;
                            updateData(mOrderItem);

                            //余额增加
                            SP_Manager.Instance().writeMoney(mOrderItem.mMoney);

                            //生成账单
                            ArrayList<BillItem> billlist = SP_Manager.Instance().getObject("billlist");
                            if (null == billlist) {
                                billlist = new ArrayList<BillItem>();
                            }
                            BillItem bi = new BillItem();
                            bi.mMoney = mOrderItem.mMoney;
                            bi.mTime = System.currentTimeMillis();
                            bi.mUsername = mOrderItem.mUsername;
                            billlist.add(0, bi);
                            SP_Manager.Instance().setObject("billlist", billlist);

                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }

                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.create().show();
                } else if (status == OrderItem.STATUS_WAITING_SEND) { //发货
                    showPopupDlg(mOrderItem);
                } else if (status == OrderItem.STATUS_APPLY_REFOUND) { //同意退款
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                    builder.setMessage("同意退款？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            mOrderItem.mStatus = OrderItem.STATUS_CLOSE;
                            updateData(mOrderItem);
                            dialog.dismiss();

                            //余额减少
                            SP_Manager.Instance().writeMoney(-mOrderItem.mMoney);

                            //生成账单
                            ArrayList<BillItem> billlist = SP_Manager.Instance().getObject("billlist");
                            if (null == billlist) {
                                billlist = new ArrayList<BillItem>();
                            }
                            BillItem bi = new BillItem();
                            bi.mMoney = -mOrderItem.mMoney;
                            bi.mTime = System.currentTimeMillis();
                            bi.mUsername = "订单退款";
                            billlist.add(0, bi);
                            SP_Manager.Instance().setObject("billlist", billlist);

                            setResult(RESULT_OK);
                            finish();
                        }

                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    builder.create().show();
                } else {
                    finish();
                }
            }
        });

    }

    private void showPopupDlg(final OrderItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
        View view = View.inflate(OrderDetailActivity.this, R.layout.pop_dlg, null);


        List<String> data_list = new ArrayList<String>();
        TextView tvTitle = (TextView)view.findViewById(R.id.tv_toptitle);
        final View llinput = view.findViewById(R.id.ll_input);

        final TextView tvname = (TextView) view.findViewById(R.id.tv_name);
        if (item.mStatus == OrderItem.STATUS_WAITING_SEND) {
            tvname.setText("发货方式");
            tvTitle.setText("发货");

            data_list.add("物流运输");
            data_list.add("当面交易");

        } else if(item.mStatus == OrderItem.STATUS_WAITING_RECEIVE) {
            tvname.setText("退款原因");
            tvTitle.setText("退款");

            data_list.add("不想买了");
            data_list.add("质量问题");
            data_list.add("与需求有差异");
            data_list.add("其他理由");
        }
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        mSelectedStr = data_list.get(0);

        //适配器
        ArrayAdapter arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedStr = (String)parent.getAdapter().getItem(position);
                if (mSelectedStr.equals("物流运输")) {
                    llinput.setVisibility(View.VISIBLE);
                } else {
                    llinput.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        Button ok = (Button) view.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.mStatus == OrderItem.STATUS_WAITING_SEND) {

                    String sheetid = "";
                    if (mSelectedStr.equals("物流运输")) {
                        EditText et = (EditText)llinput.findViewById(R.id.et_input);
                        String tmp = et.getText().toString().trim();
                        if (Utils.isEmpty(tmp)) {
                            Utils.showToast("请输入快递单号");
                            return;
                        }
                        sheetid = tmp;
                    }
                    item.mSheetID = sheetid;
                    item.mStatus = OrderItem.STATUS_WAITING_RECEIVE;
                } else if(item.mStatus == OrderItem.STATUS_WAITING_RECEIVE) {
                    item.mStatus = OrderItem.STATUS_APPLY_REFOUND;
                    item.mReason = mSelectedStr;
                }

                updateData(item);

                mDialog.dismiss();

                setResult(RESULT_OK);
                finish();

            }
        });
        builder.setView(view);
        mDialog = builder.show();

    }

    private void updateData(OrderItem oi) {
        List<OrderItem> list = SP_Manager.Instance().getObject("orderlist");
        for (int i = 0; i < list.size(); i++) {
            OrderItem item = list.get(i);
            if (item.mUsername.equals(oi.mUsername) &&
                    item.mTime == oi.mTime) {
                list.set(i, oi);
                SP_Manager.Instance().setObject("orderlist", list);
                break;
            }
        }
    }


}
