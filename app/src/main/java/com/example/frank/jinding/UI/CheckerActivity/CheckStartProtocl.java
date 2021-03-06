package com.example.frank.jinding.UI.CheckerActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baoyz.widget.PullRefreshLayout;
import com.example.frank.jinding.Adapter.ProtocolAdapter;
import com.example.frank.jinding.Bean.OrderBean.ConsignmentDetail;
import com.example.frank.jinding.Bean.OrderBean.DeviceType;
import com.example.frank.jinding.Bean.OrderBean.OrderDeviceDetail;
import com.example.frank.jinding.Conf.CheckControl;
import com.example.frank.jinding.R;
import com.example.frank.jinding.Service.ApiService;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckStartProtocl extends AppCompatActivity {

    private ImageButton back;
    private TextView title;
    private ListView lv_task;
    private ProtocolAdapter waitAdapter;

    private Button endtest,backwork;
    private String submission_id="";
    private Boolean isMainChecker=false;
    private String refreshcode="23";

    private List<ConsignmentDetail> consignmentList;
    private List<OrderDeviceDetail> deviceList;
    private List<DeviceType> deviceTypeList;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner mSpinner;
    private String orderId="";
    //private SwipeRefreshLayout refreshLayout;
    private PullRefreshLayout pullRefreshLayout;
    private AlertDialog processDialog;
    private boolean isNotComplete=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_start_protocl);

        submission_id=getIntent().getStringExtra("submission_id");
        orderId=getIntent().getStringExtra("order_id");
        isMainChecker=getIntent().getBooleanExtra("isMainChecker",false);
        init();
        View processView = View.inflate(this, R.layout.simple_processbar, null);
        processDialog= new AlertDialog.Builder(this).create();
        processDialog.setView(processView);
        title.setText("设备检验");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNotComplete)
                {
                    // 创建退出对话框
                    AlertDialog isExit = new AlertDialog.Builder(CheckStartProtocl.this).create();
                    // 设置对话框标题
                    isExit.setTitle("系统提示");
                    // 设置对话框消息
                    isExit.setMessage("您还有设备检验任务没有完成，是否确定离开？");
                    // 添加选择按钮并注册监听
                    isExit.setButton("确定", listener);
                    isExit.setButton2("取消", listener);
                    // 显示对话框
                    isExit.show();

                }else {
                    finish();
                }
            }
        });



        endtest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText et=new EditText(CheckStartProtocl.this);

                new AlertDialog.Builder(CheckStartProtocl.this)
                        .setMessage("请输入终止检验申请理由：")
                        .setView(et)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {
                                        if (et.getText().toString().trim().length()>2) {
                                            processDialog.show();
                                            HashMap<String,String> map_data=new HashMap<>();
                                            map_data.put("submission_id",submission_id);
                                            map_data.put("orderId",orderId);
                                            map_data.put("reason",et.getText().toString());
                                            Map<String, Object> paremetes = new HashMap<>();
                                            //paremetes.put("data", submission_id + "## " + et.getText()+ " ##" +orderId);
                                            paremetes.put("data",JSON.toJSONString(map_data));
                                            ApiService.GetString(CheckStartProtocl.this, "terminationTest", paremetes, new RxStringCallback() {
                                                @Override
                                                public void onNext(Object tag, String response) {

                                                    if (response.trim().equals("true")) {
                                                        endtest.setVisibility(endtest.INVISIBLE);
                                                        backwork.setVisibility(backwork.INVISIBLE);
                                                        CheckControl.start=true;
                                                        finish();
                                                        Toast.makeText(CheckStartProtocl.this, "申请提交成功，请等候审批结果", Toast.LENGTH_SHORT).show();
                                                        OperationProcess.handler.postDelayed(OperationProcess.runnable, 100);
                                                    } else {
                                                        Toast.makeText(CheckStartProtocl.this, "申请提交失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                    processDialog.dismiss();
                                                }

                                                @Override
                                                public void onError(Object tag, Throwable e) {
                                                    processDialog.dismiss();
                                                    Toast.makeText(CheckStartProtocl.this, "申请提交失败" + e, Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancel(Object tag, Throwable e) {
                                                    processDialog.dismiss();
                                                    Toast.makeText(CheckStartProtocl.this, "申请提交失败" + e, Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }else {
                                            Toast.makeText(CheckStartProtocl.this, "申请提交失败，请输入合理的申请理由" , Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }).show();


            }
        });


        backwork.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final EditText et=new EditText(CheckStartProtocl.this);
                new AlertDialog.Builder(CheckStartProtocl.this)
                        .setMessage("请输入重新派工申请理由：")
                        .setView(et)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {
                                        if (et.getText().toString().trim().length()>2) {
                                            processDialog.show();
                                            HashMap<String,String> map_data=new HashMap<>();
                                            map_data.put("submission_id",submission_id);
                                            map_data.put("orderId",orderId);
                                            map_data.put("reason",et.getText().toString());
                                            Map<String, Object> paremetes = new HashMap<>();
                                            paremetes.put("data", JSON.toJSONString(map_data));
                                            ApiService.GetString(CheckStartProtocl.this, "reAssignment", paremetes, new RxStringCallback() {
                                                @Override
                                                public void onNext(Object tag, String response) {

                                                    if (response.trim().equals("true")) {
                                                        endtest.setVisibility(endtest.INVISIBLE);
                                                        backwork.setVisibility(backwork.INVISIBLE);
                                                        CheckControl.start=true;
                                                        finish();
                                                        Toast.makeText(CheckStartProtocl.this, "申请提交成功，请等候审批结果", Toast.LENGTH_SHORT).show();
                                                        OperationProcess.handler.postDelayed(OperationProcess.runnable, 100);
                                                    } else {
                                                        Toast.makeText(CheckStartProtocl.this, "申请提交失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                    processDialog.dismiss();
                                                }

                                                @Override
                                                public void onError(Object tag, Throwable e) {
                                                    processDialog.dismiss();
                                                    Toast.makeText(CheckStartProtocl.this, "申请提交失败" + e, Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onCancel(Object tag, Throwable e) {
                                                    processDialog.dismiss();
                                                    Toast.makeText(CheckStartProtocl.this, "申请提交失败" + e, Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }else {
                                            Toast.makeText(CheckStartProtocl.this, "申请提交失败，请输入合理的申请理由" , Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }).show();

            }
        });

        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Intent intent=new Intent(CheckStartProtocl.this,CheckStartDevice.class);
                intent.putExtra("ItemPosition", arg2);
                intent.putExtra("orderId", orderId);
                intent.putExtra("submission_id", submission_id);
                intent.putExtra("consignmentId",waitAdapter.list.get(arg2).getConsignmentId());
                intent.putExtra("deviceType",waitAdapter.list.get(arg2).getDeviceTypeId());
                intent.putExtra("isMainChecker",isMainChecker);

//                intent.putExtra("consignmentId",consignmentList.get(arg2).getConsignmentId());
//                intent.putExtra("deviceType",consignmentList.get(arg2).getDeviceTypeId());
//                intent.putExtra("ItemPosition", consignmentList.get(arg2).getConsignmentId());

                //startActivity(intent);
                startActivityForResult(intent,123);





            }
        });

        /*lv_task.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem==0)
                    refreshLayout.setEnabled(true);
                else
                    refreshLayout.setEnabled(false);
            }
        });*/

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(refreshcode);
            }
        });

       /* refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);


            }
        });*/





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==123&&CheckControl.order_finish){
            CheckControl.order_finish=false;
            finish();
        }
        else if (requestCode ==123) {

            getData(refreshcode);
        }
    }

    private void init(){


        endtest=(Button)this.findViewById(R.id.btn_check_start_end_check);
        backwork=(Button)this.findViewById(R.id.check_start_renew_dispatch);


        back = (ImageButton) this.findViewById(R.id.titleback);
        title = (TextView) this.findViewById(R.id.titleplain);
        //refreshLayout=(SwipeRefreshLayout)this.findViewById(R.id.refresh_check_start_protocol);
        pullRefreshLayout=(PullRefreshLayout)this.findViewById(R.id.refreshCheckStartProtocol);
        mSpinner=(Spinner)this.findViewById(R.id.check_start_protocol_spinner);
        lv_task = (ListView) this.findViewById(R.id.lv_check_start_protocol);
        consignmentList=new ArrayList<>();
        waitAdapter = new ProtocolAdapter(this,consignmentList,null,false,false,false);//得到一个MyAdapter对象

        List<String> spinnerList=new ArrayList<>();
        spinnerList.add("待检验协议");
        spinnerList.add("已检验协议");
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //  TODO  Auto-generated  method  stub
                /*  将所选mySpinner  的值带入myTextView  中*/
                if (mSpinner.getSelectedItem().equals("待检验协议")){
                    waitAdapter.list=consignmentList;
                    lv_task.setAdapter(waitAdapter);//为ListView绑定Adapter
                    getData("23");
                    refreshcode="23";
                }else if (mSpinner.getSelectedItem().equals("已检验协议")){

                    waitAdapter.list=consignmentList;
                    lv_task.setAdapter(waitAdapter);//为ListView绑定Adapter
                    getData("07");
                    refreshcode="07";
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //  TODO  Auto-generated  method  stub
                //myTextView.setText("NONE");
            }

        });

        if (!isMainChecker){
            endtest.setVisibility(View.INVISIBLE);
            backwork.setVisibility(View.INVISIBLE);
        }

    }



    private void getData(final String statusCode){
        Map<String,Object> map=new HashMap<>();
        map.put("orderId",orderId);
        map.put("statusCode",statusCode);
        ApiService.GetString(this,"orderConsignmentDetail",map, new RxStringCallback(){
            @Override
            public void onError(Object tag, Throwable e) {
              Log.i(TAG,e.getMessage());
              //refreshLayout.setRefreshing(false);
                pullRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancel(Object tag, Throwable e) {
                //refreshLayout.setRefreshing(false);
                pullRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(Object tag, String response) {
                consignmentList.clear();
                waitAdapter.list.clear();
                if (response!=null) {
                    Log.i(TAG,"获取协议成功"+response.toString());
                    //consignmentList.clear();
                    List<ConsignmentDetail> list= JSON.parseArray(response, ConsignmentDetail.class);
                    for (int i=0;i<list.size();i++){
                        consignmentList.add(list.get(i));
                    }
                    Log.i(TAG,consignmentList.toString());
                   // waitAdapter.notifyDataSetChanged();
                    Log.i(TAG, consignmentList.size() + "个");
                    if (consignmentList.size()>0&&refreshcode.equals("23")){
                        backwork.setEnabled(true);
                        endtest.setEnabled(true);
                        isNotComplete=false;
                    }else {
                        backwork.setEnabled(false);
                        endtest.setEnabled(false);
                        isNotComplete=true;
                    }
                }else {
                    String toastStr=null;
                    if (statusCode.equals("23")) {
                        toastStr = "协议全部检验完成";

                    }
                    else if (statusCode.equals("07")) {
                        toastStr = "还没有已检验完成的协议";
                    }
                    new AlertDialog.Builder(CheckStartProtocl.this).setTitle(toastStr).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                waitAdapter.notifyDataSetChanged();
                pullRefreshLayout.setRefreshing(false);
            }






        });
     //   getDeviceType();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK &&!isNotComplete)
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("您还有设备检验任务没有完成，是否确定离开？");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();

        }else {
          finish();
        }

        return false;

    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                {
                    isNotComplete=false;
                    finish();
                }
                break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };




}
