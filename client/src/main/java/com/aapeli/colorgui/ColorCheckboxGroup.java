package com.aapeli.colorgui;

import java.util.ArrayList;
import java.util.List;

public final class ColorCheckboxGroup {

    private List<ColorCheckbox> checkboxes = new ArrayList<>();

    protected void addCheckbox(ColorCheckbox checkbox) {
        synchronized (this.checkboxes) {
            this.checkboxes.add(checkbox);
        }
    }

    protected boolean checkboxClicked(boolean checked) {
        if (checked) {
            this.updateCheckboxes();
            return true;
        } else {
            return false;
        }
    }

    private void updateCheckboxes() {
        for (ColorCheckbox colorCheckbox : checkboxes) {
            colorCheckbox.realSetState(false);
        }
    }
}
