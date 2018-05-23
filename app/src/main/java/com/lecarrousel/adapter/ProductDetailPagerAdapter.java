package com.lecarrousel.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lecarrousel.R;
import com.lecarrousel.model.ProductListModel;

import java.util.List;

public class ProductDetailPagerAdapter extends PagerAdapter {
    private List<ProductListModel.ImgData> mImageList;
    private LayoutInflater inflater;
    private Context context;


    public ProductDetailPagerAdapter(Context context, List<ProductListModel.ImgData> mImageList) {
        this.context = context;
        this.mImageList = mImageList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {


        View imageLayout = inflater.inflate(R.layout.row_product_detail_image, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.ivProductImg);
        final ImageView ivLeft = (ImageView) imageLayout.findViewById(R.id.ivProductImg);
        final ImageView ivRIght = (ImageView) imageLayout.findViewById(R.id.ivProductImg);

        if(mImageList.size() == 1){

        }
        if (mImageList.get(position).img != null) {
            Glide.with(context).load(mImageList.get(position).img).centerCrop().crossFade().into(imageView);
        }
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
