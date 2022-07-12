package ar.edu.unicen.isistan.asistan.views.asistan.movements.inquirer;

import android.animation.Animator;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import com.github.florent37.viewtooltip.ViewTooltip;

import java.util.ArrayList;
import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.views.asistan.movements.MyDiaryFragment;
import ar.edu.unicen.isistan.asistan.views.utils.DraggableFloatingActionButton;

import static com.github.florent37.viewtooltip.ViewTooltip.ALIGN.CENTER;
import static com.github.florent37.viewtooltip.ViewTooltip.Position.LEFT;

public class Inquirer {

    private MyDiaryFragment parent;
    private DraggableFloatingActionButton view;
    private List<Visit> visits;
    private int index;

    public Inquirer(DraggableFloatingActionButton view, MyDiaryFragment parent) {
        this.view = view;
        this.view.setDraggableX(false);
        this.view.setVisibility(View.INVISIBLE);
        this.parent = parent;
        this.view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                Inquirer.this.goNext();
             }
        });
    }

    public void refresh() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Visit> visits = Database.getInstance().mobility().selectVisitsToReview();

                if (Inquirer.this.visits != null || !visits.isEmpty()) {
                    if (Inquirer.this.parent.getActivity() != null) {
                        Inquirer.this.parent.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Inquirer.this.view.setVisibility(View.VISIBLE);
                            }
                        });
                        if (Inquirer.this.visits == null)
                            Inquirer.this.visits = new ArrayList<>();
                        else
                            Inquirer.this.visits.clear();
                        Inquirer.this.visits.addAll(visits);
                        Inquirer.this.index = 0;
                        Inquirer.this.showTooltip();
                    }
                }
            }
        });
    }

    private Visit getNext() {
        if (this.visits != null && !this.visits.isEmpty()) {
            if (index >= this.visits.size())
                index = 0;
            Visit visit = this.visits.get(this.index);
            index++;
            return visit;
        }
        return null;
    }

    private void showTooltip() {
        if (this.visits != null && !this.visits.isEmpty()) {
            String msg;
            if (this.visits.size() == 1)
                msg = "Tienes 1 visita para revisar ¿Quieres revisarla?";
            else
                msg = "Tienes " + this.visits.size() + " visitas para revisar ¿Quieres revisarlas?";
            ViewTooltip.on(this.parent,this.view)
            .autoHide(true, 2000)
            .clickToHide(true)
            .align(CENTER)
            .position(LEFT)
            .text(msg)
            .textColor(Color.WHITE)
            .color(Color.BLACK)
            .corner(10)
            .arrowWidth(15)
            .arrowHeight(15)
            .distanceWithView(0)
            .animation(new ViewTooltip.TooltipAnimation() {
                @Override
                public void animateEnter(View view, Animator.AnimatorListener animatorListener) {

                }

                @Override
                public void animateExit(View view, Animator.AnimatorListener animatorListener) {
                    view.animate().alpha(0).setDuration(4000).setListener(animatorListener);
                }
            })
            .onDisplay(new ViewTooltip.ListenerDisplay() {
                @Override
                public void onDisplay(View view) {

                }
            })
            .onHide(new ViewTooltip.ListenerHide() {
                @Override
                public void onHide(View view) {
                }
            })
            .show();
        } else {
            ViewTooltip.on(this.parent,this.view)
            .autoHide(true, 2000)
            .clickToHide(true)
            .align(CENTER)
            .position(LEFT)
            .text("Listo! Muchas gracias!")
            .textColor(Color.WHITE)
            .color(Color.BLACK)
            .corner(10)
            .arrowWidth(15)
            .arrowHeight(15)
            .distanceWithView(0)
            .animation(new ViewTooltip.TooltipAnimation() {
                @Override
                public void animateEnter(View view, Animator.AnimatorListener animatorListener) {

                }

                @Override
                public void animateExit(View view, Animator.AnimatorListener animatorListener) {
                    view.animate().alpha(0).setDuration(2000).setListener(animatorListener);
                }
            })
            .onDisplay(new ViewTooltip.ListenerDisplay() {
                @Override
                public void onDisplay(View view) {

                }
            })
            .onHide(new ViewTooltip.ListenerHide() {
                @Override
                public void onHide(View view) {
                    if (Inquirer.this.parent.getActivity() != null) {
                        Inquirer.this.parent.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Inquirer.this.view.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            })
            .show();
        }
    }

    private void  goNext() {
        Visit next = this.getNext();
        if (next != null) {
            this.parent.focus(next);
        }
    }

}
