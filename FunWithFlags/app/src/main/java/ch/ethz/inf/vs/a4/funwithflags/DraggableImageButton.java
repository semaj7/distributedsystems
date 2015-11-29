package ch.ethz.inf.vs.a4.funwithflags;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Andres on 26.11.15.
 */
public class DraggableImageButton extends ImageButton{
    public DraggableImageButton(Context context) {
        super(context);

        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipData clipData = ClipData.newPlainText("", "");
                View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);
                view.startDrag(clipData, dsb, view, 0);
                view.setVisibility(View.INVISIBLE);
                System.out.println("Clicked for a long long time");
                return true;
            }
        });

        this.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.d("DEBUG", "Action is DragEvent.ACTION_DRAG_STARTED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d("DEBUG", "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d("DEBUG", "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();

                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d("DEBUG", "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d("DEBUG", "Action is DragEvent.ACTION_DRAG_ENDED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d("DEBUG", "ACTION_DROP event");

                        // Do nothing
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        
        //TODO: don't forget to set the onClickMethod based on what you want after the initialization

    }


    //override this function because android sucks
    @Override
    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return false;
            default:
                return super.onDragEvent(event);
        }
    }
}
