package cryptocast.client.bufferedmediaplayer;

public interface OnStatusChangeListener {
    public void onStatusChange(String message);
    public void bufferUpdate(int percentage);

}
