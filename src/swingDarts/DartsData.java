package swingDarts;

import java.util.*;

public class DartsData {
	// �����x, �p���x�ێ��p�����o�ϐ�
	List<Integer> accelX = new ArrayList<Integer>();
	List<Integer> accelY = new ArrayList<Integer>();
	List<Integer> accelZ = new ArrayList<Integer>();
	List<Integer> gyroX = new ArrayList<Integer>();
	List<Integer> gyroY = new ArrayList<Integer>();
	List<Integer> gyroZ = new ArrayList<Integer>();
	
	// �O���t�`��p��300�f�[�^�̈�m��
	// 100Hz�����炾���炨���炭3�b���ɂȂ�͂�
	int[] gDataX = new int[300];
	int[] gDataZ = new int[300];
	
	// �R���X�g���N�^
	public DartsData() {
		// �O���t�`��p�f�[�^�̈揉����
		for (int i = 0; i < 300; i++) {
			gDataX[i] = -1;
			gDataZ[i] = -1;
		}
	}
	
	// �O���t�`��p�f�[�^ 4�v�f�V�t�g
	void displaceData() {
		int[] tempX = new int[300];
		int[] tempY = new int[300];
		
		for (int i = 0; i < 300; i++) {
			tempX[i] = gDataX[i];
			tempY[i] = gDataZ[i];
		}
		
		for (int i = 0; i < 296; i++) {
			gDataX[i] = tempX[i + 4];
			gDataZ[i] = tempY[i + 4];
		}
	}
	
	// setter
	void addAx(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			accelX.add(buffer[i]);
		}
	}

	void addAy(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			accelY.add(buffer[i]);
		}
	}
	
	void addAz(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			accelZ.add(buffer[i]);
		}
	}
	
	void addGx(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			gyroX.add(buffer[i]);
		}
	}
	
	void addGy(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			gyroY.add(buffer[i]);
		}
	}
	
	void addGz(int[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			gyroZ.add(buffer[i]);
		}
	}
	
	// getter
	List<Integer> getGx() {
		return gyroX;
	}
	
	List<Integer> getGy() {
		return gyroY;
	}
	
	List<Integer> getGz() {
		return gyroZ;
	}

	int getGDataX(int i) {
		return gDataX[i];
	}
	
	int getGDataZ(int i) {
		return gDataZ[i];
	}
	
}
