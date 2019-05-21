package viva.oneplatinum.com.viva.adapters;

import java.util.LinkedList;

import viva.oneplatinum.com.viva.circleview.CircleLayoutAdapter;
import viva.oneplatinum.com.viva.circleview.CircularLayoutItem;
import viva.oneplatinum.com.viva.models.Event;

/**
 * Created by mindvalley on 12/17/14.
 */


public class MyCircleLayoutAdapter extends CircleLayoutAdapter {


    private LinkedList<Integer> adapter=new LinkedList<Integer>();
    private LinkedList<Event> eventList=new LinkedList<Event>();
    private int startingIdIndex=0;
    private boolean isStartingIdSetten=false;


    public boolean setStartingIndex(int startingIndex) {
        if(!isStartingIdSetten)
        {
            isStartingIdSetten=true;
            startingIdIndex = startingIndex;

            return true;
        }
        return false;
    }

    @Override
    public void add(Object object,Object event)
    {
        Integer model=(Integer)object;
        Event events=(Event)event;
        adapter.add(model);
        eventList.add(events);

    }


    @Override
    public Integer getRoundedIndex(Integer index)
    {
        try {
            int new_index=((-1*index)%adapter.size()+adapter.size())%adapter.size();
            return new_index;
        }
        catch (Exception e) {
            return -1;
        }
    }

    public Object getCurrentObject()
    {
        return adapter.get( getRoundedIndex(getRoundedIndex(parent.getCurrent_step())-startingIdIndex));
    }

    @Override
    public CircularLayoutItem get(int index)
    {
//        AQuery aq=new AQuery(context);
        Integer drawable=adapter.get(getRoundedIndex((getRoundedIndex(index)-startingIdIndex)));
        MyCircleLayoutItem civ = new MyCircleLayoutItem(context);
//        View view= LayoutInflater.from(context).inflate(R.layout.event_view_item_layout,null);
//        TextView eventSponsored=(TextView)view.findViewById(R.id.eventSponser);
//        TextView eventName=(TextView)view.findViewById(R.id.eventSponser);
//        CircularImageView eventImage=(CircularImageView)view.findViewById(R.id.eventImage);
//        eventSponsored.setText(adapter.get(index).eventName);
//        eventName.setText(adapter.get(index).eventName);
//        aq.id(eventImage).image(adapter.get(index).eventImages.get(0));
//        civ.setBackground(R.drawable.animation1);
         civ.setBackground(context.getResources().getDrawable(drawable),eventList.get(getRoundedIndex((getRoundedIndex(index)-startingIdIndex))),context);
        civ.setIndex(index);
        return civ;
    }


    public int getSize()
    {
        return adapter.size();
    }

}