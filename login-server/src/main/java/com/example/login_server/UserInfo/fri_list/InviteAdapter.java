package com.example.login_server.UserInfo.fri_list;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.login_server.R;

import java.util.ArrayList;
import java.util.List;

//邀请信息列表页面的适配器
public class InviteAdapter extends BaseAdapter {
    private Context mcontext;
    private List<Fri> mInvitationInfos = new ArrayList<>();
    private OnInviteLister mOninviteLister;

    public InviteAdapter(Context context,OnInviteLister onInviteLister) {
        mcontext = context;

        mOninviteLister = onInviteLister;
    }

    //刷新数据方法
    public void refresh(List<Fri> invatationInfos){
        if(invatationInfos != null && invatationInfos.size()>=0 ){
            mInvitationInfos.clear();
            mInvitationInfos.addAll(invatationInfos);
            //通知刷新页面
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitationInfos == null ?0:mInvitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1获取或创建ViewHoler
        ViewHolder holder =null;
        if(convertView == null){
            holder = new ViewHolder();

            convertView = View.inflate(mcontext, R.layout.item_invite,null);
            holder.name = convertView.findViewById(R.id.tv_invite_name);
            holder.reason = convertView.findViewById(R.id.tv_invite_reason);
            holder.accept = convertView.findViewById(R.id.bt_invite_accept);
            holder.reject = convertView.findViewById(R.id.bt_invite_reject);

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        //  2获取当前Item数据
        final Fri invatationInfo = mInvitationInfos.get(position);

        // 3显示当前Item数据
            //展示名称
        holder.name.setText(invatationInfo.getName());
            //原因--应当根据状态返回信息，比如被接收或者拒绝
        holder.reason.setText("请求添加好友");
           //如果原因是被接受或者拒绝，那么应当将按钮Gone掉
           //holder.accept.setVisibility(View.GONE);
           //holder.reject.setVisibility(View.GONE);

            //按钮的处理
        //接受
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOninviteLister.onAccept(invatationInfo);
                //holder.accept.setVisibility(View.GONE);
            }
        });
        //拒绝
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOninviteLister.onReject(invatationInfo);
            }
        });
        //  4返回View

        return convertView;
    }

    private class ViewHolder{
        private TextView name;
        private TextView reason;
        private Button accept;
        private Button reject;
    }

    public interface OnInviteLister{
        //联系人接受按钮的点击事件
        void onAccept(Fri invatationInfo);

        //联系人拒绝按钮的点击事件
        void onReject(Fri invatationInfo);
    }
}
