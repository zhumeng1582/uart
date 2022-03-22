package com.industio.uart.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.industio.uart.R;

import java.util.ArrayList;
import java.util.List;

public class ErrorInfoAdapter extends RecyclerView.Adapter<ErrorInfoAdapter.ViewHolder> {

    private final List<String> stringList = new ArrayList<>();

    private boolean isErrorInfo = false;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text);
        }
    }
    public void add(RecyclerView recyclerView,String log){
        if (isErrorInfo) {
            stringList.clear();
        }

        isErrorInfo = false;
        stringList.add(log);
        notifyItemInserted(stringList.size()-1);

        //滑动到底部了
        if (!recyclerView.canScrollVertically(1)) {
            recyclerView.scrollToPosition(getItemCount() - 1);
        }
    }
    public void clearAll(){
        isErrorInfo = false;
        stringList.clear();
        notifyDataSetChanged();
    }
    public void errorInfo(String error){
        isErrorInfo = true;
        stringList.clear();
        stringList.add(error);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_error, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String fruit = stringList.get(position);
        holder.text.setText(fruit);
        if(holder.text.getText().toString().contains("测试通过")) {
            holder.text.setTextColor(Color.BLUE);
        } else if(holder.text.getText().toString().contains("超时错误")) {
            holder.text.setTextColor(Color.YELLOW);
        } else {
            holder.text.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
