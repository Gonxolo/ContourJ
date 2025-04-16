/*-
 * #%L
 * A Fiji plugin that provides a GUI for the active contours model (GVF/GGVF snakes).
 * %%
 * Copyright (c) 2023 - 2025, Gonzalo Alarcón Usui
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package cl.scian.contourj;

import org.scijava.command.Command;
import org.scijava.log.LogLevel;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ImageJ;
import cl.scian.contourj.gui.MainAppFrame;
import org.scijava.ui.DialogPrompt;

@Plugin(type = Command.class, menuPath = "Plugins>Segmentation>ContourJ")
public class ContourJPlugin implements Command {

    @Parameter
    private ImageJ ij;

    @Parameter
    private LogService log;

    public static final String PLUGIN_NAME = "ContourJ";
    public static final String VERSION = version();

    private static String version() {
        String version = null;
        final Package pack = ContourJPlugin.class.getPackage();
        // System.out.println(pack);
        if (pack != null) {
            version = pack.getImplementationVersion();
            // System.out.println(version);
        }
        return version == null ? "DEVELOPMENT" : version;
    }

    @Override
    public void run() {
        //if (VERSION == "DEVELOPMENT") {
        //    log.setLevel(LogLevel.DEBUG);
        //}
        log.debug("Running " + PLUGIN_NAME + " version " + VERSION);

        try {
            ContourWorkflow activeContours = new ContourWorkflow(ij.context());

            // Launch JavaFX interface
            MainAppFrame app = new MainAppFrame(ij, activeContours);
            app.setTitle(PLUGIN_NAME);// + " version " + VERSION);
            app.initialize();

        } catch (Exception e) {
            ij.ui().showDialog("Error during initialization", e.getMessage(), DialogPrompt.MessageType.ERROR_MESSAGE);
            log.error("Error during initialization");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

        /*
            try {
                // final Dataset dataset = ij.scifio().datasetIO().open("https://imagej.net/images/blobs.gif");
                final Dataset dataset = ij.scifio().datasetIO().open("https://imagej.net/ij/images/mitosis.tif");
                ij.ui().show(dataset);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ij.command().run(ContourJPlugin.class, true);
        */

    }
}
