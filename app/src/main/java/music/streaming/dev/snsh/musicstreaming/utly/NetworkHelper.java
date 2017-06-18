/*
* Copyright (C) 2014 The Android Open Source Project
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

package music.streaming.dev.snsh.musicstreaming.utly;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Generic reusable network methods.
 */
public class NetworkHelper {
    /**
     * @param context to use to check for network connectivity.
     * @return true if connected, false otherwise.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void showNetworkErrorDialog(final Activity activity, int colorPrimary) {

        Aesthetic.get()
                .colorAccent()
                .take(1)
                .subscribe(colorAccent -> {
                    // Use color (an integer)
                    new MaterialDialog.Builder(activity)
                            .content("No Internet Connection!\nPlease Connect to the Internet!")
                            .positiveText("Ok")
                            .backgroundColor(colorPrimary)
                            .positiveColor(colorAccent)
                            .cancelable(false)
                            .show();
                });
                /*.onPositive((dialog, which) -> {
                    activity.finish();
                    activity.startActivity(activity.getIntent());
                })*/

    }
}
