package de.schwedt.weightlifting.app.helper.UniversalImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import de.schwedt.weightlifting.app.R;

/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

/*Modification:
- only show ImageListFragment or ImageGalleryFragment
*/

public class SimpleImageActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;
        int titleRes;
        switch (frIndex) {
            default:
            case ImageGridFragment.INDEX:
                //Hands over GALLERY_POSITION
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.nav_gallery;
                break;
            case ImagePagerFragment.INDEX:
                //Hands over GALLERY_POSITION, IMAGE_POSITION
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.nav_gallery;
                break;
        }

        setTitle(titleRes);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, fr, tag).commit();
    }
}
