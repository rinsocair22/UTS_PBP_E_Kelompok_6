package com.example.jasaphotographerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterPortfolioClient extends RecyclerView.Adapter<AdapterPortfolioClient.HolderPortfolioClient> {

    private Context context;
    public ArrayList<ModelPortfolio> portfolioList;

    public AdapterPortfolioClient(Context context, ArrayList<ModelPortfolio> portfolioList) {
        this.context = context;
        this.portfolioList = portfolioList;
    }

    @NonNull
    @Override
    public HolderPortfolioClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflat layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_portfolio_client,parent,false);
        return new HolderPortfolioClient(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPortfolioClient holder, int position) {
    //get data
        ModelPortfolio modelPortfolio = portfolioList.get(position);
        String pfID = modelPortfolio.getPfID();
        String pfInfo = modelPortfolio.getPfInfo();
        String pfName = modelPortfolio.getPfName();
        String pgID = modelPortfolio.getPgID();
        String pfLink1 = modelPortfolio.getPfLink1();
        String pfLink2 = modelPortfolio.getPfLink2();
        String pfLink3 = modelPortfolio.getPfLink3();
        String pfLink4 = modelPortfolio.getPfLink4();
        String pfLink5 = modelPortfolio.getPfLink5();
        String pfLink6 = modelPortfolio.getPfLink6();

        //set data
        holder.pfNameTv.setText(pfName);
        holder.pfInfoTv.setText("Info:"+" "+pfInfo);
        //upload all picture from storage
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(pfLink1));
        slideModels.add(new SlideModel(pfLink2));
        slideModels.add(new SlideModel(pfLink3));
        slideModels.add(new SlideModel(pfLink4));
        slideModels.add(new SlideModel(pfLink5));
        slideModels.add(new SlideModel(pfLink6));
        holder.slider.setImageList(slideModels,true);

        try{

            Picasso.get().load(pfLink1).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo1IV);
            Picasso.get().load(pfLink2).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo2IV);
            Picasso.get().load(pfLink3).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo3IV);
            Picasso.get().load(pfLink4).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo4IV);
            Picasso.get().load(pfLink5).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo5IV);
            Picasso.get().load(pfLink6).fit().placeholder(R.drawable.ic_add_photo).into(holder.photo6IV);

        }
        catch(Exception e){

            holder.photo1IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo2IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo3IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo4IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo5IV.setImageResource(R.drawable.ic_add_photo);
            holder.photo6IV.setImageResource(R.drawable.ic_add_photo);

        }
    }

    @Override
    public int getItemCount() {
        return portfolioList.size();
    }

    class HolderPortfolioClient extends RecyclerView.ViewHolder{

        //views of  recycleView

        private TextView pfNameTv,pfInfoTv;
        private ImageView photo1IV,photo2IV,photo3IV,photo4IV,photo5IV,photo6IV;
        private ImageSlider slider;

        public HolderPortfolioClient(@NonNull View itemView) {
            super(itemView);

            pfNameTv = itemView.findViewById(R.id.pfNameTv);
            pfInfoTv = itemView.findViewById(R.id.pfInfoTv);
            photo1IV = itemView.findViewById(R.id.photo1IV);
            photo2IV = itemView.findViewById(R.id.photo2IV);
            photo3IV = itemView.findViewById(R.id.photo3IV);
            photo4IV = itemView.findViewById(R.id.photo4IV);
            photo5IV = itemView.findViewById(R.id.photo5IV);
            photo6IV = itemView.findViewById(R.id.photo6IV);
            slider = itemView.findViewById(R.id.slider);
        }
    }
}
