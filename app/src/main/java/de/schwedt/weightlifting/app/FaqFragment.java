package de.schwedt.weightlifting.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;

import de.schwedt.weightlifting.app.faq.FaqAnswerFragment;
import de.schwedt.weightlifting.app.faq.FaqItem;
import de.schwedt.weightlifting.app.faq.FaqListAdapter;

public class FaqFragment extends Fragment {

    public static ArrayList<FaqItem> faqEntries = new ArrayList<FaqItem>();
    private final int DRUCKER = 0;
    ViewPager mViewPager;
    private WeightliftingApp app;
    private View fragment;
    private FaqListAdapter adapter;
    private LinearLayout content;
    private LayoutInflater inflater;

    public FaqFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(WeightliftingApp.TAG, "Showing Faq fragment");

        this.inflater = inflater;
        fragment = inflater.inflate(R.layout.fragment_faq, container, false);
        app = (WeightliftingApp) getActivity().getApplicationContext();

        adapter = new FaqListAdapter(faqEntries, getActivity());

        ListView faqMenu = (ListView) fragment.findViewById(R.id.listView_faqs);
        faqMenu.setAdapter(adapter);
        faqMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show an article fragment and put the selected index as argument
                Fragment faqAnswer = new FaqAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("item", position);
                faqAnswer.setArguments(bundle);
                ((MainActivity) getActivity()).addFragment(faqAnswer, getString(R.string.nav_faq), true);
            }
        });

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private Vector<View> pages;

        public CustomPagerAdapter(Context context, Vector<View> pages) {
            this.mContext = context;
            this.pages = pages;
        }

        @Override

        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = pages.get(position);
            container.addView(page);
            return page;
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}