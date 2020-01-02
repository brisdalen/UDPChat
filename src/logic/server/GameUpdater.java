package logic;

public class GameUpdater extends CustomThread {

    private long lastUpdate;
    private long currentEpoch;

    @Override
    public void run() {
        while(running) {
            currentEpoch = System.currentTimeMillis();
            // Updaten forskyves med 1 millisekund ved hver iterasjon
            if(currentEpoch - lastUpdate >= 600) {
                lastUpdate = System.currentTimeMillis();
                System.out.println("[logic.GameUpdater]Update @" + lastUpdate);
            }
        }
    }

    public void update(long millis) {
        System.out.println("[logic.GameUpdater]Update @" + millis);
    }

    @Override
    public void start() {
        lastUpdate = System.currentTimeMillis();
        super.start();
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long millis) {
        this.lastUpdate = millis;
    }
}
