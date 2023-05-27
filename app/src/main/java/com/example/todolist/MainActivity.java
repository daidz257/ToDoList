package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.MINUTE;

public class MainActivity extends AppCompatActivity {

    Database database;
    ListView lvTasks;
    ImageView addImg;
    ArrayList<Task> arrayTask;
    TaskAdapter adapter;
    SearchView searchView_home;

    public String testPriority;
    public static final String NOTIFICATION_CHANNEL_ID = "11003";
    private final static String default_notification_channel_id = "default";
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addImg = (ImageView) findViewById(R.id.img_add);
        lvTasks = (ListView) findViewById(R.id.listviewCongViec);
        searchView_home = findViewById(R.id.searchView_home);

        arrayTask = new ArrayList<>();

        adapter = new TaskAdapter(this, R.layout.task_item, arrayTask);
        lvTasks.setAdapter(adapter);

        //khởi tạo database Todolist
        database = new Database(this, "todolist.sqlite", null, 2);

        //tạo bảng Tasks
        database.QueryData("CREATE TABLE IF NOT EXISTS Tasks(Id INTEGER PRIMARY KEY AUTOINCREMENT, TenCV VARCHAR(200), DateCV VARCHAR(200), TimeCV VARCHAR(200), StatusCV INT, PriorityCV VARCHAR(200) ); ");
       // Lấy dữ liệu tại bảng Tasks
        GetDataTasks();

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogThem();
            }
        }
        );

        //tim kiem
        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        List<Task> filteredList = new ArrayList<>();
        for (Task taskSingle : arrayTask){
            if(taskSingle.getTenCV().toLowerCase().contains(newText.toLowerCase())
                    ){
                filteredList.add(taskSingle);
            }
        }
        adapter.filterList(filteredList);
    }

    //dialog sửa ghi chú
    public void DialogEditTasks(String ten, String date, String time, final int id, String priority) {
        final Calendar calendar = Calendar.getInstance();
        final  Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit);

        final EditText edtTenCV = (EditText) dialog.findViewById(R.id.editTextTenCVEdit);
        final TextView tvDate = (TextView) dialog.findViewById(R.id.textDateCVEdit);
        final TextView tvTime = (TextView) dialog.findViewById(R.id.textTimeCVEdit);
        final RadioGroup tvGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupEdit);

        calendar.setTimeInMillis(System.currentTimeMillis());
        pickPriority(tvGroup);

        Button btnXacNhan = (Button) dialog.findViewById(R.id.buttonXacNhan);
        Button btnHuy = (Button) dialog.findViewById(R.id.buttonHuyEdit);

        edtTenCV.setText(ten);
        tvDate.setText(date);
        tvTime.setText(time);


        //bắt sự kiện chọn giờ trong dialog
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(tvTime, edtTenCV, calendar);
            }
        });
        //bắt sự kiện chọn ngày trong dialog
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(tvDate, calendar);
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMoi = edtTenCV.getText().toString().trim();
                String dateMoi = tvDate.getText().toString().trim();
                String timeMoi = tvTime.getText().toString().trim();
                String priorityMoi = testPriority;

                database.QueryData("UPDATE Tasks SET TenCV ='"+tenMoi+"', DateCV = '"+dateMoi+"', TimeCV = '"+timeMoi+"', StatusCV = '0', PriorityCV = '"+priorityMoi+"' WHERE Id = '"+ id +"'");
                Toast.makeText(MainActivity.this, "Đã cập nhập",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                GetDataTasks();
            }
        });
        dialog.show();
    }

    //dialog xác nhận xóa ghi chú
    public void DialogDeleteTask(String tencv, final int id){
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(this);
        dialogXoa.setMessage("Bạn có muốn xóa không?");
        dialogXoa.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.QueryData("DELETE FROM Tasks WHERE Id = '"+id+"'");
                Toast.makeText(MainActivity.this, "Đã xóa",Toast.LENGTH_SHORT).show();
                GetDataTasks();
            }
        });
        dialogXoa.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogXoa.show();
    }

    //lấy dữ liệu trong bảng Tasks
    private void GetDataTasks(){
        Cursor dataCongViec = database.GetData("SELECT * FROM Tasks");
        arrayTask.clear();
        while (dataCongViec.moveToNext()){
            int id = dataCongViec.getInt(0);
            String ten = dataCongViec.getString(1);
            String ngay = dataCongViec.getString(2);
            String gio = dataCongViec.getString(3);
            int status = dataCongViec.getInt(4);
            String priority = dataCongViec.getString(5);
            arrayTask.add(new Task(id, ten, ngay, gio, status, priority));
        }
        adapter.notifyDataSetChanged();
    }

    //chọn sự kiện Thêm ghi chú trên thanh menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.tt_dackMode:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
            case R.id.tt_lightMode:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
            default: break;
        }
//        if(item.getItemId() == R.id.menuSetting){
//            startActivity(new Intent(MainActivity.this, SettingActivity.class));
//        }
        return true;
    }

    // thêm ghi chú
    private void DialogThem(){
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addtask);

        final Calendar calendar = Calendar.getInstance();
        final EditText edtTen = (EditText) dialog.findViewById(R.id.editTextTenCV);
        final TextView txtDate = (TextView) dialog.findViewById(R.id.textDateCV);
        final TextView txtTime = (TextView) dialog.findViewById(R.id.textTimeCV);
        final RadioGroup txtGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);

        pickPriority(txtGroup);

        calendar.setTimeInMillis(System.currentTimeMillis());

        Button btnThem = (Button) dialog.findViewById(R.id.buttonThem);
        Button btnHuy =  (Button) dialog.findViewById(R.id.buttonHuy);

        //bắt sự kiện chọn giờ
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(txtTime, edtTen, calendar);
            }
        });
        //bắt sự kiện chọn ngày
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(txtDate, calendar);
            }
        });
        //bắt sự kiện xác nhận thêm ghi chú
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tencv = edtTen.getText().toString();
                String datecv = txtDate.getText().toString();
                String timecv = txtTime.getText().toString();
                String prioritycv = testPriority;

                //kiểm tra không nhập gì vào ô editText
                if(tencv.equals("")){
                    Toast.makeText(MainActivity.this, "Vui lòng nhập ghi chú công việc.",Toast.LENGTH_SHORT).show();
                }else {
                    //insert database
                    database.QueryData("INSERT INTO Tasks (Id, TenCV, DateCV, TimeCV, StatusCv, PriorityCV ) VALUES (null, '" + tencv + "','"+datecv+"','"+timecv+"','0','"+prioritycv+"');");
                    scheduleNotification(getNotification(tencv), calendar.getTimeInMillis());
                    Toast.makeText(MainActivity.this, "Đã thêm.",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    GetDataTasks();
                }
            }
        });
        //bắt sự kiện hủy
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //chon priority
    public void pickPriority(RadioGroup txtGroup) {
        if (txtGroup != null) {
        txtGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkId) {
                switch (checkId) {
                    case R.id.radioButton:
                        testPriority = "High";
                        break;
                    case R.id.radioButton2:
                        testPriority = "Medium";
                        break;
                    case R.id.radioButton3:
                        testPriority = "Low";
                        break;
                }
            }
        } );
    } else testPriority = " ";}

    public ListView getLvTasks() {
        return lvTasks;
    }


    //dialog chọn giờ
    private void pickTime(final TextView txtTime, final EditText edtTen, final Calendar calendar) {
        final String tencv = edtTen.getText().toString();
        int gio = calendar.get(Calendar.HOUR);
        int phut = calendar.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                txtTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, gio, phut, false);
        timePickerDialog.show();

    }

    //dialog chọn ngày
    private void pickDate(final TextView txtDate, final Calendar calendar) {
        int ngay = calendar.get(Calendar.DAY_OF_MONTH);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam, thang, ngay);
        datePickerDialog.show();
    }




    //Schedule alarm notification
    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        Log.d(TAG, "scheduleNotification: Notification set successfully!");
    }

    //Build notification
    private Notification getNotification(String content) {
        //on notification click open MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("ToDo Reminder");
        builder.setContentText(content);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }
    public void updateStatus(int id, int check) {
        database.QueryData("UPDATE Tasks SET StatusCV ='"+check+"' WHERE Id = '"+ id +"'");
    }

}
