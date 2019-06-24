/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sterlingcommerce.emea.support.l2.qwa;

/**
 *
 * @author AAuklend
 */
public class QWTest {
    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
/*
    private JFreeChart createChart(CategoryDataset dataset,String title,String yaxis) {
        // create the chart...
             
        JFreeChart chart = ChartFactory.createBarChart(
            title,       // chart title
            "Time",               // domain axis label
            yaxis,                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );
        String shdr = "   From: " + qwa.start + "    To: " + qwa.last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);

        Rectangle rt = jPanel1.getBounds();
        int xx = rt.width;
        int yy = rt.height;

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(Color.blue);
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        renderer.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("Tooltip: {0}"));

        CategoryAxis domainAxis = plot.getDomainAxis();
        CategoryAxis axis = plot.getDomainAxis();
      
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
/*
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                      Math.PI / 6.0));
*/
/*    
         try {
//            ChartUtilities.saveChartAsJPEG(new File("C:\\chart2.jpg"), chart, xx, yy);
             if (graphPrt) 
                 ChartUtilities.saveChartAsJPEG(new File(jpgChart), chart, xx, yy);        
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        } 
        try { 

            BufferedImage image = chart.createBufferedImage(xx-10,yy-10);
            adjustPlot(xx-10,yy);
 //                   jLabel2.setBounds(0, 0, 500, 300);
            jLabel2.setIcon(new ImageIcon(image));
            ChartPanel cpanel = new ChartPanel(chart);
            jPanel1.add(cpanel);
            jTabbedPane1.setSelectedComponent(jPanel1);
            jPanel1.setVisible(true);
        } catch (Exception e) {
            System.out.println("Problem occurred creating chart.");
        }

        return chart;

    }
*/
}
