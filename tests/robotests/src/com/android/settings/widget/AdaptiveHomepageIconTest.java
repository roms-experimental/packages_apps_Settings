/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.widget;

import static com.android.settingslib.drawer.TileUtils.META_DATA_PREFERENCE_ICON_BACKGROUND_ARGB;
import static com.android.settingslib.drawer.TileUtils.META_DATA_PREFERENCE_ICON_BACKGROUND_HINT;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import com.android.settings.R;
import com.android.settings.homepage.AdaptiveIconShapeDrawable;
import com.android.settingslib.drawer.CategoryKey;
import com.android.settingslib.drawer.Tile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class AdaptiveHomepageIconTest {

    private Context mContext;
    private ActivityInfo mActivityInfo;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
        mActivityInfo = new ActivityInfo();
        mActivityInfo.packageName = mContext.getPackageName();
        mActivityInfo.name = "class";
        mActivityInfo.metaData = new Bundle();
    }

    @Test
    public void createIcon_shouldSetBackgroundAndInset() {
        final AdaptiveHomepageIcon icon =
                new AdaptiveHomepageIcon(mContext, new ColorDrawable(Color.BLACK));

        assertThat(icon.getNumberOfLayers()).isEqualTo(2);
        assertThat(icon.getDrawable(0)).isInstanceOf(AdaptiveIconShapeDrawable.class);
    }

    @Test
    public void setBackgroundColor_shouldUpdateColorFilter() {
        final AdaptiveHomepageIcon icon =
                spy(new AdaptiveHomepageIcon(mContext, new ColorDrawable(Color.BLACK)));
        final ShapeDrawable background = mock(ShapeDrawable.class);
        when(icon.getDrawable(0)).thenReturn(background);

        icon.setBackgroundColor(Color.BLUE);

        verify(background).setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
    }

    @Test
    public void setBackgroundColor_externalTileWithBackgroundColorRawValue_shouldUpdateIcon() {
        final Tile tile = spy(new Tile(mActivityInfo, CategoryKey.CATEGORY_HOMEPAGE));
        mActivityInfo.metaData.putInt(META_DATA_PREFERENCE_ICON_BACKGROUND_ARGB, 0xff0000);
        doReturn(Icon.createWithResource(mContext, R.drawable.ic_settings_accent))
                .when(tile).getIcon(mContext);
        final AdaptiveHomepageIcon icon =
                new AdaptiveHomepageIcon(mContext, new ColorDrawable(Color.BLACK));

        icon.setBackgroundColor(mContext, tile);
        assertThat(icon.mBackgroundColor).isEqualTo(0xff0000);
    }

    @Test
    public void onBindTile_externalTileWithBackgroundColorHint_shouldUpdateIcon() {
        final Tile tile = spy(new Tile(mActivityInfo, CategoryKey.CATEGORY_HOMEPAGE));
        mActivityInfo.metaData.putInt(META_DATA_PREFERENCE_ICON_BACKGROUND_HINT,
                R.color.material_blue_500);
        doReturn(Icon.createWithResource(mContext, R.drawable.ic_settings_accent))
                .when(tile).getIcon(mContext);

        final AdaptiveHomepageIcon icon =
                new AdaptiveHomepageIcon(mContext, new ColorDrawable(Color.BLACK));
        icon.setBackgroundColor(mContext, tile);

        assertThat(icon.mBackgroundColor)
                .isEqualTo(mContext.getColor(R.color.material_blue_500));
    }
}
