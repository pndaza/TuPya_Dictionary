package mm.pndaza.thupyadictionary.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.thupyadictionary.R;
import mm.pndaza.thupyadictionary.adapters.RecentAdapter;
import mm.pndaza.thupyadictionary.database.DBOpenHelper;
import mm.pndaza.thupyadictionary.models.Recent;
import mm.pndaza.thupyadictionary.utils.MDetect;
import mm.pndaza.thupyadictionary.utils.Rabbit;


public class RecentFragment extends Fragment implements RecentAdapter.OnRecentItemClickListener {


    public interface OnRecentCallbackListener {
        void onRecentClick(Recent recent);
    }

    private OnRecentCallbackListener callbackListener;
    final private ArrayList<Recent> recents =
            DBOpenHelper.getInstance(getContext()).getAllRecents();
    private RecentAdapter adapter;
    private TextView tv_empty_info;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.title_recent_mm)));
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecentAdapter(recents, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        tv_empty_info = view.findViewById(R.id.empty_info);
        setupEmptyInfoView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callbackListener = (OnRecentCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implemented OnRecentCallbackListener");

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_clear_all, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_clear_all) {
            clearRecent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearRecent() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.AlertDialogTheme);

        String message = "လက်တလော ကြည့်ရှုထားမှုများကို ဖျက်မှာလား";
        String comfirm = "ဖျက်မယ်";
        String cancel = "မဖျက်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        alertDialog.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                            adapter.removeAll();
                            adapter.notifyDataSetChanged();
                            setupEmptyInfoView();
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        alertDialog.show();
    }

    private void setupEmptyInfoView() {
        tv_empty_info.setText(MDetect.getDeviceEncodedText(getString(R.string.empty_recent)));
        tv_empty_info.setVisibility(recents.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void onRecentItemClick(Recent recent) {
        callbackListener.onRecentClick(recent);
    }

}
