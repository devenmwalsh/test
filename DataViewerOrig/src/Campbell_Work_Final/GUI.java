package Campbell_Work_Final;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import edu.du.dudraw.DrawListener;
import edu.du.dudraw.Draw;


public class GUI implements DrawListener{
	
	private final static int 		GUI_MODE_MAIN_MENU = 0;
    private final static int 		GUI_MODE_DATA = 1;
    private final static String 	DEFAULT_COUNTRY = "United States";
	private final static String[] 	VISUALIZATION_MODES = { "Raw", "Extrema (within 10% of min/max)" };

	
	// GUI-related settings    
    private int m_guiMode = GUI_MODE_MAIN_MENU; // Menu by default
    private String m_selectedCountry = DEFAULT_COUNTRY;
    private Integer m_selectedEndYear;
    private String m_selectedState;
    private Integer m_selectedStartYear;
    private String m_selectedVisualization = VISUALIZATION_MODES[0];
	private TreeMap<Integer, SortedMap<Integer,Double>> m_plotData = null;
    private SortedSet<String> m_dataCountries;
    private SortedSet<String> m_dataStates;
    private SortedSet<Integer> m_dataYears;
    private DrawMainMenu mainMenu;
    private DrawData drawData;
    private Debugging debug;
    private ExtractData data;
    private UpdatePlot plot;
    private Draw m_window;

	
	public GUI(String dataFile) throws FileNotFoundException {
		mainMenu = new DrawMainMenu();
		drawData = new DrawData();
		debug = new Debugging();
		data = new ExtractData(dataFile, m_selectedCountry);
		plot = new UpdatePlot();
		
		
		m_dataCountries = new TreeSet<>(); // Initialize as TreeSet
	    m_dataStates = new TreeSet<>();    // Initialize as TreeSet
	    m_dataYears = new TreeSet<>();     // Initialize as TreeSet
	    
	    m_window = new Draw("DataViewer Application");
        m_window.setCanvasSize(1320, 720);
        m_window.enableDoubleBuffering();
        m_window.addListener(this); // Register as a listener for key event
        
        loadData();
	}
	
	private void loadData() {
	    try {
	        data.loadData(m_selectedCountry); // Load the data from the file into ExtractData

	        // Update GUI instance variables with loaded data
	        m_dataStates = data.getDataStates();
	        m_dataCountries = data.getDataCountries();
	        m_dataYears = data.getDataYears();

	        // Set initial selection variables based on loaded data
	        m_selectedState = m_dataStates.isEmpty() ? null : m_dataStates.first();
	        System.out.println(m_dataStates);
	        m_selectedStartYear = m_dataYears.isEmpty() ? null : m_dataYears.first();
	        m_selectedEndYear = m_dataYears.isEmpty() ? null : m_dataYears.last();

	        debug.info( "Data loaded successfully.");
	    } catch (FileNotFoundException e) {
	        debug.error("Data file not found: %s", e.getMessage());
	        JOptionPane.showMessageDialog(null, "Data file not found!", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    
	}
	
	public void update() {
		loadData();
	    if (m_guiMode == GUI_MODE_MAIN_MENU) {
	        mainMenu.drawMainMenu(m_window, m_selectedCountry, m_selectedState, m_selectedStartYear, m_selectedEndYear, m_selectedVisualization);
	        mainMenu.show(m_window); // Show main menu
	    } else if (m_guiMode == GUI_MODE_DATA) {
	        drawData.drawData(m_window, m_selectedStartYear, m_selectedEndYear, m_selectedVisualization, m_selectedState,
	                m_selectedCountry, VISUALIZATION_MODES, plot.getPlotMonthlyMax(), plot.getPlotMonthlyMin(), plot.getPlotData());
	        drawData.show(m_window); // Show data view
	    } else {
	        throw new IllegalStateException(String.format("Unexpected drawMode=%d", m_guiMode));
	    }
	}

	
	
	// Below are the mouse/key listeners
    /**
     * Handle key press.  Q always quits.  Otherwise process based on GUI mode.
     */
	@Override public void keyPressed(int key) {
		boolean needsUpdate = false;
		boolean needsUpdatePlotData = false;
		debug.trace("key pressed '%c'", (char)key);
		// regardless of draw mode, 'Q' or 'q' means quit:
		if(key == 'Q') {
			System.out.println("Bye");
			System.exit(0);
		}
		else if(m_guiMode == GUI_MODE_MAIN_MENU) {
			if(key == 'P') {
				// plot the data
				m_guiMode = GUI_MODE_DATA;
				if(m_plotData == null) {
					// first time going to render data need to generate the plot data
					needsUpdatePlotData = true;
				}
				needsUpdate = true;
			}
			else if(key == 'C') {
				// set the Country
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a Country", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataCountries.toArray(), m_selectedCountry);
				
				if(selectedValue != null) {
					//debug.info(DEFAULT_COUNTRY, VISUALIZATION_MODES);
					if(!selectedValue.equals(m_selectedCountry)) {
						// change in data
						m_selectedCountry = (String)selectedValue;
						debug.info("Updated selected country to: %s", m_selectedCountry);
						try {
							data.loadData(m_selectedCountry);
						}
						catch(FileNotFoundException e) {
							// convert to a runtime exception since
							// we can't add throws to this method
							throw new RuntimeException(e);
						}
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}

			else if(key == 'T') {
				// set the state
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a State", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataStates.toArray(), m_selectedState);
				
				if(selectedValue != null) {
					debug.info(DEFAULT_COUNTRY, VISUALIZATION_MODES);
					if(!selectedValue.equals(m_selectedState)) {
						// change in data
						m_selectedState = (String)selectedValue;
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}
			else if(key == 'S') {
				// set the start year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the start year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedStartYear);
				
				if(selectedValue != null) {
					debug.info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedEndYear) > 0) {
						debug.error("new start year (%d) must not be after end year (%d)", year, m_selectedEndYear);
					}
					else {
						if(!m_selectedStartYear.equals(year)) {
							m_selectedStartYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'E') {
				// set the end year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the end year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedEndYear);
				
				if(selectedValue != null) {
					debug.info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedStartYear) < 0) {
						debug.error("new end year (%d) must be not be before start year (%d)", year, m_selectedStartYear);
					}
					else {
						if(!m_selectedEndYear.equals(year)) {
							m_selectedEndYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'V') {
				// set the visualization
				Object selectedValue = JOptionPane.showInputDialog(null,
						"Choose the visualization mode", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						VISUALIZATION_MODES, m_selectedVisualization);

				if(selectedValue != null) {
					debug.info("User seleted: '%s'", selectedValue);
					String visualization = (String)selectedValue;
					if(!m_selectedVisualization.equals(visualization)) {
						m_selectedVisualization = visualization;
						needsUpdate = true;
					}
				}
			}

		}
		else if (m_guiMode == GUI_MODE_DATA) {
			if(key == 'M') {
				m_guiMode = GUI_MODE_MAIN_MENU;
				needsUpdate = true;
			}
		}
		else {
			throw new IllegalStateException(String.format("unexpected mode: %d", m_guiMode));
		}
		if(needsUpdatePlotData) {
			// something changed with the data that needs to be plotted
			plot.updatePlotData(data.getRawData(), m_selectedStartYear, m_selectedEndYear, m_selectedState);
		}
		if(needsUpdate) {
			update();
		}
	}

	@Override
	public void keyReleased(int key) {}

	@Override
	public void keyTyped(char key) {}

	@Override
	public void mouseClicked(double x, double y) {}
	
	@Override
	public void mouseDragged(double x, double y) {}

	@Override
	public void mousePressed(double x, double y) {}

	@Override
	public void mouseReleased(double x, double y) {}  
}