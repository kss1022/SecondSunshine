package com.example.secondsunshine;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.secondsunshine.Data.AppDataBase;

public class AddTaskViewModelfactory extends ViewModelProvider.NewInstanceFactory
{
    private  final AppDataBase mDb;
    private  final int mTaskId;



    AddTaskViewModelfactory(AppDataBase dataBase, int TaskId)
    {
        mDb = dataBase;
        mTaskId = TaskId;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTaskViewModel(mDb, mTaskId);
    }
}
