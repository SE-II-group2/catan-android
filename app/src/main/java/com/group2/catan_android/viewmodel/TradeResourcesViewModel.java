package com.group2.catan_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;

import java.lang.reflect.Field;

public class TradeResourcesViewModel extends ViewModel {

    private static final int LOGIC_FOREST_INDEX = 2;
    private static final int LOGIC_HILLS_INDEX = 3;
    private static final int LOGIC_PASTURE_INDEX = 1;
    private static final int LOGIC_FIELDS_INDEX = 0;
    private static final int LOGIC_MOUNTAINS_INDEX = 4;
    private final int[] resources;

    private final MutableLiveData<int[]> resourceLiveData;
    public TradeResourcesViewModel(){
        resources = new int[]{0,0,0,0,0};
        resourceLiveData = new MutableLiveData<>();
    }
    public MutableLiveData<int[]> getResourceLiveData(){
        return resourceLiveData;
    }

    public void increase(int index){
        ++resources[index];
        resourceLiveData.setValue(resources);
    }
    public void decrease(int index){
        if(resources[index]>0){
            --resources[index];
            resourceLiveData.setValue(resources);
        }
    }
    public int[] getResources(){
        return resources;
    }

}
