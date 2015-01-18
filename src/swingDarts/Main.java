package swingDarts;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartColor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		MainFrame frame = new MainFrame("swingDarts");
		Thread thread = new Thread(frame);
		thread.start();
		
		/*
		 * gyroX, gyroZのグラフ設定を行う
		 * 横ラベルを削除して、自動レンジ調整を無しに
		 * さらにレンジを-2000 ~ 2000に設定
		 */
		
		CategoryPlot plotX = frame.chart1.getCategoryPlot();
		CategoryAxis axisX = plotX.getDomainAxis();
		
		axisX.setVisible(false);
		ValueAxis valueAxisX = plotX.getRangeAxis();
		valueAxisX.setAutoRange(false);
		valueAxisX.setRange(-2000, 2000);
		LineAndShapeRenderer rendererX = (LineAndShapeRenderer)plotX.getRenderer();
		rendererX.setSeriesPaint(0, ChartColor.BLUE);
		
		CategoryPlot plotZ = frame.chart2.getCategoryPlot();
		CategoryAxis axisZ = plotZ.getDomainAxis();
		axisZ.setVisible(false);
		ValueAxis valueAxisZ = plotZ.getRangeAxis();
		valueAxisZ.setAutoRange(false);
		valueAxisZ.setRange(-2000, 2000);
		LineAndShapeRenderer rendererZ = (LineAndShapeRenderer)plotZ.getRenderer();
		rendererZ.setSeriesPaint(0, ChartColor.RED);
		
	}
}