package com.industio.uart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CloneUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.industio.uart.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogInfoAdapter extends RecyclerView.Adapter<LogInfoAdapter.ViewHolder> {

    private final List<String> stringList = new ArrayList<>();
    private final List<String> temp = new CopyOnWriteArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text);
        }
    }

    public void add(String log) {
        temp.add(log);
    }

    public void refresh(RecyclerView recyclerView) {
        //滑动到底部了
        if (!recyclerView.canScrollVertically(1)) {
            if (!temp.isEmpty()) {
                stringList.addAll(temp);
                temp.clear();
                notifyDataSetChanged();
            }
            recyclerView.scrollToPosition(getItemCount() - 1);
        }
    }

    public void clearAll() {
        stringList.clear();
        notifyDataSetChanged();
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String fruit = stringList.get(position);
        holder.text.setText(fruit);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
