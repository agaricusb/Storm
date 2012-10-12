package com.github.StormTeam.Storm.Earthquake.Tasks;

import com.github.StormTeam.Storm.Earthquake.Quake;

/**
 * @author Giant
 */
public class RiftLoader implements Runnable {

    @SuppressWarnings("FieldCanBeLocal")
    private Quake q;

    public RiftLoader(Quake q) {
        this.q = q;
    }

    @Override
    public void run() {

    }
}
