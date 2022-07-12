package ar.edu.unicen.isistan.asistan.views.asistan.stats.calculator;

import android.widget.LinearLayout;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.utils.time.Date;

public abstract class StatsCalculator {

    protected Date startDate;
    protected Date endDate;
    protected String description;
    protected LinearLayout view;
    protected ArrayList<Movement> movements;

    public String getDescription() {
        return description;
    }
    
    public void setView(LinearLayout view) {
        this.view = view;
        this.init();
    }

    protected abstract void init();

    public void newData(Date startDate, Date endDate, ArrayList<Movement> movements) {
        this.movements = movements;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clear();
        this.calculate();
    }

    protected abstract void calculate();

    protected abstract void clear();

    public abstract void updateView();

}
