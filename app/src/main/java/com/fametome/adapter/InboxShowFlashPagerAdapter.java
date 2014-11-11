package com.fametome.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fametome.activity.member.MainActivity;
import com.fametome.object.ParseMessage;
import com.fametome.R;
import com.fametome.object.Flash;
import com.fametome.object.User;

public class InboxShowFlashPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater = null;
    ParseMessage message;

    public InboxShowFlashPagerAdapter(Context context, ParseMessage message){
        this.context = context;
        this.message = message;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return message.getFlashNumber();
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

        final View v = inflater.inflate(R.layout.item_inbox_show_flash, null);

        ImageView picture = (ImageView) v.findViewById(R.id.picture);
        TextView text = (TextView) v.findViewById(R.id.text);

        final Flash currentFlash = message.getFlash(position);

        if (currentFlash.getType() == Flash.TYPE_TEXT) {
            picture.setVisibility(View.GONE);
            text.setText(currentFlash.getText());

        } else if (currentFlash.getType() == Flash.TYPE_PICTURE) {
            picture.setImageBitmap(currentFlash.getPicture().getBitmap());
            text.setVisibility(View.GONE);
            v.findViewById(R.id.textFrame).setVisibility(View.GONE);

        } else if (currentFlash.getType() == Flash.TYPE_FACE) {
            picture.setImageBitmap(currentFlash.getPicture().getBitmap());
            text.setText(currentFlash.getText());

        }

        container.addView(v, 0);

        return v;
    }
}
