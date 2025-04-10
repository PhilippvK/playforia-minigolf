package com.aapeli.multiuser;

import com.aapeli.colorgui.Checkbox;
import java.awt.event.MouseEvent;

public class NoChallCheckbox extends Checkbox {

    private long[] aLongArray4727;

    public NoChallCheckbox(String var1) {
        this(var1, false);
    }

    public NoChallCheckbox(String var1, boolean var2) {
        super(var1, var2);
        this.aLongArray4727 = new long[] {0L, 0L, 0L, 0L};
    }

    public void mouseReleased(MouseEvent var1) {
        long var2 = System.currentTimeMillis();
        if (var2 >= this.aLongArray4727[1] + 2000L) {
            if (var2 >= this.aLongArray4727[2] + 5000L) {
                if (var2 >= this.aLongArray4727[3] + 10000L) {
                    this.aLongArray4727[3] = this.aLongArray4727[2];
                    this.aLongArray4727[2] = this.aLongArray4727[1];
                    this.aLongArray4727[1] = this.aLongArray4727[0];
                    this.aLongArray4727[0] = var2;
                    super.mouseReleased(var1);
                }
            }
        }
    }
}
