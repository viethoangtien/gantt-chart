package com.nama_gatsuo.dreamplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.nama_gatsuo.dreamplan.View.DateView;
import com.nama_gatsuo.dreamplan.dao.SubTaskDao;
import com.nama_gatsuo.dreamplan.dao.TaskDao;
import com.nama_gatsuo.dreamplan.model.Task;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class TaskEditActivity extends ActionBarActivity {

    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private Task task;
    private SQLiteDatabase db;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // IntentでのTaskの受け取り
        task = (Task)getIntent().getSerializableExtra("Task");

        EditText taskName = (EditText)findViewById(R.id.task_edit_name);
        final DateView startDate = (DateView)findViewById(R.id.task_edit_startDate);
        final DateView endDate = (DateView)findViewById(R.id.task_edit_endDate);
        Spinner status = (Spinner)findViewById(R.id.task_edit_status);
        EditText description = (EditText)findViewById(R.id.task_edit_description);

        // Database接続
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        taskDao = new TaskDao(db);
        subTaskDao = new SubTaskDao(db);

        // Taskが既に存在していればViewに値をセット
        if(taskDao.exists(task.getTaskID())) {
            taskName.setText(task.getName());
            startDate.setDate(task.getStartDate());
            endDate.setDate(task.getEndDate());
            status.setSelection(task.getStatus());
            description.setText(task.getDescription());
        }

        // startDateにbetterpickerのClickListnerを設定
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();

                // Fragmentに渡す日付を決定
                DateTime dt;
                if (startDate.getText().toString().length() == 0) {
                    dt = DateTime.now().withTimeAtStartOfDay();
                } else {
                    dt = new DateTime().withMillis(startDate.getDate());
                }
                CalendarDatePickerDialog cdpd = CalendarDatePickerDialog
                        .newInstance(startDate, dt.getYear(), dt.getMonthOfYear() - 1,
                                dt.getDayOfMonth());
                cdpd.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });

        // endDateにbetterpickerのClickListnerを設定
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();

                // Fragmentに渡す日付を決定
                DateTime dt;
                if (endDate.getText().toString().length() == 0) {
                    dt = DateTime.now().withTimeAtStartOfDay();
                } else {
                    dt = new DateTime().withMillis(endDate.getDate());
                }
                CalendarDatePickerDialog cdpd = CalendarDatePickerDialog
                        .newInstance(endDate, dt.getYear(), dt.getMonthOfYear() - 1,
                                dt.getDayOfMonth());
                cdpd.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });
    }

    // Save Button
    public void onClickSave(View v) {
        try {
            task.setName(((EditText) findViewById(R.id.task_edit_name)).getText().toString());
            task.setDescription(((EditText) findViewById(R.id.task_edit_description)).getText().toString());
            task.setStatus(((Spinner) findViewById(R.id.task_edit_status)).getSelectedItemPosition());
            task.setStartDate(((DateView)findViewById(R.id.task_edit_startDate)).getDate());
            task.setEndDate(((DateView)findViewById(R.id.task_edit_endDate)).getDate());

            if (taskDao.save(task) < 0) {
                throw new Exception("could not save Task");
            }
            Toast.makeText(this, R.string.toast_success_save, Toast.LENGTH_LONG).show();

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.toast_fail_save, Toast.LENGTH_LONG).show();
        }
    }

    // Cancel Button
    public void onClickCancel(View v) {
        finish();
    }

    // Delete Button
    public void onClickDelete(View v) {
        // Show confirming message.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.edit_alert_title);
        alertDialogBuilder.setMessage(R.string.task_edit_alert_message);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete this task and its children.
                        try {
                            if (taskDao.deleteByTaskID(task.getTaskID()) < 0 || subTaskDao.deleteByTaskID(task.getTaskID()) < 0) {
                                throw new Exception("could not delete Task");
                            }
                            Toast.makeText(TaskEditActivity.this, R.string.toast_success_delete, Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TaskEditActivity.this, R.string.toast_fail_delete, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }
}