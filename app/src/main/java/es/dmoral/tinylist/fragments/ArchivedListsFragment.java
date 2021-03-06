package es.dmoral.tinylist.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.tinylist.R;
import es.dmoral.tinylist.adapters.ArchivedListsAdapter;
import es.dmoral.tinylist.helpers.TinyListSQLHelper;
import es.dmoral.tinylist.models.TaskList;

public class ArchivedListsFragment extends BaseFragment {

    @Bind(R.id.archived_list_recycler_view)
    RecyclerView archivedListsRecyclerView;

    public ArchivedListsFragment() {
        // Required empty public constructor
    }

    /**
     * Using Singleton pattern (Android best practices)
     *
     * @return an instance of the fragment
     */
    public static ArchivedListsFragment getInstance() {
        return new ArchivedListsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_archived_lists, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    /**
     * Method used to set up the entire view, here we check if the user is adding
     * a new TaskList or editing an existing one.
     */
    @Override
    void setupViews() {
        archivedListsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter mAdapter = new ArchivedListsAdapter(TinyListSQLHelper.getSqlHelper(getActivity()).getTaskLists(true), getActivity());
        archivedListsRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                /* Unarchiving the swiped item. */
                final TaskList taskListToArchive = ((ArchivedListsAdapter) archivedListsRecyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
                taskListToArchive.setIsArchived(false);
                TinyListSQLHelper.getSqlHelper(getActivity()).addOrUpdateTaskList(taskListToArchive);
                ((ArchivedListsAdapter) archivedListsRecyclerView.getAdapter()).removeItem(viewHolder.getAdapterPosition());
            }
        };
        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(archivedListsRecyclerView);
    }

    /**
     * Method used to redraw the items inside the main RecyclerView
     */
    @Override
    public void redrawItems() {
        ((ArchivedListsAdapter) archivedListsRecyclerView.getAdapter()).replaceWith(TinyListSQLHelper.getSqlHelper(getActivity()).getTaskLists(true));
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Redraw items when this fragment is first viewed by the user. */
        redrawItems();
    }

}
