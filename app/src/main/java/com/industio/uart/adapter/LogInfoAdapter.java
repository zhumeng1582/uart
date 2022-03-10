package com.industio.uart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.industio.uart.R;

import java.util.ArrayList;
import java.util.List;

public class LogInfoAdapter extends RecyclerView.Adapter<LogInfoAdapter.ViewHolder> {



    private boolean isNeedRefresh;
    private final List<String> stringList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text);
        }
    }

    public void add(String log) {
        isNeedRefresh = true;
        stringList.add(log);
    }

    public void refresh(RecyclerView recyclerView) {
        //滑动到底部了
        if (!recyclerView.canScrollVertically(1)) {
            if (isNeedRefresh) {
                isNeedRefresh = false;
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
