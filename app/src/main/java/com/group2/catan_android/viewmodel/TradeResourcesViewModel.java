package com.group2.catan_android.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.group2.catan_android.data.repository.gamestate.CurrentGamestateRepository;

public class TradeResourcesViewModel extends ViewModel {

    private int[] resources;

    public TradeResourcesViewModel(){
        resources = new int[]{0,0,0,0,0};
    }

    public int togglePlus(int index){
        return ++resources[index];
    }
    public int toggleMinus(int index){
        if(resources[index]==0){
            return -1;
        }else {
            return --resources[index];
        }
    }
    public int[] getResources(){
        int[] result = new int[5];
        result[2]=resources[0];
        result[3]=resources[1];
        result[1]=resources[2];
        result[0]=resources[3];
        result[4]=resources[4];
        return result;
    }

}
