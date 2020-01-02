package logic;

public abstract class CustomThread extends Thread {

    protected boolean running;

    public CustomThread() { }

    public CustomThread(String name) {
        super(name);
    }

    @Override
    public void start() {
        this.running = true;
        super.start();
    }

    @Override
    public abstract void run();

    public void stopThread() {
        running = false;
    }
}
