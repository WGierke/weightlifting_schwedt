package de.schwedt.weightlifting.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.schwedt.weightlifting.app.buli.CompetitionsFragment;
import de.schwedt.weightlifting.app.buli.TableFragment;
import de.schwedt.weightlifting.app.buli.TeamFragment;

public class BuliFragment extends Fragment {

    private static final int FRAGMENT_TEAM = 0;
    private static final int FRAGMENT_COMPETITIONS = 1;
    private static final int FRAGMENT_TABLE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d(WeightliftingApp.TAG, "Showing Buli fragment");

        View fragment = inflater.inflate(R.layout.pager_tab_strip, container, false);

        BuliCollectionPagerAdapter mBuliCollectionPagerAdapter = new BuliCollectionPagerAdapter(getActivity().getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) fragment.findViewById(R.id.pager);
        mViewPager.setAdapter(mBuliCollectionPagerAdapter);

        return fragment;
    }

    public class BuliCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public BuliCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case FRAGMENT_TEAM:
                    fragment = new TeamFragment();
                    break;
                case FRAGMENT_COMPETITIONS:
                    fragment = new CompetitionsFragment();
                    break;
                case FRAGMENT_TABLE:
                    fragment = new TableFragment();
                    break;
                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            switch (position) {
                case FRAGMENT_TEAM:
                    title = getString(R.string.buli_team);
                    break;
                case FRAGMENT_COMPETITIONS:
                    title = getString(R.string.buli_competitions);
                    break;
                case FRAGMENT_TABLE:
                    title = getString(R.string.buli_table);
                    break;
                default:
                    title = getString(R.string.nav_buli);
            }
            return title;
        }
    }
}
