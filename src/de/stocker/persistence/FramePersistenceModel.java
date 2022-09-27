package de.stocker.persistence;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.BollingerBand;
import de.stocker.model.dataWrappers.SimpleMovingAverage;
import de.stocker.view.ChartFrame;

/**
 * The Class FramePersistenceModel is a wrapper class for all data needed to
 * save the current state of the GUI to disk and restore it from there. For that
 * the class pulls all persistable information from an array of all open
 * internal frames currently displayed.
 * 
 * @author Matthias Rudolph
 */
public class FramePersistenceModel {

    private List<OpenFrame> openFrames;

    /**
     * Instantiates a new frame persistence model for an array of all currently
     * displayed internal frames.
     *
     * @param frames the internal frames
     */
    public FramePersistenceModel(JInternalFrame[] frames) {
        this.openFrames = new ArrayList<OpenFrame>();
        collectFramePersistenceInformation(frames);
    }

    /**
     * Collect all information necessary to save the state of the GUI from the
     * array of open internal frames.
     *
     * @param frames the internal frames
     */
    public void collectFramePersistenceInformation(JInternalFrame[] frames) {
        for (JInternalFrame f : frames) {
            EFrameType frameType = ((BaseInternalFrame) f).getFrameType();

            // Common information for all charts
            int xPosition = f.getX();
            int yPosition = f.getY();
            int width = f.getWidth();
            int height = f.getHeight();

            // Additional info for chart frames: stock id, resolution,
            // chart type, indicators, alarms
            if (frameType == EFrameType.CHART) {
                String stockId = ((ChartFrame) f).getStockId();
                EChartResolution resolution = ((ChartFrame) f).getChartResolution();
                EChartType chartType = ((ChartFrame) f).getChartType();

                Color alarmColor = ((ChartFrame) f).getAlarmColor();
                Color movingAvgColor = ((ChartFrame) f).getMovingAvgColor();
                Color bollingerColor = ((ChartFrame) f).getBollingerColor();

                List<SimpleMovingAverage> movingAvgs = new ArrayList<SimpleMovingAverage>();
                List<BollingerBand> bollingers = new ArrayList<BollingerBand>();
                List<SimpleMovingAverage> smasFromFrame = ((ChartFrame) f).getMovingAvgs();
                List<BollingerBand> bbsFromFrame = ((ChartFrame) f).getBollingerBands();

                if (smasFromFrame != null) {
                    for (SimpleMovingAverage a : smasFromFrame) {
                        int n = a.getN();
                        Color c = a.getColor();
                        SimpleMovingAverage newA = new SimpleMovingAverage(n, null);
                        newA.setColor(c);
                        movingAvgs.add(newA);
                    }
                }

                if (bbsFromFrame != null) {
                    for (BollingerBand b : bbsFromFrame) {
                        double ff = b.getF();
                        int n = b.getN();
                        Color c = b.getColor();
                        BollingerBand newB = new BollingerBand(ff, n, null, null, null);
                        newB.setColor(c);
                        bollingers.add(newB);
                    }
                }

                openFrames.add(new OpenFrame(frameType, xPosition, yPosition, width, height, stockId, resolution,
                        chartType, alarmColor, movingAvgColor, bollingerColor, movingAvgs, bollingers));
            } else if (frameType == EFrameType.SEARCH || frameType == EFrameType.WATCHLIST) {
                openFrames.add(new OpenFrame(frameType, xPosition, yPosition, width, height));
            }
        }
    }

    /**
     * Gets the list of open frame objects currently saved in the persistence
     * model.
     *
     * @return the open frames
     */
    public List<OpenFrame> getOpenFrames() {
        return openFrames;
    }

}