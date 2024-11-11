package dataviewer1orig;

import java.awt.Color;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.du.dudraw.Draw;

public class DrawData {
    private final static double DATA_WINDOW_BORDER = 50.0;
    private final static String WINDOW_TITLE = "DataViewer Application";
    private final static int WINDOW_WIDTH = 1320;
    private final static int WINDOW_HEIGHT = 720;
    private final static int VISUALIZATION_EXTREMA_IDX = 1;
    private final static double EXTREMA_PCT = 0.1;
    private final static String[] MONTH_NAMES = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private final static double TEMPERATURE_MIN_C = -10.0;
    private final static double TEMPERATURE_MAX_C = 30.0;
    private final static double TEMPERATURE_RANGE = TEMPERATURE_MAX_C - TEMPERATURE_MIN_C;

    private Debugging debug;

    public DrawData() {
        //m_window = new Draw(WINDOW_TITLE);
        //m_window.setCanvasSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        //m_window.enableDoubleBuffering();
        //debug = new Debugging();
    }
    
    
	
	public void drawData(Draw m_window, Integer m_selectedEndYear, Integer m_selectedStartYear, String m_selectedVisualization, String m_selectedState, String m_selectedCountry, String[] VISUALIZATION_MODES, 
			TreeMap<Integer, Double> m_plotMonthlyMaxValue, TreeMap<Integer, Double> m_plotMonthlyMinValue, TreeMap<Integer,SortedMap<Integer,Double>> m_plotData) {
		
		debug = new Debugging();
        m_window.setCanvasSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        m_window.enableDoubleBuffering(); // Too slow otherwise -- need to use .show() later
        
     // Give a buffer around the plot window
        m_window.setXscale(-DATA_WINDOW_BORDER, WINDOW_WIDTH+DATA_WINDOW_BORDER);
		m_window.setYscale(-DATA_WINDOW_BORDER, WINDOW_HEIGHT+DATA_WINDOW_BORDER);

    	// gray background
    	m_window.clear(Color.LIGHT_GRAY);

    	// white plot area
		m_window.setPenColor(Color.WHITE);
		m_window.filledRectangle(WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0, WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0);  

    	m_window.setPenColor(Color.BLACK);
    	
    	double nCols = 12; // one for each month
    	double nRows = m_selectedEndYear - m_selectedStartYear + 1; // for the years
    	
    	debug.debug("nCols = %f, nRows = %f", nCols, nRows);
 		
        double cellWidth = WINDOW_WIDTH / nCols;
        double cellHeight = WINDOW_HEIGHT / nRows;
        
        debug.debug("cellWidth = %f, cellHeight = %f", cellWidth, cellHeight);
        
        boolean extremaVisualization = m_selectedVisualization.equals(VISUALIZATION_MODES[VISUALIZATION_EXTREMA_IDX]);
        debug.info("visualization: %s (extrema == %b)", m_selectedVisualization, extremaVisualization);
    
        for(int month = 1; month <= 12; month++) {
            double fullRange = m_plotMonthlyMaxValue.get(month) - m_plotMonthlyMinValue.get(month);
            double extremaMinBound = m_plotMonthlyMinValue.get(month) + EXTREMA_PCT * fullRange;
            double extremaMaxBound = m_plotMonthlyMaxValue.get(month) - EXTREMA_PCT * fullRange;

            
            // draw the line separating the months and the month label
        	m_window.setPenColor(Color.BLACK);
        	double lineX = (month-1.0)*cellWidth;
        	m_window.line(lineX, 0.0, lineX, WINDOW_HEIGHT);
        	m_window.text(lineX+cellWidth/2.0, -DATA_WINDOW_BORDER/2.0, MONTH_NAMES[month]);
        	
        	// there should always be a map for the month
        	SortedMap<Integer,Double> monthData = m_plotData.get(month);
        	
        	for(int year = m_selectedStartYear; year <= m_selectedEndYear; year++) {

        		// month data structure might not have every year
        		if(monthData.containsKey(year)) {
        			Double value = monthData.get(year);
        			
        			double x = (month-1.0)*cellWidth + 0.5 * cellWidth;
        			double y = (year-m_selectedStartYear)*cellHeight + 0.5 * cellHeight;
        			
        			Color cellColor = null;
        			
        			// get either color or grayscale depending on visualization mode
        			if(extremaVisualization && value > extremaMinBound && value < extremaMaxBound) {
        				cellColor = getDataColor(value, true);
        			}
        			else if(extremaVisualization) {
        				// doing extrema visualization, show "high" values in red "low" values in blue.
        				if(value >= extremaMaxBound) {
        					cellColor = Color.RED;
        				}
        				else {
        					cellColor = Color.BLUE;
        				}
        			}
        			else {
        				cellColor = getDataColor(value, false);
        			}
        			
        			// draw the rectangle for this data point
        			m_window.setPenColor(cellColor);
        			debug.trace("month = %d, year = %d -> (%f, %f) with %s", month, year, x, y, cellColor.toString());
        			m_window.filledRectangle(x, y, cellWidth/2.0, cellHeight/2.0);
        		}
        	}
        }
        
        // draw the labels for the y-axis
        m_window.setPenColor(Color.BLACK);

        double labelYearSpacing = (m_selectedEndYear - m_selectedStartYear) / 5.0;
        double labelYSpacing = WINDOW_HEIGHT/5.0;
        // spaced out by 5, but need both the first and last label, so iterate 6
        for(int i=0; i<6; i++) {
        	int year = (int)Math.round(i * labelYearSpacing + m_selectedStartYear);
        	String text = String.format("%4d", year);
        	
        	m_window.textRight(0.0, i*labelYSpacing, text);
        	m_window.textLeft(WINDOW_WIDTH, i*labelYSpacing, text);
        }
     
        // draw rectangle around the whole data plot window
        m_window.rectangle(WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0, WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0);
        
        // put in the title
        String title = String.format("%s, %s from %d to %d. Press 'M' for Main Menu.  Press 'Q' to Quit.",
        		m_selectedState, m_selectedCountry, m_selectedStartYear, m_selectedEndYear);
        m_window.text(WINDOW_WIDTH/2.0, WINDOW_HEIGHT+DATA_WINDOW_BORDER/2.0, title);

	}
	
	
    
    /**
     * Return a Color object based on the value passed in.
     * @param value - controls the color
     * @param doGrayscale - if true, return a grayscale value (r, g, b are all equal);
     * 	otherwise return a range of red to green.
     * @return null is value is null, otherwise return a Color object
     */
    private Color getDataColor(Double value, boolean doGrayscale) {
    	if(null == value) {
    		return null;
    	}
    	double pct = (value - TEMPERATURE_MIN_C) / TEMPERATURE_RANGE;
    	debug.trace("converted %f raw value to %f %%", value, pct);
    
    	if (pct > 1.0) {
            pct = 1.0;
        }
        else if (pct < 0.0) {
            pct = 0.0;
        }
        int r, g, b;
        // Replace the color scheme with my own
        if (!doGrayscale) {
        	r = (int)(255.0 * pct);
        	g = 0;
        	b = (int)(255.0 * (1.0-pct));
        	
        } else {
        	// Grayscale for the middle extema
        	r = g = b = (int)(255.0 * pct);
        }
        
        debug.trace("converting %f to [%d, %d, %d]", value, r, g, b);

		return new Color(r, g, b);
	}
    
    public void show(Draw m_window) {
    	// for double-buffering
        m_window.show();
    }

}
