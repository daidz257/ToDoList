package com.example.todolist;

public class Task {
    private int IdCV;
    private String TenCV;
    private String DateCV;
    private String TimeCV;
    private int StatusCv;
    private String PriorityCV;


    public Task(int idCV, String tenCV, String dateCV, String timeCV, int statusCV, String priorityCV)
    {
        IdCV = idCV;
        TenCV = tenCV;
        DateCV = dateCV;
        TimeCV = timeCV;
        StatusCv = statusCV;
        PriorityCV = priorityCV;
    }
    public Task(int idCV, String dateCV, String timeCV, int statusCV, String priorityCV)
    {
        IdCV = idCV;
        DateCV = dateCV;
        TimeCV = timeCV;
        StatusCv = statusCV;
        PriorityCV = priorityCV;
    }

    public int getIdCV() {
        return IdCV;
    }

    public String getTenCV() {
        return TenCV;
    }

    public void setIdCV(int idCV) {
        IdCV = idCV;
    }

    public void setTenCV(String tenCV) {
        TenCV = tenCV;
    }
    public String getDateCV() {
        return DateCV;
    }

    public void setDateCV(String dateCV) {
        DateCV = dateCV;
    }

    public String getTimeCV() {
        return TimeCV;
    }

    public void setTimeCV(String timeCV) {
        TimeCV = timeCV;
    }

    public int getStatusCv() { return StatusCv; }

    public void setStatusCv(int statusCv) { StatusCv = statusCv;}

    public String getPriorityCV() { return  PriorityCV; }

    public void setPriorityCV(String priorityCV) { PriorityCV = priorityCV; }
}
