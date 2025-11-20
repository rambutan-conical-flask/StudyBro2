package com.example.studybro;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LedgerActivity extends AppCompatActivity {

    private LedgerViewModel viewModel;
    private RecyclerView recyclerView;
    private LedgerMonthAdapter adapter;
    private List<LedgerMonth> monthList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);

        recyclerView = findViewById(R.id.rv_ledger);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);

        adapter = new LedgerMonthAdapter(this, monthList, viewModel);
        recyclerView.setAdapter(adapter);

        // 观察所有记录，然后手动按年月分组
        viewModel.getAllLedgers().observe(this, ledgers -> {
            if (ledgers != null) {
                monthList.clear();
                monthList.addAll(groupLedgersByYearMonth(ledgers));
                adapter.setMonthList(monthList);
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(v -> showAddDialog());
    }

    private List<LedgerMonth> groupLedgersByYearMonth(List<Ledger> ledgers) {
        Map<String, LedgerMonth> map = new LinkedHashMap<>();

        Log.d("LedgerDebug", "=== 开始分组 ===");
        Log.d("LedgerDebug", "总记录数: " + ledgers.size());

        for (Ledger ledger : ledgers) {
            String key = ledger.getYear() + "-" + ledger.getMonth();
            Log.d("LedgerDebug", "记录: " + ledger.getName() + " " + ledger.getAmount() +
                    " " + ledger.getType() + " " + key);

            LedgerMonth month = map.get(key);
            if (month == null) {
                month = new LedgerMonth(ledger.getYear(), ledger.getMonth(), 0, 0);
                map.put(key, month);
                Log.d("LedgerDebug", "创建新月份: " + key);
            }

            if ("income".equals(ledger.getType())) {
                month.incomeTotal += ledger.getAmount();
            } else {
                month.expenseTotal += ledger.getAmount();
            }

            Log.d("LedgerDebug", "更新后 - 收入: " + month.incomeTotal + " 支出: " + month.expenseTotal);
        }

        Log.d("LedgerDebug", "=== 分组完成 ===");
        Log.d("LedgerDebug", "月份组数: " + map.size());
        for (LedgerMonth month : map.values()) {
            Log.d("LedgerDebug", "月份组: " + month.year + "-" + month.month +
                    " 收入: " + month.incomeTotal + " 支出: " + month.expenseTotal);
        }

        return new ArrayList<>(map.values());
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_ledger, null);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etAmount = view.findViewById(R.id.et_amount);
        EditText etYear = view.findViewById(R.id.et_year);
        EditText etMonth = view.findViewById(R.id.et_month);
        RadioButton rbIncome = view.findViewById(R.id.rb_income);
        RadioButton rbExpense = view.findViewById(R.id.rb_expense);

        // 设置默认值为当前年月
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        etYear.setText(String.valueOf(calendar.get(java.util.Calendar.YEAR)));
        etMonth.setText(String.valueOf(calendar.get(java.util.Calendar.MONTH) + 1));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("添加记账")
                .setCancelable(true)
                .create();

        view.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String yearStr = etYear.getText().toString().trim();
            String monthStr = etMonth.getText().toString().trim();

            if (name.isEmpty() || amountStr.isEmpty() || yearStr.isEmpty() || monthStr.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!rbIncome.isChecked() && !rbExpense.isChecked()) {
                Toast.makeText(this, "请选择收入或支出", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);

                // 验证月份范围
                if (month < 1 || month > 12) {
                    Toast.makeText(this, "月份必须在1-12之间", Toast.LENGTH_SHORT).show();
                    return;
                }

                String type = rbIncome.isChecked() ? "income" : "expense";

                viewModel.addLedger(type, name, amount, year, month);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void refreshData() {
        // 重新加载数据
        viewModel.getAllLedgers().observe(this, ledgers -> {
            if (ledgers != null) {
                monthList.clear();
                monthList.addAll(groupLedgersByYearMonth(ledgers));
                adapter.setMonthList(monthList);
            }
        });
    }

    public LedgerViewModel getViewModel() {
        return viewModel;
    }
}