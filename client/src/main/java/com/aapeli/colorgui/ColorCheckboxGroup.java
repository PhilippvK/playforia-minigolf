package com.aapeli.colorgui;

import java.util.Enumeration;
import java.util.Vector;

public final class ColorCheckboxGroup {

    private Vector<ColorCheckbox> aVector1553 = new Vector<>();


    protected void method1747(ColorCheckbox var1) {
        this.aVector1553.addElement(var1);
    }

    protected boolean method1748(boolean var1) {
        if (var1) {
            this.method1749();
            return true;
        } else {
            return false;
        }
    }

    private void method1749() {
        Enumeration<ColorCheckbox> var1 = this.aVector1553.elements();

        while (var1.hasMoreElements()) {
            var1.nextElement().realSetState(false);
        }

    }
}
