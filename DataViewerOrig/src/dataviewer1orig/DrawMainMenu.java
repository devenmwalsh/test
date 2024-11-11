package dataviewer1orig;

import java.awt.Color;
import edu.du.dudraw.Draw;

public class DrawMainMenu {
    private final static double MENU_STARTING_X = 40.0;
    private final static double MENU_STARTING_Y = 90.0;
    private final static double MENU_ITEM_SPACING = 5.0;


    public DrawMainMenu() {

    }

    public void drawMainMenu(Draw m_window, String m_selectedCountry, String m_selectedState, Integer m_selectedStartYear,
                             Integer m_selectedEndYear, String m_selectedVisualization) {
        m_window.clear(Color.WHITE);

        String[] menuItems = {
                "Type the menu number to select that option:",
                "",
                String.format("C     Set country: [%s]", m_selectedCountry),
                String.format("T     Set state: [%s]", m_selectedState),
                String.format("S     Set start year [%d]", m_selectedStartYear),
                String.format("E     Set end year [%d]", m_selectedEndYear),
                String.format("V     Set visualization [%s]", m_selectedVisualization),
                "P     Plot data",
                "Q     Quit",
        };

        m_window.setXscale(0, 100);
        m_window.setYscale(0, 100);

        m_window.setPenColor(Color.BLACK);
        drawMenuItems(menuItems, m_window);
    }

    public void drawMenuItems(String[] menuItems, Draw m_window) {
        double yCoord = MENU_STARTING_Y;

        for (String menuItem : menuItems) {
            m_window.textLeft(MENU_STARTING_X, yCoord, menuItem);
            yCoord -= MENU_ITEM_SPACING;
        }
    }

    public void show(Draw m_window) {
        m_window.show();
    }
}
