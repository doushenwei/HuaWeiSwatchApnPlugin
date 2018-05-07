/*
 * IMPORTANT:  This Huawei software is supplied to you by Huawei Technologies Co., Ltd.
 * ("Huawei") in consideration of your agreement to the following
 * terms, and your use, copy, installation, modification or redistribution of
 * this Huawei software constitutes acceptance of these terms.  If you do
 * not agree with these terms, please do not use, copy, install, modify or
 * redistribute this Huawei software.

 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, Huawei grants you a personal, non-exclusive
 * license, under Huawei's copyrights in this original Huawei software(hereinafter referred as ��Huawei Software��), to use, reproduce, modify and redistribute the Huawei Software, with or without modifications, in source and/or binary forms;
 * provided that if you redistribute the Huawei Software in its entirety and
 * without modifications, you must retain this notice and the following
 * text and disclaimers in all such redistributions of the Huawei Software.
 * Neither the name, trademarks, service marks or logos of Huawei Technologies Co.. Ltd. may
 * be used to endorse or promote products derived from the Huawei Software
 * without specific prior written permission from Huawei.  Except as
 * expressly stated in this notice, no other rights or licenses, express or
 * implied, are granted by Huawei herein, including but not limited to any
 * patent rights that may be infringed by your derivative works or by other
 * works in which the Huawei Software may be incorporated.

 * The Huawei Software is provided by Huawei on an "AS IS" basis.  Huawei
 * MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE, REGARDING THE HUAWEI SOFTWARE OR ITS USE AND
 * OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.

 * IN NO EVENT SHALL HUAWEI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 * MODIFICATION AND/OR DISTRIBUTION OF THE HUAWEI SOFTWARE, HOWEVER CAUSED
 * AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 * STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.

 * Copyright (C) 2015 Huawei Technologies Co., Ltd. All Rights Reserved.

 * Trademarks and Permissions
 * Huawei and other Huawei trademarks are trademarks of Huawei Technologies Co., Ltd.
 * All other trademarks and trade names mentioned in this document are the property of their respective holders.
 */
package cordova.plugin.huaweiswatchapn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Utils {
    public static String TAG = "SampleUtils";
    /**
     * Get help string from html file
     * @param context: Context object
     * @param filePath: html file path
     * @return string
     */
    public static String getStringFromHtmlFile (Context context, String filePath) {
        String result = "";
        if (null == context || null == filePath) {
            return result;
        }

        InputStream stream = null;
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        try {
            // Read html file into buffer
            stream = context.getAssets().open(filePath);
            streamReader = new InputStreamReader(stream, "utf-8");
            reader = new BufferedReader(streamReader);
            StringBuilder builder = new StringBuilder();
            String line = null;

            boolean readCurrentLine = true;
            // Read each line of the html file, and build a string.
            while ((line = reader.readLine()) != null) {
                // Don't read the Head tags when CSS styling is not supporeted.
                if (line.contains("<style")) {
                    readCurrentLine = false;
                } else if (line.contains("</style")) {
                    readCurrentLine = true;
                }
                if (readCurrentLine) {
                    builder.append(line).append("\n");
                }
            }
            result = builder.toString();
        } catch (FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != streamReader) {
                try {
                    streamReader.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        return result;
    }

}
