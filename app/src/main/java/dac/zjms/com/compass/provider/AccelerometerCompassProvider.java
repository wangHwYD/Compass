package dac.zjms.com.compass.provider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_ACCELEROMETER
 * Accelerometer} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 *
 * @author Alexander Pacha
 */
public class AccelerometerCompassProvider extends OrientationProvider {

    /**
     * Compass values
     */
    private float[] magnitudeValues = new float[3];

    /**
     * Accelerometer values
     */
    private float[] accelerometerValues = new float[3];

    /**
     * Inclination values
     */
    private float[] inclinationValues = new float[16];
    private float[] values = new float[3];
    private float[] R = new float[9];

    public Callback callback = null;

    /**
     * Initialises a new AccelerometerCompassProvider
     *
     * @param sensorManager The android sensor manager
     */
    public AccelerometerCompassProvider(SensorManager sensorManager) {
        super(sensorManager);

        //Add the compass and the accelerometer
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnitudeValues = event.values;

            System.arraycopy(event.values, 0, magnitudeValues, 0, magnitudeValues.length);
            if (callback != null) {
                callback.onMagneticValues(magnitudeValues[0], magnitudeValues[1], magnitudeValues[2]);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accelerometerValues = event.values;
            System.arraycopy(event.values, 0, accelerometerValues, 0, accelerometerValues.length);
            if (callback != null) {
                callback.onAccelerometerChanged(accelerometerValues[1], accelerometerValues[2], accelerometerValues[0]);
            }
        }


        if (magnitudeValues != null && accelerometerValues != null) {
            // Fuse accelerometer with compass


            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, inclinationValues, accelerometerValues,
                    magnitudeValues);
            // Transform rotation matrix to quaternion
            SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, values);
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
            if (callback != null) {
                callback.onOrientationChanged(values[0], values[1], values[2]);
            }

        }
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public float[] getR() {
        return R;
    }

    public void setR(float[] r) {
        R = r;
    }

    public float[] getMagnitudeValues() {
        return magnitudeValues;
    }

    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    public float[] getInclinationValues() {
        return inclinationValues;
    }


    public abstract static class Callback {
        public void onOrientationChanged(double orientation, double pitch, double roll) {

        }

        public void onAccelerometerChanged(double dx, double dy, double dz) {

        }

        public void onMagneticValues(double dx, double dy, double dz) {

        }
    }
}
