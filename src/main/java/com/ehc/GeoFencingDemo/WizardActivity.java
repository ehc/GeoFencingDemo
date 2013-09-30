package com.ehc.GeoFencingDemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ehc
 * Date: 26/9/13
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class WizardActivity extends GeoFencingActivity {
  private final int REQUEST_CODE = 1;
  Bitmap picture = null;
  ImageView frontImage;
  Button submit;
  Button cancel;
  Button takeSnap;
  FrameLayout cameraView;
  private Camera camera;
  private CameraPreview cameraPreview;
  public static final int MEDIA_TYPE_IMAGE = 1;


  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.step_front_image);
    getWidgets();
    applyProperties();
    //TODO: we should open front camera
    openCamera();
  }


//  private void takePicture() {
//    Intent frontCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    frontCameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//    startActivityForResult(frontCameraIntent, REQUEST_CODE);
//  }

//  @Override
//  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    if (resultCode == RESULT_OK) {
//      Bundle resultData = data.getExtras();
//      picture = (Bitmap) resultData.get("data");
//      if (picture != null) {
//        frontImage.setImageDrawable(new BitmapDrawable(getResources(), picture));
//      }
//    }
//  }

  private void getWidgets() {
    frontImage = (ImageView) findViewById(R.id.front_image);
    submit = (Button) findViewById(R.id.step1_submit);
    cancel = (Button) findViewById(R.id.step1_cancel);
    takeSnap = (Button) findViewById(R.id.step1_take_snap);
    cameraView = (FrameLayout) findViewById(R.id.camera_preview);
  }

  private void callSecondStep() {
    Intent secondStep = new Intent(this, SecondStep.class);
    Bundle bundle = new Bundle();
    bundle.putParcelable("frontImage", picture);
    secondStep.putExtras(bundle);
    startActivity(secondStep);
  }

  private void applyProperties() {
    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        callSecondStep();
      }
    });

    takeSnap.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        camera.takePicture(null, null, pictureCallback);
      }
    });
  }


  private void openCamera() {
    camera = getCameraInstance();
    cameraPreview = new CameraPreview(this, camera);
    cameraView.addView(cameraPreview);
  }

  public Camera getCameraInstance() {
    Camera camera = null;
    try {
      int frontCam = getFrontCamId();
      if (frontCam > 0)
        camera = Camera.open(getFrontCamId());
      else
        camera = Camera.open();
    } catch (Exception e) {
    }
    return camera;
  }

  private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
      File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
      if (pictureFile == null) {
        return;
      }
      try {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        fos.write(data);
        fos.close();
      } catch (FileNotFoundException e) {
      } catch (IOException e) {
      }
    }
  };

  private static File getOutputMediaFile(int type) {
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), "MyCameraApp");
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        return null;
      }
    }
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "IMG_" + timeStamp + ".jpg");
    } else {
      return null;
    }
    return mediaFile;
  }

  @Override
  protected void onPause() {
    super.onPause();
    releaseCamera();
  }

  private void releaseCamera() {
    if (camera != null) {
      camera.release();
      camera = null;
    }
  }

  private int getFrontCamId() {
    int cameraCount = 0;
    int frontCam = -1;
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    cameraCount = Camera.getNumberOfCameras();
    for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
      Camera.getCameraInfo(camIdx, cameraInfo);
      if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        frontCam = camIdx;
      }
    }
    return frontCam;
  }
}

