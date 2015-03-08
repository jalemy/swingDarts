package swingDarts;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame implements Runnable {
	SerialCom serialCom = new SerialCom();
	JButton openButton;
	JButton closeButton;
	JLabel notificationLabel;
	JLabel firstThrow;
	JLabel secondThrow;
	JLabel thirdThrow;
	JLabel firstThrowNumber;
	JLabel firstThrowNumberX;
	JLabel secondThrowNumber;
	JLabel secondThrowNumberX;
	JLabel thirdThrowNumber;
	JLabel thirdThrowNumberX;
	JLabel resultLabel;
	JLabel resultNumber;
	JButton resetButton;

	DefaultCategoryDataset gDataX = new DefaultCategoryDataset();
	DefaultCategoryDataset gDataZ = new DefaultCategoryDataset();

	JFreeChart chart1 = ChartFactory.createLineChart("Angular Velocity(gx)",
					"Time(sec)", "dps", gDataX);
	
	JFreeChart chart2 = ChartFactory.createLineChart(
					"Angular Velocity(gz)", "Time(sec)", "dps", gDataZ);
	
	MainFrame(String title) {
		setTitle(title); // �^�C�g����ݒ�
		setSize(960, 720); // �t���[���T�C�Y�̐ݒ�
		setLocationRelativeTo(null); // �E�B���h�E�ʒu�𒆉��ɐݒ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X�{�^�������������̏�����ݒ�
		setLayout(null); // ���C�A�E�g���蓮�ɐݒ�

		getContentPane().setLayout(null);

		// open�{�^���쐬
		openButton = new JButton("open");
		openButton.setFont(new Font("Meiryo UI", Font.PLAIN, 20));
		openButton.setBounds(40, 20, 100, 40);
		openButton.addActionListener(new openListener());
		getContentPane().add(openButton);

		// close�{�^���쐬
		closeButton = new JButton("close");
		closeButton.setFont(new Font("Meiryo UI", Font.PLAIN, 20));
		closeButton.setBounds(160, 20, 100, 40);
		closeButton.addActionListener(new closeListener());
		getContentPane().add(closeButton);
		
		// reset�{�^��
		resetButton = new JButton("reset");
		resetButton.setFont(new Font("Meiryo UI", Font.PLAIN, 20));
		resetButton.setBounds(40, 620, 100, 40);
		resetButton.addActionListener(new resetListener());
		getContentPane().add(resetButton);

		// �ʐM�p�ʒm���x���쐬
		notificationLabel = new JLabel();
		notificationLabel.setText("");
		notificationLabel.setBounds(40, 80, 260, 40);
		notificationLabel.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		LineBorder border = new LineBorder(Color.BLACK, 1, true);
		notificationLabel.setBorder(border);
		getContentPane().add(notificationLabel);
		
		// 1���ڕ��̓��x��
		firstThrow = new JLabel();
		firstThrow.setText("First Throw");
		firstThrow.setBounds(40, 160, 260, 40);
		firstThrow.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		getContentPane().add(firstThrow);
		
		// 1���ڐ��l
		firstThrowNumber = new JLabel();
		firstThrowNumber.setText("");
		firstThrowNumber.setHorizontalAlignment(JLabel.CENTER);
		firstThrowNumber.setBounds(40, 200, 120, 40);
		firstThrowNumber.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		firstThrowNumber.setBorder(border);
		getContentPane().add(firstThrowNumber);
		
		// 1���ڐ��lX
		firstThrowNumberX = new JLabel();
		firstThrowNumberX.setText("");
		firstThrowNumberX.setHorizontalAlignment(JLabel.CENTER);
		firstThrowNumberX.setBounds(170, 200, 120, 40);
		firstThrowNumberX.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		firstThrowNumberX.setBorder(border);
		getContentPane().add(firstThrowNumberX);
		
		// 2���ڕ���
		secondThrow = new JLabel();
		secondThrow.setText("Second Throw");
		secondThrow.setBounds(40, 260, 260, 40);
		secondThrow.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		getContentPane().add(secondThrow);
		
		// 2���ڐ��l
		secondThrowNumber = new JLabel();
		secondThrowNumber.setText("");
		secondThrowNumber.setHorizontalAlignment(JLabel.CENTER);
		secondThrowNumber.setBounds(40, 300, 120, 40);
		secondThrowNumber.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		secondThrowNumber.setBorder(border);
		getContentPane().add(secondThrowNumber);
		
		// 2���ڐ��lX
		secondThrowNumberX = new JLabel();
		secondThrowNumberX.setText("");
		secondThrowNumberX.setHorizontalAlignment(JLabel.CENTER);
		secondThrowNumberX.setBounds(170, 300, 120, 40);
		secondThrowNumberX.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		secondThrowNumberX.setBorder(border);
		getContentPane().add(secondThrowNumberX);
		
		// 3���ڕ���
		thirdThrow = new JLabel();
		thirdThrow.setText("Third Throw");
		thirdThrow.setBounds(40, 360, 260, 40);
		thirdThrow.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		getContentPane().add(thirdThrow);
		
		// 3���ڐ��l
		thirdThrowNumber = new JLabel();
		thirdThrowNumber.setText("");
		thirdThrowNumber.setHorizontalAlignment(JLabel.CENTER);
		thirdThrowNumber.setBounds(40, 400, 120, 40);
		thirdThrowNumber.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		thirdThrowNumber.setBorder(border);
		getContentPane().add(thirdThrowNumber);
		
		// 3���ڐ��lX
		thirdThrowNumberX = new JLabel();
		thirdThrowNumberX.setText("");
		thirdThrowNumberX.setHorizontalAlignment(JLabel.CENTER);
		thirdThrowNumberX.setBounds(170, 400, 120, 40);
		thirdThrowNumberX.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		thirdThrowNumberX.setBorder(border);
		getContentPane().add(thirdThrowNumberX);
		
		// ���ʃ��x��
		resultLabel = new JLabel();
		resultLabel.setText("Result");
		resultLabel.setBounds(40, 480, 260, 40);
		resultLabel.setFont(new Font("Meiryo UI", Font.PLAIN, 16));
		getContentPane().add(resultLabel);
		
		// ���ʐ��l�\��
		resultNumber = new JLabel();
		resultNumber.setText("");
		resultNumber.setBounds(40, 520, 260, 60);
		resultNumber.setBorder(border);
		resultNumber.setFont(new Font("Meiryo UI", Font.PLAIN, 30));
		getContentPane().add(resultNumber);
		
		// �O���tgx
		// DefaultCategoryDataset gDataX = new DefaultCategoryDataset();
		gDataX.addValue(100, "test", "test");
		gDataX.addValue(200, "test", "test2");
		// JFreeChart chart = ChartFactory.createLineChart("Angular Velocity(gx)",
		//		"Time(sec)", "dps", gDataX);
		ChartPanel cpanel = new ChartPanel(chart1);
		cpanel.setBounds(320, 20, 600, 300);
		getContentPane().add(cpanel);

		// �O���tgz
		// DefaultCategoryDataset gDataZ = new DefaultCategoryDataset();
		for (int i = 0; i < 300; i++) {
			gDataZ.addValue(serialCom.dartsData.getGDataZ(i), "hoge",
					Integer.toString(i));
		}
		//JFreeChart chart2 = ChartFactory.createLineChart(
		//		"Angular Velocity(gz)", "Time(sec)", "dps", gDataZ);
		ChartPanel cpanel2 = new ChartPanel(chart2);
		cpanel2.setBounds(320, 350, 600, 300);
		getContentPane().add(cpanel2);

		setVisible(true); // �t���[����\��
	}

	public class openListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			serialCom.SerialOpen("COM6", 115200);
			notificationLabel.setText(serialCom.portStatus);
		}
	}

	public class closeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			serialCom.SerialClose();
			notificationLabel.setText(serialCom.portStatus);
		}
	}
	
	public class resetListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < 300; i++) {
				serialCom.dartsData.gDataX[i] = 0;
				serialCom.dartsData.gDataZ[i] = 0;
			}
			serialCom.sift.resetParameter();
		}
	}

	@Override
	public void run() {
		while (true) {
			gDataX.clear();
			gDataZ.clear();
			
			for (int i = 0; i < 300; i++) {
				gDataX.addValue(serialCom.dartsData.getGDataX(i), "gyroX", Integer.toString(i));
				gDataZ.addValue(serialCom.dartsData.getGDataZ(i), "gyroZ", Integer.toString(i));
			}
			firstThrowNumber.setText(serialCom.sift.first);
			firstThrowNumberX.setText(serialCom.sift.firstX);
			secondThrowNumber.setText(serialCom.sift.second);
			secondThrowNumberX.setText(serialCom.sift.secondX);
			thirdThrowNumber.setText(serialCom.sift.third);
			thirdThrowNumberX.setText(serialCom.sift.thirdX);
			resultNumber.setText(serialCom.sift.result);
			
			setVisible(true);
			try {
				Thread.sleep(80);
			} catch(InterruptedException e) {
				
			}
		}
	}

}
