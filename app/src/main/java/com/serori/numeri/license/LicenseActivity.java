package com.serori.numeri.license;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.serori.numeri.R;
import com.serori.numeri.activity.NumeriActivity;
import com.serori.numeri.activity.SubsidiaryActivity;

/**
 */
public class LicenseActivity extends SubsidiaryActivity {

    private static String license = "";
    public static String twitter4JLicense = "\nTwitter4J Licence\n\n" +
            "Copyright 2007 Yusuke Yamamoto\n" +
            "\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n" +
            "\n" +
            "      http://www.apache.org/licenses/LICENSE-2.0\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, software\n" +
            "Distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.";

    public static String ormLiteLicense = "\nORMLite Licence\n\n"
            + "THE SOFTWARE IS PROVIDED \"AS IS\" AND THE AUTHOR DISCLAIMS \n" +
            "ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.\n" +
            " IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES\n" +
            " WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, \n" +
            "ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE. ";

    public static void show(Context context, String licence) {
        if (context instanceof NumeriActivity) {
            LicenseActivity.license = licence;
            Log.v("LicenseActivity", licence);
            ((NumeriActivity) context).startActivity(LicenseActivity.class, false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("LicenseActivity", "onCreate");
        setContentView(R.layout.activity_license);
        TextView textView = (TextView) findViewById(R.id.license_text);
        textView.setText(license);
    }
}
