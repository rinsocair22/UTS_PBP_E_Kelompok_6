package com.example.jasaphotographerapp;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterBooking extends Filter {

    private AdapterBookingPhotographer adapter;

    private ArrayList<ModelBooking> filterList;

    public FilterBooking(AdapterBookingPhotographer adapter, ArrayList<ModelBooking> filterList){
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results = new FilterResults();
        //validate data for search query
        if(constraint!=null && constraint.length()>0){
            //search filed not empty, search something, perform search

            //change to upper case , to make case insensative
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelBooking> filteredModels = new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //check
                if(filterList.get(i).getBookStatus().toUpperCase().contains(constraint) ||
                        filterList.get(i).getBookDate().toUpperCase().contains(constraint)||
                        filterList.get(i).getBookLocation().toUpperCase().contains(constraint)){

                    //add filtered data to list

                    filteredModels.add(filterList.get(i));

                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
        //search filed  empty, not searching, return orginal/all/complete list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

            adapter.bookingList = (ArrayList<ModelBooking>) results.values;

            //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
