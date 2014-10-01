package com.fametome.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.listener.CameraListener;
import com.fametome.listener.OnFaceClickListener;
import com.fametome.object.Face;
import com.fametome.object.Flash;
import com.fametome.object.Message;
import com.fametome.object.ParseFace;
import com.fametome.object.ParseFlash;
import com.fametome.object.User;
import com.fametome.util.FTBitmap;
import com.fametome.util.FTDefaultBitmap;
import com.parse.Parse;

public class OutboxFlashsListAdapter extends BaseAdapter {

    private MainActivity activity;

    private LayoutInflater inflater = null;

    private Message message;

    public OutboxFlashsListAdapter(MainActivity activity){
        this.activity = activity;
        inflater = (LayoutInflater)activity.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        message = new Message();

        message.addFlash(new ParseFlash());
        message.addFlash(new ParseFlash());
        message.addFlash(new ParseFlash());
    }

    @Override
    public int getCount() {
        return message.getFlashNumber();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.d("OutboxFlashListAdapter", "getView - item n°" + position);

        convertView = inflater.inflate(R.layout.item_outbox_list, parent, false);

        final AutoCompleteTextView text = (AutoCompleteTextView)convertView.findViewById(R.id.text);
        final ImageButton picture = (ImageButton)convertView.findViewById(R.id.picture);

        OutboxFlashsListAutoCompleteAdapter autoCompleteAdapter = new OutboxFlashsListAutoCompleteAdapter(activity);

        text.setAdapter(autoCompleteAdapter);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence currentText, int start, int before, int count) {
                ParseFace face = User.getInstance().getFaceWithText(currentText.toString());

                if(face != null){
                    Log.d("OutboxFlashListAdapter", "getView - the face of item n°" + position + " is modified by the face with text : " + face.getText());
                    message.getFlash(position).setFace(face);
                    picture.setImageBitmap(face.getPicture().getBitmap());
                }else{
                    Log.d("OutboxFlashListAdapter", "getView - the text of item n°" + position + " is modified by : " + currentText);
                    message.getFlash(position).setText(currentText.toString());
                    picture.setImageBitmap(FTDefaultBitmap.getInstance().getDefaultPicture());
                }
            }
        });

        autoCompleteAdapter.setFaceClickListener(new OnFaceClickListener() {
            @Override
            public void onFaceClicked(ParseFace face) {
                Log.d("OutboxFlashListAdapter", "getView - the face of item n°" + position + " is modified by the face with text : " + face.getText());
                message.getFlash(position).setFace(face);
                picture.setImageBitmap(face.getPicture().getBitmap());
                text.setText(face.getText());
                text.dismissDropDown();
                text.setSelection(text.getText().length());
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.showCamera(new CameraListener() {
                    @Override
                    public void onPictureTaken(FTBitmap takenPicture) {
                        Log.d("OutboxFlashListAdapter", "getView - the picture of item n°" + position + " is modified");
                        message.getFlash(position).setPicture(takenPicture);
                        picture.setImageBitmap(takenPicture.getBitmap());
                        text.setHint(R.string.outbox_flash_text_when_picture);
                        text.setEnabled(false);
                    }
                });
            }
        });

        final Flash currentFlash = message.getFlash(position);

        if(currentFlash.getType() == Flash.TYPE_FACE){
            picture.setImageBitmap(currentFlash.getFace().getPicture().getBitmap());
            text.setText(currentFlash.getText());
        }else if(currentFlash.getType() == Flash.TYPE_TEXT){
            text.setText(currentFlash.getText());
        }else if(currentFlash.getType() == Flash.TYPE_PICTURE){
            picture.setImageBitmap(currentFlash.getPicture().getBitmap());
            text.setHint(R.string.outbox_flash_text_when_picture);
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addItem(){
        message.addFlash(new ParseFlash());
        super.notifyDataSetChanged();
    }

    public int getFlashsNumber(){
        return message.getFlashNumber();
    }

    public Message getMessage(){

        boolean isMessageEmpty = true;

        for(int i = 0; i < message.getFlashNumber(); i++){
            if(message.getFlash(i).getType() != Flash.NO_TYPE){
                isMessageEmpty = false;
            }
        }
        if(isMessageEmpty){
            return null;
        }
        Log.d("OutboxlistFlashAdapter", "getMessage - message length = " + message.getFlashNumber());
        return message;
    }

}
