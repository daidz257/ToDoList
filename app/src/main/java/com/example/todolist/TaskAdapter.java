package com.example.todolist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private MainActivity context;
    private int layout;
    private List<Task> taskList;

    public TaskAdapter(MainActivity context, int layout, List<Task> taskList) {
        this.context = context;
        this.layout = layout;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        CheckBox checkBox;
        TextView txtTen;
        TextView txtDate;
        TextView txtTime;
        ImageView imgDelete;
        TextView txtPriority;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            holder.txtTen = (TextView) convertView.findViewById(R.id.textviewTen);
            holder.txtDate = (TextView) convertView.findViewById(R.id.textviewDate);
            holder.txtTime = (TextView) convertView.findViewById(R.id.textviewTime);
            holder.txtPriority = (TextView) convertView.findViewById(R.id.textviewPriority);

            holder.imgDelete = (ImageView) convertView.findViewById(R.id.imageviewDelete);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_id);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }



        final Task task = taskList.get(position);
        holder.txtTen.setText(task.getTenCV());
        holder.txtDate.setText(task.getDateCV());
        holder.txtTime.setText(task.getTimeCV());
        holder.txtPriority.setText(task.getPriorityCV());
        holder.checkBox.setChecked(toBoolean(task.getStatusCv()));
        if (toBoolean(task.getStatusCv()) == true) {
            holder.txtTen.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
        {
            holder.txtTen.setPaintFlags(holder.txtTen.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtTime.setPaintFlags(holder.txtTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    context.updateStatus(task.getIdCV(), 1);
                } else {
                    context.updateStatus(task.getIdCV(), 0);
                }
            }
        });



        //bắt sự kiện xóa và sửa
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.DialogEditTasks(task.getTenCV(), task.getDateCV(), task.getTimeCV() , task.getIdCV(), task.getPriorityCV());
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.DialogDeleteTask(task.getTenCV(), task.getIdCV());
            }
        });

        //bắt sự kiện checkbox
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    holder.checkBox.setChecked(true);
                    holder.txtTen.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtDate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.txtTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else
                {
                    holder.checkBox.setChecked(false);
                    holder.txtTen.setPaintFlags(holder.txtTen.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtDate.setPaintFlags(holder.txtDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.txtTime.setPaintFlags(holder.txtTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        return convertView;
    }

    private boolean toBoolean(int statusCv) {
        if (statusCv == 0) {
            return false;
        } else return true;
    }

    public void filterList(List<Task> filteredList){
        taskList = filteredList;
        notifyDataSetChanged();
    }
}
