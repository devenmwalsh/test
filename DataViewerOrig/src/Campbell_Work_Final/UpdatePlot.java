package Campbell_Work_Final;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UpdatePlot {
    private final static int RECORD_YEAR_IDX = 0;
    private final static int RECORD_STATE_IDX = 3;
    private final static int RECORD_MONTH_IDX = 1;
    private final static int RECORD_TEMPERATURE_IDX = 2;

    private TreeMap<Integer, SortedMap<Integer, Double>> m_plotData = null;
    private TreeMap<Integer, Double> m_plotMonthlyMaxValue = null;
    private TreeMap<Integer, Double> m_plotMonthlyMinValue = null;
    public List<List<Object>> m_dataRaw;

    public UpdatePlot() {
    	m_dataRaw = new ArrayList<>();
    	
    }

    public void updatePlotData(List<List<Object>> m_dataRaw, Integer m_selectedStartYear, Integer m_selectedEndYear, String m_selectedState) {
        m_plotData = new TreeMap<>();
        m_plotMonthlyMaxValue = new TreeMap<>();
        m_plotMonthlyMinValue = new TreeMap<>();

        for (int i = 1; i <= 12; i++) {
            m_plotData.put(i, new TreeMap<>());
            m_plotMonthlyMaxValue.put(i, Double.MIN_VALUE);
            m_plotMonthlyMinValue.put(i, Double.MAX_VALUE);
        }

        for (List<Object> rec : m_dataRaw) {
            String state = (String) rec.get(RECORD_STATE_IDX);
            Integer year = (Integer) rec.get(RECORD_YEAR_IDX);

            if (state.equals(m_selectedState) && (year >= m_selectedStartYear && year <= m_selectedEndYear)) {
                Integer month = (Integer) rec.get(RECORD_MONTH_IDX);
                Double value = (Double) rec.get(RECORD_TEMPERATURE_IDX);

                m_plotMonthlyMinValue.put(month, Math.min(m_plotMonthlyMinValue.get(month), value));
                m_plotMonthlyMaxValue.put(month, Math.max(m_plotMonthlyMaxValue.get(month), value));
                m_plotData.get(month).put(year, value);
            }
        }
    }

    public TreeMap<Integer, Double> getPlotMonthlyMax() {
        return m_plotMonthlyMaxValue;
    }

    public TreeMap<Integer, Double> getPlotMonthlyMin() {
        return m_plotMonthlyMinValue;
    }

    public TreeMap<Integer, SortedMap<Integer, Double>> getPlotData() {
        return m_plotData;
    }
}
