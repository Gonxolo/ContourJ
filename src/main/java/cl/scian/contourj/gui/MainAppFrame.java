package cl.scian.contourj.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cl.scian.contourj.ContourWorkflow;
import cl.scian.contourj.gui.controller.MainController;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import net.imagej.ImageJ;
import net.imagej.display.ImageDisplay;


public class MainAppFrame extends JFrame {

    @Parameter
    private LogService log;

    @Parameter
    private Context context;

    public ImageJ ij;

    private JFXPanel fxPanel;

    private final ContourWorkflow activeContours;
    private final ImageDisplay imd;

    public MainAppFrame(ImageJ ij, ContourWorkflow activeContours){
        ij.context().inject(this);
        this.ij = ij;
        this.activeContours = activeContours;
        this.imd = activeContours.getSourceDisplay();

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                log.info("Quitting Active Contours... Bye bye !");
                SwingUtilities.invokeLater(() -> {
                    setVisible(true);
                    dispose();
                });
            }
        });
    }


    /**
     * Create the JFXPanel that makes the link between Swing (IJ) and JavaFX plugin.
     */
    public void initialize() {

        Platform.setImplicitExit(false);

        // Create the JavaFX panel
        this.fxPanel = new JFXPanel();
        this.getContentPane().add(this.fxPanel, BorderLayout.CENTER);
        this.setVisible(true);
        this.setResizable(false);

        // Initialize the JavaFX panel
        // The call to runLater() avoids a mix between JavaFX thread and Swing thread.
        Platform.runLater(() -> initFx(fxPanel));
    }

    public void initFx(JFXPanel fxPanel) {
        try {
            // Load the main UI
            FXMLLoader loader = new FXMLLoader(MainAppFrame.class.getResource("/active-contours.fxml"));

            // Create and set the main controller
            MainController mainController = new MainController(this.context, this.activeContours);
            loader.setController(mainController);

            // Show the scene containing the root layout
            BorderPane mainScreen = (BorderPane) loader.load();
            Scene scene = new Scene(mainScreen);
            this.fxPanel.setScene(scene);

            // Resize the JFrame to the JavaFX scene
            this.getContentPane().setPreferredSize(new Dimension((int) scene.getWidth(), (int) scene.getHeight()));
            this.pack();

            // Position the window
            // TODO: Fix NullPointerException
            // ImagePlus imp = convert.convert(activeContours.getSourceImage(), ImagePlus.class);
            // GUIUtils.positionWindow(this, imp.getWindow());

            mainController.loadPanes();

        } catch (IOException e) {
            log.error(e);
        }
    }

}