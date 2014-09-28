package com.fametome.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.fametome.R;
import com.fametome.listener.CameraListener;
import com.fametome.listener.SeekbarListener;
import com.fametome.util.FTBitmap;
import com.fametome.widget.CameraView;

public class CameraDialog extends AlertDialog implements DialogInterface.OnCancelListener, DialogInterface.OnShowListener, CameraListener{

    private CameraListener cameraListener;

    private FTBitmap currentPicture;

    private CameraView camera = null;
    private Button toggle = null;
    private Button takePicture = null;
    private Button acceptPicture = null;
    private Button declinePicture = null;

    public CameraDialog(final Context context){
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_camera, null);

        this.setView(v);
        this.setOnShowListener(this);

        this.setOnCancelListener(this);

        camera = (CameraView)v.findViewById(R.id.camera);
        toggle = (Button) v.findViewById(R.id.toggle);
        takePicture = (Button)v.findViewById(R.id.take_picture);
        acceptPicture = (Button)v.findViewById(R.id.accept);
        declinePicture = (Button)v.findViewById(R.id.decline);

        camera.setDestroyable(false);

        if(Camera.getNumberOfCameras() == 1) {
            toggle.setVisibility(View.GONE);
        }

        camera.setCameraListener(this);
        toggle.setOnClickListener(clickToggle);
        takePicture.setOnClickListener(clickTakePicture);
        acceptPicture.setOnClickListener(clickAcceptPicture);
        declinePicture.setOnClickListener(clickDeclinePicture);
    }

    private SeekbarListener progressZoom = new SeekbarListener() {
        @Override
        public void onProgressChanged(int value) {
            camera.zoom(value);
            Log.i("CameraDialog", "progressZoom - progress value : " + value);
        }
    };

    private View.OnClickListener clickTakePicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.takePicture();
            takePicture.setVisibility(View.GONE);
            toggle.setVisibility(View.GONE);
            acceptPicture.setVisibility(View.VISIBLE);
            declinePicture.setVisibility(View.VISIBLE);
            camera.setAutoFocusEnabled(false);
        }
    };

    private View.OnClickListener clickToggle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            camera.toggle();
        }
    };

    private View.OnClickListener clickAcceptPicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePicture.setVisibility(View.VISIBLE);
            toggle.setVisibility(View.VISIBLE);
            acceptPicture.setVisibility(View.GONE);
            declinePicture.setVisibility(View.GONE);
            if(currentPicture.getBitmap() == null){
                Log.d("CameraDialog", "clickAcceptPicture - the picture bitmap is null");
            }
            cameraListener.onPictureTaken(currentPicture);
            camera.setAutoFocusEnabled(true);
            cancel();
        }
    };

    private View.OnClickListener clickDeclinePicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePicture.setVisibility(View.VISIBLE);
            toggle.setVisibility(View.VISIBLE);
            acceptPicture.setVisibility(View.GONE);
            declinePicture.setVisibility(View.GONE);
            camera.setAutoFocusEnabled(true);
            camera.startPreview();
        }
    };

    public void setCameraListener(CameraListener cameraListener){
        this.cameraListener = cameraListener;
    }

    public void reconnectCamera(){
        camera.reloadCamera();
    }

    public void unlockCamera(){
        camera.releaseCamera();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Log.d("CameraDialog", "onShow");

        if(camera.getCamera() == null){
            cancel();
            camera.handleNullCamera(true);
        }

        camera.setAutoFocusEnabled(true);

        camera.startPreview();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d("CameraDialog", "onCancel");
        this.cameraListener = null;
        camera.stopPreview();

        camera.setAutoFocusEnabled(false);
    }

    @Override
    public void onPictureTaken(FTBitmap picture) {
        Log.d("CameraDialog", "onPictureTaken - a picture is taken");
        this.currentPicture = picture;
    }
}
