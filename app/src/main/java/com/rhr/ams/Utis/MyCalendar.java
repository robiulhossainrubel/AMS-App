package com.rhr.ams.Utis;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

public class MyCalendar extends DialogFragment {
    Calendar calendar=Calendar.getInstance();
    public interface OnCalendarClickListener{
        void OnClickCalendar(int year,int month,int day);
    }
    public OnCalendarClickListener onCalendarClickListener;

    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        this.onCalendarClickListener = onCalendarClickListener;
    }
    public void SetData(int year,int month,int day){
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(Objects.requireNonNull(requireActivity()),(view, year, month, dayOfMonth) -> onCalendarClickListener.OnClickCalendar(year,month,dayOfMonth),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }
    public String getData(){
        return DateFormat.format("dd/MM/yyyy",calendar).toString();
    }
}
