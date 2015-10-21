package com.xuyinan.barcodesearcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {
    private SurfaceView view;
    private CameraDevice cd;
    private CameraCaptureSession ccs;
    private ImageReader reader;
    private CaptureRequest.Builder builder2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = (SurfaceView) findViewById(R.id.surfaceView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ccs.capture(builder2.build(), new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                            Intent intent = new Intent();
                            Image image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            intent.putExtra("data", bytes);
                            intent.putExtra("width", reader.getWidth());
                            intent.putExtra("height", reader.getHeight());
                            CameraActivity.this.setResult(RESULT_OK, intent);
                            CameraActivity.this.finish();
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        openCamera();
    }

    @Override
    protected void onResume() {
        openCamera();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (null != ccs) {
            ccs.close();
        }
        if (null != cd) {
            cd.close();
        }
        super.onPause();
    }

    private void openCamera() {
        final CameraManager cm = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        SurfaceHolder holder = view.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    Point point = new Point();
                    getWindowManager().getDefaultDisplay().getSize(point);
                    StreamConfigurationMap map = cm.getCameraCharacteristics(cm.getCameraIdList()[0])
                            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                    Size size = getSize(point, sizes);
                    holder.setFixedSize(size.getWidth(), size.getHeight());
                    reader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 2);
                    cm.openCamera(cm.getCameraIdList()[0], new CameraCallback(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private Size getSize(Point point, Size[] sizes) {
                for (Size size : sizes) {
                    if (size.getWidth() < point.x)
                        return size;
                }
                return null;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public class CameraCallback extends CameraDevice.StateCallback {
        @Override
        public void onOpened(CameraDevice camera) {
            cd = camera;
            try {
                final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(view.getHolder().getSurface());
                builder2 = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                builder2.addTarget(reader.getSurface());
                builder2.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                builder2.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                camera.createCaptureSession(Arrays.asList(view.getHolder().getSurface(), reader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                ccs = session;
                                builder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                builder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                try {
                                    session.setRepeatingRequest(builder.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {

                            }
                        }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
        }
    }
}
