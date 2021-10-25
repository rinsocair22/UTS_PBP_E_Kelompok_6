package com.example.jasaphotographerapp;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterPhotographer extends Filter {

    private AdapterPhotographer adapter;
    private ArrayList<ModelPhotographer> filterList;

    public FilterPhotographer (AdapterPhotographer adapter, ArrayList<ModelPhotographer> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if(constraint != null && constraint.length()>0){
            //search filled not empty, search something,perform search

            //change to upper case, to make case insensitive
            constraint = constraint.toString().toLowerCase();
            //store our filtered list
            ArrayList<ModelPhotographer> filteredModels = new ArrayList<>();
            for (int i=0;i<filterList.size();i++){
                //check, search by Name and type
                if(filterList.get(i).getName().toLowerCase().contains(constraint) ||
                        filterList.get(i).getType().toLowerCase().contains(constraint) ){
                    //add filtered data to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count= filteredModels.size();
            results.values = filteredModels;
        }
        else{
            //search filled empty, not searching,return original/all/complete list
            results.count= filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.pgList=(ArrayList<ModelPhotographer>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();

    }
}
