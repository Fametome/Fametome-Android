package com.fametome.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.fametome.Dialog.FTDialog;
import com.fametome.R;
import com.fametome.listener.CameraListener;
import com.fametome.util.FTBitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraView extends FrameLayout {

    private boolean isRealesed = false;

    private boolean isAutoFocusEnabled = true;

    private int currentCameraId;
    private Camera camera;
    private CameraPreview cameraPreview;

    private CameraListener cameraListener;

    private boolean isDestroyable = true;

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        camera = getFrontCamera();

        isRealesed = false;

        cameraPreview = new CameraPreview(getContext(), camera);
        this.addView(cameraPreview);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isAutoFocusEnabled) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        camera.autoFocus(autoFocusCallback);
                    }
                }
                return false;
            }
        });
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if(success) {
                camera.cancelAutoFocus();
            }
        }
    };

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        Log.d("CameraView", "getSmallestPictureSize - there is " + parameters.getSupportedPictureSizes().size());

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            }else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return(result);
    }

    public void toggle(){
        Log.d("CameraView", "toggle - click on toggle camera button");

        camera.stopPreview();
        camera.release();

        currentCameraId = 1 - currentCameraId;
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            camera = getBackCamera();
        }else{
            camera = getFrontCamera();
        }

        super.removeView(cameraPreview);
        cameraPreview = new CameraPreview(getContext(), camera);
        super.addView(cameraPreview);

    }

    public void zoom(int zoomFactor){
        zoomFactor = Math.min(camera.getParameters().getMaxZoom(), zoomFactor);
        camera.stopSmoothZoom();
        camera.startSmoothZoom(zoomFactor);
    }

    public void takePicture(){
        camera.takePicture(null, null, pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("test", "picture taken, the weight of the picture is : " + (data.length / 1000) + " ko");

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data .length, options);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                matrix.preScale(-1.0f, 1.0f);
            }

            Bitmap finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] pictureData = stream.toByteArray();

            Log.d("test", "picture taken, the final weight of the picture is : " + (pictureData.length / 1000) + " ko");

            // si la photo est toujours supérieure à 200 ko
            while(pictureData.length > 200000){
                options.inSampleSize *= 2;

                bitmap = BitmapFactory.decodeByteArray(data , 0, data .length, options);
                finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

                stream = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                pictureData = stream.toByteArray();

            }

            Log.d("test", "picture taken, the realy realy final weight of the picture is : " + (pictureData.length / 1000) + " ko");

            if(cameraListener != null) {
                cameraListener.onPictureTaken(new FTBitmap(finalBitmap, pictureData));
            }
        }
    };

    private Camera getFrontCamera(){
        Camera c = null;

        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

        try {
            c = Camera.open(currentCameraId);
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(currentCameraId, info);
            Camera.Size size = getSmallestPictureSize(c.getParameters());
            c.getParameters().setPictureSize(size.width, size.height);
            c.setDisplayOrientation((360 - (info.orientation % 360)) % 360);
            isRealesed = false;
        } catch (RuntimeException e){
            Log.e("CameraView", "getFrontCamera() - Camera failed to open: " + e.getLocalizedMessage());
            handleNullCamera(false);
        }

        return c;
    }

    private Camera getBackCamera(){
        Camera c = null;

        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        try {
            c = Camera.open(currentCameraId);
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(currentCameraId, info);
            Camera.Size size = getSmallestPictureSize(c.getParameters());
            c.getParameters().setPictureSize(size.width, size.height);
            c.setDisplayOrientation(info.orientation % 360);
            isRealesed = false;
        } catch (RuntimeException e){
            Log.e("CameraView", "getBackCamera() - Camera failed to open: " + e.getLocalizedMessage());
            handleNullCamera(false);
        }

        return c;
    }

    public void handleNullCamera(boolean retry){

        if(retry){
            getFrontCamera();
            return;
        }

        final FTDialog dialog = new FTDialog(getContext());
        dialog.setTitle(R.string.camera_error_title);
        dialog.setMessage(R.string.camera_error_message);
        dialog.show();
    }

    public void startPreview(){
        if(camera != null && !isRealesed) {
            /*
            * Camera.Parameters parameters = camera.getParameters();
            * parameters.setPreviewSize(getWidth(), getHeight());
            * camera.setParameters(parameters);
            */
            camera.startPreview();
        }
    }

    public void stopPreview(){
        if(camera != null && !isRealesed) {
            camera.stopPreview();
        }
    }

    public void setCameraListener(CameraListener cameraListener){
        this.cameraListener = cameraListener;
    }

    public void setDestroyable(boolean isDestroyable){
        this.isDestroyable = isDestroyable;
    }

    public void destroy(){
        if(camera != null && !isRealesed) {
            camera.stopPreview();

            if (isDestroyable) {
                Log.i("CameraPreview", "SurfaceDestroyed");
                camera.release();
                isRealesed = true;
                camera = null;
            }
        }
    }

    public Camera getCamera(){
        return camera;
    }

    public void reloadCamera(){

        if(camera == null){

            camera = getFrontCamera();

        }else{
            Log.d("CameraView", "reloadCamera - camera is not null");
        }
    }

    public void releaseCamera(){
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void setAutoFocusEnabled(boolean isAutoFocusEnabled){
        this.isAutoFocusEnabled = isAutoFocusEnabled;
    }

    private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder holder;

        public CameraPreview(Context context) {
            super(context);
        }

        public CameraPreview(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public CameraPreview(Context context, Camera camera) {
            super(context);
            Log.i("CameraPreview", "Constructor");
            CameraView.this.camera = camera;

            holder = getHolder();
            holder.addCallback(this);

            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i("CameraPreview", "SurfaceCreated");
            try {
                if (holder == null) {
                    camera.setPreviewDisplay(holder);
                    startPreview();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("CameraPreview", "Error setting camera preview: " + e.getMessage());
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.i("CameraPreview", "SurfaceChanged");
            if (camera != null) {
                if (holder.getSurface() == null) {
                    return;
                }

                try {
                    camera.stopPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    camera.setPreviewDisplay(holder);
                    startPreview();
                } catch (Exception e) {
                    Log.d("CameraPreview", "Error starting camera preview: " + e.getMessage());
                }
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
            destroy();
        }


    }
}