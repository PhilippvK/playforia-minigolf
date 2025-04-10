package com.aapeli.colorgui;

import java.util.ArrayList;
import java.util.List;

public final class CheckboxGroup {

    private List<Checkbox> checkboxes = new ArrayList<>();

    protected void addCheckbox(Checkbox checkbox) {
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
        for (Checkbox checkbox : checkboxes) {
            checkbox.realSetState(false);
        }
    }
}
