package com.aapeli.colorgui;

import com.aapeli.tools.Tools;

class SpinnerStateChangeDebounceThread implements Runnable {

    private Spinner spinner;
    private int delay;
    private boolean running;

    protected SpinnerStateChangeDebounceThread(Spinner spinner, int delay) {
        this.spinner = spinner;
        this.delay = delay;
        this.running = true;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void run() {
        Tools.sleep(this.delay);
        if (this.running) {
            this.spinner.notifyListeners();
        }
    }

    protected void stop() {
        this.running = false;
    }
}
