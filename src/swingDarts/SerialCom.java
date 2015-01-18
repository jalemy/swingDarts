package swingDarts;

import gnu.io.*;

import java.io.*;
import java.math.BigDecimal;

public class SerialCom {
	// ���o�̓X�g���[��
	InputStream input;
	OutputStream output;
	
	// �|�[�g�̏�Ԓʒm�p
	String portStatus;
	
	// �V���A���ʐM�𗘗p���邽�߂̃N���X
	SerialPort port;
	CommPortIdentifier comID;
	
	// �{�[���[�g, �f�W�^���f�[�^��1�b�Ԃɉ���ϕ����ł��邩�������l
	int baudRate;
	// open�ȂǂŎw�肷��|�[�g��
	String comName;
	
	// �����f�[�^�ۊǗp�N���X
	DartsData dartsData = new DartsData();
	
	// Sift�v�Z�p�N���X
	SiftParameter sift = new SiftParameter();
		
	boolean SerialOpen(String comName, int baudrate) {
		try {
			// ����comName�̖��O�ŐV����COM�|�[�g���擾
			comID = CommPortIdentifier.getPortIdentifier(comName);
		} catch (NoSuchPortException e) {
			portStatus =  "�|�[�g���擾�ł��܂���ł��� ";
			return false;
		}
		
		// �|�[�g���J���Ă��Ȃ��ꍇ�A�|�[�g���J���Ċe��ݒ���s��
		if (comID.isCurrentlyOwned() == false) {
			try {
				// ��1�����͂��̃|�[�g���g�p����A�v���P�[�V������, ��2�����̓^�C���A�E�g����
				// �V���A���|�[�g�̃C���X�^���X�𐶐�
				port = (SerialPort)comID.open("swingDarts", 2000);
				
				// �C�x���g���X�i�[��o�^
				port.addEventListener(new SerialPortListener());
				
				// 49byte��M������C�x���g��������悤�ɐݒ�
				port.enableReceiveThreshold(49);
				
				// �C�x���g�Ď���ON�ɂ���
				port.notifyOnDataAvailable(true);
				
				// �{�[���[�g, �f�[�^�r�b�g��, �X�g�b�v�r�b�g��, �p���e�B��ݒ�
				port.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				
				// �t���[���䖳��
				port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				
				// ���o�̓X�g���[���ݒ�
				input = port.getInputStream();
				output = port.getOutputStream();
			} catch (Exception e) {
				portStatus = "�|�[�g���J�����ƂɎ��s���܂���";
				return false;
			}
		} else {
			portStatus = "���Ƀ|�[�g���J���Ă��܂�";
			return false;
		}
		
		// ����I���Ȃ�΁Atrue��Ԃ�
		portStatus = "�|�[�g���J�����Ƃɐ������܂���";
		return true;
	}
	
	// �|�[�gclose�̃��\�b�h
	boolean SerialClose() {
		if (comID == null) {
			portStatus = "�|�[�g���I������Ă��܂���";
			return false;
		}
		// �����|�[�g���J���Ă���ꍇ�́c�c
		if (comID.isCurrentlyOwned()) {
			try {
				port.close();
			} catch (Exception e) {
				portStatus = "�|�[�g����邱�ƂɎ��s���܂���";
				return false;
			}
		} else {
			portStatus = "�|�[�g�͊��ɕ��Ă��܂�";
			return false;
		}
		
		// ����I���Ȃ�΁c�c
		portStatus = "�|�[�g����邱�Ƃɐ������܂���";
		
		// test�p//////////////////
		try {
			File file = new File("test.csv");
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			
			for (int i = 0; i < dartsData.accelX.size(); i++) {
				pw.println(dartsData.accelX.get(i) + "," +
						   dartsData.accelY.get(i) + "," +
						   dartsData.accelZ.get(i) + "," +
						   dartsData.gyroX.get(i) + "," +
						   dartsData.gyroY.get(i) + "," +
						   dartsData.gyroZ.get(i));
			}
			pw.close();
			System.out.println("file output succeeded");
		} catch (IOException e) {
			// ���ɂȂ�
			System.out.println("file error");
		}
		
		return true;
	}
	
	class SerialPortListener implements SerialPortEventListener {
		// �o�b�t�@�[
		byte[] readBuffer = new byte[49];
		
		int cnt = 0;
		// ��M�C�x���g�������ɌĂяo����郁�\�b�h
		public void serialEvent(SerialPortEvent event) {
			if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					input.read(readBuffer);
				} catch (IOException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				
				int ax[] = new int[4];
				int ay[] = new int[4];
				int az[] = new int[4];
				int gx[] = new int[4];
				int gy[] = new int[4];
				int gz[] = new int[4];
				
				// �o�b�t�@�[�Ɏ󂯎�������̂��r�b�g�V�t�g����int�^�Ƃ��Ċi�[
				for (int i = 0; i < 4; i++) {
					ax[i] = (readBuffer[i * 12 + 1] << 8)  + readBuffer[i * 12 + 2];
					ay[i] = (readBuffer[i * 12 + 3] << 8)  + readBuffer[i * 12 + 4];
					az[i] = (readBuffer[i * 12 + 5] << 8)  + readBuffer[i * 12 + 6];
					gx[i] = (readBuffer[i * 12 + 7] << 8)  + readBuffer[i * 12 + 8];
					gy[i] = (readBuffer[i * 12 + 9] << 8)  + readBuffer[i * 12 + 10];
					gz[i] = (readBuffer[i * 12 + 11] << 8) + readBuffer[i * 12 + 12];
				}
				
				// dps�ɕϊ�
				for (int i = 0; i < 4; i++) {
					ax[i] = ax[i] * 2000 / 32767;
					ay[i] = ay[i] * 2000 / 32767;
					az[i] = az[i] * 2000 / 32767;
					gx[i] = gx[i] * 2000 / 32767;
					gy[i] = gy[i] * 2000 / 32767;
					gz[i] = gz[i] * 2000 / 32767;
				}
				
				dartsData.addAx(ax);
				dartsData.addAy(ay);
				dartsData.addAz(az);
				dartsData.addGx(gx);
				dartsData.addGy(gy);
				dartsData.addGz(gz);
				
				// �O���t�`��p�̃f�[�^��4�v�f���炷
				dartsData.displaceData();
				// ���4�v�f��V���ȃf�[�^�ɏ�������
				for (int i = 0; i < 4; i++) {
					dartsData.gDataX[296 + i] = gx[i];
					dartsData.gDataZ[296 + i] = gz[i];
				}
				
				/*
				System.out.println("SEQ:" + readBuffer[0]);
				for (int i = 0; i < 4; i++) {
					System.out.println("ax: " + ax[i] + " ay: " + ay[i] + " az: " + az[i]);
					System.out.println("gz: " + gx[i] + " gy: " + gy[i] + " gz: " + gz[i]);
				}
				*/
				// -----
				for (int i = 0; i < 4; i++) {
					sift.t += 0.01;
					sift.ax = ax[i];
					sift.ay = ay[i];
					sift.az = az[i];
					sift.gx = gx[i];
					sift.gy = gy[i];
					sift.gz = gz[i];
					
					sift.calcSift();
				}
			}
		}
	}
}
