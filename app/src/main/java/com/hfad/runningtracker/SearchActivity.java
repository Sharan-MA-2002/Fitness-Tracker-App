package com.hfad.runningtracker;

import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.hfad.runningtracker.Adapters.RunAdapter;
import com.hfad.runningtracker.db.Run;
import com.hfad.runningtracker.db.RunDAO;
import com.hfad.runningtracker.db.RunningDatabase;
import com.hfad.runningtracker.ui.MainActivity;

import java.util.List;
import java.util.Locale;

public class SearchActivity extends MainActivity{
    List<RunningDatabase>list;
    List<RunningDatabase>tempList;
    RunAdapter adapter;

    public void filterList(String text){
        for(RunningDatabase run:list){
            if(run.getRunDao().getAllRunsSortedByDate().toString().contains(text.toLowerCase())){
                tempList.add(run);
            }
        }
        if(tempList.isEmpty()){
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            adapter.setFilteredList(tempList);
        }
    }
}
