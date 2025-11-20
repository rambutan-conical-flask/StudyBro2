package com.example.studybro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LedgerMonthAdapter extends RecyclerView.Adapter<LedgerMonthAdapter.MonthViewHolder> {

    private List<LedgerMonth> monthList;
    private Context context;
    private LedgerViewModel viewModel;

    public LedgerMonthAdapter(Context context, List<LedgerMonth> monthList, LedgerViewModel viewModel) {
        this.context = context;
        this.monthList = monthList;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ledger_month, parent, false);
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        LedgerMonth month = monthList.get(position);

        // 设置月份显示
        holder.tvMonth.setText(month.year + "年" + month.month + "月");

        // 设置收入和支出总额
        holder.tvIncome.setText(String.format("收入: ¥%.2f", month.incomeTotal));
        holder.tvExpense.setText(String.format("支出: ¥%.2f", month.expenseTotal));

        // 展开/折叠明细
        boolean isExpanded = month.isExpanded;
        holder.recyclerItems.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setRotation(isExpanded ? 180 : 0);

        // 清除旧的适配器，防止数据复用
        if (!isExpanded) {
            holder.recyclerItems.setAdapter(null);
        }

        // 箭头点击事件
        holder.ivArrow.setOnClickListener(v -> {
            month.isExpanded = !month.isExpanded;

            if (month.isExpanded) {
                // 在后台线程加载该月份的详细记录
                LedgerDatabase.databaseWriteExecutor.execute(() -> {
                    List<Ledger> ledgerList = viewModel.getLedgersForMonth(month.year, month.month);

                    // 回到主线程更新UI
                    holder.itemView.post(() -> {
                        // 创建新的适配器，确保数据正确
                        LedgerAdapter ledgerAdapter = new LedgerAdapter(ledgerList);
                        holder.recyclerItems.setLayoutManager(new LinearLayoutManager(context));
                        holder.recyclerItems.setAdapter(ledgerAdapter);
                        holder.recyclerItems.setVisibility(View.VISIBLE);

                        // 调试日志
                        Log.d("LedgerDebug", "展开 " + month.year + "年" + month.month + "月");
                        Log.d("LedgerDebug", "加载记录数: " + ledgerList.size());
                        for (Ledger ledger : ledgerList) {
                            Log.d("LedgerDebug", "记录: " + ledger.getName() + " " +
                                    ledger.getAmount() + " " + ledger.getType() + " " +
                                    ledger.getYear() + "-" + ledger.getMonth());
                        }
                    });
                });
            } else {
                holder.recyclerItems.setVisibility(View.GONE);
                holder.recyclerItems.setAdapter(null); // 清除适配器
            }

            notifyItemChanged(position);
        });

        // 如果已经是展开状态，重新加载数据
        if (month.isExpanded) {
            LedgerDatabase.databaseWriteExecutor.execute(() -> {
                List<Ledger> ledgerList = viewModel.getLedgersForMonth(month.year, month.month);
                holder.itemView.post(() -> {
                    LedgerAdapter ledgerAdapter = new LedgerAdapter(ledgerList);
                    holder.recyclerItems.setLayoutManager(new LinearLayoutManager(context));
                    holder.recyclerItems.setAdapter(ledgerAdapter);

                    // 调试日志
                    Log.d("LedgerDebug", "重新加载 " + month.year + "年" + month.month + "月");
                    Log.d("LedgerDebug", "记录数: " + ledgerList.size());
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return monthList == null ? 0 : monthList.size();
    }

    public void setMonthList(List<LedgerMonth> monthList) {
        this.monthList = monthList;
        notifyDataSetChanged();
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvIncome, tvExpense, tvNetAmount;
        ImageView ivArrow;
        RecyclerView recyclerItems;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvIncome = itemView.findViewById(R.id.tv_income);
            tvExpense = itemView.findViewById(R.id.tv_expense);

            ivArrow = itemView.findViewById(R.id.iv_arrow);
            recyclerItems = itemView.findViewById(R.id.recycler_items);
        }
    }
}