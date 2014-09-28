package com.fametome.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.R;
import com.fametome.object.User;

public class AccountFacePagerAdapter extends PagerAdapter {

    LayoutInflater inflater = null;

    public AccountFacePagerAdapter(Context context){
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return User.getInstance().getFacesNumber();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == ((View) o);
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View v = inflater.inflate(R.layout.item_account_face_pager, null);

        ImageView picture = (ImageView)v.findViewById(R.id.picture);
        TextView text = (TextView)v.findViewById(R.id.text);

        picture.setImageBitmap(User.getInstance().getFace(position).getPicture().getBitmap());
        text.setText(User.getInstance().getFace(position).getText());

        container.addView(v, 0);

        return v;
    }
}
