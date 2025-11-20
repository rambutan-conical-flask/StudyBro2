package com.example.studybro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.ViewHolder> {

    private List<Ledger> ledgerList;
    private Context context;

    public LedgerAdapter(List<Ledger> ledgerList) {
        this.ledgerList = ledgerList;
    }

    @NonNull
    @Override
    public LedgerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ledger_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LedgerAdapter.ViewHolder holder, int position) {
        Ledger ledger = ledgerList.get(position);
        holder.tvName.setText(ledger.getName());
        holder.tvAmount.setText((ledger.getType().equals("income") ? "+" : "-") + "¥" + ledger.getAmount());
        holder.tvAmount.setTextColor(
                ledger.getType().equals("income") ?
                        holder.tvAmount.getResources().getColor(android.R.color.holo_green_dark) :
                        holder.tvAmount.getResources().getColor(android.R.color.holo_red_dark));

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            // 显示确认对话框
            new android.app.AlertDialog.Builder(context)
                    .setTitle("确认删除")
                    .setMessage("确定要删除 \"" + ledger.getName() + "\" 这条记录吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        deleteLedger(ledger, position);
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void deleteLedger(Ledger ledger, int position) {
        // 在后台线程删除记录
        LedgerDatabase.databaseWriteExecutor.execute(() -> {
            // 从数据库删除
            long ledgerId = ledger.getId();

            // 使用 ViewModel 删除（需要传入 context 来获取 ViewModel）
            if (context instanceof LedgerActivity) {
                LedgerViewModel viewModel = ((LedgerActivity) context).getViewModel();
                viewModel.deleteLedger(ledgerId);
            }

            // 回到主线程更新UI
            ((android.app.Activity) context).runOnUiThread(() -> {
                // 从列表中移除
                ledgerList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ledgerList.size());
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return ledgerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}