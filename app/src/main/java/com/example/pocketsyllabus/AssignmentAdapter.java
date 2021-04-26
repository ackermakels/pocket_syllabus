package com.example.pocketsyllabus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class AssignmentAdapter extends ArrayAdapter {
    ArrayList<Assignment> arrayList;
    Context context;

    public AssignmentAdapter( Context context, ArrayList<Assignment> arrayList ) {
        super( context, 0, arrayList );
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem( int position ) {
        return arrayList.get( position );
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // retrieve an assignment to "inflate"
        Assignment assignment = arrayList.get( position );

        // setup layout to be "inflated"
        LayoutInflater layoutInflater = LayoutInflater.from( context );
        convertView = layoutInflater.inflate( R.layout.assignment_row, null );

        // retrieve fields from view
        TextView assignmentNameView = convertView.findViewById( R.id.assignmentName );
        TextView assignmentDueDateView = convertView.findViewById( R.id.assignmentDueDate );

        // set text based on assignment
        assignmentNameView.setText( assignment.getName() );
        assignmentDueDateView.setText( assignment.getDueDate() );

        return convertView;
    }
}
